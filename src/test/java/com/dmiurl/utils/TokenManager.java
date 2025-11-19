package  com.dmiurl.utils;
public class TokenManager {
    private static String authToken;
    private static String influencerId;
    private static String firstName;
    

    public static String getAuthToken() {
        return authToken;
    }

    public static void setAuthToken(String token) {
        authToken = token;
    }
    
    public static String getInfluencerId() {
        return influencerId;
    }

    public static void setInfluencerId(String influencerID) {
    	influencerId = influencerID;
    }
    
    public static String getFirstName() {
        return firstName;
    }

    public static void setFirstName(String firstname) {
    	firstName = firstname;
    }
}
