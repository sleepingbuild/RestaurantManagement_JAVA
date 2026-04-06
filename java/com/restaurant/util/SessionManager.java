package com.restaurant.util;

import com.restaurant.model.User;

public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isAdminLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}