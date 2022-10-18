package fr.cgi.minibadge.service.impl;

import fr.cgi.minibadge.Minibadge;
import fr.cgi.minibadge.core.constants.Database;
import fr.cgi.minibadge.helper.PromiseHelper;
import fr.cgi.minibadge.helper.SqlHelper;
import fr.cgi.minibadge.helper.UserHelper;
import fr.cgi.minibadge.model.BadgeAssigned;
import fr.cgi.minibadge.model.User;
import fr.cgi.minibadge.service.BadgeAssignedService;
import fr.cgi.minibadge.service.BadgeService;
import fr.cgi.minibadge.service.UserService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.entcore.common.sql.Sql;
import org.entcore.common.sql.SqlResult;
import org.entcore.common.user.UserInfos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fr.cgi.minibadge.core.constants.Field.*;
import static fr.cgi.minibadge.service.impl.DefaultBadgeService.BADGE_TABLE;
import static fr.cgi.minibadge.service.impl.DefaultBadgeTypeService.BADGE_TYPE_TABLE;
import static fr.cgi.minibadge.service.impl.DefaultUserService.USER_TABLE;

public class DefaultBadgeAssignedService implements BadgeAssignedService {

    public static final String BADGE_ASSIGNED_VALID_TABLE = String.format("%s.%s", Minibadge.dbSchema, Database.BADGE_ASSIGNED_VALID);
    private static final String BADGE_ASSIGNED_TABLE = String.format("%s.%s", Minibadge.dbSchema, Database.BADGE_ASSIGNED);
    private final Sql sql;
    private final BadgeService badgeService;
    private final UserService userService;

    public DefaultBadgeAssignedService(Sql sql, BadgeService badgeService, UserService userService) {
        this.sql = sql;
        this.badgeService = badgeService;
        this.userService = userService;
    }

    @Override
    public Future<Void> assign(long typeId, List<String> ownerIds, UserInfos assignor) {
        Promise<Void> promise = Promise.promise();
        badgeService
                .createBadges(typeId, ownerIds)
                .compose(badgeResult -> createBadgeAssignedRequest(typeId, ownerIds, assignor))
                .onSuccess(badgeTypes -> promise.complete())
                .onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<List<BadgeAssigned>> getBadgesGiven(EventBus eb, String query, String startDate, String endDate, String sortBy,
                                                      Boolean sortAsc, String assignorId) {
        Promise<List<BadgeAssigned>> promise = Promise.promise();
        getBadgesGivenRequest(assignorId, startDate, endDate, sortBy, sortAsc, query)
                .onSuccess(badgesGiven -> promise.complete(new BadgeAssigned().toList(badgesGiven)))
                .onFailure(promise::fail);

        return promise.future();
    }

    private Future<JsonArray> getBadgesGivenRequest(String assignorId, String startDate, String endDate,
                                                    String sortBy, Boolean sortAsc, String query) {
        Promise<JsonArray> promise = Promise.promise();
        List<String> acceptedSort = Arrays.asList(LABEL, CREATED_AT, REVOKED_AT, DISPLAY_NAME);
        List<String> columns = Arrays.asList(DISPLAY_NAME, LABEL);
        JsonArray params = new JsonArray();
        params.add(assignorId);
        boolean hasDates = startDate != null && endDate != null;
        boolean hasSort = sortBy != null && sortAsc != null;
        if (hasDates) {
            params.add(startDate);
            params.add(endDate);
        }
        String request = "SELECT ba.id, ba.badge_id, ba.assignor_id, ba.revoked_at, ba.updated_at, " +
                " ba.created_at as created_at , bt.picture_id , us.display_name ," +
                " bt.label as label, badge.owner_id " +
                ", badge.id as " + BADGE_ID + " , bt.id as  " + BADGE_TYPE_ID +
                " FROM " + BADGE_ASSIGNED_TABLE + " as ba " +
                " INNER JOIN " + BADGE_TABLE + " " +
                " on ba.badge_id = badge.id " +
                " INNER JOIN " + BADGE_TYPE_TABLE + " as bt " +
                " on badge.badge_type_id = bt.id " +
                " INNER JOIN " + USER_TABLE + " as us " +
                " ON us.id = badge.owner_id " +
                " WHERE ba.assignor_id = ? " +
                ((hasDates) ? " AND ba.created_at::date  >= to_date(?,'DD-MM-YYYY') " +
                        " AND ba.created_at::date  <= to_date( ?, 'DD-MM-YYYY') " : "") +
                ((query != null && !query.isEmpty()) ? " AND " + SqlHelper.searchQueryInColumns(query, columns, params) : " ") +
                " ORDER BY " +
                ((hasSort && acceptedSort.contains(sortBy)) ? sortBy + (sortAsc ? " ASC " : " DESC ") : " id ") +
                " ; ";

        sql.prepared(request, params, SqlResult.validResultHandler(PromiseHelper.handler(promise,
                String.format("[Minibadge@%s::getBadgesGivenRequest] Fail to retrieve badge given",
                        this.getClass().getSimpleName()))));

        return promise.future();
    }


    private Future<JsonArray> createBadgeAssignedRequest(long typeId,
                                                         List<String> ownerIds, UserInfos assignor) {
        Promise<JsonArray> promise = Promise.promise();

        String request = String.format("INSERT INTO %s (badge_id, assignor_id) " +
                        " SELECT id as badge_id, ? as assignor_id FROM %s " +
                        " WHERE badge_type_id = ? AND owner_id IN %s", BADGE_ASSIGNED_TABLE,
                DefaultBadgeService.BADGE_ASSIGNABLE_TABLE, Sql.listPrepared(ownerIds));

        JsonArray params = new JsonArray()
                .add(assignor.getUserId())
                .add(typeId)
                .addAll(new JsonArray(ownerIds));

        sql.prepared(request, params, SqlResult.validResultHandler(PromiseHelper.handler(promise,
                String.format("[Minibadge@%s::createBadgeAssignedRequest] Fail to create badge assigned",
                        this.getClass().getSimpleName()))));

        return promise.future();
    }

    @Override
    public Future<List<User>> getBadgeTypeAssigners(long typeId, UserInfos badgeOwner, int limit, Integer offset) {
        Promise<List<User>> promise = Promise.promise();

        List<User> assigners = new ArrayList<>();
        getBadgeTypeAssignerIdsRequest(typeId, badgeOwner, limit, offset)
                .compose(users -> {
                    assigners.addAll(new User().toList(users));
                    return userService.getUsers(assigners.stream().map(UserInfos::getUserId)
                            .collect(Collectors.toList()));
                })
                .onSuccess(users -> promise.complete(UserHelper.mergeUsernamesAndProfiles(users, assigners)))
                .onFailure(promise::fail);

        return promise.future();
    }


    private Future<JsonArray> getBadgeTypeAssignerIdsRequest(long typeId, UserInfos badgeOwner, int limit, Integer offset) {
        Promise<JsonArray> promise = Promise.promise();
        JsonArray params = new JsonArray()
                .add(badgeOwner.getUserId())
                .add(typeId);

        String request = String.format(" SELECT DISTINCT(assignor_id) as id, bav.created_at " +
                        " FROM %s bav INNER JOIN %s bp on bp.id = bav.badge_id " +
                        " WHERE owner_id = ? AND badge_type_id = ? " +
                        " ORDER BY bav.created_at DESC %s", BADGE_ASSIGNED_VALID_TABLE,
                DefaultBadgeService.BADGE_PUBLIC_TABLE, SqlHelper.addLimitOffset(limit, offset, params));

        sql.prepared(request, params,
                SqlResult.validResultHandler(PromiseHelper.handler(promise,
                        String.format("[Minibadge@%s::getBadgeTypeAssignerIdsRequest] " +
                                        "Fail to retrieve badge types assigners",
                                this.getClass().getSimpleName()))));

        return promise.future();
    }

    @Override
    public Future<Integer> countBadgeTypeAssigners(long typeId, UserInfos badgeOwner) {
        Promise<Integer> promise = Promise.promise();

        countBadgeTypeAssignerIdsRequest(typeId, badgeOwner)
                .onSuccess(result -> promise.complete(SqlHelper.getResultCount(result)))
                .onFailure(promise::fail);

        return promise.future();
    }


    private Future<JsonObject> countBadgeTypeAssignerIdsRequest(long typeId, UserInfos badgeOwner) {
        Promise<JsonObject> promise = Promise.promise();
        JsonArray params = new JsonArray()
                .add(badgeOwner.getUserId())
                .add(typeId);

        String request = String.format(" SELECT COUNT(DISTINCT(assignor_id)) " +
                        " FROM %s bav INNER JOIN %s bp on bp.id = bav.badge_id " +
                        " WHERE owner_id = ? AND badge_type_id = ?", BADGE_ASSIGNED_VALID_TABLE,
                DefaultBadgeService.BADGE_PUBLIC_TABLE);

        sql.prepared(request, params,
                SqlResult.validUniqueResultHandler(PromiseHelper.handler(promise,
                        String.format("[Minibadge@%s::countBadgeTypeAssignerIdsRequest] " +
                                        "Fail to retrieve badge types assigners",
                                this.getClass().getSimpleName()))));

        return promise.future();
    }

    @Override
    public Future<JsonArray> revoke(String userId, long badgeId) {
        Promise<JsonArray> promise = Promise.promise();

        JsonArray params = new JsonArray();
        params.add(badgeId)
                .add(userId);

        String request = "UPDATE " + BADGE_ASSIGNED_TABLE +
                " SET revoked_at = NOW ()" +
                " WHERE id = ? and assignor_id = ? ; ";

        sql.prepared(request, params, SqlResult.validResultHandler(PromiseHelper.handler(promise,
                String.format("[Minibadge@%s::getBadgesTypesRequest] Fail to revoke badge",
                        this.getClass().getSimpleName()))));
        return promise.future();
    }
}
