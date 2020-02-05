package com.gabyreilly.workshop.second;

import com.gabyreilly.workshop.models.Owner;
import com.gabyreilly.workshop.models.Pet;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;


public class ReminderReferenceTest {

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

    @After
    public void tearDown() throws Exception {
        //Ensure we have verified every expected interaction -- no surprises should show up!
        Mockito.verifyNoMoreInteractions(mockDataSource, mockEmailSender);
    }


    @Test
    public void testOneOwner() throws Exception {
        //Step 1: Set up mocks

        //We can use a fake Pet and a pet Owner to train the mocks.  Save them in variables for later use
        Pet fakePet = new Pet(1, "something fake");
        Owner fakeOwner = new Owner(1, "fake name", "fake email");

        //Train the mock DataSource to return the Pet object when we query for ID 1
        Mockito.when(mockDataSource.getPet(1)).thenReturn(Optional.of(fakePet));

        //Train the mock DataSource to return the Owner object when we query for pet ID 1
        Mockito.when(mockDataSource.getOwnersForPet(1)).thenReturn(Lists.newArrayList(fakeOwner));

        //The email sender does not need to be trained -- we only need to train methods that return a value.
        // Void methods can stay untrained.

        //Step 2: Exercise the Reminder class
        reminder.emailOwners(1);

        //Step 3: Verify all expected interactions
        Mockito.verify(mockDataSource).getPet(1);
        Mockito.verify(mockDataSource).getOwnersForPet(1);

        Mockito.verify(mockEmailSender).sendEmail(ArgumentMatchers.eq(fakeOwner), ArgumentMatchers.eq(fakePet));
    }

    @Test
    public void testOneOwnerWithCapture() throws Exception {
        //Step 1: Set up mocks
        Pet fakePet = new Pet(1, "something fake");
        Owner fakeOwner = new Owner(1, "fake name", "fake email");

        //Train the mock DataSource to return the Pet object when we query for ID 1
        Mockito.when(mockDataSource.getPet(1)).thenReturn(Optional.of(fakePet));

        Mockito.when(mockDataSource.getOwnersForPet(1)).thenReturn(Lists.newArrayList(fakeOwner));

        //Step 2: Exercise the Reminder class
        reminder.emailOwners(1);

        //Use ArgumentCaptor on the Verify call.
        // Mockito will load the actual parameters into the ArgumentCaptor variables


        //Step 3: Verify all expected interactions
        Mockito.verify(mockDataSource).getPet(1);
        Mockito.verify(mockDataSource).getOwnersForPet(1);

        ArgumentCaptor<Owner> capturedOwner = ArgumentCaptor.forClass(Owner.class);
        ArgumentCaptor<Pet> capturedPet = ArgumentCaptor.forClass(Pet.class);

        Mockito.verify(mockEmailSender).sendEmail(capturedOwner.capture(), capturedPet.capture());

        //Use the captured values to make JUnit assertions
        assertEquals(fakeOwner.getOwnerName(), capturedOwner.getValue().getOwnerName());
        assertEquals(fakePet.getPetName(), capturedPet.getValue().getPetName());
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

        ArgumentCaptor<Owner> capturedOwner = ArgumentCaptor.forClass(Owner.class);
        ArgumentCaptor<Pet> capturedPet = ArgumentCaptor.forClass(Pet.class);

        //Note the change here!  Verify takes a parameter, times(2) to indicate the method is called twice
        Mockito.verify(mockEmailSender, Mockito.times(2)).sendEmail(capturedOwner.capture(), capturedPet.capture());

        //The captors grabbed each *set* of parameters for the two method calls, so instead of getValue() use getAllValues()
        List<Owner> ownerParameters = capturedOwner.getAllValues();
        List<Pet> petParameters = capturedPet.getAllValues();

        //The first index is the first method call, and the second index is the second method call
        assertEquals(2, ownerParameters.size());
        assertEquals(2, petParameters.size());

        //Use the captured values to make JUnit assertions

        //First method call
        assertEquals(ownerOne, ownerParameters.get(0));
        assertEquals(fakePet, petParameters.get(0));

        //Second method call
        assertEquals(ownerTwo, ownerParameters.get(1));
        assertEquals(fakePet, petParameters.get(1));
    }

    @Test
    public void testZeroOwners() throws Exception {
        //Step 1: Set up mocks
        Pet fakePet = new Pet(1, "something fake");

        //Train the mock DataSource to return the Pet object when we query for ID 1
        Mockito.when(mockDataSource.getPet(1)).thenReturn(Optional.of(fakePet));

        //Empty list for owners
        Mockito.when(mockDataSource.getOwnersForPet(1)).thenReturn(Lists.newArrayList());

        //Step 2: Exercise the Reminder class
        reminder.emailOwners(1);

        //Use ArgumentCaptor on the Verify call.
        // Mockito will load the actual parameters into the ArgumentCaptor variables


        //Step 3: Verify all expected interactions
        Mockito.verify(mockDataSource).getPet(1);
        Mockito.verify(mockDataSource).getOwnersForPet(1);

        //We should send zero emails in this case
        Mockito.verifyZeroInteractions(mockEmailSender);
    }



    @Test
    public void testPetNotFound() throws Exception {
        //Step 1: Set up mocks

        //Train the mock DataSource to return empty for ID 1
        Mockito.when(mockDataSource.getPet(1)).thenReturn(Optional.empty());

        //Step 2: Exercise the Reminder class
        reminder.emailOwners(1);

        //Use ArgumentCaptor on the Verify call.
        // Mockito will load the actual parameters into the ArgumentCaptor variables


        //Step 3: Verify all expected interactions
        Mockito.verify(mockDataSource).getPet(1);

        //Our code does not getOwners if the pet is not found
        Mockito.verifyNoMoreInteractions(mockDataSource);

        //We should send zero emails in this case
        Mockito.verifyZeroInteractions(mockEmailSender);
    }
}
