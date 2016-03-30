package com.mycompany.servicetime.firebase;

import com.mycompany.servicetime.firebase.model.TimeSlotItem;
import com.mycompany.servicetime.firebase.model.TimeSlotList;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by szhx on 3/23/2016.
 */
public interface FirebaseEndpointInterface {

    @PUT(FirebaseConstants.FIREBASE_LOCATION_TIMESLOT_LISTS +
            "/{" + FirebaseConstants.PATH_USER_EMAIL + "}/" +
            FirebaseConstants.FIREBASE_LOCATION_DEVICE_ID + ".json")
    Call<String> addTimeSlotList(@Path(FirebaseConstants.PATH_USER_EMAIL) String userEmail,
                                 @Body HashMap<String, Object> timeSlotListJson,
                                 @Query("auth") String auth);

    @DELETE(FirebaseConstants.FIREBASE_LOCATION_TIMESLOT_LISTS +
            "/{" + FirebaseConstants.PATH_USER_EMAIL + "}/" +
            FirebaseConstants.FIREBASE_LOCATION_DEVICE_ID + "/" +
            FirebaseConstants.FIREBASE_LOCATION_TIMESLOT_ITEMS + ".json")
    Call<String> deleteTimeSlotItems(@Path(FirebaseConstants.PATH_USER_EMAIL) String userEmail,
                                     @Query("auth") String auth);

    @POST(FirebaseConstants.FIREBASE_LOCATION_TIMESLOT_LISTS +
            "/{" + FirebaseConstants.PATH_USER_EMAIL + "}/" +
            FirebaseConstants.FIREBASE_LOCATION_DEVICE_ID + "/" +
            FirebaseConstants.FIREBASE_LOCATION_TIMESLOT_ITEMS + ".json")
    Call<String> addTimeSlotItemList(@Path(FirebaseConstants.PATH_USER_EMAIL) String userEmail,
                                     @Body HashMap<String, Object> timeSlotItemListJson,
                                     @Query("auth") String auth);

    @GET(FirebaseConstants.FIREBASE_LOCATION_TIMESLOT_LISTS +
            "/{" + FirebaseConstants.PATH_USER_EMAIL + "}/" +
            FirebaseConstants.FIREBASE_LOCATION_DEVICE_ID + "/" +
            FirebaseConstants.FIREBASE_LOCATION_TIMESLOT_ITEMS + ".json")
    Call<HashMap<String, TimeSlotItem>> getTimeSlotItemList(
            @Path(FirebaseConstants.PATH_USER_EMAIL) String userEmail,
            @Query("auth") String auth);
}
