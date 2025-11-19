package  com.dmiurl.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

    private Sheet sheet;

    public ExcelUtils(String filePath, String sheetName) throws Exception {
        FileInputStream fis = new FileInputStream(new File(filePath));
        try (Workbook workbook = new XSSFWorkbook(fis)) {
            this.sheet = workbook.getSheet(sheetName);
        }
        if (this.sheet == null) {
            throw new RuntimeException("Sheet not found: " + sheetName);
        }
    }

    // public Map<String, String> getAllMetadata() {
    //     Map<String, String> metadata = new HashMap<>();
    //     for (int row = 0; row < 8; row++) {
    //     	/*
    //     	Row currentRow = sheet.getRow(row);
    //         Cell keyCell = currentRow.getCell(0);
    //         Cell valueCell = currentRow.getCell(1);

    //         System.out.println("Row: " + row +
    //             ", KeyCellType: " + keyCell.getCellType() +
    //             ", ValueCellType: " + valueCell.getCellType());
    //         */
    //         String key = sheet.getRow(row).getCell(0).getStringCellValue().trim();
    //         String value = sheet.getRow(row).getCell(1).getStringCellValue().trim();
    //         metadata.put(key.toUpperCase(), value);
            
    //     }
    //     return metadata;
    // }

    public List<Map<String, String>> getTestData() {
        List<Map<String, String>> dataList = new ArrayList<>();

        // Row 0 is the header (index = 0)
        Row headerRow = sheet.getRow(0);
        int numCols = headerRow.getLastCellNum();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // from row 1 onward
            Row row = sheet.getRow(i);
            Map<String, String> data = new HashMap<>();

            for (int j = 0; j < numCols; j++) {
                String key = headerRow.getCell(j).getStringCellValue().trim();
                Cell cell = row.getCell(j);
                String value = (cell == null) ? "" : cell.toString().trim();
                data.put(key.toUpperCase(), value);
            }

            dataList.add(data);
        }

        return dataList;
    }

}
