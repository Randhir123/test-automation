package context;

import java.util.HashMap;
import java.util.Properties;

public class World {
    public static Properties envConfig;
    public HashMap<String, Object> featureContext;
    public HashMap<String, Object> scenarioContext = new HashMap<>();
    public static ThreadLocal<HashMap<String, Object>> threadLocal= new ThreadLocal<>();
}