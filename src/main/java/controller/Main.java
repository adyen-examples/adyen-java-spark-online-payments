package controller;

import com.adyen.model.checkout.PaymentsDetailsRequest;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.net.MediaType;
import org.apache.http.NameValuePair;
import spark.QueryParamsMap;
import view.RenderUtil;

import java.io.*;
import java.util.*;


import model.PaymentMethods;
import model.Payments;
import model.PaymentsDetails;

import static com.adyen.model.PaymentResult.ResultCodeEnum.*;
import static com.adyen.model.PaymentResult.ResultCodeEnum.PENDING;
import static spark.Spark.*;

import spark.Response;

import javax.xml.ws.ResponseWrapper;


public class Main {

	private static final File FAVICON_PATH = new File("src/main/resources/static/img/favicon.ico");
	private static final String configFile = "config.properties";

	public static String merchantAccount = "";
	public static String apiKey = "";
	public static String clientKey = "";

	public static final HashMap<String, String> paymentDataStore = new HashMap<>();

	public static void main(String[] args) {
		port(8080);
		staticFiles.location("/static");
		readConfigFile();

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
			context.put("paymentMethods", PaymentMethods.getPaymentMethods(integrationType));
			context.put("clientKey", clientKey);
			context.put("integrationType", integrationType);

			return RenderUtil.render(context, "templates/component.html");
		});

		post("/api/getPaymentMethods", (req, res) -> {
			String paymentMethods = PaymentMethods.getPaymentMethods("");

			return paymentMethods;
		});

		post("/api/initiatePayment", (req, res) -> {
			System.out.println("Response received from client:\n" + req.body());
			PaymentsRequest request = FrontendParser.parsePayment(req.body());
			String response = Payments.makePayment(request);

			return response;
		});

		post("/api/submitAdditionalDetails", (req, res) -> {
			PaymentsDetailsRequest details = FrontendParser.parseDetails(req.body());
			String response = PaymentsDetails.getPaymentsDetails(details);

			return response;
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

			PaymentsResponse response = PaymentsDetails.getPaymentsDetailsObject(detailsRequest);
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

			PaymentsResponse response = PaymentsDetails.getPaymentsDetailsObject(detailsRequest);
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

		get("/favicon.ico", (req, res) -> {
			return getFavicon(res);
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

	private static Object getFavicon(Response res) {
		try {
			InputStream in = null;
			OutputStream out;
			try {
				in = new BufferedInputStream(new FileInputStream(FAVICON_PATH));
				out = new BufferedOutputStream(res.raw().getOutputStream());
				res.raw().setContentType(MediaType.ICO.toString());
				ByteStreams.copy(in, out);
				out.flush();
				return "";
			} finally {
				Closeables.close(in, true);
			}
		} catch (FileNotFoundException ex) {
			res.status(404);
			return ex.getMessage();
		} catch (IOException ex) {
			res.status(500);
			return ex.getMessage();
		}
	}

	private static void readConfigFile() {

		Properties prop = new Properties();

		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(configFile));
			prop.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		merchantAccount = prop.getProperty("merchantAccount");
		apiKey = prop.getProperty("apiKey");
		clientKey = prop.getProperty("clientKey");
	}
}
