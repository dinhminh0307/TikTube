package com.example.tiktube.backend.helpers;

import android.util.Log;

import com.example.tiktube.backend.controllers.KeyController;
import com.example.tiktube.backend.models.StripeKey;
import com.stripe.android.paymentsheet.PaymentSheet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StripeHelper {
    private StripeKey stripeKey = new StripeKey();
    private final OkHttpClient httpClient = new OkHttpClient();

    private KeyController keyController;

    private final PaymentSheet paymentSheet;

    public StripeHelper(PaymentSheet paymentSheet) {
        this.paymentSheet = paymentSheet;
        keyController = new KeyController();
        keyController.getKey()
                .thenAccept(k -> {
                    stripeKey.setKey(k);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    public void createPaymentIntent(double amount, PaymentIntentCallback callback) {
        RequestBody body = new FormBody.Builder()
                .add("amount", String.valueOf((int) (amount * 100))) // Amount in cents
                .add("currency", "usd")
                .build();

        Request request = new Request.Builder()
                .url("https://api.stripe.com/v1/payment_intents")
                .addHeader("Authorization", "Bearer " + stripeKey.getPrivateKeyStripe())
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("StripeHelper", "Error creating PaymentIntent", e);
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        String clientSecret = jsonResponse.getString("client_secret");
                        callback.onSuccess(clientSecret);
                    } catch (JSONException e) {
                        callback.onError(e.getMessage());
                    }
                } else {
                    callback.onError("Failed to create PaymentIntent: " + response.message());
                }
            }
        });
    }

    public interface PaymentIntentCallback {
        void onSuccess(String clientSecret);

        void onError(String errorMessage);
    }
}
