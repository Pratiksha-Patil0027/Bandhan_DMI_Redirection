package com.dmiurl.utils;

import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class GenerateDynamicXML {

    public static void main(String[] args) throws Exception {

        // ✅ Path to your Excel
        String excelPath = System.getProperty("user.dir") +
                "src\\resources\\Bandhan\\DmiUrls.xlsx";

                

        // ✅ Read Excel rows
        List<Map<String, String>> rows = readExcel(excelPath);

        StringBuilder xml = new StringBuilder();

        xml.append("<!DOCTYPE suite SYSTEM \"https://testng.org/testng-1.0.dtd\" >\n");
        xml.append("<suite name=\"DynamicParallelSuite\" parallel=\"tests\" thread-count=\"")
           .append(rows.size())
           .append("\">\n\n");

        int count = 1;

        for (Map<String, String> row : rows) {

            String url = row.get("DMI_URLS").trim();
            String language = row.get("LANGUAGE").trim();

            xml.append("  <test name=\"Test_").append(count).append("\">\n");
            xml.append("    <parameter name=\"baseUrl\" value=\"").append(url).append("\"/>\n");
            xml.append("    <parameter name=\"language\" value=\"").append(language).append("\"/>\n");
            xml.append("    <classes>\n");
            xml.append("      <class name=\"com.mppilot.testcases.ImageUploadTest\"/>\n");
            xml.append("    </classes>\n");
            xml.append("  </test>\n\n");

            count++;
        }

        xml.append("</suite>");

        // ✅ Save XML file
        String filePath = System.getProperty("user.dir") + "/DynamicTestNG.xml";
        FileWriter writer = new FileWriter(filePath);
        writer.write(xml.toString());
        writer.close();

        System.out.println("✅ DynamicTestNG.xml generated successfully at:");
        System.out.println(filePath);
    }

    // ✅ Read Excel (DMI_URLS + LANGUAGE)
    public static List<Map<String, String>> readExcel(String filePath) throws Exception {
        ExcelUtils reader = new ExcelUtils(filePath, "Sheet1");
        return reader.getTestData();
    }
}
