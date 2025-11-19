package com.dmiurl.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.dmiurl.utils.ConfigReader;


public class BaseClass {
	protected ExtentReports extent;
	protected ExtentTest test;
	public WebDriver driver;
	protected ConfigReader configReader;
	private static final Set<String> countedTests = Collections.synchronizedSet(new HashSet<>()); // Shared across all
																									// tests
	private static Properties properties = new Properties();

	static {
		try (FileInputStream fis = new FileInputStream(
				System.getProperty("user.dir") + "\\src\\resources\\data.properties")) {
			properties.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public void launchBrowser(String browser) {
		if (browser.equalsIgnoreCase("chrome")) {
			// Create ChromeOptions object
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addExtensions(new File("D:\\Pratiksha\\Downloads\\Cross-Domain-CORS-Chrome-Web-Store.crx"));
			chromeOptions.setAcceptInsecureCerts(true);
			chromeOptions.addArguments("--incognito");
			driver = new ChromeDriver(chromeOptions);

		} else if (browser.equalsIgnoreCase("edge")) {
			EdgeOptions edgeOptions = new EdgeOptions();
			edgeOptions.addExtensions(new File("D:\\Pratiksha\\Downloads\\Cross-Domain-CORS-Chrome-Web-Store.crx"));
			edgeOptions.setAcceptInsecureCerts(true);
			driver = new EdgeDriver(edgeOptions);

		} else if (browser.equalsIgnoreCase("firefox")) {
			driver = new FirefoxDriver();

		} else {
			driver = new ChromeDriver();

		}
		driver.manage().window().maximize();
	}

	public ChromeOptions setChromeOption() {
		ChromeOptions option = new ChromeOptions();
		option.addArguments("start-maximized");
		option.addArguments("--remote-allow-origins=*");
		option.addArguments("incognito");
		// option.setHeadless(true);
		option.setExperimentalOption("excludeSwitches", Arrays.asList("disable-popup-clocking"));
		Map<String, String> pref = new HashMap<>();
		pref.put("download.default.directory", "D:\\");
		option.setExperimentalOption("prefs", pref);
		option.setAcceptInsecureCerts(true);
		return option;
	}

	public static String takeScreenshot(String testName, WebDriver driver) {
		File sourceScreenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File destinationScreenshotFile = new File(System.getProperty("user.dir") + "\\Screenshots\\" + testName + "_"
				+ System.currentTimeMillis() + ".png");
		try {
			FileUtils.copyFile(sourceScreenshotFile, destinationScreenshotFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destinationScreenshotFile.getAbsolutePath();
	}

}
