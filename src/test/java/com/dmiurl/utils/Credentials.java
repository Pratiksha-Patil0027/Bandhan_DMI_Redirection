package  com.dmiurl.utils;
import io.github.cdimascio.dotenv.Dotenv;
public class Credentials {
    


    private static final Dotenv dotenv = Dotenv.load();

    public static String getOutlookPassword() {
        return dotenv.get("OUTLOOKPASSWORD");
    }

    public static String getOutlookSenderMail() {
        return dotenv.get("OUTLOOKSENDERMAIL");
    }

    public static String getOutlookToRecipients() {
        return dotenv.get("OUTLOOKTORECIPIENTS");
    }

    public static String get_MAIN_DB_URL() {
        return dotenv.get("MAIN_DB_URL");
    }

     public static String get_COPY_DB_URL() {
        return dotenv.get("COPY_DB_URL");
    }

     public static String get_DB_USER() {
        return dotenv.get("DB_USER");
    }
 public static String get_DB_PASSWORD() {
        return dotenv.get("DB_PASSWORD");
    }

}


