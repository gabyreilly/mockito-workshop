package com.gabyreilly.workshop.second;

import com.gabyreilly.workshop.models.Owner;
import com.gabyreilly.workshop.models.Pet;
import org.apache.commons.configuration.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlDataSource implements DataSource {


    private final String dbConnectionString;

    public MySqlDataSource(Configuration configuration) {
        this.dbConnectionString = configuration.getString("aws.db.connectionString");
    }

    @Override
    public Optional<Pet> getPet(int petId) {
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

    @Override
    public List<Owner> getOwnersForPet(int petId) {
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
}
