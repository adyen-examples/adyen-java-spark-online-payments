package model;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Address;
import com.adyen.model.Amount;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.FrontendParser;
import controller.Main;

import java.io.IOException;

public class Payments {

	public static String makePayment(PaymentsRequest paymentsRequest) {
		Client client = new Client(Main.apiKey, Environment.TEST);
		Checkout checkout = new Checkout(client);

		paymentsRequest.setMerchantAccount(Main.merchantAccount);
		paymentsRequest.setCountryCode("NL");
		Amount amount = new Amount();
		amount.setCurrency("EUR");
		amount.setValue(1000L);
		paymentsRequest.setAmount(amount);
		paymentsRequest.setReference("Test Reference");
		paymentsRequest.setReturnUrl("http://localhost:8080/api/handleShopperRedirect");
		paymentsRequest.setChannel(PaymentsRequest.ChannelEnum.WEB);
		System.out.println("/paymentMethods response:\n" + paymentsRequest.toString());

		try {
			PaymentsResponse response = checkout.payments(paymentsRequest);
			PaymentsResponse formattedResponse = FrontendParser.formatResponseForFrontend(response);

			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String paymentsResponse = gson.toJson(formattedResponse);
			System.out.println("/payments response:\n" + paymentsResponse);
			return paymentsResponse;
		} catch (ApiException | IOException e) {
			return e.toString();
		}
	}
}
