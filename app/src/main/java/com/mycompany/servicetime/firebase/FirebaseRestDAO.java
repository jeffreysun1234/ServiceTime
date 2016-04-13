package com.mycompany.servicetime.firebase;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.servicetime.firebase.model.TimeSlotItem;
import com.mycompany.servicetime.firebase.model.TimeSlotList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
