package fr.cgi.minibadge.core.constants;

public class Database {
    public static final String BADGE = "badge";
    public static final String BADGE_ASSIGNABLE = "badge_assignable";
    public static final String BADGE_ASSIGNED = "badge_assigned";
    public static final String BADGE_TYPE = "badge_type";
    public static final String LABEL = "label";
    public static final String STRUCTUREID = "structureId";
    public static final String STRUCTURE_ID = "structure_Id";
    public static final String TYPEID = "typeId";
    public static final String PRIVATIZED_AT = "privatized_at";
    public static final String REFUSED_AT = "refused_at";

    private Database() {
        throw new IllegalStateException("Utility class");
    }
}

