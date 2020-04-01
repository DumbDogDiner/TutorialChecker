package kokumaji.tutorialchecker.util;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import sun.security.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MojangRequest {

    public static UUID getUUID(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username + "?at=" + System.currentTimeMillis() / 1000L);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream response = connection.getInputStream();
            String jsonResponse = convertStreamToString(response);

            JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(jsonResponse);

            String uuid = UUIDObject.get("id").toString();

            return UUID.fromString(formatUUID(uuid));
        } catch(Exception err) {
            return null;
        }

    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static String formatUUID(String uuid) {
        return uuid.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"
        );
    }

}
