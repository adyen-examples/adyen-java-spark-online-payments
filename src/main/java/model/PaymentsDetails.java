package model;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.PaymentsDetailsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.Main;

import java.io.IOException;
import java.util.HashMap;

public class PaymentsDetails {

	public static String getPaymentsDetails(PaymentsDetailsRequest paymentsDetailsRequest) {

		PaymentsResponse paymentsDetailsResponse = makePaymentDetailsRequest(paymentsDetailsRequest);

		Gson gson = new GsonBuilder().create();
		return gson.toJson(paymentsDetailsResponse);
	}


	public static PaymentsResponse getPaymentsDetailsObject(PaymentsDetailsRequest paymentsDetailsRequest) {

		return makePaymentDetailsRequest(paymentsDetailsRequest);
	}


	private static PaymentsResponse makePaymentDetailsRequest(PaymentsDetailsRequest paymentsDetailsRequest) {
		Client client = new Client(Main.apiKey, Environment.TEST);
		Checkout checkout = new Checkout(client);

		System.out.println("/paymentsDetails request:" + paymentsDetailsRequest.toString());
		PaymentsResponse paymentsDetailsResponse = null;
		try {
			paymentsDetailsResponse = checkout.paymentsDetails(paymentsDetailsRequest);

		} catch (ApiException | IOException e) {
			e.printStackTrace();
		} finally {
			if (paymentsDetailsResponse != null) {
				System.out.println("paymentsDetails response:\n" + paymentsDetailsResponse.toString());
			}
		}
		return paymentsDetailsResponse;
	}
}
