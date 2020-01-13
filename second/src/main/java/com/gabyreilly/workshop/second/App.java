package com.gabyreilly.workshop.second;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.BasicConfigurator;

/**
 * Hello world!
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
