package fr.cgi.minibadge.service.impl;

import fr.cgi.minibadge.Minibadge;
import fr.cgi.minibadge.core.constants.Database;
import fr.cgi.minibadge.core.constants.Field;
import fr.cgi.minibadge.core.constants.Rights;
import fr.cgi.minibadge.helper.Neo4jHelper;
import fr.cgi.minibadge.helper.PromiseHelper;
import fr.cgi.minibadge.model.User;
import fr.cgi.minibadge.service.UserService;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.entcore.common.sql.Sql;
import org.entcore.common.sql.SqlResult;
import org.entcore.common.user.UserInfos;
import org.entcore.common.user.UserUtils;

import java.util.*;
import java.util.stream.Collectors;

import static fr.cgi.minibadge.core.constants.Request.*;
import static fr.wseduc.webutils.Utils.handlerToAsyncHandler;

public class DefaultUserService implements UserService {

    private final EventBus eb;
    private final Logger log = LoggerFactory.getLogger(PromiseHelper.class);
    private final Sql sql;
    private static final String USER_TABLE = String.format("%s.%s", Minibadge.dbSchema, Database.USER);

    public DefaultUserService(Sql sql, EventBus eb) {
        this.sql = sql;
        this.eb = eb;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Future<List<User>> search(HttpServerRequest request, String query) {
        Promise<List<User>> promise = Promise.promise();
        searchRequest(request, query)
                .onFailure(promise::fail)
                .onSuccess(users -> promise.complete(new User().toList(users)
                        .stream().filter(user -> user.permissions().acceptChart() != null
                                && user.permissions().acceptReceive() != null).collect(Collectors.toList())));

        return promise.future();
    }

    private Future<JsonArray> searchRequest(HttpServerRequest request, String query) {
        Promise<JsonArray> promise = Promise.promise();
        JsonObject params = new JsonObject();

        String preFilter = Neo4jHelper.searchQueryInColumns(query,
                Arrays.asList(String.format("m.%s", Field.FIRSTNAME), String.format("m.%s", Field.LASTNAME)),
                params);

        String userAlias = "visibles";
        String prefAlias = "uac";
        String customReturn = String.format(" %s RETURN distinct visibles.id as id, visibles.lastName as lastName, " +
                        "visibles.firstName as firstName, uac.%s as permissions ",
                Neo4jHelper.matchUsersWithPreferences(userAlias, prefAlias, Database.MINIBADGECHART,
                        Neo4jHelper.usersNodeHasRight(Rights.FULLNAME_RECEIVE, params)),
                Database.MINIBADGECHART);

        UserUtils.findVisibleUsers(eb, request, false, true,
                String.format(" %s %s ", (query != null && !query.isEmpty()) ? "AND" : "", preFilter),
                customReturn, params, promise::complete);

        return promise.future();
    }

    @Override
    public Future<Void> upsert(List<String> usersIds) {
        Promise<Void> promise = Promise.promise();
        JsonArray statements = new JsonArray();
        Set<String> distinctUsersIds = new HashSet<>(usersIds);
        usersIds = new ArrayList<>(distinctUsersIds);
        JsonObject params = new JsonObject();
        params.put(USER_ID, new JsonArray(usersIds));
        params.put(ACTION, LIST_USERS);
        List<User> users = new ArrayList<>();
        eb.request(DIRECTORY, params, handlerToAsyncHandler(message -> {
            if (message.body().getString(STATUS).equals(OK)) {
                message.body().getJsonArray(RESULT).stream().forEach(userData -> {
                    User user = new User();
                    JsonObject userJo = ((JsonObject) userData);
                    user.setUserId(userJo.getString(Field.ID));
                    user.setUsername(userJo.getString(Field.USERNAME));
                    users.add(user);
                });
                users.forEach(user -> statements.add(upsertStatement(user)));
                sql.transaction(statements, event -> {
                    if (event.body().getString(STATUS).equals(OK)) {
                        promise.complete();
                    }
                });
            }
        }));
        return promise.future();
    }

    private JsonObject upsertStatement(User user) {
        String statement = String.format(" INSERT INTO %s (id , display_name ) " +
                " VALUES ( ? , ?) ON CONFLICT (id) DO UPDATE SET display_name = ?" +
                "  WHERE %s.id = EXCLUDED.id ;", USER_TABLE, USER_TABLE);
        JsonArray params = new JsonArray()
                .add(user.getUserId())
                .add(user.getUsername())
                .add(user.getUsername());

        return new JsonObject()
                .put("statement", statement)
                .put("values", params)
                .put("action", "prepared");
    }
}
