package model;

import java.io.IOException;
import com.adyen.model.checkout.PaymentMethodsRequest;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.service.exception.ApiException;

import controller.Main;

public class PaymentMethods {

    public static PaymentMethodsResponse getPaymentMethods(String type) throws IOException, ApiException {
        PaymentMethodsRequest paymentMethodsRequest = new PaymentMethodsRequest();
        paymentMethodsRequest.setMerchantAccount(Main.merchantAccount);

        paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.WEB);
        paymentMethodsRequest.setShopperReference("SparkJava Checkout Shopper");
        System.out.println("/paymentMethods context:\n" + paymentMethodsRequest.toString());

        PaymentMethodsResponse response = Main.checkout.paymentMethods(paymentMethodsRequest);
        System.out.println("/paymentMethods response:\n" + response);
        return response;
    }
}
