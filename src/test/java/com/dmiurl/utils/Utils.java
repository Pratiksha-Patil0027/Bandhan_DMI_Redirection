package  com.dmiurl.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class Utils {
	
	public static String resolvePlaceholders(String input) {
	    if (input == null) return null;

	    String resolved = input;
	    // Replace all other placeholders using GlobalStore
	    Pattern pattern = Pattern.compile("\\{(\\w+)}"); // matches {PLACEHOLDER}
	    Matcher matcher = pattern.matcher(resolved);
	    while (matcher.find()) {
	        String key = matcher.group(1);
	        String value = GlobalStore.getOrDefault(key, ""); // default to empty string if not found
	        resolved = resolved.replace("{" + key + "}", value);
	    }

	    return resolved;
	}

	
	public static void removeFieldsRecursive(JsonNode node, List<String> fieldsToRemove) {
	    if (node.isObject()) {
	        ObjectNode objNode = (ObjectNode) node;
	        for (String field : fieldsToRemove) {
	            objNode.remove(field);
	        }
	        Iterator<String> fieldNames = objNode.fieldNames();
	        while (fieldNames.hasNext()) {
	            String fieldName = fieldNames.next();
	            removeFieldsRecursive(objNode.get(fieldName), fieldsToRemove);
	        }
	    } else if (node.isArray()) {
	        for (JsonNode arrayItem : node) {
	            removeFieldsRecursive(arrayItem, fieldsToRemove);
	        }
	    }
	}
	
	public static String truncateText(String text) {
	    final int MAX_LENGTH = 32767;
	    if (text != null && text.length() > MAX_LENGTH) {
	        return text.substring(0, MAX_LENGTH - 3) + "...";
	    }
	    return text;
	}


	public String getActualFilePath(String folderPath, String baseName) {

    File folder = new File(folderPath);

    if (!folder.exists() || !folder.isDirectory()) {
        throw new RuntimeException("Folder not found: " + folderPath);
    }

    //  Allowed image extensions
    String[] extensions = {"jpg", "jpeg", "png", "webp", "gif"};

    //  Try matching by constructing file name
    for (String ext : extensions) {
        File file = new File(folderPath + baseName + "." + ext);
        if (file.exists()) {
            return file.getAbsolutePath();   //  Correct image found
        }
    }

    //  If not matched, search using startsWith() (backup)
    for (File file : folder.listFiles()) {
        if (file.getName().startsWith(baseName)) {
            return file.getAbsolutePath();
        }
    }

    throw new RuntimeException("No matching file found for base name: " + baseName);
}


public List<String> getImageNames(String folderPath) {

    File folder = new File(folderPath);
    List<String> imageNames = new ArrayList<>();

    // Supported image file extensions
    String[] extensions = {"jpg", "jpeg", "png", "gif", "webp"};

    if (folder.exists() && folder.isDirectory()) {

        File[] files = folder.listFiles((dir, name) -> {
            for (String ext : extensions) {
                if (name.toLowerCase().endsWith("." + ext)) {
                    return true;
                }
            }
            return false;
        });

        if (files != null) {
            for (File file : files) {
                imageNames.add(file.getName());  // Only file name
            }
        }
    }

    return imageNames;
}


}
