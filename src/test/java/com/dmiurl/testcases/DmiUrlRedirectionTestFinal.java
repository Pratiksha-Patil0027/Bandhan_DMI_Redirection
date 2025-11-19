package com.dmiurl.testcases;

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
import com.dmiurl.utils.ExcelUtils;
import com.dmiurl.utils.Utils;

public class DmiUrlRedirectionTestFinal extends BaseClass {

    private HomePage homePage;
    private WebDriverWait wait;
    private String baseUrl;
    private String language;


    private List<Map<String, String>> urlList = new ArrayList<>();

    @BeforeClass
    public void loadExcelData() throws Exception {

        String path = System.getProperty("user.dir") + "\\src\\resources\\Bandhan\\DmiUrls.xlsx";
        ExcelUtils reader = new ExcelUtils(path, "Sheet1");
        urlList = reader.getTestData();

        System.out.println(" Loaded " + urlList.size() + " URL entries");
    }

   @Test
public void runParallelDmiUrls() throws Exception {

    // Read Excel
    String path = System.getProperty("user.dir") + "\\src\\resources\\Bandhan\\DmiUrls.xlsx";
    ExcelUtils reader = new ExcelUtils(path, "Sheet1");
    List<Map<String, String>> urlList = reader.getTestData();

    // Parallel thread pool = number of websites
    ExecutorService executor = Executors.newFixedThreadPool(urlList.size());

    for (Map<String, String> data : urlList) {

        executor.submit(() -> {

            String baseUrl = data.get("DMI_URLS");
            String language = data.get("LANGUAGE");

            System.out.println("THREAD START → " + baseUrl + " | " + language);

            // ---------------------------
            //  1) Open Chrome in Incognito
            // ---------------------------
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

                // ---------------------------
                //  2) Your EXACT code – unchanged
                // ---------------------------

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
                Thread.sleep(5000);

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
                Thread.sleep(5000);

                driver.quit();

                // ---------------------------
                //  3) SECOND Launch – Incognito again
                // ---------------------------

                driver = new ChromeDriver(options);
                homePage = new HomePage(driver);
                wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                driver.navigate().to(baseUrl);

                Thread.sleep(5000);
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(0));

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

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try { driver.quit(); } catch (Exception ignored) {}
            }
        });
    }

    executor.shutdown();
    executor.awaitTermination(60, TimeUnit.MINUTES);
}



}