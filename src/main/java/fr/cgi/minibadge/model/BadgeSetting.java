package fr.cgi.minibadge.model;

import fr.cgi.minibadge.core.constants.Field;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static fr.cgi.minibadge.core.constants.Database.STRUCTUREID;

public class BadgeSetting implements Model {
    //A changer des que la bdd ser opérationnelle
    List<BadgeProtagonistSettingRelation> relations = new ArrayList<>();


    boolean is_self_assignable = false;
    String structureId;

    public BadgeSetting() {
        relations.add(new BadgeProtagonistSettingRelation());
    }

    public List<BadgeProtagonistSettingRelation> relations() {
        return relations;
    }

    public void setRelations(List<BadgeProtagonistSettingRelation> relations) {
        this.relations = relations;
    }

    public void addRelation(BadgeProtagonistSettingRelation relation) {
        this.relations.add(relation);
    }

    public boolean is_self_assignable() {
        return is_self_assignable;
    }

    public void is_self_assignable(boolean is_self_assignable) {
        this.is_self_assignable = is_self_assignable;
    }

    public String structureId() {
        return structureId;
    }

    public void setStructureId(String structureId) {
        this.structureId = structureId;
    }

    @Override
    public JsonObject toJson() {
        JsonArray relationsList = new JsonArray();
        relations.forEach(relation -> relationsList.add(relation.toJson()));

        return new JsonObject()
                .put(Field.RELATIONS , relationsList)
                .put(Field.ISSELFASSIGNABLE, is_self_assignable)
                .put(STRUCTUREID, structureId);
    }

    @Override
    public Model model(JsonObject model) {
        return null;
    }

    @Override
    public Model set(JsonObject model) {
        return null;
    }
}
