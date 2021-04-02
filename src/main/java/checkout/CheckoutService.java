package checkout;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.*;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CheckoutService {

    private final Checkout checkout;
    private final String merchantAccount;

    public CheckoutService(final Properties prop) {
        merchantAccount = prop.getProperty("merchantAccount");
        checkout = new Checkout(new Client(prop.getProperty("apiKey"), Environment.TEST));
    }

    public PaymentMethodsResponse getPaymentMethods() throws IOException, ApiException {
        PaymentMethodsRequest paymentMethodsRequest = new PaymentMethodsRequest();
        paymentMethodsRequest.setMerchantAccount(merchantAccount);

        paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.WEB);
        paymentMethodsRequest.setShopperReference("SparkJava Checkout Shopper");
        System.out.println("/paymentMethods context:\n" + paymentMethodsRequest.toString());

        PaymentMethodsResponse response = checkout.paymentMethods(paymentMethodsRequest);
        System.out.println("/paymentMethods response:\n" + response);
        return response;
    }

    public PaymentsResponse makePayment(PaymentsRequest paymentsRequest) throws IOException, ApiException {
        String type = paymentsRequest.getPaymentMethod().getType();

        paymentsRequest.setAmount(getAmount(type));
        paymentsRequest.setChannel(PaymentsRequest.ChannelEnum.WEB);
        paymentsRequest.setMerchantAccount(merchantAccount);

        String orderRef = UUID.randomUUID().toString();
        paymentsRequest.setReference(orderRef);
        paymentsRequest.setReturnUrl("http://localhost:8080/api/handleShopperRedirect?orderRef=" + orderRef);

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
            paymentsRequest.setShopperIP("0.0.0.1");

        } else if (type.equals("ach") || type.equals("paypal")) {
            paymentsRequest.countryCode("US");
        }

        System.out.println("/payments request:\n" + paymentsRequest.toString());

        PaymentsResponse response = checkout.payments(paymentsRequest);

        System.out.println("/payments response:\n" + response);
        return response;
    }

    public PaymentsDetailsResponse submitPaymentsDetails(PaymentsDetailsRequest paymentsDetailsRequest) throws IOException, ApiException {
        System.out.println("/paymentsDetails request:" + paymentsDetailsRequest.toString());
        PaymentsDetailsResponse paymentsDetailsResponse = checkout.paymentsDetails(paymentsDetailsRequest);
        System.out.println("paymentsDetails response:\n" + paymentsDetailsResponse.toString());
        return paymentsDetailsResponse;
    }


    private Amount getAmount(String type) {

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
            case "paypal":
                currency = "USD";
                break;
            default:
                currency = "EUR";
        }
        Amount amount = new Amount();

        amount.setCurrency(currency);
        amount.setValue(1000L);
        return amount;
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
