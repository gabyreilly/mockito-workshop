package com.gabyreilly.workshop.second;

import com.gabyreilly.workshop.models.Owner;
import com.gabyreilly.workshop.models.Pet;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.Assert.*;

public class ReminderTest {


    @Mock
    DataSource mockDataSource;

    @Mock
    EmailSender mockEmailSender;

    Reminder reminder;

    @Before
    public void setUp() throws Exception {

        //This line initializes every variable with the @Mock annotation above
        MockitoAnnotations.initMocks(this);

        reminder = new Reminder(mockDataSource, mockEmailSender);

    }

    @Test
    public void testTwoOwners() throws Exception {
        //Step 1: Set up mocks
        Pet fakePet = new Pet(1, "something fake");
        Owner ownerOne = new Owner(1, "fake name1", "fake email1");
        Owner ownerTwo = new Owner(2, "fake name2", "fake email2");

        //Train the mock DataSource to return the Pet object when we query for ID 1
        Mockito.when(mockDataSource.getPet(1)).thenReturn(Optional.of(fakePet));

        Mockito.when(mockDataSource.getOwnersForPet(1)).thenReturn(Lists.newArrayList(ownerOne, ownerTwo));

        //Step 2: Exercise the Reminder class
        reminder.emailOwners(1);

        //Use ArgumentCaptor on the Verify call.
        // Mockito will load the actual parameters into the ArgumentCaptor variables


        //Step 3: Verify all expected interactions
        Mockito.verify(mockDataSource).getPet(1);
        Mockito.verify(mockDataSource).getOwnersForPet(1);

        // Verify takes a parameter, times(2) to indicate the method is called twice
        // For this test, we are accepting "Any" Owner, and only the Pet matching fakePet.
        // See the ReminderReferenceTest for ways to expand these verifications & see the actual Owners
        Mockito.verify(mockEmailSender, Mockito.times(2)).sendEmail(ArgumentMatchers.any(), ArgumentMatchers.eq(fakePet));
    }

}