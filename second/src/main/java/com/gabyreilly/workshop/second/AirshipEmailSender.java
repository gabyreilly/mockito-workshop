package com.gabyreilly.workshop.second;

import com.gabyreilly.workshop.models.Owner;
import com.gabyreilly.workshop.models.Pet;
import com.google.common.net.HttpHeaders;
import org.apache.commons.configuration.Configuration;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.asynchttpclient.filter.ThrottleRequestFilter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class AirshipEmailSender implements EmailSender {

    // String.format this with owner's email_address, first_name, then the pet_name
    private static final String AIRSHIP_TEMPLATE = "{\n" +
            "    \"audience\": {\n" +
            "        \"create_and_send\": [\n" +
            "            {\n" +
            "                \"ua_address\": \"%s\",\n" +
            "                \"first_name\": \"%s\",\n" +
            "                \"pet_name\": \"%s\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"device_types\": [\n" +
            "        \"email\"\n" +
            "    ],\n" +
            "    \"notification\": {\n" +
            "        \"email\": {\n" +
            "            \"message_type\": \"transactional\",\n" +
            "            \"sender_name\": \"Airship\",\n" +
            "            \"sender_address\": \"no-reply@trials.urbanairship.com\",\n" +
            "            \"reply_to\": \"no-reply@trials.urbanairship.com\",\n" +
            "            \"template\": {\n" +
            "                \"template_id\": \"3dd28afb-9db4-464f-91d0-1b4e2ff0465b\"\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";


    private final String airshipAppKey;
    private final String airshipAuth;

    public AirshipEmailSender(Configuration configuration) {
        this.airshipAppKey = configuration.getString("airship.appkey");
        this.airshipAuth = configuration.getString("airship.auth");
    }


    /**
     * Send an email to the owner of the pet using Airship email templates
     * @param owner
     * @param pet
     */
    @Override
    public void sendEmail(Owner owner, Pet pet) {
            String emailPayload = String.format(AIRSHIP_TEMPLATE, owner.getOwnerEmail(), owner.getOwnerName(), pet.getPetName());

            AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder()
                    .addRequestFilter(new ThrottleRequestFilter(100))
                    .build();

            try (AsyncHttpClient client = new DefaultAsyncHttpClient(config)) {
                RequestBuilder requestBuilder = new RequestBuilder()
                        .setMethod("POST")
                        .setUrl("https://go.urbanairship.com/api/create-and-send/")
                        .setBody(emailPayload);

                requestBuilder.addHeader("X-UA-AppKey", airshipAppKey);
                requestBuilder.addHeader(HttpHeaders.AUTHORIZATION, airshipAuth);

                //NOTE: for production traffic, handle the result asynchronously, see https://github.com/AsyncHttpClient/async-http-client#dealing-with-responses
                Response response = client.executeRequest(requestBuilder).toCompletableFuture().get();

            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

    }
}
