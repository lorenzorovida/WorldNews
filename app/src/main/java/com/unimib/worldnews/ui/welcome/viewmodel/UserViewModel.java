package com.unimib.worldnews.ui.welcome.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.worldnews.model.Result;
import com.unimib.worldnews.model.User;
import com.unimib.worldnews.repository.user.IUserRepository;

import java.util.Set;


public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> userFavoriteNewsMutableLiveData;
    private MutableLiveData<Result> userPreferencesMutableLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
    }

    public MutableLiveData<Result> getUserMutableLiveData(
            String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            getUserData(email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token) {
        if (userMutableLiveData == null) {
            getUserData(token);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getUserFavoriteNewsMutableLiveData(String idToken) {
        if (userFavoriteNewsMutableLiveData == null) {
            getUserFavoriteNews(idToken);
        }
        return userFavoriteNewsMutableLiveData;
    }

    public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {
        if (idToken != null) {
            userRepository.saveUserPreferences(favoriteCountry, favoriteTopics, idToken);
        }
    }

    public MutableLiveData<Result> getUserPreferences(String idToken) {
        if (idToken != null) {
            userPreferencesMutableLiveData = userRepository.getUserPreferences(idToken);
        }
        return userPreferencesMutableLiveData;
    }

    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

        return userMutableLiveData;
    }

    private void getUserFavoriteNews(String idToken) {
        userFavoriteNewsMutableLiveData = userRepository.getUserFavoriteNews(idToken);
    }

    public void getUser(String email, String password, boolean isUserRegistered) {
        userRepository.getUser(email, password, isUserRegistered);
    }

    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    private void getUserData(String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(email, password, isUserRegistered);
    }

    private void getUserData(String token) {
        userMutableLiveData = userRepository.getGoogleUser(token);
    }
}
