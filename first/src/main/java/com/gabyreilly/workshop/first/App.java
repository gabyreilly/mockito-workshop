package com.gabyreilly.workshop.first;

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

        //Set up database
        //setUpData();

        //System.out.println("Type LIST to list all pets and SEND to email all owners");

        Reminder reminder = new Reminder(fileConfig);

        reminder.emailOwners(2);


    }


}
