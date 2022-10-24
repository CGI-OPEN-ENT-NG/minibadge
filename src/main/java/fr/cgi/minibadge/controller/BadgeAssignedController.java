package fr.cgi.minibadge.controller;

import fr.cgi.minibadge.core.constants.Database;
import fr.cgi.minibadge.core.constants.Field;
import fr.cgi.minibadge.core.constants.Request;
import fr.cgi.minibadge.model.Model;
import fr.cgi.minibadge.security.UsersAssignRight;
import fr.cgi.minibadge.service.BadgeAssignedService;
import fr.cgi.minibadge.service.ServiceFactory;
import fr.wseduc.rs.ApiDoc;
import fr.wseduc.rs.Get;
import fr.wseduc.rs.Post;
import fr.wseduc.security.ActionType;
import fr.wseduc.security.SecuredAction;
import fr.wseduc.webutils.request.RequestUtils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.entcore.common.controller.ControllerHelper;
import org.entcore.common.http.filter.ResourceFilter;
import org.entcore.common.user.UserUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BadgeAssignedController extends ControllerHelper {

    private final BadgeAssignedService badgeAssignedService;

    public BadgeAssignedController(ServiceFactory serviceFactory) {
        super();
        this.badgeAssignedService = serviceFactory.badgeAssignedService();
    }

    @Get("/assigned/given")
    @ApiDoc("get all the badge the user has given")
    public void get(HttpServerRequest request) {
        String query = request.params().get(Request.QUERY);
        String startDate = request.params().get(Request.START_DATE);
        String endDate = request.params().get(Request.END_DATE);
        String sortType = request.params().get(Request.SORT_BY);
        Boolean sortAsc = Boolean.parseBoolean(request.params().get(Request.SORT_ASC));
        UserUtils.getUserInfos(eb, request, user -> badgeAssignedService.getBadgesGiven(eb, query, startDate, endDate, sortType, sortAsc, user.getUserId())
                .onSuccess(badges -> renderJson(request, new JsonObject()
                                .put(Request.ALL, new JsonArray(badges.stream().map(Model::toJson).collect(Collectors.toList())))
                        )
                )
                .onFailure(err -> renderError(request, new JsonObject().put(Request.MESSAGE, err.getMessage()))));
    }

    @Post("/types/:typeId/assign")
    @ApiDoc("Create badge assigned with badge creation if not exists")
    @SecuredAction(value = "", type = ActionType.RESOURCE)
    @ResourceFilter(UsersAssignRight.class)
    @SuppressWarnings("unchecked")
    public void assign(HttpServerRequest request) {
        long typeId = Long.parseLong(request.params().get(Database.TYPEID));

        RequestUtils.bodyToJson(request, String.format("%s%s", pathPrefix, "badgeAssignedCreate"), body -> {
            List<String> ownerIds = body.getJsonArray(Field.OWNERIDS).getList();
            UserUtils.getUserInfos(eb, request, user -> badgeAssignedService.assign(typeId, ownerIds, user)
                    .onSuccess(badgeType -> renderJson(request, new JsonObject()))
                    .onFailure(err -> renderError(request, new JsonObject().put(Request.MESSAGE, err.getMessage()))));
        });
    }
}
