package idverify.steps;

import static util.Util.jsonTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;
import org.junit.Assert;

import context.World;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import util.RequestSpecificationFactory;

public class IDVerifySteps {

    private final World world;
    private final Properties envConfig;
    private RequestSpecification request;

    public IDVerifySteps(World world) {
        this.world = world;
        this.envConfig = World.envConfig;
        this.world.featureContext = World.threadLocal.get();
    }

    @Before
    public void setUp() {
        request = RequestSpecificationFactory.getInstance(world);
    }

    @Given("Caller presents a valid OAuth2 token")
    public void getOAuth2Token() {
        String grantType = envConfig.getProperty("idverify-grant_type");
        String clientId = envConfig.getProperty("idverify-client_id");
        String clientSecret = envConfig.getProperty("idverify-client_secret");
        String accessTokenUrl = envConfig.getProperty("idverify-access_token_url");

        String body = String.format("grant_type=%s&client_secret=%s&client_id=%s", grantType, clientSecret, clientId);

        RequestSpecification request = RequestSpecificationFactory.getInstanceOAuth2();

        Response response = request
                .accept(ContentType.URLENC)
                .body(body)
                .post(accessTokenUrl);

        String responseString = response.then().extract().asString();
        String accessToken = new JSONObject(responseString).getString("access_token");

        world.scenarioContext.put("accessToken", accessToken);
    }

    @Given("Mock is enabled on the IdVerify Service")
    public void enableMock() {
        world.scenarioContext.put("mock", "true");
        world.scenarioContext.put("dblk", "true");
    }

    @Given("Mock is disabled on the IdVerify Service")
    public void disableMock() {
        world.scenarioContext.put("mock", "false");
        world.scenarioContext.put("dblk", "true");
    }

    @Given("a person with details")
    public void getDemogLiteData(@Transpose DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String nik = data.get(0).get("nik");
        String fullName = data.get(0).get("full_name");
        String dob = data.get(0).get("dob");
        String phoneNo = data.get(0).get("phone_no");
        String email = data.get(0).get("email");

        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("nik", nik);
        valuesToTemplate.put("full_name", fullName);
        valuesToTemplate.put("dob", dob);
        valuesToTemplate.put("phone_no", phoneNo);
        valuesToTemplate.put("email", email);

        String jsonAsString = jsonTemplate(envConfig.getProperty("idverify-demote_lite_request"), valuesToTemplate);

        world.scenarioContext.put("requestStr", jsonAsString);
    }

    @Given("the match threshold is set to {double}")
    public void setThreshold(Double threshold) {
        String requestStr = world.scenarioContext.get("requestStr").toString();
        JSONObject payload = new JSONObject(requestStr);
        payload.put("threshold", threshold);

        world.scenarioContext.put("requestStr", payload.toString());
    }

    @When("request is submitted for demog verification")
    public void submitRequest() {
        String accessToken = world.scenarioContext.get("accessToken").toString();
        String payload = world.scenarioContext.get("requestStr").toString();
        String mock = world.scenarioContext.get("mock").toString();
        String dblk = world.scenarioContext.get("dblk").toString();

        Response response = request
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("mock", mock)
                .queryParam("dblk", dblk)
                .body(payload)
                .contentType(ContentType.JSON)
                .when().post(envConfig.getProperty("idverify-service_url")
                        + envConfig.getProperty("idverify-demog_lite_api"));

        world.scenarioContext.put("response", response);
    }

    @Then("verify that the HTTP response is {int}")
    public void verifyHTTPResponseCode(Integer status) {
        Response response = (Response) world.scenarioContext.get("response");
        Integer actualStatusCode = response.then()
                .extract()
                .statusCode();
        Assert.assertEquals(status, actualStatusCode);
    }

    @Then("a transaction id is returned")
    public void checkTransaction() {
        Response response = (Response) world.scenarioContext.get("response");
        String responseString = response.then().extract().asString();
        String transactionId = new JSONObject(responseString).getJSONObject("data").getString("transaction_id");
        Assert.assertNotNull(transactionId);
    }
}
