package io.github.sekassel.moea.util;

import org.moeaframework.util.TypedProperties;

public class ConfigUtil {
    public static String toJson(TypedProperties configuration) {
        final StringBuilder jsonBuilder = new StringBuilder();
        for (final String key : configuration.keySet()) {
            if (!jsonBuilder.isEmpty()) {
                jsonBuilder.append(',');
            }
            jsonBuilder.append('"').append(key).append("\":\"").append(configuration.getString(key)).append('"');
        }
        return "{" + jsonBuilder + "}";
    }
}
