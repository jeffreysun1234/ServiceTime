package com.mycompany.servicetime.firebase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.servicetime.firebase.model.TimeSlotItem;
import com.mycompany.servicetime.firebase.model.TimeSlotList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.SECONDS;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created by szhx on 3/31/2016.
 * <p/>
 * This test class is for confirming the test data is correct, and keeping all restful call methods
 * are stable.
 */
public class RestCallTest {

    final static String MOCK_SERVER_URL = "/";
    static String userEmailPath = FirebaseUtils.encodeEmail("a@a.com");

    public static MockWebServer mockServer;
    public static Retrofit retrofit;
    public static FirebaseEndpointInterface mService;

    @BeforeClass
    public static void setup() throws IOException {
        mockServer = new MockWebServer();
        mockServer.setDispatcher(new MockServerDispatcher());
        mockServer.start();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        retrofit = new Retrofit.Builder()
                .baseUrl(mockServer.url(MOCK_SERVER_URL))
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient.build())
                .build();
        mService = retrofit.create(FirebaseEndpointInterface.class);
    }

    @AfterClass
    public static void tearDown() throws IOException {
        mockServer.shutdown();
    }

    /**
     * Mock URL+Method and response
     */
    static class MockServerDispatcher extends Dispatcher {

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            String userEmailPath = FirebaseUtils.encodeEmail("a@a.com");

            String timeSlotItemListRestURL = FirebaseConstants
                    .timeSlotItemListRestURL(userEmailPath);
            String getTimeSlotItemListURLResponse = "{\"-KE8oUN5U5BGgBMey02U\":{\"beginTimeHour" +
                    "\":17,\"beginTimeMinute\":5,\"days\":\"0100000\",\"endTimeHour\":17," +
                    "\"endTimeMinute\":6,\"name\":\"test\",\"repeatFlag\":true," +
                    "\"serviceFlag\":true,\"timeSlotId\":\"1459378867092\"}," +
                    "\"-KE8oUU0SQYR9RomqBdH\":{\"beginTimeHour\":15,\"beginTimeMinute\":57," +
                    "\"days\":\"1000001\",\"endTimeHour\":16,\"endTimeMinute\":57," +
                    "\"name\":\"work\",\"repeatFlag\":true,\"serviceFlag\":false," +
                    "\"timeSlotId\":\"1459378867117\"}," +
                    "\"-KE8oUttnj3qfm-uO9ZK\":{\"beginTimeHour\":19,\"beginTimeMinute\":17," +
                    "\"days\":\"0001100\",\"endTimeHour\":22,\"endTimeMinute\":17," +
                    "\"name\":\"school\",\"repeatFlag\":true,\"serviceFlag\":false," +
                    "\"timeSlotId\":\"1459379887970\"}}";

            String addTimeSlotItemListURLResponse = "{\"name\":\"-JSOpn9ZC54A4P4RoqVa\"}";

            String timeSlotListRestURL = FirebaseConstants.timeSlotListRestURL(
                    userEmailPath);

            if (request.getPath().equals(MOCK_SERVER_URL + "example.json")) {
                return new MockResponse().setResponseCode(200).setBody("\"Hi\"");
            } else if (request.getPath().equals(MOCK_SERVER_URL + timeSlotItemListRestURL)
                    && request.getMethod().equals("GET")) {
                return new MockResponse().setResponseCode(200)
                        .setBody(getTimeSlotItemListURLResponse);
            } else if (request.getPath().equals(MOCK_SERVER_URL + timeSlotItemListRestURL)
                    && request.getMethod().equals("POST")) {
                return new MockResponse().setResponseCode(200)
                        .setBody(addTimeSlotItemListURLResponse);
            } else if (request.getPath().equals(MOCK_SERVER_URL + timeSlotItemListRestURL)
                    && request.getMethod().equals("DELETE")) {
                return new MockResponse().setResponseCode(200)
                        .setBody("\"{}\"");
            } else if (request.getPath().equals(MOCK_SERVER_URL + timeSlotListRestURL)
                    && request.getMethod().equals("PUT")) {
                return new MockResponse().setResponseCode(200)
                        .setBody("\"Ok\"");
            }
            return new MockResponse().setResponseCode(404);
        }
    }

    @Test
    public void http200Sync() throws IOException {
        Response<String> response = mService.getTestString().execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.body(), is(equalTo("Hi")));
    }

    @Test
    public void testGetTimeSlotItemList() throws IOException {
        Response<HashMap<String, TimeSlotItem>> response = mService
                .getTimeSlotItemList(FirebaseConstants.timeSlotItemListRestURL(userEmailPath),
                        null)
                .execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(((HashMap<String, TimeSlotItem>) response.body()).size(), is(equalTo(3)));
    }

    @Test
    public void testAddTimeSlotItemList() throws IOException {
        String userEmail = "a@a.com";
        TimeSlotItem timeSlotItem = new TimeSlotItem(9, 10, "0011001", 10, 10, "Test Item", false,
                false, "129303432");
        HashMap<String, Object> timeSlotItemMap = (HashMap<String, Object>)
                new ObjectMapper().convertValue(timeSlotItem, Map.class);

        Response<HashMap<String, String>> response = mService
                .addTimeSlotItemList(FirebaseConstants.timeSlotItemListRestURL(userEmailPath),
                        timeSlotItemMap, null)
                .execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(((HashMap<String, String>) response.body()).containsKey("name"), is(true));
    }

    @Test
    public void testDeleteTimeSlotItems() throws IOException, InterruptedException {
        Call<HashMap<String, String>> message = mService
                .deleteTimeSlotItems(FirebaseConstants.timeSlotItemListRestURL(userEmailPath),
                        null);

        final AtomicReference<Response<HashMap<String, String>>> responseRef = new
                AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);
        message.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call,
                                   Response<HashMap<String, String>> response) {
                responseRef.set(response);
                latch.countDown();
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

            }
        });
        //assertThat(latch.await(2, SECONDS), is(true));
        latch.await(2, SECONDS);

        Response<HashMap<String, String>> response = responseRef.get();
        assertThat(response, is(nullValue()));
    }

    @Test
    public void testAddTimeSlotList() throws IOException {
        String userEmail = "a@a.com";
        TimeSlotList newTimeSlotList = new TimeSlotList("My List", userEmail,
                FirebaseUtils.getTimestampNowObject());
        HashMap<String, Object> timeSlotListMap = (HashMap<String, Object>)
                new ObjectMapper().convertValue(newTimeSlotList, Map.class);

        Response<String> response = mService
                .addTimeSlotList(FirebaseConstants.timeSlotListRestURL(userEmailPath),
                        timeSlotListMap, null).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.body(), is(equalTo("Ok")));
    }
}
