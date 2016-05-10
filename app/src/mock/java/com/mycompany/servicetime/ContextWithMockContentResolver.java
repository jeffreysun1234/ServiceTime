package com.mycompany.servicetime;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.test.RenamingDelegatingContext;
import android.test.mock.MockContentProvider;
import android.test.mock.MockContentResolver;

import com.mycompany.servicetime.data.source.provider.CHServiceTimeContract;

import java.util.HashMap;

/**
 * Created by szhx on 5/9/2016.
 */
public class ContextWithMockContentResolver extends RenamingDelegatingContext {
    private ContentResolver contentResolver;

    public ContextWithMockContentResolver(Context context) {
        super(context, "test");
        mockContentProvider();
    }

    @Override
    public ContentResolver getContentResolver() {
        return contentResolver;
    }

    @Override
    public Context getApplicationContext() {
        return this;
    } //Added in-case my class called getApplicationContext()

    private void mockContentProvider() {
        //Step 1: Create data you want to return and put it into a matrix cursor
        String[] projection = new String[]{
                CHServiceTimeContract.TimeSlots._ID,
                CHServiceTimeContract.TimeSlots.TIME_SLOT_ID,
                CHServiceTimeContract.TimeSlots.NAME,
                CHServiceTimeContract.TimeSlots.BEGIN_TIME_HOUR,
                CHServiceTimeContract.TimeSlots.BEGIN_TIME_MINUTE,
                CHServiceTimeContract.TimeSlots.END_TIME_HOUR,
                CHServiceTimeContract.TimeSlots.END_TIME_MINUTE,
                CHServiceTimeContract.TimeSlots.DAYS,
                CHServiceTimeContract.TimeSlots.REPEAT_FLAG,
                CHServiceTimeContract.TimeSlots.SERVICE_FLAG
        };
        MatrixCursor matrixCursor = new MatrixCursor(projection);
        matrixCursor.addRow(new Object[]{1, "1", "Work", 9, 0, 17, 0, "0111110", 1, 0});
        matrixCursor.addRow(new Object[]{2, "2", "Test", 11, 0, 13, 0, "0110000", 1, 0});
        matrixCursor.addRow(new Object[]{3, "3", "School", 8, 0, 15, 30, "0111110", 1, 0});

        //Step 2: Create a stub content provider and add the matrix cursor as the expected result of the query
        HashMapMockContentProvider mockProvider = new HashMapMockContentProvider();
        mockProvider.addQueryResult(CHServiceTimeContract.TimeSlots.CONTENT_URI, matrixCursor);

        //Step 3: Create a mock resolver and add the content provider.
        MockContentResolver mockResolver = new MockContentResolver();
        mockResolver.addProvider(CHServiceTimeContract.CONTENT_AUTHORITY /*Needs to be the same as the authority of the provider you are mocking */,
                mockProvider);

        contentResolver = mockResolver;
    }

    //Specialized Mock Content provider for step 2.  Uses a hashmap to return data dependent on the uri in the query
    private class HashMapMockContentProvider extends MockContentProvider {
        private HashMap<Uri, Cursor> expectedResults = new HashMap<Uri, Cursor>();

        public void addQueryResult(Uri uriIn, Cursor expectedResult) {
            expectedResults.put(uriIn, expectedResult);
        }

        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            return expectedResults.get(uri);
        }
    }
}
