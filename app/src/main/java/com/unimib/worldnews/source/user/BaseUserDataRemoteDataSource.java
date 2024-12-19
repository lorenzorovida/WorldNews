package com.unimib.worldnews.source.user;


import com.unimib.worldnews.model.User;
import com.unimib.worldnews.repository.user.UserResponseCallback;

import java.util.Set;

public abstract class BaseUserDataRemoteDataSource {
        protected UserResponseCallback userResponseCallback;

        public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
            this.userResponseCallback = userResponseCallback;
        }

        public abstract void saveUserData(User user);

        public abstract void getUserFavoriteNews(String idToken);

        public abstract void getUserPreferences(String idToken);

        public abstract void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken);
    }

