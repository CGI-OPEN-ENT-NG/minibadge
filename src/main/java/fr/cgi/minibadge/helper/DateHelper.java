package fr.cgi.minibadge.helper;

import fr.cgi.minibadge.core.constants.DateConst;

public class DateHelper {
    private DateHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String getFormatFromConstant(String constant) {
        switch (constant) {
            case DateConst.DAY:
            default:
                return DateConst.DAY_MONTH_YEAR_KEBAB;
        }
    }

}
