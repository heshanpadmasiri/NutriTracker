package com.app.nutritracker.nutritracker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationService {
    public static FirebaseUser getUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
