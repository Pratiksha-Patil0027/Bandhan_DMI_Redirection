package  com.dmiurl.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigReader {

	Properties properties;
	private static final String CONFIG_FILE_PATH=".\\src\\resources\\data.properties";
	
	
	  public ConfigReader() {
	        properties = new Properties();
	        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_FILE_PATH)) {
	            properties.load(fileInputStream);
	        } catch (IOException e) {
	            e.printStackTrace();
	            throw new RuntimeException("Failed to load config.properties file");
	        }
	    }
	
	  
	  public String getProperty(String key)
	  {
		  return properties.getProperty(key);
	  }
	
	  
	  public int getIntProperty(String key)
	  {
		  return Integer.parseInt(properties.getProperty(key));
	  }
	  
	  public List<String> getProprtyStrList(String key) {
    String value = properties.getProperty(key);
    if (value != null && !value.isEmpty()) {
        // Split by comma, remove extra spaces
        return Arrays.stream(value.split(","))
                     .map(String::trim)
                     .toList();
    }
    return new ArrayList<>();
}


}