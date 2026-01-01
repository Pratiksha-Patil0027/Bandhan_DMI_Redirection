package  com.dmiurl.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class AutomationReportListener implements ISuiteListener {

    @Override
    public void onFinish(final ISuite suite) {
        // Track influencer-wise counts
        Map<String, int[]> influencerSummary = new HashMap<>();

        for (final ISuiteResult result : suite.getResults().values()) {
            final ITestContext context = result.getTestContext();

            for (ITestResult passed : context.getPassedTests().getAllResults()) {
                updateSummary(influencerSummary, passed, true, false, false);
            }
            for (ITestResult failed : context.getFailedTests().getAllResults()) {
                updateSummary(influencerSummary, failed, false, true, false);
            }
            for (ITestResult skipped : context.getSkippedTests().getAllResults()) {
                updateSummary(influencerSummary, skipped, false, false, true);
            }
        }

        // Build email subject and body
        final String dateTime = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date());
        final String subject = "Bandhan World API Automation Report - " + dateTime;

        StringBuilder body = new StringBuilder();
        body.append("<html><body>")
            .append("Hello Team,<br/><br/>")
            .append("Please find the attached automation reports for all influencers.<br/><br/>");

        // Per-influencer summary table
        for (Map.Entry<String, int[]> entry : influencerSummary.entrySet()) {
            String infl = entry.getKey();
            int[] counts = entry.getValue();
            int total = counts[0] + counts[1] + counts[2];

            body.append("<b>Execution Summary - ").append(infl).append("</b><br/>")
                .append("<table border='1' cellspacing='0' cellpadding='6' ")
                .append("style='border-collapse:collapse; font-family:Arial; font-size:14px;'>")
                .append("<tr style='background-color:#f2f2f2;'>")
                .append("<th>Total</th><th>Passed</th><th>Failed</th><th>Skipped</th></tr>")
                .append("<tr>")
                .append("<td align='center'>").append(total).append("</td>")
                .append("<td align='center'>").append(counts[0]).append("</td>")
                .append("<td align='center'>").append(counts[1]).append("</td>")
                .append("<td align='center'>").append(counts[2]).append("</td>")
                .append("</tr></table><br/><br/>");
        }

        body.append("Thanks & Regards,<br/><b>Bandhan World Automation</b>")
            .append("</body></html>");

        // Collect all influencer report paths
        List<String> reportPaths = GlobalStore.getAllReportPaths(); 
        // ["infl1_report.xlsx", "infl2_report.xlsx", "infl2_report.xlsx"]

         String outlookToRecipients = Credentials.getOutlookToRecipients();

        if (!reportPaths.isEmpty()) {
            OutlookMailSender.sendOutlookMailWithAttachments(
                outlookToRecipients,
                subject,
                body.toString(),
                reportPaths
            );
            System.out.println("Automation email sent with " + reportPaths.size() + " reports.");
        } else {
            System.out.println("No reports found — email not sent.");
        }
    }
    

    // Helper: update influencer summary
    private void updateSummary(Map<String, int[]> map, ITestResult result, boolean pass, boolean fail, boolean skip) {
        try {
            Map<String, String> metadata = (Map<String, String>) result.getParameters()[0];
            String folderName = metadata.get("FOLDER_NAME");

            if (folderName == null) folderName = "Unknown";

            int[] counts = map.getOrDefault(folderName, new int[3]);
            if (pass) counts[0]++;
            if (fail) counts[1]++;
            if (skip) counts[2]++;
            map.put(folderName, counts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
