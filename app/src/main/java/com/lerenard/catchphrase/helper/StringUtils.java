package com.lerenard.catchphrase.helper;

/**
 * Created by mc on 11-Jan-17.
 */

public class StringUtils {

    public static String join(String delimiter, String... elems) {
        StringBuilder builder = new StringBuilder(elems[0]);
        for (int i = 1; i < elems.length; ++i) {
            builder.append(delimiter);
            builder.append(elems[i]);
        }
        return builder.toString();
    }

    public static String trim(String text, int width) {
        if (text.length() <= width) {
            return text;
        }
        return text.substring(0, width - 3) + "...";
    }

    /**
     * @return the index of the ordinal'th instance of c in text, or -1 if it does not exist
     */
    public static int indexOf(String text, char c, int ordinal) {
        int res = -1;
        if (ordinal > 0) {
            do {
                res = text.indexOf(c, res);
            } while (--ordinal > 0 && res != -1);
        }
        return res;
    }
}
