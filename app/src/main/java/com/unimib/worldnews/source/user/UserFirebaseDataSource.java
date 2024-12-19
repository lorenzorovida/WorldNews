package com.unimib.worldnews.source.user;


import static com.unimib.worldnews.util.Constants.*;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.model.User;
import com.unimib.worldnews.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Class that gets the user information using Firebase Realtime Database.
 */
public class UserFirebaseDataSource extends BaseUserDataRemoteDataSource {

    private static final String TAG = UserFirebaseDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private final SharedPreferencesUtils sharedPreferencesUtil;

    public UserFirebaseDataSource(SharedPreferencesUtils sharedPreferencesUtil) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void saveUserData(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "User already present in Firebase Realtime Database");
                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                } else {
                    Log.d(TAG, "User not present in Firebase Realtime Database");
                    databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
            }
        });
    }

    @Override
    public void getUserFavoriteNews(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_FAVORITE_NEWS_COLLECTION).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                        userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                    }
                    else {
                        Log.d(TAG, "Successful read: " + task.getResult().getValue());

                        List<Article> articlesList = new ArrayList<>();
                        for(DataSnapshot ds : task.getResult().getChildren()) {
                            Article article = ds.getValue(Article.class);
                            articlesList.add(article);
                        }

                        userResponseCallback.onSuccessFromRemoteDatabase(articlesList);
                    }
                });
    }

    @Override
    public void getUserPreferences(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(SHARED_PREFERENCES_COUNTRY_OF_INTEREST).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String countryOfInterest = task.getResult().getValue(String.class);
                        sharedPreferencesUtil.writeStringData(
                                SHARED_PREFERENCES_FILENAME,
                                SHARED_PREFERENCES_COUNTRY_OF_INTEREST,
                                countryOfInterest);

                        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                                child(SHARED_PREFERENCES_CATEGORIES_OF_INTEREST).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            List<String> favoriteTopics = new ArrayList<>();
                                            for(DataSnapshot ds : task.getResult().getChildren()) {
                                                String favoriteTopic = ds.getValue(String.class);
                                                favoriteTopics.add(favoriteTopic);
                                            }

                                            if (favoriteTopics.size() > 0) {
                                                Set<String> favoriteNewsSet = new HashSet<>(favoriteTopics);
                                                favoriteNewsSet.addAll(favoriteTopics);

                                                sharedPreferencesUtil.writeStringSetData(
                                                        SHARED_PREFERENCES_FILENAME,
                                                        SHARED_PREFERENCES_CATEGORIES_OF_INTEREST,
                                                        favoriteNewsSet);
                                            }
                                            userResponseCallback.onSuccessFromGettingUserPreferences();
                                        }
                                    }
                                });
                    }
                });
    }

    @Override
    public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {

        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(SHARED_PREFERENCES_COUNTRY_OF_INTEREST).setValue(favoriteCountry);

        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(SHARED_PREFERENCES_CATEGORIES_OF_INTEREST).setValue(new ArrayList<>(favoriteTopics)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i(TAG, "fattoooo");
                    }
                });
    }
}
