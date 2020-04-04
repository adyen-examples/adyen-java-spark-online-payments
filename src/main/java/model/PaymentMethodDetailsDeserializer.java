package model;

import com.adyen.model.checkout.DefaultPaymentMethodDetails;
import com.adyen.model.checkout.PaymentMethodDetails;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class PaymentMethodDetailsDeserializer implements JsonDeserializer<PaymentMethodDetails> {

	@Override
	public PaymentMethodDetails deserialize(JsonElement jsonElement, Type typeOfT,
											JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		return jsonDeserializationContext.deserialize(jsonElement, DefaultPaymentMethodDetails.class);
	}
}