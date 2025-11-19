package  com.dmiurl.utils;

import java.util.*;

public class GlobalStore {
    private static final Map<String, String> store = new HashMap<>();

    // ✅ store multiple report paths separately
    private static final List<String> reportPaths = new ArrayList<>();

    // ----------------------
    // Existing key-value usage
    // ----------------------
    public static void put(String key, String value) {
        store.put(key, value);
    }

    public static String get(String key) {
        return store.get(key);
    }

    public static String getOrDefault(String key, String defaultValue) {
        return store.getOrDefault(key, defaultValue);
    }

    public static void remove(String key) {
        store.remove(key);
    }

    public static void clear() {
        store.clear();
        reportPaths.clear(); // ✅ also clear reports
    }

    public static boolean containsKey(String key) {
        return store.containsKey(key);
    }

    // ----------------------
    // New: multiple report support
    // ----------------------
    public static void addReportPath(String path) {
        if (path != null && !path.trim().isEmpty()) {
            reportPaths.add(path);
        }
    }

    public static List<String> getAllReportPaths() {
        return new ArrayList<>(reportPaths); // return copy to avoid external modification
    }

    public static void clearReportPaths() {
        reportPaths.clear();
    }
}
