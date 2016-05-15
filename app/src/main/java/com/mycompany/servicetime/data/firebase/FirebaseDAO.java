package com.mycompany.servicetime.data.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mycompany.servicetime.data.firebase.model.TimeSlotList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by szhx on 3/24/2016.
 */

/**
 * This class is not used.
 */
public class FirebaseDAO {

    /**
     * Add new TimeSlot list
     */
    public static void addTimeSlotList() {
        // Create Firebase references
        final Firebase firebaseRef = new Firebase(FirebaseConstants.FIREBASE_URL);
        Firebase TimeSlotListsRef = new Firebase(FirebaseConstants.FIREBASE_URL_TIMESLOT_LISTS);

        Firebase newListRef = TimeSlotListsRef.push();

        /* Save listsRef.push() to maintain same random Id */
        final String listId = newListRef.getKey();

        /* Build the TimeSlot list */
        TimeSlotList newTimeSlotList1 = new TimeSlotList("Temp Name", "owner@gmail.com",
                FirebaseUtils.getTimestampNowObject());

        TimeSlotList newTimeSlotList2 = new TimeSlotList("Big Name", "myself@gmail.com",
                FirebaseUtils.getTimestampNowObject());

        HashMap<String, Object> shoppingListMap1 = (HashMap<String, Object>)
                new ObjectMapper().convertValue(newTimeSlotList1, Map.class);
        HashMap<String, Object> shoppingListMap2 = (HashMap<String, Object>)
                new ObjectMapper().convertValue(newTimeSlotList2, Map.class);

        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
        arrayList.add(shoppingListMap1);
        arrayList.add(shoppingListMap2);


        // Save to Firebase
        TimeSlotListsRef.push().setValue(arrayList, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                //TODO: update the relationship with users.
            }
        });
    }

}
