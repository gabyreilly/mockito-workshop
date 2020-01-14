package com.gabyreilly.workshop.second;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.BasicConfigurator;

/**
 * This app is a basic runner for the emailOwners() method of the Reminder class.
 * When the secrets.properties file is provided, it will connect to the database in AWS,
 * look up the Pet ID submitted in the method, and send emails to the pet's owners.
 *
 * In real life, the emailOwners() method might be called by a HTTP API, an RPC method call, or
 * by some other class in the same service.
 *
 * Compared to the App class in the "first" module, this class initializes a DataSource and an EmailSender,
 * then passes them as dependencies to the Reminder class.
 */
public class App {
    public static void main(String[] args) throws Exception {

        Configuration fileConfig = new PropertiesConfiguration("secrets.properties");

        //Configure logging
        BasicConfigurator.configure();

        DataSource dataSource = new MySqlDataSource(fileConfig);
        EmailSender emailSender = new AirshipEmailSender(fileConfig);

        Reminder reminder = new Reminder(dataSource, emailSender);

        reminder.emailOwners(2);
    }
}
