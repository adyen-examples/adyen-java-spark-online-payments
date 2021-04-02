package checkout;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.adyen.model.checkout.*;
import com.google.gson.Gson;

import spark.QueryParamsMap;
import spark.Response;
import view.RenderUtil;

import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;


public class Application {

    private static final String CONFIG_FILE = "config.properties";
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        port(8080);
        staticFiles.location("/static");
        Properties prop = readConfigFile();
        String clientKey = prop.getProperty("clientKey");
        CheckoutService checkoutService = new CheckoutService(prop);

        // Routes

        get("/", (req, res) -> {
            Map<String, Object> context = new HashMap<>();
            return RenderUtil.render(context, "templates/home.html");
        });

        get("/cart/:integration", (req, res) -> {
            String integrationType = req.params(":integration");

            Map<String, Object> context = new HashMap<>();
            context.put("integrationType", "/checkout/" + integrationType);

            return RenderUtil.render(context, "templates/cart.html");
        });

        get("/checkout/:integration", (req, res) -> {
            String integrationType = req.params(":integration");

            Map<String, Object> context = new HashMap<>();
            context.put("clientKey", clientKey);
            context.put("integrationType", integrationType);

            return RenderUtil.render(context, "templates/component.html");
        });

        path("/result", () -> {
            get("/success", (req, res) -> {
                Map<String, Object> context = new HashMap<>();
                return RenderUtil.render(context, "templates/checkout-success.html");
            });

            get("/failed", (req, res) -> {
                Map<String, Object> context = new HashMap<>();
                return RenderUtil.render(context, "templates/checkout-failed.html");
            });

            get("/pending", (req, res) -> {
                Map<String, Object> context = new HashMap<>();
                return RenderUtil.render(context, "templates/checkout-success.html");
            });

            get("/error", (req, res) -> {
                Map<String, Object> context = new HashMap<>();
                return RenderUtil.render(context, "templates/checkout-failed.html");
            });
        });

        // APIs

        post("/api/getPaymentMethods", (req, res) -> {
            PaymentMethodsResponse response = checkoutService.getPaymentMethods();
            return gson.toJson(response);
        });

        post("/api/initiatePayment", (req, res) -> {
            System.out.println("Response received from client:\n" + req.body());
            PaymentsRequest request = gson.fromJson(req.body(), PaymentsRequest.class);
            PaymentsResponse response = checkoutService.makePayment(request);
            return gson.toJson(response);

        });

        post("/api/submitAdditionalDetails", (req, res) -> {
            PaymentsDetailsRequest details = gson.fromJson(req.body(), PaymentsDetailsRequest.class);
            PaymentsDetailsResponse paymentsDetails = checkoutService.submitPaymentsDetails(details);
            return gson.toJson(paymentsDetails);
        });

        get("/api/handleShopperRedirect", (req, res) -> {
            System.out.println("GET redirect handler");

            PaymentsDetailsRequest detailsRequest = new PaymentsDetailsRequest();
            QueryParamsMap queryMap = req.queryMap();

            if (queryMap.hasKey("redirectResult")) {
                detailsRequest.setDetails(Collections.singletonMap("redirectResult", queryMap.value("redirectResult")));

            } else if (queryMap.hasKey("payload")) {
                detailsRequest.setDetails(Collections.singletonMap("payload", queryMap.value("payload")));
            }

            PaymentsDetailsResponse response = checkoutService.submitPaymentsDetails(detailsRequest);
            PaymentsResponse.ResultCodeEnum result = response.getResultCode();

            setRedirect(result, res);
            return res;
        });

        System.out.println("\n----------------------------------------------------------\n\t" +
            "Application is running! Access URLs:\n\t" +
            "Local: \t\thttp://localhost:8080\n\t" +
            "\n----------------------------------------------------------");
    }

    private static void setRedirect(PaymentsResponse.ResultCodeEnum result, Response res) {
        switch (result) {
            case AUTHORISED:
                res.redirect("/result/success");
                break;
            case RECEIVED:
            case PENDING:
                res.redirect("/result/pending");
                break;
            default:
                res.redirect("/result/failed");
        }
    }

    private static Properties readConfigFile() {

        Properties prop = new Properties();

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(CONFIG_FILE))) {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
