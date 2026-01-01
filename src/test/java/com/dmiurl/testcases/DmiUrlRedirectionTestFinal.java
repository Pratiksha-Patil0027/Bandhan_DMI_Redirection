package com.dmiurl.testcases;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.dmiurl.base.BaseClass;
import com.dmiurl.pageobjects.HomePage;
import com.dmiurl.utils.ExcelLogger;
import com.dmiurl.utils.ExcelUtils;
import com.dmiurl.utils.Utils;

public class DmiUrlRedirectionTestFinal extends BaseClass {

    private List<Map<String, String>> urlList = new ArrayList<>();
    private ExcelLogger excelLogger;

    // ----------------------------------------
    // Get HTTP Response Code for URL
    // ----------------------------------------
    public int getStatus(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(7000);
            conn.setReadTimeout(7000);
            conn.setRequestMethod("GET");
            return conn.getResponseCode();
        } catch (Exception e) {
            return -1;
        }
    }

    // ----------------------------------------
    // LOAD URL LIST + INIT LOGGER
    // ----------------------------------------
    @BeforeClass
    public void loadExcelData() throws Exception {

        String path = System.getProperty("user.dir") + "\\src\\resources\\Bandhan\\DmiUrls.xlsx";
        ExcelUtils reader = new ExcelUtils(path, "Sheet1");
        urlList = reader.getTestData();

        excelLogger = new ExcelLogger();
        excelLogger.initUrlLogger(System.getProperty("user.dir") + "/Parallel_URL_Hits.xlsx");

        System.out.println("Loaded " + urlList.size() + " URL entries");
    }

    // ----------------------------------------
    // MAIN PARALLEL EXECUTION
    // ----------------------------------------
    @Test
    public void runParallelDmiUrls() throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(urlList.size());

        for (Map<String, String> data : urlList) {

            executor.submit(() -> {

                String baseUrl = data.get("DMI_URLS");
                String language = data.get("LANGUAGE");

                System.out.println("THREAD START → " + baseUrl);

                long startPreCheck = System.currentTimeMillis();
                int status = getStatus(baseUrl);

                excelLogger.logUrlHit(baseUrl, status, (status == 200 ? "PASS" : "FAIL"), startPreCheck);

                if (status != 200) {
                    System.out.println("❌ URL Broken → Skipping browser: " + baseUrl);
                    return;
                }

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--incognito");
                options.setAcceptInsecureCerts(true);
                options.addArguments("--disable-http2");
                options.addArguments("--disable-quic");

                WebDriver driver = new ChromeDriver(options);

                try {

                    HomePage homePage = new HomePage(driver);
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                    Utils util = new Utils();

                    // =====================================
                    // STEP 1: FIRST BROWSER
                    // =====================================

                    long startBrowser1 = System.currentTimeMillis();
                    excelLogger.logUrlHit(baseUrl, getStatus(baseUrl), "FIRST_VISIT", startBrowser1);

                    driver.navigate().to(baseUrl);

                    Thread.sleep(5000);
                    wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(0));

                    homePage.clickOn_Disclaimer_OkButton();
                    Thread.sleep(5000);

                    homePage.clickOn_LanguageDropdown();
                    Thread.sleep(3000);
                    homePage.clickOn_LanguageDropdown(language);

                    Thread.sleep(7000);
                    homePage.clickOn_AboutUs();
                    Thread.sleep(8000);
                    homePage.clickOn_OurServices();
                    Thread.sleep(15000);
                    homePage.clickOn_OurExperience();

                    // LOG reload
                    long startReload = System.currentTimeMillis();
                    excelLogger.logUrlHit(baseUrl, getStatus(baseUrl), "RELOAD", startReload);

                    driver.navigate().to("about:blank");
                    driver.navigate().to(baseUrl);

                    Thread.sleep(3000);
                    driver.navigate().refresh();

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

                    driver.quit();

                    // =====================================
                    // STEP 2: SECOND BROWSER
                    // =====================================

                    driver = new ChromeDriver(options);
                    homePage = new HomePage(driver);
                    wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                    long startBrowser2 = System.currentTimeMillis();
                    excelLogger.logUrlHit(baseUrl, getStatus(baseUrl), "SECOND_LAUNCH", startBrowser2);

                    driver.navigate().to(baseUrl);

                    Thread.sleep(5000);
                    wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(0));

                    homePage.clickOn_Disclaimer_OkButton();
                    Thread.sleep(5000);

                    homePage.clickOn_LanguageDropdown();
                    Thread.sleep(5000);
                    homePage.clickOn_LanguageDropdown(language);

                    homePage.clickOn_AboutUs();
                    Thread.sleep(3000);
                    homePage.clickOn_OurServices();
                    Thread.sleep(5000);
                    homePage.clickOn_OurExperience();

                    driver.quit();

                    System.out.println("COMPLETED → " + baseUrl);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try { driver.quit(); } catch (Exception ignored) {}
            }
        });
    }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.MINUTES);

        // FINAL EXCEL REPORT
        excelLogger.generateUrlReport();
    }
}
