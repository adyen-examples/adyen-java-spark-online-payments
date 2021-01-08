package controller;

import java.io.IOException;
import com.adyen.model.checkout.CheckoutPaymentsAction;
import com.adyen.model.checkout.PaymentsResponse;

public class FrontendParser {

    // Format response being passed back to frontend. Only leave resultCode and action. Don't need to pass back
    // The rest of the information
    public static PaymentsResponse formatResponseForFrontend(PaymentsResponse unformattedResponse) throws IOException {

        PaymentsResponse.ResultCodeEnum resultCode = unformattedResponse.getResultCode();
        if (resultCode != null) {
            PaymentsResponse newPaymentsResponse = new PaymentsResponse();
            newPaymentsResponse.setResultCode(resultCode);

            CheckoutPaymentsAction action = unformattedResponse.getAction();
            if (action != null) {
                newPaymentsResponse.setAction(action);
            }
            return newPaymentsResponse;
        } else {
            throw new IOException();
        }
    }
}
