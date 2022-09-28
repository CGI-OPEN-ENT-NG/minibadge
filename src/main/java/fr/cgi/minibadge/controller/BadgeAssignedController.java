package fr.cgi.minibadge.controller;

import fr.cgi.minibadge.core.constants.Database;
import fr.cgi.minibadge.core.constants.Field;
import fr.cgi.minibadge.core.constants.Request;
import fr.cgi.minibadge.service.BadgeAssignedService;
import fr.cgi.minibadge.service.ServiceFactory;
import fr.wseduc.rs.ApiDoc;
import fr.wseduc.rs.Get;
import fr.wseduc.rs.Post;
import fr.wseduc.webutils.request.RequestUtils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.entcore.common.controller.ControllerHelper;
import org.entcore.common.user.UserUtils;

import java.util.List;

public class BadgeAssignedController extends ControllerHelper {

    private final BadgeAssignedService badgeAssignedService;

    public BadgeAssignedController(ServiceFactory serviceFactory) {
        super();
        this.badgeAssignedService = serviceFactory.badgeAssignedService();
    }
    @Get("/assigned/given")
    @ApiDoc("get all the badge the user has given")
    public void get(HttpServerRequest request){
        JsonArray ja = new JsonArray();
        JsonObject jo =  new JsonObject();
        jo.put("id",1)
                .put("assignorId","assigneur")
                .put("badgeId",2)
                .put("created","test");
        ja.add(jo);
        ja.add(jo);
        ja.add(jo);
        ja.add(jo);
        ja.add(jo);
        ja.add(jo);
        renderJson(request, ja);
    }

    @Post("/types/:typeId/assign")
    @ApiDoc("Create badge assigned with badge creation if not exists")
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
