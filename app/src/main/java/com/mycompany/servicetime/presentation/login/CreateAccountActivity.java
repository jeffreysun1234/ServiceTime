package com.mycompany.servicetime.presentation.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mycompany.servicetime.R;
import com.mycompany.servicetime.base.presentation.BaseActivity;
import com.mycompany.servicetime.data.firebase.FirebaseConstants;
import com.mycompany.servicetime.data.firebase.FirebaseUtils;
import com.mycompany.servicetime.data.firebase.model.User;
import com.mycompany.servicetime.support.PreferenceSupport;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import static com.mycompany.servicetime.util.LogUtils.LOGD;
import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

/**
 * Represents Sign up screen and functionality of the app
 */
public class CreateAccountActivity extends BaseActivity {
    private static final String TAG = makeLogTag("CreateAccountActivity");

    private ProgressDialog mAuthProgressDialog;
    private Firebase mFirebaseRef;
    private EditText mEditTextUsernameCreate, mEditTextEmailCreate, mEditTextPasswordCreate;
    private String mUserName, mUserEmail, mPassword;
    private SecureRandom mRandom = new SecureRandom();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        /**
         * Create Firebase references
         */
        mFirebaseRef = new Firebase(FirebaseConstants.FIREBASE_URL);

        /**
         * Link layout elements from XML and setup the progress dialog
         */
        initializeScreen();
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
     * Link layout elements from XML and setup the progress dialog
     */
    public void initializeScreen() {
        mEditTextUsernameCreate = (EditText) findViewById(R.id.edit_text_username_create);
        mEditTextEmailCreate = (EditText) findViewById(R.id.edit_text_email_create);
        mEditTextPasswordCreate = (EditText) findViewById(R.id.edit_text_password_create);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAuthProgressDialog
                .setMessage(getResources().getString(R.string.progress_dialog_creating_user_with_firebase));
        mAuthProgressDialog.setCancelable(false);
    }

    /**
     * Open LoginActivity when user taps on "Sign in" textView
     */
    public void onSignInPressed(View view) {
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Create new account using Firebase email/password provider
     */
    public void onCreateAccountPressed(View view) {
        mUserName = mEditTextUsernameCreate.getText().toString();
        mUserEmail = mEditTextEmailCreate.getText().toString().toLowerCase();
        mPassword = mEditTextPasswordCreate.getText().toString();

        /**
         * Check that email and user name and password are okay
         */
        boolean validEmail = isEmailValid(mUserEmail);
        boolean validUserName = isUserNameValid(mUserName);
        boolean validPassword = isPasswordValid(mPassword);
        if (!validEmail || !validUserName || !validPassword)
            return;

        /**
         * If everything was valid show the progress dialog to indicate that
         * account creation has started
         */
        mAuthProgressDialog.show();

        /**
         * Create new user with specified email and password
         */
        mFirebaseRef.createUser(mUserEmail, mPassword,
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(final Map<String, Object> result) {
                        LOGD(TAG,
                                "Successfully created user account with uid: " + result.get("uid"));

                        mAuthProgressDialog.dismiss();

                        /**
                         * Save name and email to sharedPreferences to create User database record
                         * when the registered user will sign in for the first time
                         */
                        PreferenceSupport.setSignupEmail(CreateAccountActivity.this, mUserEmail);

                        /**
                         * Encode user email replacing "." with ","
                         * to be able to use it as a Firebase db key
                         */
                        createUserInFirebaseHelper((String) result.get("uid"));

                        finish();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        /* Error occurred, log the error and dismiss the progress dialog */
                        LOGD(TAG, getString(R.string.log_error_occurred) + firebaseError);
                        mAuthProgressDialog.dismiss();
                        /* Display the appropriate error message */
                        if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
                            mEditTextEmailCreate.setError(getString(R.string.error_email_taken));
                            showErrorToast(getString(R.string.error_email_taken));
                        } else {
                            showErrorToast(firebaseError.getMessage());
                        }

                    }
                });

    }

    /**
     * Creates a new user in Firebase from the Java POJO
     */
    private void createUserInFirebaseHelper(final String authUserId) {
        final String encodedEmail = FirebaseUtils.encodeEmail(mUserEmail);

        /**
         * Create the user and uid mapping
         */
        HashMap<String, Object> userAndUidMapping = new HashMap<String, Object>();

        /* Set raw version of date to the ServerValue.TIMESTAMP value and save into
        dateCreatedMap */
        HashMap<String, Object> timestampJoined = FirebaseUtils.getTimestampNowObject();

        /* Create a HashMap version of the user to add */
        User newUser = new User(mUserName, encodedEmail, timestampJoined);
        HashMap<String, Object> newUserMap = (HashMap<String, Object>)
                new ObjectMapper().convertValue(newUser, Map.class);

        /* Add the user and UID to the update map */
        userAndUidMapping.put("/" + FirebaseConstants.FIREBASE_LOCATION_USERS + "/" + encodedEmail,
                newUserMap);
        userAndUidMapping.put("/" + FirebaseConstants.FIREBASE_LOCATION_UID_MAPPINGS + "/"
                + authUserId, encodedEmail);

        /* Try to update the database; if there is already a user, this will fail */
        mFirebaseRef.updateChildren(userAndUidMapping, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    /* Try just making a uid mapping */
                    mFirebaseRef.child(FirebaseConstants.FIREBASE_LOCATION_UID_MAPPINGS)
                            .child(authUserId).setValue(encodedEmail);
                }
                /**
                 *  The value has been set or it failed; either way, log out the user since
                 *  they were only logged in with a temp password
                 **/
                mFirebaseRef.unauth();
            }
        });
    }

    private boolean isEmailValid(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            mEditTextEmailCreate
                    .setError(String.format(getString(R.string.error_invalid_email_not_valid),
                            email));
            return false;
        }
        return isGoodEmail;
    }

    private boolean isUserNameValid(String userName) {
        if (userName.equals("")) {
            mEditTextUsernameCreate
                    .setError(getResources().getString(R.string.error_cannot_be_empty));
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password.equals("")) {
            mEditTextPasswordCreate
                    .setError(getResources().getString(R.string.error_cannot_be_empty));
            return false;
        }
        return true;
    }

    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(CreateAccountActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
