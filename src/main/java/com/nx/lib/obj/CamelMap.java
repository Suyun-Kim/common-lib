package com.nx.lib.obj;

import java.util.LinkedHashMap;

public class CamelMap extends LinkedHashMap {

    @Override
    public Object put(Object key, Object value) {
        return super.put(convertToCamelCase((String) key), value);
    }

    protected String convertToCamelCase(String columnName) {

        if (columnName.equals("LID") || columnName.equals("PID")) {
            return columnName.toLowerCase();
        } else if(columnName.equals("NPCDropBoxTemplateID")) {
            return "npcDropBoxTemplateID";
        }

        StringBuilder result = new StringBuilder();
        int len = columnName.length();

        for (int i = 0; i < len; i++) {

            char currentChar = columnName.charAt(i);

            if (i <= 1) result.append(Character.toLowerCase(currentChar));
            else result.append(currentChar);

        }
        return result.toString();
    }

}
