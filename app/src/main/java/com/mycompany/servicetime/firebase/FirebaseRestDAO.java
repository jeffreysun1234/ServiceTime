package com.mycompany.servicetime.firebase;

import android.content.Context;
import android.database.Cursor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.servicetime.CHApplication;
import com.mycompany.servicetime.firebase.model.TimeSlotItem;
import com.mycompany.servicetime.firebase.model.TimeSlotList;
import com.mycompany.servicetime.provider.CHServiceTimeDAO;
import com.mycompany.servicetime.provider.ColumnIndexCache;
import com.mycompany.servicetime.support.PreferenceSupport;
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

    private static Context mContext;

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

        if (mContext == null)
            mContext = CHApplication.getContext();
    }

    public static FirebaseRestDAO create() {
        return new FirebaseRestDAO();
    }

    /**
     * Add new TimeSlot list
     */
    public void addTimeSlotList(String userEmail) {
        /* build a TimeSlot list */
        TimeSlotList newTimeSlotList = new TimeSlotList("My List", userEmail,
                FirebaseUtils.getTimestampNowObject());
        HashMap<String, Object> timeSlotListMap = (HashMap<String, Object>)
                new ObjectMapper().convertValue(newTimeSlotList, Map.class);

        /* access firebase database */
        Call<String> message = mService.addTimeSlotList(userEmail, timeSlotListMap,
                PreferenceSupport.getAuthToken(mContext));
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
    public void backupTimeSlotItemList() {
        // Get all TimeSlot from DB
        Cursor cursor = CHServiceTimeDAO.create(mContext).getAllTimeSlot();
        if (cursor == null)
            return;

        String encodedEmail = PreferenceSupport.getEncodedEmail(mContext);

        // add a TimeSlotList to Firebase
        addTimeSlotList(encodedEmail);

        // clear TimeSlotItems on Firebase
        if (cursor.getCount() > 0) {
            Call<String> message = mService.deleteTimeSlotItems(encodedEmail,
                    PreferenceSupport.getAuthToken(mContext));
            message.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        LOGD(TAG, "successful clear TimeSlotItems on Firebase.");

                    } else {
                        // error response, no access to resource?
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }

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
            Call<String> message = mService.addTimeSlotItemList(encodedEmail, timeSlotItemMap,
                    PreferenceSupport.getAuthToken(mContext));
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

    /**
     * restore TimeSlot list
     */
    public void restoreTimeSlotItemList() {
        String encodedEmail = PreferenceSupport.getEncodedEmail(mContext);

        Call<HashMap<String, TimeSlotItem>> message = mService
                .getTimeSlotItemList(encodedEmail, PreferenceSupport.getAuthToken(mContext));
        message.enqueue(new Callback<HashMap<String, TimeSlotItem>>() {
            @Override
            public void onResponse(Call<HashMap<String, TimeSlotItem>> call,
                                   Response<HashMap<String, TimeSlotItem>> response) {
                LOGD(TAG, "response body: " + response.body());

                if (response.isSuccessful()) {
                    HashMap<String, TimeSlotItem> body = response.body();
                    if (body != null && body.values().size() > 0) {
                        String currentTimeSlotId;
                        CHServiceTimeDAO dao = CHServiceTimeDAO.create(mContext);
                        // clear DB
                        dao.deleteAllTimeSlot();
                        for (TimeSlotItem tsItem : body.values()) {
                            // add a timeslot, timeSlotId will be a new value.
                            currentTimeSlotId = dao.addOrUpdateTimeSlot("", tsItem.getName(),
                                    tsItem.getBeginTimeHour(), tsItem.getBeginTimeMinute(),
                                    tsItem.getEndTimeHour(), tsItem.getEndTimeMinute(),
                                    tsItem.getDays(), tsItem.isRepeatFlag());
                            // restore the service flag
                            dao.updateServiceFlag(currentTimeSlotId, tsItem.isServiceFlag());
                        }
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<HashMap<String, TimeSlotItem>> call, Throwable t) {
                LOGD(TAG, "Failure: " + t.getMessage());
            }
        });

    }
}
