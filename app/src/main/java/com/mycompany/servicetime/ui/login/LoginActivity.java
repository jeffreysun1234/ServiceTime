package com.mycompany.servicetime.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mycompany.servicetime.R;
import com.mycompany.servicetime.firebase.FirebaseConstants;
import com.mycompany.servicetime.firebase.FirebaseUtils;
import com.mycompany.servicetime.firebase.model.User;
import com.mycompany.servicetime.support.PreferenceSupport;
import com.mycompany.servicetime.ui.BaseActivity;
import com.mycompany.servicetime.ui.MainActivity;

import static com.mycompany.servicetime.util.LogUtils.LOGD;
import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

/**
 * Represents Sign in screen and functionality of the app
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = makeLogTag("LoginActivity");

    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;
    /* References to the Firebase */
    private Firebase mFirebaseRef;
    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    private EditText mEditTextEmailInput, mEditTextPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * Create Firebase references
         */
        mFirebaseRef = new Firebase(FirebaseConstants.FIREBASE_URL);

        /**
         * Link layout elements from XML and setup progress dialog
         */
        initializeScreen();

        /**
         * Call signInPassword() when user taps "Done" keyboard action
         */
        mEditTextPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    signInPassword();
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * This is the authentication listener that maintains the current user session
         * and signs in automatically on application launch
         */
        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthProgressDialog.dismiss();

                /**
                 * If there is a valid session to be restored, start MainActivity.
                 * No need to pass data via SharedPreferences because app
                 * already holds userName/provider data from the latest session
                 */
                if (authData != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
        /* Add auth listener to Firebase ref */
        mFirebaseRef.addAuthStateListener(mAuthStateListener);

        /**
         * Get the newly registered user email if present, use null as default value
         */
        String signupEmail = PreferenceSupport.getSignupEmail(LoginActivity.this);

        /**
         * Fill in the email editText and remove value from SharedPreferences if email is present
         */
        if (signupEmail != null) {
            mEditTextEmailInput.setText(signupEmail);

            /**
             * Clear signupEmail sharedPreferences to make sure that they are used just once
             */
            PreferenceSupport.setSignupEmail(LoginActivity.this, null);
        }
    }

    /**
     * Cleans up listeners tied to the user's authentication state
     */
    @Override
    public void onPause() {
        super.onPause();
        mFirebaseRef.removeAuthStateListener(mAuthStateListener);
    }

    /**
     * Override onCreateOptionsMenu to inflate nothing
     *
     * @param menu The menu with which nothing will happen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    /**
     * Sign in with Password provider when user clicks sign in button
     */
    public void onSignInPressed(View view) {
        signInPassword();
    }

    /**
     * Open CreateAccountActivity when user taps on "Sign up" TextView
     */
    public void onSignUpPressed(View view) {
        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }

    /**
     * Link layout elements from XML and setup the progress dialog
     */
    public void initializeScreen() {
        mEditTextEmailInput = (EditText) findViewById(R.id.edit_text_email);
        mEditTextPasswordInput = (EditText) findViewById(R.id.edit_text_password);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog
                .setMessage(getString(R.string.progress_dialog_authenticating_with_firebase));
        mAuthProgressDialog.setCancelable(false);
    }

    /**
     * Sign in with Password provider (used when user taps "Done" action on keyboard)
     */
    public void signInPassword() {
        String email = mEditTextEmailInput.getText().toString();
        String password = mEditTextPasswordInput.getText().toString();

        /**
         * If email and password are not empty show progress dialog and try to authenticate
         */
        if (email.equals("")) {
            mEditTextEmailInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }

        if (password.equals("")) {
            mEditTextPasswordInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }
        mAuthProgressDialog.show();
        mFirebaseRef.authWithPassword(email, password,
                new MyAuthResultHandler(FirebaseConstants.PASSWORD_PROVIDER));
    }

    /**
     * Handle user authentication that was initiated with mFirebaseRef.authWithPassword
     */
    private class MyAuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public MyAuthResultHandler(String provider) {
            this.provider = provider;
        }

        /**
         * On successful authentication call setAuthenticatedUser if it was not already
         * called in
         */
        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.dismiss();
            LOGD(TAG, provider + " " + getString(R.string.log_message_auth_successful));

            if (authData != null) {
                /**
                 * If user has logged in with Google provider
                 */
                if (authData.getProvider().equals(FirebaseConstants.PASSWORD_PROVIDER)) {
                    setAuthenticatedUserPasswordProvider(authData);
                } else {
                    LOGD(TAG, getString(R.string.log_error_invalid_provider) +
                            authData.getProvider());
                }

                /* Save provider name and encodedEmail for later use and start MainActivity */
                PreferenceSupport.setProvider(LoginActivity.this, authData.getProvider());
                PreferenceSupport.setEncodedEmail(LoginActivity.this, mEncodedEmail);
                PreferenceSupport.setAuthToken(LoginActivity.this, authData.getToken());

                /* Go to main activity */
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.dismiss();

            /**
             * Use utility method to check the network connection state
             * Show "No network connection" if there is no connection
             * Show Firebase specific error message otherwise
             */
            switch (firebaseError.getCode()) {
                case FirebaseError.INVALID_EMAIL:
                case FirebaseError.USER_DOES_NOT_EXIST:
                    mEditTextEmailInput.setError(getString(R.string.error_message_email_issue));
                    break;
                case FirebaseError.INVALID_PASSWORD:
                    mEditTextPasswordInput.setError(firebaseError.getMessage());
                    break;
                case FirebaseError.NETWORK_ERROR:
                    showErrorToast(getString(R.string.error_message_failed_sign_in_no_network));
                    break;
                default:
                    showErrorToast(firebaseError.toString());
            }
        }
    }

    /**
     * Helper method that makes sure a user is created if the user
     * logs in with Firebase's email/password provider.
     *
     * @param authData AuthData object returned from onAuthenticated
     */
    private void setAuthenticatedUserPasswordProvider(AuthData authData) {
        final String unprocessedEmail = authData.getProviderData()
                .get(FirebaseConstants.FIREBASE_PROPERTY_EMAIL).toString().toLowerCase();
        /**
         * Encode user email replacing "." with ","
         * to be able to use it as a Firebase db key
         */
        mEncodedEmail = FirebaseUtils.encodeEmail(unprocessedEmail);

        final Firebase userRef = new Firebase(FirebaseConstants.FIREBASE_URL_USERS)
                .child(mEncodedEmail);

        /**
         * Check if current user has logged in at least once
         */
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {

                    /**
                     * If recently registered user has hasLoggedInWithPassword = "false"
                     * (never logged in using password provider)
                     */
                    if (!user.isHasLoggedInWithPassword()) {

                        /**
                         * Change password if user that just signed in signed up recently
                         * to make sure that user will be able to use temporary password
                         * from the email more than 24 hours
                         */
                        mFirebaseRef.changePassword(unprocessedEmail,
                                mEditTextPasswordInput.getText().toString(),
                                mEditTextPasswordInput.getText().toString(),
                                new Firebase.ResultHandler() {
                                    @Override
                                    public void onSuccess() {
                                        userRef.child(
                                                FirebaseConstants
                                                        .FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD)
                                                .setValue(true);
                                        /* The password was changed */
                                        LOGD(TAG, getString(
                                                R.string.log_message_password_changed_successfully) +
                                                mEditTextPasswordInput.getText().toString());
                                    }

                                    @Override
                                    public void onError(FirebaseError firebaseError) {
                                        LOGD(TAG, getString(
                                                R.string.log_error_failed_to_change_password) +
                                                firebaseError);
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                LOGD(TAG, getString(R.string.log_error_the_read_failed) +
                        firebaseError.getMessage());
            }
        });

    }

    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

}
