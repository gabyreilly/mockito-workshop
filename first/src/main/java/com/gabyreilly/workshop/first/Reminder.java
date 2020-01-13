package com.gabyreilly.workshop.first;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class Reminder {
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

    private final String dbConnectionString;
    private final String airshipAppKey;
    private final String airshipAuth;

    public Reminder(Configuration configuration) {
        this.dbConnectionString = configuration.getString("aws.db.connectionString");
        this.airshipAppKey = configuration.getString("airship.appkey");
        this.airshipAuth = configuration.getString("airship.auth");
    }

    public void emailOwners(int petId) {
        //Retrieve pet information
        Optional<Pet> matchingPet = getPet(petId);

        if (matchingPet.isPresent()) {
            System.out.println(matchingPet.get());

            List<Owner> matchingOwners = getOwnersForPet(petId);

            //Send email to each owner of the pet
            for (Owner owner : matchingOwners) {
                sendEmail(owner, matchingPet.get());
            }
        }
        //TODO: what do we do if the pet is not found?
    }


    private Optional<Pet> getPet(int petId) {
        Connection connection = null;
        try {
            // create a database connection
            connection =  DriverManager.getConnection(dbConnectionString);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            String sql = "select * from pet where pet_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, petId);
            //statement.
            try (ResultSet rs = preparedStatement.executeQuery()) {
                //Only read 1 row from the iterator
                if (rs.next()) {
                    // read the result set
                    String foundPetName = rs.getString("pet_name");
                    int foundPetId = rs.getInt("pet_id");
                    return Optional.of(new Pet(foundPetId, foundPetName));
                } else {
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
            return Optional.empty();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }


    /**
     * Connect to the database.  Given a petId, return all the Owners of the pet.
     * The list may be empty, or it may contain multiple owners.
     * @param petId
     * @return
     */
    private List<Owner> getOwnersForPet(int petId) {
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection(dbConnectionString);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            String sql = "select owner_id, first_name, last_name, email_address from owner o where exists" +
                    "(select * from owner_pet op where op.pet_id = ? and op.owner_id = o.owner_id )";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, petId);

            List<Owner> foundOwners = new ArrayList<>();

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    // read the result set

                    int foundOwnerId = rs.getInt("owner_id");
                    String foundOwnerName = rs.getString("first_name");
                    String foundOwnerEmail = rs.getString("email_address");

                    foundOwners.add(new Owner(foundOwnerId, foundOwnerName, foundOwnerEmail));
                }
            }
            return foundOwners;

        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
            return new ArrayList<>();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }


    /**
     * Send an email to the owner of the pet using Airship email templates
     * @param owner
     * @param pet
     */
    private void sendEmail(Owner owner, Pet pet) {
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


