package com.gabyreilly.workshop.second;

import com.gabyreilly.workshop.models.Owner;
import com.gabyreilly.workshop.models.Pet;

import java.util.List;
import java.util.Optional;

public interface DataSource {
    /**
     * Return the Pet information for a given Pet ID
     * @param petId int of the pet's ID
     * @return Optional of Pet object if found, Optional.empty if not found
     */
    Optional<Pet> getPet(int petId);


    /**
     * Given a petId, return all the Owners of the pet.
     * @param petId int of the pet's ID
     * @return The list may be empty, or it may contain any number of owners.
     */
    List<Owner> getOwnersForPet(int petId);
}
