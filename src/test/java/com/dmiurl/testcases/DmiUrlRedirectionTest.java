package com.dmiurl.testcases;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.dmiurl.base.BaseClass;
import com.dmiurl.pageobjects.HomePage;
import com.dmiurl.utils.ConfigReader;
import com.dmiurl.utils.ExcelUtils;
import com.dmiurl.utils.Utils;

public class DmiUrlRedirectionTest extends BaseClass {

    private HomePage homePage;
    private WebDriverWait wait;
    private String baseUrl;
    private String language;

    @Parameters({ "baseUrl", "language" })
    @BeforeClass
    public void setUp(String baseUrl, String language) {
        this.baseUrl = baseUrl;
        this.language = language;

        System.out.println("BeforeClass - baseUrl: " + this.baseUrl);
        System.out.println("BeforeClass - language: " + this.language);
    }

    @Test(dataProvider = "excelData")
    public void dmiRedirectionTestCases(Map<String, String> testData) throws InterruptedException {

        launchBrowser("chrome"); // FIRST launch browseOr
        homePage = new HomePage(driver); // THEN create HomePage (driver is NOT null now)
        new Utils();

        // String dmiUrl = testData.get("DMI_URLS");
        // System.out.println("DMI URL: " + dmiUrl);

        System.out.println("baseUrl: " + baseUrl);

        driver.navigate().to(baseUrl);

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Thread.sleep(5000);

        // Wait for iframe and switch
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(0));
        System.out.println("Switched to iframe(0)");

        Thread.sleep(5000);
        homePage.clickOn_Disclaimer_OkButton();

        // Switch back to main content
        // driver.switchTo().defaultContent();
        Thread.sleep(5000);
        homePage.clickOn_LanguageDropdown();

        // String language = testData.get("LANGUAGE");
        Thread.sleep(3000);

        homePage.clickOn_LanguageDropdown(language);
        Thread.sleep(7000);
        homePage.clickOn_AboutUs();
        Thread.sleep(8000);
        homePage.clickOn_OurServices();
        Thread.sleep(15000);
        homePage.clickOn_OurExperience();
        Thread.sleep(5000);

        driver.navigate().to("about:blank");
        driver.navigate().to(baseUrl);
        Thread.sleep(3000);
        driver.navigate().refresh();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Thread.sleep(20000);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(0));
        homePage.clickOn_LanguageDropdown();

        Thread.sleep(5000);
        homePage.clickOn_LanguageDropdown(language);
        Thread.sleep(7000);
        homePage.clickOn_AboutUs();
        Thread.sleep(8000);
        homePage.clickOn_OurServices();
        Thread.sleep(10000);
        homePage.clickOn_OurExperience();
        Thread.sleep(5000);

        driver.quit();

        launchBrowser("chrome"); // FIRST launch browseOr
        homePage = new HomePage(driver); // THEN create HomePage (driver is NOT null now)
        new Utils();

        driver.navigate().to(baseUrl);

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Thread.sleep(5000);
        // Wait for iframe and switch
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(0));
        System.out.println("Switched to iframe(0)");

        Thread.sleep(5000);
        homePage.clickOn_Disclaimer_OkButton();
        Thread.sleep(5000);
        homePage.clickOn_LanguageDropdown();

        Thread.sleep(5000);
        homePage.clickOn_LanguageDropdown(language);
        Thread.sleep(3000);
        homePage.clickOn_AboutUs();
        Thread.sleep(3000);
        homePage.clickOn_OurServices();
        Thread.sleep(5000);
        homePage.clickOn_OurExperience();
        Thread.sleep(5000);

        driver.quit();

        System.out.println("COMPLETED → " + baseUrl);

    }

    @DataProvider(name = "excelData")
    public Object[][] dataProviderAllFiles() throws Exception {
        configReader = new ConfigReader();
        String folderNamesStr = configReader.getProperty("FOLDERNAME");
        String[] folderNames = folderNamesStr.split(",");

        List<Object[]> combinedData = new ArrayList<>();

        for (String folderName : folderNames) {
            folderName = folderName.trim();
            String folderPath = System.getProperty("user.dir") + "\\src\\resources\\" + folderName + "\\";
            File folder = new File(folderPath);

            if (!folder.exists())
                continue;

            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".xlsx")) {

                    ExcelUtils reader = new ExcelUtils(file.getAbsolutePath(), "Sheet1");
                    List<Map<String, String>> testDataList = reader.getTestData();

                    for (Map<String, String> row : testDataList) {
                        combinedData.add(new Object[] { row });
                    }
                }
            }
        }

        return combinedData.toArray(new Object[0][0]);
    }

}