package com.noljo.nolzo.support;

public enum SystemTableName {
    INFORMATION_SCHEMA,
    SYSTEM_,
    ENUM_,
    IN_,
    CONSTANTS,
    RIGHTS,
    ROLES,
    SESSIONS,
    SESSION_STATE,
    SETTINGS,
    USERS,
    SYNONYMS,
    QUERY_STATISTICS,
    LOCKS,
    INDEXES,
    INDEX_COLUMNS;

    public static boolean matches(String tableName) {
        for (SystemTableName table : SystemTableName.values()) {
            if (tableName.startsWith(table.name())) {
                return true;
            }
            if (table.name().equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }
}
