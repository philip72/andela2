package com.disruption.travelmantix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    private static final int RC_SIGN_IN = 123;
    private static final String ADMINISTRATORS_PATH = "administrators";
    private static final String DEALS_PICTURES_PATH = "deals_pictures";
    public static FirebaseDatabase sFirebaseDatabase;
    public static DatabaseReference sDatabaseReference;
    static FirebaseStorage sFirebaseStorage;
    static StorageReference sStorageReference;
    private static FirebaseUtil sFirebaseUtil;
    private static FirebaseAuth sFirebaseAuth;
    private static FirebaseAuth.AuthStateListener sAuthStateListener;
    public static ArrayList<TravelDeal> sDeals;
    private static ListActivity sCallerActivity;
    public static boolean sIsUserAdmin;

    private FirebaseUtil() {
    }

    public static void openFirebaseReference(String reference, final ListActivity callerActivity) {
        if (sFirebaseUtil == null) {
            sFirebaseUtil = new FirebaseUtil();
            sFirebaseDatabase = FirebaseDatabase.getInstance();
            sFirebaseAuth = FirebaseAuth.getInstance();
            sCallerActivity = callerActivity;
            sAuthStateListener = firebaseAuth -> {
                if (firebaseAuth.getCurrentUser() == null) {
                    //No one is signed in so show the sign in screen
                    signIn();
                } else {
                    //Check if the user is admin so that we show the add deal menu or not
                    String userId = firebaseAuth.getUid();
                    checkIfUserIsAdmin(userId);
                }
            };
            connectStorage();
        }
        sDeals = new ArrayList<>();
        sDatabaseReference = sFirebaseDatabase.getReference().child(reference);
    }

    private static void checkIfUserIsAdmin(String userId) {
        FirebaseUtil.sIsUserAdmin = false;
        DatabaseReference reference = sFirebaseDatabase.getReference().child(ADMINISTRATORS_PATH)
                .child(userId);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.sIsUserAdmin = true;
                sCallerActivity.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        reference.addChildEventListener(childEventListener);
    }

    private static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        sCallerActivity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.deal_image)      // Set logo drawable
                        .setTheme(R.style.AppTheme)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachListener() {
        sFirebaseAuth.addAuthStateListener(sAuthStateListener);
    }

    public static void detachListener() {
        sFirebaseAuth.removeAuthStateListener(sAuthStateListener);
    }

    private static void connectStorage() {
        sFirebaseStorage = FirebaseStorage.getInstance();
        sStorageReference = sFirebaseStorage.getReference().child(DEALS_PICTURES_PATH);
    }
}
