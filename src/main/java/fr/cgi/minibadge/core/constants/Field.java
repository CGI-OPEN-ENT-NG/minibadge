package fr.cgi.minibadge.core.constants;

public class Field {
    public static final String ID = "id";
    public static final String OWNER = "owner";
    public static final String OWNERID = "ownerId";
    public static final String OWNERIDS = "ownerIds";
    public static final String OWNER_ID = "owner_id";
    public static final String PICTUREID = "pictureId";
    public static final String PICTURE_ID = "picture_id";
    public static final String LABEL = "label";
    public static final String DESCRIPTION = "description";
    public static final String CREATEDAT = "createdAt";
    public static final String CREATED_AT = "created_at";
    public static final String DISPLAYNAME = "displayName";
    public static final String FIRSTNAME = "firstName";
    public static final String LASTNAME = "lastName";
    public static final String USERNAME = "username";
    public static final String IDUSERS = "idUsers";
    public static final String Q = "q";
    public static final String FIELDS = "fields";
    public static final String PROFILE = "profile";

    //BADGE
    public static final String PRIVATIZED_AT = "privatized_at";
    public static final String PRIVATIZEDAT = "privatizedAt";
    public static final String REFUSED_AT = "refused_at";
    public static final String REFUSEDAT = "refusedAt";
    public static final String DISABLED_AT = "disabled_at";
    public static final String DISABLEDAT = "disabledAt";

    // CHART
    public static final String ACCEPTCHART = "acceptChart";
    public static final String ACCEPTASSIGN = "acceptAssign";
    public static final String ACCEPTRECEIVE = "acceptReceive";
    public static final String PERMISSIONS = "permissions";

    //USER
    public static final String USERIDS = "userIds";

    private Field() {
        throw new IllegalStateException("Utility class");
    }
}

