package fr.cgi.minibadge.model;

import fr.cgi.minibadge.core.constants.Field;
import io.vertx.core.json.JsonObject;


public class Config implements Model<Config> {

    private Integer mostAssignedTypeListSize;
    private Integer mostRefusedTypeListSize;

    public Config() {
    }

    public Config(JsonObject statistics) {
        this.set(statistics);
    }

    @Override
    public Config set(JsonObject statistics) {
        this.mostAssignedTypeListSize = statistics.getInteger(Field.MOST_ASSIGNED_TYPE_LIST_SIZE);
        this.mostRefusedTypeListSize = statistics.getInteger(Field.MOST_REFUSED_TYPE_LIST_SIZE);
        return this;
    }

    public Integer mostAssignedTypeListSize() {
        return this.mostAssignedTypeListSize != null ? this.mostAssignedTypeListSize : 3;
    }

    public Integer mostRefusedTypeListSize() {
        return this.mostRefusedTypeListSize != null ? this.mostRefusedTypeListSize : 3;
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject()
                .put(Field.MOST_ASSIGNED_TYPE_LIST_SIZE, mostAssignedTypeListSize)
                .put(Field.MOST_REFUSED_TYPE_LIST_SIZE, mostRefusedTypeListSize);
    }

    @Override
    public Config model(JsonObject model) {
        return new Config(model);
    }
}