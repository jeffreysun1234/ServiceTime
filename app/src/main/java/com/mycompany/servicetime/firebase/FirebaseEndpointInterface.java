package com.mycompany.servicetime.firebase;

import com.mycompany.servicetime.firebase.model.TimeSlotList;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by szhx on 3/23/2016.
 */
public interface FirebaseEndpointInterface {

    @POST(FirebaseConstants.FIREBASE_LOCATION_TIMESLOT_LISTS + ".json")
    Call<String> addTimeSlotList(@Body HashMap<String, Object> timeSlotListJson);

    @POST(FirebaseConstants.FIREBASE_LOCATION_TIMESLOT_LISTS +
            "/{" + FirebaseConstants.PATH_UNIIQUE_ID + "}/" +
            FirebaseConstants.FIREBASE_LOCATION_TIMESLOT_ITEMS + ".json")
    Call<String> addTimeSlotItemList(@Path(FirebaseConstants.PATH_UNIIQUE_ID) String uniqueId,
                                        @Body HashMap<String, Object> timeSlotItemListJson);
}
