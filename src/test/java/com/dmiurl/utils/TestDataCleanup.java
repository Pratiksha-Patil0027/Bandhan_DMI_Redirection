package  com.dmiurl.utils;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
 
public class TestDataCleanup {
 
    String outlookPassword = Credentials.get_MAIN_DB_URL();
    private static final String MAIN_DB_URL = Credentials.get_MAIN_DB_URL();
 
    private static final String COPY_DB_URL = Credentials.get_COPY_DB_URL();
 
    private static final String DB_USER = Credentials.get_DB_USER();
    private static final String DB_PASSWORD = Credentials.get_DB_PASSWORD();
 
    public static Connection getConnection(boolean isMainDb) throws Exception {
        return DriverManager.getConnection(isMainDb ? MAIN_DB_URL : COPY_DB_URL, DB_USER, DB_PASSWORD);
    }
 
    // 1. Cleanup Redemption
    public static void cleanupRedemption(int achieveId) {
        System.out.println("achieveId_DB_: " + achieveId);
        /*
         * String rejectRedemption = "DECLARE @ACHIEVEID INT = " + achieveId + ";\n" +
         * "UPDATE LOY_ACHIEVEMENTS SET ISAPPROVED = 2, APPROVEDATE = GETDATE(), APPROVEDBY = 7, APPROVEDREMARK = 'Test Remark' WHERE ACHIEVEID = @ACHIEVEID;\n"
         * +
         * "UPDATE MP SET MP.LOCKPOINTS = (SELECT ISNULL(SUM(REDEMPPOINTS), 0) FROM LOY_ACHIEVEMENTS LA1 WHERE ISNULL(LA1.ISAPPROVED, 0) = 0 AND LA1.INFLUENCERID = MP.INFLUENCERID), "
         * +
         * "MP.LASTUPDATEDATE = GETDATE() FROM MLP_MasterPassbook MP JOIN LOY_ACHIEVEMENTS LA ON LA.INFLUENCERID = MP.INFLUENCERID WHERE LA.ACHIEVEID = @ACHIEVEID;"
         * ;
         */
 
        String rejectRedemption = "DECLARE @ACHIEVEID INT = " + achieveId + ";\n" +
 
                "-- Step 1: Reject the redemption\n" +
                "UPDATE LOY_ACHIEVEMENTS \n" +
                "SET \n" +
                "    ISAPPROVED = 2,\n" +
                "    APPROVEDATE = GETDATE(),\n" +
                "    APPROVEDBY = 7,\n" +
                "    APPROVEDREMARK = 'Test Remark'\n" +
                "WHERE ACHIEVEID = @ACHIEVEID;\n\n" +
 
                "-- Step 2: Update LOCKPOINTS\n" +
                "UPDATE MP\n" +
                "SET MP.LOCKPOINTS = (\n" +
                "    SELECT ISNULL(SUM(REDEMPPOINTS), 0)\n" +
                "    FROM dbo.LOY_ACHIEVEMENTS (NOLOCK) la1\n" +
                "    WHERE ISNULL(la1.ISAPPROVED, 0) = 0\n" +
                "    AND la1.INFLUENCERID = LA.INFLUENCERID\n" +
                "),\n" +
                "MP.LASTUPDATEDATE = GETDATE()\n" +
                "FROM MLP_MasterPassbook MP WITH(NOLOCK)\n" +
                "INNER JOIN LOY_ACHIEVEMENTS LA WITH(NOLOCK)\n" +
                "    ON LA.INFLUENCERID = MP.INFLUENCERID\n" +
                "WHERE LA.ACHIEVEID = @ACHIEVEID;";
 
        executeSingleBatchScript(rejectRedemption, true); // Run on main DB
        executeSingleBatchScript(rejectRedemption, false); // Run on copy DB
 
        String deleteRedemption = "DELETE FROM LOY_ACHIEVEMENTS WHERE ACHIEVEID = " + achieveId + ";";
 
        executeSingleBatchScript(deleteRedemption, true); // Run on main DB
        executeSingleBatchScript(deleteRedemption, false); // Run on copy DB
 
    }
 
    // 2. Cleanup Claim
    public static void cleanupClaim(String claimId) {
        System.out.println("claimId_DB_ " + claimId);
 
       String script = 
    "DECLARE @vCLAIMID VARCHAR(100) = '" + claimId + "';\n" +
    "DELETE FROM LOY_DISPUTE WHERE CLAIMID = @vCLAIMID;\n" +
    "DELETE FROM LOY_DISPUTE_APPROVAL WHERE DISPUTEID = (SELECT DISPUTEID FROM LOY_DISPUTE WHERE CLAIMID = @vCLAIMID);\n" +
    "DELETE FROM LOY_DISPUTETRANS WHERE DISPUTEID = (SELECT DISPUTEID FROM LOY_DISPUTE WHERE CLAIMID = @vCLAIMID);\n" +
    "DELETE FROM CEP_TASK WHERE CLAIMID = @vCLAIMID;";

 
        executeSingleBatchScript(script, true); // Main DB
        executeSingleBatchScript(script, false); // Copy DB
 
    }
 
    // 3. Delete Influencer Query
    public static void deleteInfluencerQueryById(int id) {
 
        System.out.println("QUERYID_DB_ " + id);
        String sql = "DELETE FROM LOY_INFLUENCER_QUERY WHERE ID = " + id + ";";
        executeSingleBatchScript(sql, true);
        executeSingleBatchScript(sql, false);
    }
 
    // 4. Close Task
    public static void closeTask(int tid) {
        System.out.println("closeTask_DB_ " + tid);
        String sql = "UPDATE cep_task SET ISCLOSE = 1 WHERE tid = " + tid + ";";
        executeSingleBatchScript(sql, true);
        executeSingleBatchScript(sql, false);
    }
 
    // 5. Delete DMI Data from Main DB
public static void deleteDMIFormMainDb(int wid) {
    System.out.println("MainWid_DB_ " + wid);
    String script = "DECLARE @vWID INT = " + wid + ";\n" +
            "DELETE FROM DRF_BND_INF_PROFILE_TEAMS WHERE WID = @vWID;\n" +
            "DELETE FROM DRF_BND_INF_PROFILE_VALUES WHERE WID = @vWID;\n" +
            "DELETE FROM DRF_BND_INF_WHYCHOOSEUS WHERE WID = @vWID;\n" +
            "DELETE FROM DRF_BND_INF_SERVICES WHERE WID = @vWID;\n" +
            "DELETE FROM DRF_BND_INF_PROFILE_CAPABILITIES WHERE WID = @vWID;\n" +
            "DELETE FROM DRF_BND_INF_BRANDS WHERE WID = @vWID;\n" +
            "DELETE FROM DRF_BND_INF_PROJECTS_IMAGES WHERE PRO_ID IN (SELECT PRO_ID FROM DRF_BND_INF_PROJECTS WHERE WID = @vWID);\n" +
            "DELETE FROM DRF_BND_INF_PROJECTS WHERE WID = @vWID;\n" +
            "DELETE FROM DRF_BND_INF_PROFILE_TRANS WHERE WID IN (SELECT WID FROM DRF_BND_INF_PROFILE_MAST WHERE WID = @vWID);\n" +
            "DELETE FROM DRF_BND_INF_PROFILE_MAST WHERE WID = @vWID;";
    executeSingleBatchScript(script, true);
}

 
    // 6. Delete DMI Data from Copy DB
public static void deleteDMIFormCopyDb(int wid) {
    System.out.println("CopyWid_DB_ " + wid);
    String script = "DECLARE @vWID INT = " + wid + ";\n" +
            "DELETE FROM APVL_BND_INF_PROFILE_TEAMS WHERE WID = @vWID;\n" +
            "DELETE FROM APVL_BND_INF_PROFILE_VALUES WHERE WID = @vWID;\n" +
            "DELETE FROM APVL_BND_INF_WHYCHOOSEUS WHERE WID = @vWID;\n" +
            "DELETE FROM APVL_BND_INF_SERVICES WHERE WID = @vWID;\n" +
            "DELETE FROM APVL_BND_INF_PROFILE_CAPABILITIES WHERE WID = @vWID;\n" +
            "DELETE FROM APVL_BND_INF_BRAND_MAP WHERE WID = @vWID;\n" +
            "DELETE FROM APVL_BND_INF_PROJECTS_IMAGES WHERE PRO_ID IN (SELECT PRO_ID FROM APVL_BND_INF_PROJECTS WHERE WID = @vWID);\n" +
            "DELETE FROM APVL_BND_INF_PROJECTS WHERE WID = @vWID;\n" +
            "DELETE FROM BND_INF_ABOUTUS_LONG WHERE APVLID = (SELECT APVLID FROM APVL_BND_INF_PROFILE_MAST WHERE WID = @vWID);\n" +
            "DELETE FROM BND_INF_ABOUTUS_SHORT WHERE APVLID = (SELECT APVLID FROM APVL_BND_INF_PROFILE_MAST WHERE WID = @vWID);\n" +
            "DELETE FROM BND_INF_SEO_KEYWORDS WHERE APVLID = (SELECT APVLID FROM APVL_BND_INF_PROFILE_MAST WHERE WID = @vWID);\n" +
            "DELETE FROM APVL_BND_INF_PROFILE_TRANS WHERE WID IN (SELECT WID FROM APVL_BND_INF_PROFILE_MAST WHERE WID = @vWID);\n" +
            "DELETE FROM APVL_BND_INF_PROFILE_MAST WHERE WID = @vWID;";
    executeSingleBatchScript(script, false);
}

 
    // 7. Revert Traning Program Status
    public static void revertTraningProgramStatus(int contentID, int influencerID) {
        System.out.println("DB_contentID: " + contentID);
        System.out.println("DB_InflID: " + influencerID);
 
        String sql = "UPDATE TRAINING_PROGRAM_MAPPING \n" +
                "SET PROGRESS_STATUS = 0 \n" +
                "WHERE CONTENTID = " + contentID + " AND INFLUENCERID = " + influencerID + ";";
 
        executeSingleBatchScript(sql, true); // Main DB
 
    }

    // Revert Digital Card status to Approve/Reject Stage
public static void revertDigitalCardStatusToApproveReject(int bizCardID) {
    System.out.println("DB_bizCardID: " + bizCardID);
String revertSql =
    "DECLARE @vCARDID INT = " + bizCardID + ";\n" +
    "\n" +
    "-- For ACTIVITY REVERT \n" +
    "DELETE FROM BND_DIGITALCARD_ACTIVITY \n" +
    "WHERE CARDID = @vCARDID AND EVENTID > 0;\n" +
    "\n" +
    "--For  REQUEST DELETE \n" +
    ";WITH CTE AS (\n" +
    "    SELECT REQID,\n" +
    "           ROW_NUMBER() OVER (PARTITION BY CARDID ORDER BY REQID ASC) AS rn\n" +
    "    FROM BND_DIGITALCARD_REQ_MAST\n" +
    "    WHERE CARDID = @vCARDID\n" +
    ")\n" +
    "DELETE FROM CTE WHERE rn > 1;\n" +
    "\n" +
    "-- For STATUS PENDING \n" +
    "UPDATE BND_DIGITALCARD_REQ_MAST SET REQ_STATUS = 0 WHERE CARDID = @vCARDID;\n" +
    "UPDATE BND_DIGITALCARD_INFO SET STATUS = 0, ISIMAGEGENERATED = 1 WHERE ID = @vCARDID;";

// Execute 
executeSingleBatchScript(revertSql, true);  // For main DB
//executeSingleBatchScript(revertSql, false); // For secondary DB (if needed)

}


// Revert Digital Card status to Reprint Stage
public static void revertDigitalCardStatusToReprint(int bizCardID) {
    System.out.println("DB_bizCardID: " + bizCardID);

   String revertSql =
    "-- REPRINT BUTTON\n" +
    "DECLARE @vCARDID INT = " + bizCardID + "\n" +
        "\n" +
    "-- Remove duplicate requests, keep the first one only\n" +
    ";WITH CTE AS (\n" +
    "    SELECT REQID,\n" +
    "           ROW_NUMBER() OVER (PARTITION BY CARDID ORDER BY REQID ASC) AS rn\n" +
    "    FROM BND_DIGITALCARD_REQ_MAST\n" +
    "    WHERE CARDID = @vCARDID\n" +
    ")\n" +
    "DELETE FROM CTE WHERE rn > 1;\n" +
    "\n" +
    "-- Delete specific activity event\n" +
    "DELETE FROM BND_DIGITALCARD_ACTIVITY \n" +
    "WHERE CARDID = @vCARDID AND EVENTID = 90;";


    // Execute against primary DB
    executeSingleBatchScript(revertSql, true);
    
}

// Delete Added Insurance Nominee
public static void deleteAddedInsuranceNominee(int INS_ACTID) {
    System.out.println("DB_INS_ACTID: " + INS_ACTID);

   String revertSql =
    "declare @VINS_ACTID int=" + INS_ACTID + ";\n" +
    "delete from BND_INSURANCE_NOMINEE where INS_ACTID=@VINS_ACTID;\n" +
    "delete from BND_INSURANCE_ACTIVATE where INS_ACTID=@VINS_ACTID;";

    // Execute against primary DB
    executeSingleBatchScript(revertSql, true);
    
}


    // Common executor
    public static void executeSingleBatchScript(String fullScript, boolean isMainDB) {
        try (Connection conn = getConnection(isMainDB); Statement stmt = conn.createStatement()) {
            stmt.execute(fullScript);
            System.out.println((isMainDB ? "Main DB" : "Copy DB") + " → Script executed.");
        } catch (Exception e) {
            System.err.println((isMainDB ? "Main DB" : "Copy DB") + " → Error executing script:");
            e.printStackTrace();
        }
    }
}