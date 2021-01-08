package model;

import java.io.IOException;
import com.adyen.model.checkout.PaymentsDetailsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.exception.ApiException;

import controller.Main;

public class PaymentsDetails {

    public static PaymentsResponse getPaymentsDetails(PaymentsDetailsRequest paymentsDetailsRequest) throws IOException, ApiException {
        System.out.println("/paymentsDetails request:" + paymentsDetailsRequest.toString());
        PaymentsResponse paymentsDetailsResponse = null;
        paymentsDetailsResponse = Main.checkout.paymentsDetails(paymentsDetailsRequest);
        System.out.println("paymentsDetails response:\n" + paymentsDetailsResponse.toString());
        return paymentsDetailsResponse;
    }
}
