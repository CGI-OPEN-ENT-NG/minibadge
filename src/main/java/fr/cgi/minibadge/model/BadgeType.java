package fr.cgi.minibadge.model;

import fr.cgi.minibadge.core.constants.Database;
import fr.cgi.minibadge.core.constants.Field;
import fr.wseduc.webutils.I18n;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class BadgeType implements Model<BadgeType> {

    private Long id;
    private String structureId;
    private String ownerId;
    private String pictureId;
    private String label;
    private String description;
    private String createdAt;
    private User owner;
    private String level = "Éleves";
    private final List<String> assignableBy = Collections.singletonList("Éleves");

    public BadgeType() {
    }

    public BadgeType(JsonObject badgeType) {
        this.set(badgeType);
    }

    @Override
    public BadgeType set(JsonObject badgeType) {
        this.id = badgeType.getLong(Field.ID);
        this.structureId = badgeType.getString(Database.STRUCTURE_ID, badgeType.getString(Database.STRUCTUREID));
        this.ownerId = badgeType.getString(Field.OWNER_ID, badgeType.getString(Field.OWNERID));
        this.pictureId = badgeType.getString(Field.PICTURE_ID, badgeType.getString(Field.PICTUREID));
        this.label = badgeType.getString(Field.LABEL);
        this.description = badgeType.getString(Field.DESCRIPTION);
        this.createdAt = badgeType.getString(Field.CREATED_AT, badgeType.getString(Field.CREATEDAT));
        return this;
    }

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String structureId() {
        return structureId;
    }

    public void setStructureId(String structureId) {
        this.structureId = structureId;
    }

    public String ownerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String pictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public String label() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String description() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String level() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public User owner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public JsonObject toJson() {
        JsonObject badgeType = new JsonObject()
                .put(Field.ID, this.id)
                .put(Database.STRUCTUREID, this.structureId)
                .put(Field.OWNERID, this.ownerId)
                .put(Field.PICTUREID, this.pictureId)
                .put(Field.LABEL, this.label)
                .put(Field.CREATEDAT, this.createdAt)
                .put(Field.DESCRIPTION, this.description)
                .put(Field.LEVEL, this.level)
                .put(Field.ASSIGNABLEBY, this.assignableBy);

        if (this.owner != null)
            badgeType.put(Field.OWNER, this.owner.toJson());

        return badgeType;
    }

    @Override
    public BadgeType model(JsonObject badgeType) {
        return new BadgeType(badgeType);
    }
}