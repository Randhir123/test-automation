package context;

import java.util.HashMap;

import org.apache.logging.log4j.ThreadContext;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {
    private final World world;

    public Hooks(World world) {
        this.world = world;
    }

    @Before(order = 0)
    public void doSetupBeforeExecution(Scenario scenario) {
//        if(FeatureContext.get()==null) {
//            FeatureContext.set(new HashMap<String, Object>());
//            world.featureContext = FeatureContext.get();
//        }

        if (World.threadLocal.get()==null) {
            World.threadLocal.set(new HashMap<String, Object>());
        }
        world.featureContext = World.threadLocal.get();
        world.scenarioContext.put("scenario", scenario);
        ThreadContext.put("featureName", getFeatureFileNameFromScenarioId(scenario));
        ThreadContext.put("scenarioName", scenario.getName());
    }

    private String getFeatureFileNameFromScenarioId(Scenario scenario) {
        String scenarioId = scenario.getId();
        int start = scenarioId.lastIndexOf("/") + 1;
        int end = scenarioId.indexOf(".");
        return scenarioId.substring(start, end);
    }
}
