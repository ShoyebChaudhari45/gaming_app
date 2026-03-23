package com.example.gameapp.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.session.SessionManager;

/**
 * Base Activity for handling role-based navigation.
 * All activities that need to redirect to home based on user type should extend this.
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Navigate to the appropriate home screen based on user type.
     * Call this when you want to go "home" from any screen.
     */
    protected void navigateToHome() {
        Intent intent;
        if (SessionManager.isEmployee(this)) {
            intent = new Intent(this, EmployeeHomeActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }

        // Clear all activities in the stack and start fresh
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Check if user type matches the required type for this screen.
     * Returns true if access is allowed, false otherwise.
     */
    protected boolean checkUserTypeAccess(boolean requireEmployee) {
        boolean isEmployee = SessionManager.isEmployee(this);
        return requireEmployee == isEmployee;
    }

    /**
     * Redirect to appropriate home if user type doesn't match.
     * Call this in onCreate() or onResume() to protect screens.
     */
    protected void enforceUserTypeAccess(boolean requireEmployee) {
        if (!checkUserTypeAccess(requireEmployee)) {
            navigateToHome();
        }
    }
}