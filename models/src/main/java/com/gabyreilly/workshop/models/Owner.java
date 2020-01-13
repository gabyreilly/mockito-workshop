package com.gabyreilly.workshop.models;

import java.util.Objects;

public class Owner {
    private final int ownerId;
    private final String ownerName;
    private final String ownerEmail;

    public Owner(int ownerId, String ownerName, String ownerEmail) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, ownerName, ownerEmail);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Owner other = (Owner) obj;
        return Objects.equals(this.ownerId, other.ownerId)
                && Objects.equals(this.ownerName, other.ownerName)
                && Objects.equals(this.ownerEmail, other.ownerEmail);
    }

    @Override
    public String toString() {
        return "Owner{" +
                "ownerId=" + ownerId +
                ", ownerName='" + ownerName + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                '}';
    }
}
