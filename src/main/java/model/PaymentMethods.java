package model;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.PaymentMethodsRequest;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.Main;

import java.io.IOException;

public class PaymentMethods {

	public static String getPaymentMethods() {
		Client client = new Client(Main.apiKey, Environment.TEST);
		Checkout checkout = new Checkout(client);

		PaymentMethodsRequest paymentMethodsRequest = new PaymentMethodsRequest();
		paymentMethodsRequest.setMerchantAccount(Main.merchantAccount);
		paymentMethodsRequest.setCountryCode("NL");
		Amount amount = new Amount();
		amount.setCurrency("EUR");
		amount.setValue(1000L);
		paymentMethodsRequest.setAmount(amount);
		paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.WEB);
		System.out.println("/paymentMethods context:\n" + paymentMethodsRequest.toString());

		try {
			PaymentMethodsResponse response = checkout.paymentMethods(paymentMethodsRequest);
			Gson gson = new GsonBuilder().create();
			String paymentMethodsResponseStringified = gson.toJson(response);
			System.out.println("/paymentMethods response:\n" + paymentMethodsResponseStringified);
			return paymentMethodsResponseStringified;
		} catch (ApiException | IOException e) {
			return e.toString();
		}
	}
}
