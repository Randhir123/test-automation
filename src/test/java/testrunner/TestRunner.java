package testrunner;

import static org.apache.commons.io.FileUtils.listFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import context.World;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"src/test/java/"},
        glue = {"."},
        //tags = "@Sanity",
        dryRun = false,
        plugin = {"pretty"},
        monochrome = true
)
public class TestRunner {

    public static final String DEFAULT_ENV = "dev";
    private static final ThreadLocal<HashMap<String, Object>> FeatureContext = new ThreadLocal<>();
    public static Properties envConfig;
    public static String testEnv;

    @BeforeClass
    public static void runThisBeforeTestExecution() throws Exception {
        System.out.println("Runner class : JUnitRunner");
        testEnv = System.getProperty("env") == null ? "local" : System.getProperty("env").toLowerCase();

        envConfig = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Collection<File> files = listFiles(new File(System.getProperty("user.dir") + "/src/test/java"), new String[]{"properties"}, true);
        for (File file : files) {
            if (file.getAbsolutePath().endsWith(testEnv + File.separator + "config.properties") || file.getAbsolutePath().endsWith("common" + File.separator + "config.properties")) {
                try {
                    envConfig.load(new FileInputStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        envConfig.list(System.out);

        World.envConfig = envConfig;
        World.threadLocal = FeatureContext;
    }
}
