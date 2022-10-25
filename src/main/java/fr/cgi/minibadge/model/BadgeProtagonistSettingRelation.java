package fr.cgi.minibadge.model;

import fr.cgi.minibadge.core.constants.Field;
import io.vertx.core.json.JsonObject;
//CORRESPOND A LA TABLE : https://confluence.support-ent.fr/display/BAD/Ajout+de+la+table+Protagoniste
public class BadgeProtagonistSettingRelation implements Model{
    BadgeProtagonistSetting assignor = new BadgeProtagonistSetting();
    BadgeProtagonistSetting receiver = new BadgeProtagonistSetting();

    public BadgeProtagonistSettingRelation() {
    }

    @Override
    public JsonObject toJson() {
     return new JsonObject().put(Field.ASSIGNORTYPE,assignor.toJson())
                .put(Field.RECEIVERTYPE,receiver.toJson());
    }

    public BadgeProtagonistSetting assignor() {
        return assignor;
    }

    public void setAssignor(BadgeProtagonistSetting assignor) {
        this.assignor = assignor;
    }

    public BadgeProtagonistSetting receiver() {
        return receiver;
    }

    public void setReceiver(BadgeProtagonistSetting receiver) {
        this.receiver = receiver;
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
