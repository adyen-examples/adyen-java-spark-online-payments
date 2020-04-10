package model;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.LineItem;
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

		String type = paymentsRequest.getPaymentMethod().getType();

		setAmount(paymentsRequest, type);
		paymentsRequest.setChannel(PaymentsRequest.ChannelEnum.WEB);
		paymentsRequest.setMerchantAccount(Main.merchantAccount);
		paymentsRequest.setReturnUrl("http://localhost:8080/api/handleShopperRedirect");

		paymentsRequest.setReference("Java Integration Test Reference");
		paymentsRequest.setShopperReference("Java Checkout Shopper");

		paymentsRequest.setCountryCode("NL");

		if (type.equals("alipay")) {
			paymentsRequest.setCountryCode("CN");

		} else if (type.contains("klarna")) {
			paymentsRequest.setShopperEmail("myEmail@adyen.com");
			paymentsRequest.setShopperLocale("en_US");

			addLineItems(paymentsRequest);

		} else if (type.equals("directEbanking") || type.equals("giropay")) {
			paymentsRequest.countryCode("DE");

		} else if (type.equals("dotpay")) {
			paymentsRequest.countryCode("PL");
			paymentsRequest.getAmount().setCurrency("PLN");

		} else if (type.equals("scheme")) {
			paymentsRequest.setOrigin("http://localhost:8080");
			paymentsRequest.putAdditionalDataItem("allow3DS2", "true");

		} else if (type.equals("ach")) {
			paymentsRequest.countryCode("US");
		}

		System.out.println("/payments request:\n" + paymentsRequest.toString());

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


	private static void setAmount(PaymentsRequest paymentsRequest, String type) {
		Amount amount = new Amount();

		String currency;

		switch (type) {
			case "alipay":
				currency = "CNY";
				break;
			case "dotpay":
				currency = "PLN";
				break;
			case "boletobancario":
				currency = "BRL";
				break;
			case "ach":
				currency = "USD";
				break;
			default:
				currency = "EUR";
		}

		amount.setCurrency(currency);
		amount.setValue(1000L);
		paymentsRequest.setAmount(amount);
	}

	private static void addLineItems(PaymentsRequest paymentsRequest) {
		String item1 = "{\n" +
				"                \"quantity\": \"1\",\n" +
				"                \"amountExcludingTax\": \"450\",\n" +
				"                \"taxPercentage\": \"1111\",\n" +
				"                \"description\": \"Sunglasses\",\n" +
				"                \"id\": \"Item #1\",\n" +
				"                \"taxAmount\": \"50\",\n" +
				"                \"amountIncludingTax\": \"500\",\n" +
				"                \"taxCategory\": \"High\"\n" +
				"            }";
		String item2 = "{\n" +
				"                \"quantity\": \"1\",\n" +
				"                \"amountExcludingTax\": \"450\",\n" +
				"                \"taxPercentage\": \"1111\",\n" +
				"                \"description\": \"Headphones\",\n" +
				"                \"id\": \"Item #2\",\n" +
				"                \"taxAmount\": \"50\",\n" +
				"                \"amountIncludingTax\": \"500\",\n" +
				"                \"taxCategory\": \"High\"\n" +
				"            }";

		Gson gson = new GsonBuilder().create();
		LineItem lineItem1 = gson.fromJson(item1, LineItem.class);
		LineItem lineItem2 = gson.fromJson(item2, LineItem.class);

		paymentsRequest.addLineItemsItem(lineItem1);
		paymentsRequest.addLineItemsItem(lineItem2);
	}
}
