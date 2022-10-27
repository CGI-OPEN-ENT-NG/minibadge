package fr.cgi.minibadge.model;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public interface Model<I extends Model<I>> {
    JsonObject toJson();

    I model(JsonObject model);

    I set(JsonObject model);

    @SuppressWarnings("unchecked")
    default List<I> toList(JsonArray results) {
        return ((List<JsonObject>) results.getList()).stream().map(this::model).collect(Collectors.toList());
    }

    default JsonArray toArray(List<I> models) {
        return new JsonArray(models.stream().map(Model::toJson).collect(Collectors.toList()));
    }

}
