package com.gabyreilly.workshop.second;

import com.gabyreilly.workshop.models.Owner;
import com.gabyreilly.workshop.models.Pet;

public interface EmailSender {

    /**
     * Send an email to an owner of the pet
     * @param owner the Owner object, not null
     * @param pet the Pet object, not null
     */
    void sendEmail(Owner owner, Pet pet);
}
