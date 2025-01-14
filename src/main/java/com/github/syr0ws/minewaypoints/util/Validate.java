package com.github.syr0ws.minewaypoints.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {

    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    public static boolean isUUID(String str) {
        Matcher matcher = UUID_PATTERN.matcher(str);
        return matcher.matches();
    }
}
