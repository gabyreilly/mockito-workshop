package com.gabyreilly.workshop.second;

import com.gabyreilly.workshop.models.Owner;
import com.gabyreilly.workshop.models.Pet;

import java.util.List;
import java.util.Optional;

public interface DataSource {
    Optional<Pet> getPet(int petId);

    List<Owner> getOwnersForPet(int petId);
}
