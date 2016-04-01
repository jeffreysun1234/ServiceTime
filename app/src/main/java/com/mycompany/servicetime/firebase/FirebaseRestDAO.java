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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
            // set logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FirebaseConstants.FIREBASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            /* get the interface of restful service */
            mService = retrofit.create(FirebaseEndpointInterface.class);
        }

//        if (mContext == null)
//            mContext = CHApplication.getContext();
    }

    public static FirebaseRestDAO create() {
        return new FirebaseRestDAO();
    }

    /**
     * Add new TimeSlot list
     */
    public TimeSlotList addTimeSlotList(String encodedUserEmail, String authToken) throws
            IOException {
        /* build a TimeSlot list */
        TimeSlotList newTimeSlotList = new TimeSlotList("My List", encodedUserEmail,
                FirebaseUtils.getTimestampNowObject());
        HashMap<String, Object> timeSlotListMap = (HashMap<String, Object>)
                new ObjectMapper().convertValue(newTimeSlotList, Map.class);

        /* access firebase database */
        Response<TimeSlotList> response = mService.addTimeSlotList(
                FirebaseConstants.timeSlotListRestURL(encodedUserEmail), timeSlotListMap,
                authToken).execute();
        if (response.isSuccessful()) {
            return (TimeSlotList) response.body();
        } else {
            return null;
        }
    }

    /**
     * Backup TimeSlot list
     */
    public void backupTimeSlotItemList() throws IOException {
        // Get all TimeSlot from DB
        Cursor cursor = CHServiceTimeDAO.create(mContext).getAllTimeSlot();
        if (cursor == null)
            return;

        String encodedEmail = PreferenceSupport.getEncodedEmail(mContext);

        // add a TimeSlotList to Firebase
        addTimeSlotList(encodedEmail, PreferenceSupport.getAuthToken(mContext));

        // clear TimeSlotItems on Firebase
        if (cursor.getCount() > 0) {
            Call<HashMap<String, String>> message = mService.deleteTimeSlotItems(
                    FirebaseConstants.timeSlotItemListRestURL(encodedEmail),
                    PreferenceSupport.getAuthToken(mContext));
            message.enqueue(new Callback<HashMap<String, String>>() {
                @Override
                public void onResponse(Call<HashMap<String, String>> call,
                                       Response<HashMap<String, String>> response) {
                    if (response.isSuccessful()) {
                        LOGD(TAG, "successful clear TimeSlotItems on Firebase.");
                    } else {
                        LOGD(TAG, "fail to clear TimeSlotItems on Firebase.");
                    }
                }

                @Override
                public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                    LOGD(TAG, t.getMessage());
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
            Call<HashMap<String, String>> message = mService.addTimeSlotItemList(
                    FirebaseConstants.timeSlotItemListRestURL(encodedEmail), timeSlotItemMap,
                    PreferenceSupport.getAuthToken(mContext));
            message.enqueue(new Callback<HashMap<String, String>>() {
                @Override
                public void onResponse(Call<HashMap<String, String>> call,
                                       Response<HashMap<String, String>> response) {
                    if (response.isSuccessful()) {
                        LOGD(TAG, ((HashMap<String, String>) response.body()).get("name"));
                    } else {
                        LOGD(TAG, response.toString());
                    }
                }

                @Override
                public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                    LOGD(TAG, t.getMessage());
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
                .getTimeSlotItemList(FirebaseConstants.timeSlotItemListRestURL(encodedEmail),
                        PreferenceSupport.getAuthToken(mContext));
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

    /**
     * restore TimeSlot list
     */
    public Collection<TimeSlotItem> restoreTimeSlotItemList(String encodedUserEmail,
                                                            String authToken) throws
            IOException {
        Response<HashMap<String, TimeSlotItem>> response = mService
                .getTimeSlotItemList(FirebaseConstants.timeSlotItemListRestURL(encodedUserEmail),
                        authToken).execute();

        if (response.isSuccessful()) {
            HashMap<String, TimeSlotItem> body = response.body();
            if (body != null && body.values().size() > 0) {
                return body.values();
            }
        }

        return null;
    }

    /**
     * Backup TimeSlot list
     */
    public boolean backupTimeSlotItemList(String encodedUserEmail, String authToken,
                                          ArrayList<TimeSlotItem> timeSlotItems) throws
            IOException {

        // add a TimeSlotList to Firebase
        addTimeSlotList(encodedUserEmail, authToken);

        if (timeSlotItems != null && timeSlotItems.size() > 0) {
            // clear TimeSlotItems on Firebase
            Response<HashMap<String, String>> response = mService.deleteTimeSlotItems(
                    FirebaseConstants.timeSlotItemListRestURL(encodedUserEmail), authToken)
                    .execute();
            if (response.isSuccessful()) {
                LOGD(TAG, "successful clear TimeSlotItems on Firebase.");

                int ii = 0; // count successful save.
                HashMap<String, Object> timeSlotItemMap;
                for (TimeSlotItem tsItem : timeSlotItems) {
                    // convert TimeSlotItem model to HashMap object
                    timeSlotItemMap = (HashMap<String, Object>)
                            new ObjectMapper().convertValue(tsItem, Map.class);

                    // save to Firebase
                    Response<HashMap<String, String>> message = mService.addTimeSlotItemList(
                            FirebaseConstants.timeSlotItemListRestURL(encodedUserEmail),
                            timeSlotItemMap, authToken).execute();
                    if (message.isSuccessful())
                        ii++;
                }

                if (ii == timeSlotItems.size()) {
                    LOGD(TAG, "successful backup all TimeSlotItem on Firebase.");

                    return true;
                } else {
                    LOGD(TAG, "fail backup all TimeSlotItem on Firebase.");
                }

            } else {
                LOGD(TAG, "fail to clear TimeSlotItems on Firebase.");
            }

        }

        return false;
    }
}
