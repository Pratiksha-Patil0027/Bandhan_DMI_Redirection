package com.dmiurl.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExcelLogger implements ITestListener {

    // ======================================================
    //  EXISTING API REPORT LOGIC (UNCHANGED)
    // ======================================================

    private final Map<String, List<Object[]>> influencerResults = new HashMap<>();
    private final List<Object[]> globalTests = new ArrayList<>();

    @Override
    public void onTestSuccess(ITestResult result) {
        recordResult(result, "PASS", "Assertion passed successfully");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String assertionMessage = result.getThrowable() != null ?
                result.getThrowable().getMessage() : "No error message";
        recordResult(result, "FAIL", assertionMessage);
    }

    private void recordResult(ITestResult result, String status, String assertionMessage) {

        String testID = (String) result.getAttribute("TestID");
        String imageCount = (String) result.getAttribute("ImageCount");
        String baseName = (String) result.getAttribute("baseName");
        String folderPath = (String) result.getAttribute("FolderPath");
        String expected = (String) result.getAttribute("Expected");
        String actual = (String) result.getAttribute("Actual");

        Object[] row = new Object[]{
                testID != null ? testID : "N/A",
                imageCount != null ? imageCount : "N/A",
                baseName != null ? baseName : "N/A",
                folderPath != null ? folderPath : "N/A",
                expected != null ? expected : "N/A",
                actual != null ? actual : "N/A",
                status,
                assertionMessage,
                expected != null ? expected : "null",
                actual != null ? actual : "null"
        };

        if (baseName == null || baseName.isEmpty()) {
            globalTests.add(row);
        } else {
            influencerResults.computeIfAbsent(baseName, k -> new ArrayList<>()).add(row);
        }
    }


    @Override
    public void onFinish(ITestContext context) {
        try {
            String dateFolder = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            File reportsDir = new File("reports/" + dateFolder);
            if (!reportsDir.exists()) reportsDir.mkdirs();

            for (Map.Entry<String, List<Object[]>> entry : influencerResults.entrySet()) {

                String influencerId = entry.getKey();
                List<Object[]> results = entry.getValue();
                List<Object[]> combinedResults = new ArrayList<>(globalTests);
                combinedResults.addAll(results);

                try (Workbook workbook = new XSSFWorkbook()) {

                    Sheet sheet = workbook.createSheet("Bandhan API Automation Test Report");

                    CellStyle headerStyle = workbook.createCellStyle();
                    headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    headerStyle.setBorderTop(BorderStyle.THIN);
                    headerStyle.setBorderBottom(BorderStyle.THIN);
                    headerStyle.setBorderLeft(BorderStyle.THIN);
                    headerStyle.setBorderRight(BorderStyle.THIN);

                    Font headerFont = workbook.createFont();
                    headerFont.setBold(true);
                    headerFont.setColor(IndexedColors.WHITE.getIndex());
                    headerStyle.setFont(headerFont);

                    Row header = sheet.createRow(0);
                    String[] columns = {
                            "MODULE_NAME", "TESTCASE_ID", "TEST_DESC",
                            "INFLUENCER_MOBILE", "INFLUENCER_ID", "STATUS",
                            "ASSERTION_MESSAGE", "EXPECTED_RESULT", "ACTUAL_RESULT"
                    };

                    for (int i = 0; i < columns.length; i++) {
                        Cell cell = header.createCell(i);
                        cell.setCellValue(columns[i]);
                        cell.setCellStyle(headerStyle);
                    }

                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    cellStyle.setWrapText(true);

                    int rowNum = 1;
                    for (Object[] data : combinedResults) {
                        Row row = sheet.createRow(rowNum++);
                        for (int i = 0; i < data.length; i++) {
                            Cell cell = row.createCell(i);
                            String cellValue = data[i] != null ? data[i].toString() : "";
                            cell.setCellValue(Utils.truncateText(cellValue));
                            cell.setCellStyle(cellStyle);
                        }
                    }

                    for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);

                    String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                    String fileName = influencerId + "_API_Automation_TestResults_" + timestamp + ".xlsx";
                    String filePath = reportsDir.getPath() + "/" + fileName;

                    try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                        workbook.write(fileOut);
                    }

                    System.out.println("Excel report generated for " + influencerId + ": " + filePath);

                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(new File(filePath));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // ======================================================
    //  NEW — URL LOGGER MODULE
    // ======================================================

    private final Object urlLogLock = new Object();
    private Workbook urlWorkbook;
    private Sheet urlSheet;
    private String urlLogFile;

    /**
     * Initialize URL Logger Workbook + Sheet (Thread-safe)
     */
    public void initUrlLogger(String filePath) {
        synchronized (urlLogLock) {
            try {
                this.urlLogFile = filePath;
                File file = new File(filePath);

                if (file.exists()) {
                    urlWorkbook = new XSSFWorkbook(new FileInputStream(file));
                } else {
                    urlWorkbook = new XSSFWorkbook();
                }

                urlSheet = urlWorkbook.getSheet("URL_LOGS");

              if (urlSheet == null) {
    urlSheet = urlWorkbook.createSheet("URL_LOGS");

    Row header = urlSheet.createRow(0);
    header.createCell(0).setCellValue("URL");
    header.createCell(1).setCellValue("STATUS_CODE");
    header.createCell(2).setCellValue("RESULT");
    header.createCell(3).setCellValue("START_TIME");
    header.createCell(4).setCellValue("END_TIME");
    header.createCell(5).setCellValue("TIME_SPENT(ms)");
    header.createCell(6).setCellValue("THREAD");
}


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write a URL hit entry into the Excel file
     */
/**
 * Log URL hit with full timing information
 */
public void logUrlHit(String url, int statusCode, String result, long startTimeMillis) {
    synchronized (urlLogLock) {
        try {

            long endTimeMillis = System.currentTimeMillis();
            long timeSpent = endTimeMillis - startTimeMillis;

            int rowCount = urlSheet.getLastRowNum() + 1;

            Row row = urlSheet.createRow(rowCount);
            row.createCell(0).setCellValue(url);
            row.createCell(1).setCellValue(statusCode);
            row.createCell(2).setCellValue(result);
            row.createCell(3).setCellValue(formatTime(startTimeMillis));
            row.createCell(4).setCellValue(formatTime(endTimeMillis));
            row.createCell(5).setCellValue(timeSpent); // ms
            row.createCell(6).setCellValue(Thread.currentThread().getName());

            try (FileOutputStream out = new FileOutputStream(urlLogFile)) {
                urlWorkbook.write(out);
            }

            System.out.println(
                "URL LOGGED → " + url + 
                " | " + statusCode +
                " | " + timeSpent + " ms"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/** Helper for formatting timestamps */
private String formatTime(long millis) {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(millis));
}

    /**
 * Final Excel generation for URL Logs (Auto-size columns only)
 */
public void generateUrlReport() {
    synchronized (urlLogLock) {
        try {
            if (urlWorkbook == null || urlSheet == null) {
                System.out.println(" URL Logger not initialized — No report generated.");
                return;
            }

            // Auto-size all columns
            for (int i = 0; i < 5; i++) {
                urlSheet.autoSizeColumn(i);
            }

            try (FileOutputStream out = new FileOutputStream(urlLogFile)) {
                urlWorkbook.write(out);
            }

            System.out.println("⭐⭐ URL Log Report Generated → " + urlLogFile);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(urlLogFile));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

}
