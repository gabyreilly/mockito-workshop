# Mockito Workshop

This repo will be used for our Mockito Workshop, see full instructions at:

https://gabyreilly.github.io/mockito

In this repo, the `first` module is our starting point. 
 It includes a program which could be used by a vet clinic to send 
 reminder emails to pet owners.
 
 The main() method in App requires a resource file `secrets.properties` which is not provided in GitHub, so you
 will not be able to run the App right away.
 
 We will modify the `first` module during the workshop.  We will refactor the 
 Reminder class using the principle of dependency injection.
 
 Then, after refactoring, we will use Mockito to write unit tests for the 
 Reminder class.
 
 The `second` module is a reference module for what our code might look like 
 at the end of the workshop.  
 
 The `models` module contains the Pet and Owner models, which are shared between
 `first` and `second`, and do not need to be modified during the workshop.