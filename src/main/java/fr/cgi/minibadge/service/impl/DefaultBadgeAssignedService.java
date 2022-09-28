package fr.cgi.minibadge.service.impl;

import fr.cgi.minibadge.Minibadge;
import fr.cgi.minibadge.core.constants.Database;
import fr.cgi.minibadge.core.constants.Field;
import fr.cgi.minibadge.helper.PromiseHelper;
import fr.cgi.minibadge.helper.SqlHelper;
import fr.cgi.minibadge.model.BadgeAssigned;
import fr.cgi.minibadge.model.BadgeType;
import fr.cgi.minibadge.service.BadgeAssignedService;
import fr.cgi.minibadge.service.BadgeService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import org.entcore.common.sql.Sql;
import org.entcore.common.sql.SqlResult;
import org.entcore.common.user.UserInfos;

import javax.xml.crypto.Data;
import java.util.Collections;
import java.util.List;

import static fr.cgi.minibadge.service.impl.DefaultBadgeService.BADGE_TABLE;
import static fr.cgi.minibadge.service.impl.DefaultBadgeTypeService.BADGE_TYPE_TABLE;

public class DefaultBadgeAssignedService implements BadgeAssignedService {

    private final Sql sql;
    private final BadgeService badgeService;
    private static final String BADGE_ASSIGNED_TABLE = String.format("%s.%s", Minibadge.dbSchema, Database.BADGE_ASSIGNED);
    public static final String BADGE_ASSIGNED_VALID_TABLE = String.format("%s.%s", Minibadge.dbSchema, Database.BADGE_ASSIGNED_VALID);

    public DefaultBadgeAssignedService(Sql sql, BadgeService badgeService) {
        this.sql = sql;
        this.badgeService = badgeService;
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
    public Future<List<BadgeAssigned>> getBadgesGiven(String assignorId) {
        Promise<List<BadgeAssigned>> promise = Promise.promise();
        getBadgesGivenRequest(assignorId)
                .onSuccess(badgesGiven -> promise.complete(new BadgeAssigned().toList(badgesGiven)))
                .onFailure(promise::fail);

        return promise.future();
    }

    private Future<JsonArray> getBadgesGivenRequest(String assignorId) {
        Promise<JsonArray> promise = Promise.promise();

        JsonArray params = new JsonArray();
        params.add(assignorId);

        String request = "SELECT ba.id, ba.badge_id, ba.assignor_id, ba.accepted_at, ba.revoked_at, ba.updated_at, ba.created_at , bt.picture_id , bt.label" +
                ", badge.id as " + Field.BADGE_ID+ " , bt.id as  " + Field.BADGE_TYPE_ID +
                " FROM " + BADGE_ASSIGNED_TABLE +" as ba " +
                " INNER JOIN " + BADGE_TABLE +" " +
                " on ba.badge_id = badge.id " +
                " INNER JOIN " + BADGE_TYPE_TABLE +" as bt " +
                " on badge.badge_type_id = bt.id" +
                " WHERE ba.assignor_id = ? " +
                " ; ";


        sql.prepared(request, params, SqlResult.validResultHandler(PromiseHelper.handler(promise,
                String.format("[Minibadge@%s::getBadgesTypesRequest] Fail to retrieve badge types",
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
}
