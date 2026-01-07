package com.dmiurl.testcases;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.dmiurl.base.BaseClass;
import com.dmiurl.pageobjects.HomePage;
import com.dmiurl.utils.ConfigReader;
import com.dmiurl.utils.ExcelLogger;
import com.dmiurl.utils.ExcelUtils;

public class DmiUrlRedirectionTestFinal extends BaseClass {

    private List<Map<String, String>> urlList;
    private ExcelLogger excelLogger;
    private ConfigReader configReader;

    // ==============================
    // HTTP STATUS CHECK
    // ==============================
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

    // ==============================
    // ACCEPTED PAGE CHECK
    // ==============================
    // ---------------- UI VALIDATION ----------------
    public boolean isAcceptedPageOpened(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(0));

            WebElement okButton = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath( "//*[@id=\"root\"]/div[2]/div/div/button")));

            return okButton.isDisplayed();

        } catch (TimeoutException e) {
            return false;
        }
    }

    // ==============================
    // LOAD EXCEL DATA
    // ==============================
    @BeforeClass
    public void loadExcelData() throws Exception {

        configReader = new ConfigReader();
        String DEVURLSHEET = configReader.getProperty("DEVURLSHEET");
        String PRODUCTIONURLSHEET = configReader.getProperty("PRODUCTIONURLSHEET");

        String path = System.getProperty("user.dir") + "\\src\\resources\\Bandhan\\DmiUrls.xlsx";
        ExcelUtils reader = new ExcelUtils(path, PRODUCTIONURLSHEET);
        urlList = reader.getTestData();

        excelLogger = new ExcelLogger();
        final String dateTime = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date());
        excelLogger.initUrlLogger(System.getProperty("user.dir") + "/Parallel_URL_Hits.xlsx");
        System.out.println("Total URLs Loaded: " + urlList.size());
    }

    // ==============================
    // MAIN TEST
    // ==============================
    @Test
    public void runParallelDmiUrls() throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (Map<String, String> data : urlList) {

            executor.submit(() -> {

                WebDriver driver = null;
                String baseUrl = data.get("DMI_URLS");
                String language = data.get("LANGUAGE");

                long start = System.currentTimeMillis();
                int status = getStatus(baseUrl);

                excelLogger.logUrlHit(baseUrl, status,
                        status == 200 ? "HTTP_PASS" : "HTTP_FAIL",
                        start);

                if (status != 200)
                    return;

                try {
                    ChromeOptions options = new ChromeOptions();
                options.addArguments("--incognito");
                options.setAcceptInsecureCerts(true);
                options.addArguments("--disable-http2");
                options.addArguments("--disable-quic");
                    options.setAcceptInsecureCerts(true);

                    driver = new RemoteWebDriver(
                            new URL("http://localhost:4444"),
                            options);

                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
                    HomePage homePage = new HomePage(driver);

                    // =====================================
                    // STEP 1: FIRST BROWSER
                    // =====================================
                    excelLogger.logUrlHit(baseUrl, 200, "FIRST_VISIT",
                            System.currentTimeMillis());

                    driver.navigate().to(baseUrl);

                    if (!isAcceptedPageOpened(driver)) {
                        excelLogger.logUrlHit(baseUrl, 200,
                                "FAIL_FIRST_VISIT",
                                System.currentTimeMillis());
                        return;
                    }

                    Thread.sleep(5000);
                   // wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(0));

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

                    // =====================================
                    // RELOAD CHECK
                    // =====================================
                    excelLogger.logUrlHit(baseUrl, 200, "SECOND_VISIT",
                            System.currentTimeMillis());

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

                    driver = new RemoteWebDriver(
                            new URL("http://localhost:4444"),
                            options);

                    wait = new WebDriverWait(driver, Duration.ofSeconds(20));
                    homePage = new HomePage(driver);

                    excelLogger.logUrlHit(baseUrl, 200, "THIRD_VISIT",
                            System.currentTimeMillis());

                    driver.navigate().to(baseUrl);

                    if (!isAcceptedPageOpened(driver)) {
                        excelLogger.logUrlHit(baseUrl, 200,
                                "FAIL_THIRD_VISIT",
                                System.currentTimeMillis());
                        return;
                    }

                    Thread.sleep(5000);
                    //wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(0));

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

                    excelLogger.logUrlHit(baseUrl, 200,
                            "TEST_COMPLETED",
                            System.currentTimeMillis());

                              System.out.println("COMPLETED → " + baseUrl);

                } catch (Exception e) {
                    excelLogger.logUrlHit(baseUrl, 200,
                            "EXCEPTION - " + e.getMessage(),
                            System.currentTimeMillis());
                } finally {
                    if (driver != null) {
                        try {
                            driver.quit();
                        } catch (Exception ignored) {
                        }
                    }
                }


            });
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.MINUTES);
        excelLogger.generateUrlReport();
    }
}
