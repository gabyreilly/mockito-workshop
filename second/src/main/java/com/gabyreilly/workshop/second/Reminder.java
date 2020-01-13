package com.gabyreilly.workshop.second;

import com.gabyreilly.workshop.models.Owner;
import com.gabyreilly.workshop.models.Pet;

import java.util.List;
import java.util.Optional;

public class Reminder {

    private final DataSource dataSource;
    private final EmailSender emailSender;

    public Reminder(DataSource dataSource, EmailSender emailSender) {
        this.dataSource = dataSource;
        this.emailSender = emailSender;
    }

    public void emailOwners(int petId) {
        //Retrieve pet information
        Optional<Pet> matchingPet = dataSource.getPet(petId);

        if (matchingPet.isPresent()) {
            List<Owner> matchingOwners = dataSource.getOwnersForPet(petId);

            //Send email to each owner of the pet
            for (Owner owner : matchingOwners) {
                emailSender.sendEmail(owner, matchingPet.get());
            }
        }
        //TODO: what do we do if the pet is not found?
    }

}


