package com.talend.tuj.generator.utils;

import java.util.HashMap;
import java.util.Map;

public class SubstitutionCmdHandler {
    /*
    arg : xxx=>yyy;aa=>bb
     */
    public static Map<String, String> processArgument(String arg) {
        Map<String, String> substitutions = new HashMap<>();

        for (String substitution : arg.split(";")) {
            String[] parts = substitution.split("=>");
            substitutions.put(parts[0], parts[1]);
        }

        return substitutions;
    }
}
