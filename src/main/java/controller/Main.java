package controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.model.checkout.PaymentsDetailsRequest;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.Checkout;
import com.google.gson.Gson;

import model.PaymentMethods;
import model.Payments;
import model.PaymentsDetails;
import spark.QueryParamsMap;
import spark.Response;
import view.RenderUtil;

import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;


public class Main {

    private static final File FAVICON_PATH = new File("src/main/resources/static/img/favicon.ico");
    private static final String configFile = "config.properties";
    private static final Gson gson = new Gson();

    private static String apiKey = "";
    private static String clientKey = "";

    public static String merchantAccount = "";
    public static Checkout checkout;
    public static Map<String, String> paymentDataStore = new HashMap<>();

    public static void main(String[] args) {
        port(8080);
        staticFiles.location("/static");
        readConfigFile();
        checkout = new Checkout(new Client(apiKey, Environment.TEST));
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

        post("/api/getPaymentMethods", (req, res) -> {
            PaymentMethodsResponse response = PaymentMethods.getPaymentMethods("");
            return gson.toJson(response);
        });

        post("/api/initiatePayment", (req, res) -> {
            System.out.println("Response received from client:\n" + req.body());
            PaymentsRequest request = gson.fromJson(req.body(), PaymentsRequest.class);
            PaymentsResponse response = Payments.makePayment(request);
            return gson.toJson(response);

        });

        post("/api/submitAdditionalDetails", (req, res) -> {
            PaymentsDetailsRequest details = gson.fromJson(req.body(), PaymentsDetailsRequest.class);
            PaymentsResponse paymentsDetails = PaymentsDetails.getPaymentsDetails(details);
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
            detailsRequest.setPaymentData(paymentDataStore.get(queryMap.value("orderRef")));

            PaymentsResponse response = PaymentsDetails.getPaymentsDetails(detailsRequest);
            PaymentsResponse.ResultCodeEnum result = response.getResultCode();

            setRedirect(result, res);
            return res;
        });


        post("/api/handleShopperRedirect", (req, res) -> {
            System.out.println("POST redirect handler");
            QueryParamsMap queryMap = req.queryMap();

            PaymentsDetailsRequest detailsRequest = new PaymentsDetailsRequest();
            HashMap<String, String> details = new HashMap<>();
            details.put("MD", queryMap.value("MD"));
            details.put("PaRes", queryMap.value("PaRes"));
            detailsRequest.setDetails(details);
            detailsRequest.setPaymentData(paymentDataStore.get(queryMap.value("orderRef")));

            PaymentsResponse response = PaymentsDetails.getPaymentsDetails(detailsRequest);
            PaymentsResponse.ResultCodeEnum result = response.getResultCode();

            setRedirect(result, res);
            return res;
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

    private static void readConfigFile() {

        Properties prop = new Properties();

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(configFile));) {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        merchantAccount = prop.getProperty("merchantAccount");
        apiKey = prop.getProperty("apiKey");
        clientKey = prop.getProperty("clientKey");
    }
}
