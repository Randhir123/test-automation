package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.json.JSONObject;

public class Util {

    public static String stringFromFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    public static String jsonTemplate(String path, Map<String, Object> values) throws IOException {
        String jsonTemplate = stringFromFile(System.getProperty("user.dir") + path);
        JSONObject payload = new JSONObject(jsonTemplate);
        JSONObject params = payload.getJSONObject("parameters");

        values.forEach(params::put);

        return payload.toString();
    }
}
