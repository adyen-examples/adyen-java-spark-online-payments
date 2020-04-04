package controller;

import com.adyen.model.checkout.PaymentDetails;
import com.adyen.model.checkout.PaymentsDetailsRequest;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.net.MediaType;
import org.apache.http.NameValuePair;
import view.RenderUtil;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import model.PaymentMethods;
import model.Payments;
import model.PaymentsDetails;

import static spark.Spark.*;
import spark.Response;


public class Main {

	private static final File FAVICON_PATH = new File("src/main/resources/static/img/favicon.ico");

	public static String merchantAccount = "";
	public static String apiKey = "";
	public static String originKey = "";
	public static String paymentMethodsUrl = "";
	public static String paymentsUrl = "";
	public static String paymentsDetailsUrl = "";

	public static void main(String[] args) {
		port(8080);
		staticFiles.location("/static");
		initalizeConstants();

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
			context.put("paymentMethods", PaymentMethods.getPaymentMethods());
			context.put("originKey", originKey);
			context.put("integrationType", integrationType);

			return RenderUtil.render(context, "templates/component.html");
		});

		post("/api/getPaymentMethods", (req, res) -> {
			String paymentMethods = PaymentMethods.getPaymentMethods();
			return paymentMethods;
		});

		post("/api/initiatePayment", (req, res) -> {
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
			System.out.println("GET result\n" + req.body());
			res.redirect("/error"); //TODO: Evaluate contents of res at this point to handle redirect
			return res;
		});

		post("/api/handleShopperRedirect", (req, res) -> {
			System.out.println("POST result\n" + req.body() + "\n");

			// Triggers when POST contains query params. Triggers on call back from issuer after 3DS2 challenge w/ MD & PaRes
			if (req.body().contains("&")) {
				List<NameValuePair> params = FrontendParser.parseQueryParams(req.body());
				System.out.println(params.toString());
				String md = params.get(0).getValue();
				String paRes = params.get(1).getValue();

				Map<String, Object> context = new HashMap<>();
				context.put("MD", md);
				context.put("PaRes", paRes);
				return RenderUtil.render(context, "templates/fetch-payment-data.html"); // Get paymentData from localStorage

			} else {
				PaymentsDetailsRequest pdr = FrontendParser.parseDetails(req.body());

				PaymentsResponse paymentResult = PaymentsDetails.getPaymentsDetailsObject(pdr);
				PaymentsResponse.ResultCodeEnum result = paymentResult.getResultCode();

				switch (result) {
					case AUTHORISED:
						res.redirect("/success");
					case RECEIVED: case PENDING:
						res.redirect("/pending");
					default:
						res.redirect("/failed");
				}
				return res;
			}
		});

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

		get("/favicon.ico", (req, res) -> {
			return getFavicon(res);
		});
	}

	private static void initalizeConstants() {
		merchantAccount = "TylerDouglas";
		apiKey = "AQEyhmfxL4jIYhVBw0m/n3Q5qf3VaY9UCJ1+XWZe9W27jmlZiiSaWGoa4mOFeQne5hiuhQsQwV1bDb7kfNy1WIxIIkxgBw==-hJYG90gqLYPclLs6We+q8CUtAsa+KgXr/iWftd+rrCM=-89EKHpWfW8ABmGF3";
		originKey = "pub.v2.8115499067697722.aHR0cDovL2xvY2FsaG9zdDo4MDgw.I4ixvXum4JGOjgI0Nd3YQ49P4AWvIncxMv41suCoW1Y";
		paymentMethodsUrl = "https://checkout-test.adyen.com/v52/paymentMethods";
		paymentsUrl = "https://checkout-test.adyen.com/v52/payments";
		paymentsDetailsUrl = "https://checkout-test.adyen.com/v52/payments/details";
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
}
