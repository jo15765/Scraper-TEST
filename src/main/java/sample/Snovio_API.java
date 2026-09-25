package sample;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;

public class Snovio_API {

//    private final static String API_USER_ID = "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZ";
//    private final static String API_SECRET = "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZ";
    private final static String API_USER_ID = "0d2d8575327db2f14bf616a2c5aa7ffc";
    private final static String API_SECRET = "e69d6c57e82f0e7a7d98af2414408a9d";


    //These methods are working
    public String getAccessToken() {
        try {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("grant_type", "client_credentials");
            parameters.put("client_id", API_USER_ID);
            parameters.put("client_secret", API_SECRET);

            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }

            String resultString = result.toString();
            resultString = resultString.length() > 0
                    ? resultString.substring(0, resultString.length() - 1)
                    : resultString;
            System.out.println(resultString);
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.snov.io/v1/oauth/access_token").openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(resultString);
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            String access_token = content.toString();
            access_token = StringUtils.substringBetween(access_token, "access_token\":\"", "\",\"token_type");
            return access_token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getEmailCount(String domain) {
        String access_token = getAccessToken();
        //access_token = access_token.substring(1, access_token.length() - 1);
        try {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("access_token", access_token);
            parameters.put("domain", domain);

            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }
            String resultString = result.toString();
            resultString = resultString.length() > 0
                    ? resultString.substring(0, resultString.length() - 1) : resultString;
            //'https://api.snov.io/v1/get-domain-emails-count'
            //HttpURLConnection con = (HttpURLConnection) new URL("https://app.snov.io/restapi/get-domain-emails-count").openConnection();
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.snov.io/v1/get-domain-emails-count").openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(resultString);
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            Gson gson = new Gson();
            JsonObject jsonObject = new Gson().fromJson(content.toString(), JsonObject.class);
            String returnData = "The domain " + jsonObject.get("domain") + " - " + jsonObject.get("result") + " emails";
            return returnData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDomainSearch(String domain) throws JSONException {
        String access_token = getAccessToken();
        try {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("access_token", access_token);
            parameters.put("domain", domain);
            //here you can use all - personal - generic
            //generic will return example - contact@snov.io
            parameters.put("type", "personal");
            parameters.put("limit", "10");
            parameters.put("offset", "0");

            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }
            String resultString = result.toString();
            resultString = resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.snov.io/v1/get-domain-emails-with-info").openConnection();

            con.setRequestMethod("POST");
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(resultString);
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                content.append(inputLine);
            }
            in.close();

            String mail = null;

            JsonObject jsonObject = new Gson().fromJson(content.toString(), JsonObject.class);
            JsonArray arr = jsonObject.getAsJsonArray("emails");
            if (arr != null) {
                for (int a = 0; a < arr.size(); a++) {
                    JsonObject emailJson = arr.get(a).getAsJsonObject();
                    String status = emailJson.get("status").getAsString();
                    String email = emailJson.get("email").getAsString();
                    mail += email + " - " + status + "\n";
                    System.out.println(mail);
                }
            } else {
                mail = "No Record Found";
            }
            System.out.println(mail);
            return mail;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getProfileWithEmail(String email) {
        String access_token = getAccessToken();
        access_token = access_token.substring(1, access_token.length() - 1);
        try {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("access_token", access_token);
            parameters.put("email", email);

            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }
            String resultString = result.toString();
            resultString = resultString.length() > 0
                    ? resultString.substring(0, resultString.length() - 1) : resultString;

            HttpURLConnection con = (HttpURLConnection) new URL("https://app.snov.io/restapi/get-profile-by-email").openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(resultString);
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            String response = content.toString();
            System.out.println(response);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //These methods are in beta and need to be tested
    public void getEmailVerifier() throws JSONException {
        System.out.println(">> getEmailVerifier()");
        String url = "https://app.snov.io/restapi/get-emails-verification-status?emails[]=gavin.vanrooyen@octagon.com";
        String jsonInputString = "{ ";

        String response = genericPost(url, jsonInputString);

        if (!"".equals(response)) {
            JSONObject jsonObject = new JSONObject(response);
            System.out.println("Response = " + jsonObject.toString());
        }
        System.out.println("<< getEmailVerifier()");
    }

    public void addEmailsForVerification() throws JSONException {
        System.out.println(">> addEmailsForVerification()");
        String url = "https://app.snov.io/restapi/add-emails-to-verification?emails[]=gavin.vanrooyen@octagon.com";
        String jsonInputString = "{ ";

        String response = genericPost(url, jsonInputString);

        if (!"".equals(response)) {
            JSONObject jsonObject = new JSONObject(response);
            System.out.println("Response = " + jsonObject.toString());
        }
        System.out.println("<< addEmailsForVerification()");
    }

    public void getEmailFinder() throws JSONException {
        System.out.println(">> getEmailFinder()");
        String url = "https://app.snov.io/restapi/get-emails-from-names";
        String jsonInputString
                = "{\"domain\": \"octagon.com\", "
                + "\"firstName\": \"gavin\", "
                + "\"lastName\": \"vanrooyen\", ";

        String response = genericPost(url, jsonInputString);

        if (!"".equals(response)) {
            JSONObject jsonObject = new JSONObject(response);
            System.out.println("Response = " + jsonObject.toString());
        }
        System.out.println("<< getEmailFinder()");
    }

    public void getAddNamesToFindEmails() throws JSONException {
        System.out.println(">> getAddNamesToFindEmails()");
        String url = "https://app.snov.io/restapi/add-names-to-find-emails";
        String jsonInputString
                = "{\"domain\": \"octagon.com\", "
                + "\"firstName\": \"gavin\", "
                + "\"lastName\": \"vanrooyen\", ";

        String response = genericPost(url, jsonInputString);

        if (!"".equals(response)) {
            JSONObject jsonObject = new JSONObject(response);
            System.out.println("Response = " + jsonObject.toString());
        }
        System.out.println("<< getAddNamesToFindEmails()");
    }

    public void getProfileByEmail() throws JSONException {
        System.out.println(">> getProfileByEmail()");
        String url = "https://app.snov.io/restapi/get-profile-by-email";
        String jsonInputString
                = "{\"email\": \"gavin.vanrooyen@octagon.com\", ";

        String response = genericPost(url, jsonInputString);

        if (!"".equals(response)) {
            JSONObject jsonObject = new JSONObject(response);
            System.out.println("Profile = " + jsonObject.toString());
        }
        System.out.println("<< getProfileByEmail()");
    }

    public void addProspectToList() throws JSONException {
        System.out.println(">> addProspectToList()");
        String url = "https://app.snov.io/restapi/add-prospect-to-list";
        String jsonInputString
                = "{\"email\": \"john.doe@example.com\", "
                + "\"fullName\": \"John Doe\", "
                + "\"firstName\": \"John\", "
                + "\"lastName\": \"Doe\", "
                + "\"country\": \"United States\", "
                + "\"locality\": \"Woodbridge, New Jersey\", "
                + "\"social[linkedin]\": \"https://www.linkedin.com/in/johndoe/&social\", "
                + "\"social[twiiter]\": \"https://twitter.com/johndoe&social\", "
                + "\"customFields[phone number]\": \"+ 1 888 2073333\", "
                + "\"position\": \"Vice President of Sales\", "
                + "\"companyName\": \"GoldenRule\", "
                + "\"companySite\": \"https://goldenrule.com\", "
                + "\"updateContact\": true, "
                + "\"listId\": \"12345\", ";

        String response = genericPost(url, jsonInputString);

        if (!"".equals(response)) {
            JSONObject jsonObject = new JSONObject(response);
            System.out.println("Profile = " + jsonObject.toString());
        }
        System.out.println("<< addProspectToList()");
    }

    public void getProspectById() throws JSONException {
        System.out.println(">> getProspectById()");
        String url = "https://app.snov.io/restapi/get-prospect-by-id";
        String jsonInputString
                = "{\"id\": \"xusD3-T_K5IktGoaa8Jc8A==\", ";

        String response = genericPost(url, jsonInputString);

        if (!"".equals(response)) {
            JSONObject jsonObject = new JSONObject(response);
            System.out.println("Response = " + jsonObject.toString());
        }
        System.out.println("<< getProspectById()");
    }

    public void getProspectsByEmail() throws JSONException {
        System.out.println(">> getProspectsByEmail()");
        String url = "https://app.snov.io/restapi/get-prospects-by-email";
        String jsonInputString
                = "{\"email\": \"gavin.vanrooyen@octagon.com\", ";

        String response = genericPost(url, jsonInputString);

        if (!"".equals(response)) {
            JSONObject jsonObject = new JSONObject(response);
            System.out.println("Response = " + jsonObject.toString());
        }
        System.out.println("<< getProspectsByEmail()");
    }

    public void changeRecipientStatus() throws JSONException {
        System.out.println(">> changeRecipientStatus()");
        String url = "https://app.snov.io/restapi/change-recipient-status";
        String jsonInputString
                = "{\"email\": \"gavin.vanrooyen@octagon.com\", "
                + "\"campaign_id\": \"179025\", "
                + "\"status\": \"Paused\", ";

        String response = genericPost(url, jsonInputString);

        if (!"".equals(response)) {
            JSONObject jsonObject = new JSONObject(response);
            System.out.println("Response = " + jsonObject.toString());
        }
        System.out.println("<< changeRecipientStatus()");
    }

    public String genericPost(String api_endpoint, String inputString) {
        try {
            String token = getAccessToken();

            if ("".equals(token)) {
                return "";
            }

            inputString += "\"access_token\": \"" + token + "\"}";

            URL url = new URL(api_endpoint);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = inputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            System.out.println(code);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            } catch (Exception ex) {
                System.out.println(ex);
                System.out.println("Failed");
            }
        } catch (IOException ex) {
            System.out.println(ex);
            System.out.println("Failed");
        }

        return "";
    }
}
