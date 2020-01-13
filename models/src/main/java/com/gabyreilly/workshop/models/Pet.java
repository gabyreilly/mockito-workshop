package com.gabyreilly.workshop.models;

import java.util.Objects;

public class Pet {
    private final int petId;
    private final String petName;

    public Pet(int petId, String petName) {
        this.petId = petId;
        this.petName = petName;
    }

    public int getPetId() {
        return petId;
    }

    public String getPetName() {
        return petName;
    }


    @Override
    public int hashCode() {
        return Objects.hash(petId, petName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Pet other = (Pet) obj;
        return Objects.equals(this.petId, other.petId)
                && Objects.equals(this.petName, other.petName);
    }

    @Override
    public String toString() {
        return "Pet{" +
                "petId=" + petId +
                ", petName='" + petName + '\'' +
                '}';
    }
}
