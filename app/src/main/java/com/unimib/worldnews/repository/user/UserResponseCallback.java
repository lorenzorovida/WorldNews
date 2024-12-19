package com.unimib.worldnews.repository.user;

import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.model.User;

import java.util.List;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessFromRemoteDatabase(List<Article> articlesList);
    void onSuccessFromGettingUserPreferences();
    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();
}
