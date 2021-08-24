package util;

import static io.restassured.RestAssured.given;

import java.io.PrintStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;

import context.World;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.SSLConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;

public class RequestSpecificationFactory {

    private static final Logger LOG = LogManager.getLogger(RequestSpecificationFactory.class);
    private static final PrintStream logStream = IoBuilder.forLogger(LOG).buildPrintStream();

    public static RequestSpecification getInstance(World world) {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        Scenario scenario = (Scenario) world.scenarioContext.get("scenario");

        requestSpecBuilder.addFilter(RequestLoggingFilter.logRequestTo(logStream))
                .addFilter(new CustomLogFilter(scenario))
                .addFilter(ResponseLoggingFilter.logResponseTo(logStream));

        requestSpecBuilder.setConfig(RestAssured.config().sslConfig(new SSLConfig()
                .relaxedHTTPSValidation().allowAllHostnames()));

        return given(requestSpecBuilder.build());
    }

    public static RequestSpecification getInstanceOAuth2() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();

        requestSpecBuilder.setConfig(RestAssured.config().sslConfig(new SSLConfig()
                .relaxedHTTPSValidation().allowAllHostnames()).encoderConfig(EncoderConfig.encoderConfig()
                .encodeContentTypeAs("x-www-form-urlencoded",
                        ContentType.URLENC)))
                .setContentType("application/x-www-form-urlencoded; charset=UTF-8");

        return given(requestSpecBuilder.build());
    }

    public static class CustomLogFilter implements Filter {

        private final Scenario scenario;

        public CustomLogFilter(Scenario scenario) {
            this.scenario = scenario;
        }

        @Override
        public Response filter(FilterableRequestSpecification filterableRequestSpecification,
                               FilterableResponseSpecification filterableResponseSpecification, FilterContext filterContext) {
            Response response = filterContext.next(filterableRequestSpecification, filterableResponseSpecification);
            String requestLogs = "Request:" +
                    "\n" +
                    "Request method: " + objectValidation(filterableRequestSpecification.getMethod()) +
                    "\n" +
                    "Request URI: " + objectValidation(filterableRequestSpecification.getURI()) +
                    "\n" +
                    "Form Params: " + objectValidation(filterableRequestSpecification.getFormParams()) +
                    "\n" +
                    "Request Param: " + objectValidation(filterableRequestSpecification.getRequestParams()) +
                    "\n" +
                    "Headers: " + objectValidation(filterableRequestSpecification.getHeaders()) +
                    "\n" +
                    "Cookies: " + objectValidation(filterableRequestSpecification.getCookies()) +
                    "\n" +
                    "Proxy: " + objectValidation(filterableRequestSpecification.getProxySpecification()) +
                    "\n" +
                    "Body: " + objectValidation(filterableRequestSpecification.getBody()) +
                    "\n" +
                    "******************************";
            String responseLogs = "\n" + "Response:" + "\n" +
                    "Status Code: " + response.getStatusCode() +
                    "\n" +
                    "Status Line: " + response.getStatusLine() +
                    "\n" +
                    "Response Cookies: " + response.getDetailedCookies() +
                    "\n" +
                    "Response Content Type: " + response.getContentType() +
                    "\n" +
                    "Response Headers: " + response.getHeaders() +
                    "\n" +
                    "Response Body: " + "\n" + response.getBody().prettyPrint();

            scenario.log(requestLogs + responseLogs);
            return response;
        }

        public String objectValidation(Object o) {
            if (o == null) {
                return null;
            } else {
                return o.toString();
            }
        }
    }
}