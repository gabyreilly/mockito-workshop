package com.gabyreilly.workshop.second;

import com.gabyreilly.workshop.models.Owner;
import com.gabyreilly.workshop.models.Pet;

public interface EmailSender {
    void sendEmail(Owner owner, Pet pet);
}
