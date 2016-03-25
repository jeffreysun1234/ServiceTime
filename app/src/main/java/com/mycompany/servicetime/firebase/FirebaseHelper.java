package com.mycompany.servicetime.firebase;

import com.firebase.client.ServerValue;

import java.util.HashMap;

/**
 * Created by szhx on 3/24/2016.
 */
public class FirebaseHelper {

    /**
     * Set raw version of date to the ServerValue.TIMESTAMP value.
     */
    public static HashMap<String, Object> getTimestampNowObject() {
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject
                .put(FirebaseConstants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        return timestampNowObject;
    }
}
