package com.mycompany.servicetime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.mycompany.servicetime.R;
import com.mycompany.servicetime.firebase.FirebaseConstants;
import com.mycompany.servicetime.support.PreferenceSupport;
import com.mycompany.servicetime.ui.login.CreateAccountActivity;
import com.mycompany.servicetime.ui.login.LoginActivity;

/**
 * BaseActivity class is used as a base class for all activities in the app
 * It enable "Logout" in all activities
 * and defines variables that are being shared across all activities
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected String mProvider, mEncodedEmail;
    protected Firebase.AuthStateListener mAuthListener;
    protected Firebase mFirebaseRef;
    protected boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Getting mProvider and mEncodedEmail from SharedPreferences
         */
        mEncodedEmail = PreferenceSupport.getEncodedEmail(BaseActivity.this);
        mProvider = PreferenceSupport.getProvider(BaseActivity.this);

        if (!((this instanceof LoginActivity) || (this instanceof CreateAccountActivity))) {
            mFirebaseRef = new Firebase(FirebaseConstants.FIREBASE_URL);
            mAuthListener = new Firebase.AuthStateListener() {
                @Override
                public void onAuthStateChanged(AuthData authData) {
                     /* The user has been logged out */
                    if (authData == null) {
                        /* Clear out shared preferences */
                        PreferenceSupport.setEncodedEmail(BaseActivity.this, null);
                        PreferenceSupport.setProvider(BaseActivity.this, null);

                        // Set option menu to login or logout
                        isLogin = false;
                    } else {
                        isLogin = true;
                    }
                }
            };
            mFirebaseRef.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Cleanup the AuthStateListener */
        if (!((this instanceof LoginActivity) || (this instanceof CreateAccountActivity))) {
            mFirebaseRef.removeAuthStateListener(mAuthListener);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isLogin)
            menu.add(Menu.NONE, R.id.menu_action_logout, 1000,
                    getResources().getString(R.string.action_logout));
        else
            menu.add(Menu.NONE, R.id.menu_action_login, 1000,
                    getResources().getString(R.string.action_login));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        if (id == R.id.menu_action_logout) {
            logout();
            return true;
        }

        if (id == R.id.menu_action_login) {
            takeUserToLoginScreenOnUnAuth();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Logs out the user from their current session and starts LoginActivity.
     */
    protected void logout() {
        /* Logout if mProvider is not null */
        if (mProvider != null) {
            mFirebaseRef.unauth();
        }

        invalidateOptionsMenu();
    }

    private void takeUserToLoginScreenOnUnAuth() {
        /* Move user to LoginActivity, and remove the backstack */
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
