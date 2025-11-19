package  com.dmiurl.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExcelLogger implements ITestListener {

    // Map to store test results per influencer
    private final Map<String, List<Object[]>> influencerResults = new HashMap<>();

    // Store global tests like login
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
            // Add global tests separately
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

                // Include all global tests (like Company Login) for each influencer
                List<Object[]> combinedResults = new ArrayList<>(globalTests);
                combinedResults.addAll(results);

                try (Workbook workbook = new XSSFWorkbook()) {
                    Sheet sheet = workbook.createSheet("Bandhan API Automation Test Report");

                    // Header Style
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

                    // Header Row
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

                    // Cell style
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    cellStyle.setWrapText(true);

                    // Fill Data Rows
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

                    // Autosize columns
                    for (int i = 0; i < columns.length; i++) {
                        sheet.autoSizeColumn(i);
                    }

                    // File name with influencer
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                    String fileName = influencerId + "_API_Automation_TestResults_" + timestamp + ".xlsx";
                    String filePath = reportsDir.getPath() + "/" + fileName;

                    try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                        workbook.write(fileOut);
                    }

                    System.out.println("Excel report generated for " + influencerId + ": " + filePath);
                    GlobalStore.addReportPath(filePath);

                    // Auto-open Excel
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(new File(filePath));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
