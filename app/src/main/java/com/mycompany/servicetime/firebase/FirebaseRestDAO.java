package com.mycompany.servicetime.firebase;

import android.database.Cursor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.servicetime.CHApplication;
import com.mycompany.servicetime.firebase.model.TimeSlotItem;
import com.mycompany.servicetime.firebase.model.TimeSlotList;
import com.mycompany.servicetime.provider.CHServiceTimeDAO;
import com.mycompany.servicetime.provider.ColumnIndexCache;
import com.mycompany.servicetime.util.ModelConverter;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.mycompany.servicetime.util.LogUtils.LOGD;
import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

/**
 * Created by szhx on 3/24/2016.
 */
public class FirebaseRestDAO {
    public static final String TAG = makeLogTag(FirebaseRestDAO.class);

    private static FirebaseEndpointInterface mService;

    private FirebaseRestDAO() {
        if (mService == null) {
            /* build a retrofit instance */
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FirebaseConstants.FIREBASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();

            /* get the interface of restful service */
            mService = retrofit.create(FirebaseEndpointInterface.class);
        }
    }

    public static FirebaseRestDAO create() {
        return new FirebaseRestDAO();
    }

    /**
     * Add new TimeSlot list
     */
    public void addTimeSlotList() {
        /* build a TimeSlot list */
        TimeSlotList newTimeSlotList1 = new TimeSlotList("Temp Name", "owner@gmail.com",
                FirebaseUtils.getTimestampNowObject());
        HashMap<String, Object> shoppingListMap1 = (HashMap<String, Object>)
                new ObjectMapper().convertValue(newTimeSlotList1, Map.class);

        /* access firebase database */
        Call<String> message = mService.addTimeSlotList(shoppingListMap1);
        message.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    LOGD(TAG, response.body());

                } else {
                    // error response, no access to resource?
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * Backup TimeSlot list
     */
    public void backupTimeSlotList() {
        // Get all TimeSlot from DB
        Cursor cursor = CHServiceTimeDAO.create(CHApplication.getContext()).getAllTimeSlot();
        if (cursor == null)
            return;

        ColumnIndexCache columnIndexCache = new ColumnIndexCache();
        TimeSlotItem timeSlotItem;
        HashMap<String, Object> timeSlotItemMap;

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            // convert cursor to TimeSlotItem model
            timeSlotItem = ModelConverter.cursorToTimeSlotItem(cursor, columnIndexCache);

            // convert TimeSlotItem model to HashMap object
            timeSlotItemMap = (HashMap<String, Object>)
                    new ObjectMapper().convertValue(timeSlotItem, Map.class);

            // save to Firebase
            Call<String> message = mService.addTimeSlotItemList("-KDeiOhCyfYj2PHzfI1e",
                    timeSlotItemMap);
            message.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        LOGD(TAG, response.body());

                    } else {
                        // error response, no access to resource?
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }

        cursor.close();
    }
}
