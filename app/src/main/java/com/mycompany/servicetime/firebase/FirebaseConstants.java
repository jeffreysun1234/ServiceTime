package com.mycompany.servicetime.firebase;

import com.mycompany.servicetime.BuildConfig;

/**
 * Created by szhx on 3/23/2016.
 */
public final class FirebaseConstants {
    /**
     * Constants related to locations in Firebase, such as the name of the node
     * where user lists are stored (ie "userLists")
     */
    public static final String FIREBASE_LOCATION_TIMESLOT_ITEMS = "timeSlotItems";
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_TIMESLOT_LISTS = "timeSlotLists";
    public static final String FIREBASE_LOCATION_USER_LISTS = "userLists";
    public static final String FIREBASE_LOCATION_USER_FRIENDS = "userFriends";
    public static final String FIREBASE_LOCATION_LISTS_SHARED_WITH = "sharedWith";
    public static final String FIREBASE_LOCATION_UID_MAPPINGS = "uidMappings";
    public static final String FIREBASE_LOCATION_OWNER_MAPPINGS = "ownerMappings";
    //TODO: a temporary name
    public static final String FIREBASE_LOCATION_DEVICE_ID = "phoneIMEI";

    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_PROPERTY_BOUGHT = "bought";
    public static final String FIREBASE_PROPERTY_BOUGHT_BY = "boughtBy";
    public static final String FIREBASE_PROPERTY_LIST_NAME = "listName";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "timestampLastChanged";
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_ITEM_NAME = "itemName";
    public static final String FIREBASE_PROPERTY_EMAIL = "email";
    public static final String FIREBASE_PROPERTY_USERS_SHOPPING = "usersShopping";
    public static final String FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD =
            "hasLoggedInWithPassword";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED_REVERSE =
            "timestampLastChangedReverse";


    /**
     * Constants for Firebase URL
     */
    public static final String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_USER_LISTS =
            FIREBASE_URL + "/" + FIREBASE_LOCATION_USER_LISTS;
    public static final String FIREBASE_URL_USER_FRIENDS =
            FIREBASE_URL + "/" + FIREBASE_LOCATION_USER_FRIENDS;
    public static final String FIREBASE_URL_LISTS_SHARED_WITH =
            FIREBASE_URL + "/" + FIREBASE_LOCATION_LISTS_SHARED_WITH;
    public static final String FIREBASE_URL_TIMESLOT_LISTS =
            FIREBASE_URL + FIREBASE_LOCATION_TIMESLOT_LISTS;

    /**
     * Constants for Firebase login
     */
    public static final String PASSWORD_PROVIDER = "password";
    public static final String GOOGLE_PROVIDER = "google";
    public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";

    /**
     * Constants for the replacement variable in URL
     */
    public static final String PATH_UNIIQUE_ID = "uniqueId";
    public static final String PATH_USER_EMAIL = "userEmail";
}
