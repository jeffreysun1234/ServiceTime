/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycompany.servicetime.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.mycompany.servicetime.provider.CHServiceTimeDatabase.Tables;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.mycompany.servicetime.provider.CHServiceTimeContract.TimeSlots;
import static com.mycompany.servicetime.util.LogUtils.LOGV;
import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

/**
 * {@link ContentProvider} that stores {@link CHServiceTimeContract} data. Data is
 * usually queried using {@link android.app.LoaderManager} pattern.
 */
public class CHServiceTimeProvider extends ContentProvider {

    private static final String TAG = makeLogTag(CHServiceTimeProvider.class);

    private CHServiceTimeDatabase mOpenHelper;

    private CHServiceTimeProviderUriMatcher mUriMatcher;

    @Override
    public boolean onCreate() {
        mOpenHelper = new CHServiceTimeDatabase(getContext());
        mUriMatcher = new CHServiceTimeProviderUriMatcher();
        return true;
    }

    private void deleteDatabase() {
        // TODO: wait for content provider operations to finish, then tear down
        mOpenHelper.close();
        Context context = getContext();
        CHServiceTimeDatabase.deleteDatabase(context);
        mOpenHelper = new CHServiceTimeDatabase(getContext());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType(Uri uri) {
        CHServiceTimeUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        return matchingUriEnum.contentType;
    }

    /**
     * Returns a tuple of question marks. For example, if {@code count} is 3, returns "(?,?,?)".
     */
    private String makeQuestionMarkTuple(int count) {
        if (count < 1) {
            return "()";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(?");
        for (int i = 1; i < count; i++) {
            stringBuilder.append(",?");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        CHServiceTimeUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);

        LOGV(TAG, "uri=" + uri + " code=" + matchingUriEnum.code + " proj=" +
                Arrays.toString(projection) + " selection=" + selection + " args="
                + Arrays.toString(selectionArgs) + ")");

        switch (matchingUriEnum) {
            default: {
                // Most cases are handled with simple SelectionBuilder.
                final SelectionBuilder builder = buildExpandedSelection(uri, matchingUriEnum.code);

                boolean distinct = CHServiceTimeContractHelper.isQueryDistinct(uri);

                Cursor cursor = builder
                        .where(selection, selectionArgs)
                        .query(db, distinct, projection, sortOrder, null);

                Context context = getContext();
                if (null != context) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        LOGV(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        CHServiceTimeUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        if (matchingUriEnum.table != null) {
            db.insertOrThrow(matchingUriEnum.table, null, values);
            notifyChange(uri);
        }

        switch (matchingUriEnum) {
            case TIMESLOTS: {
                return TimeSlots.buildTimeSlotUri(
                        values.getAsString(TimeSlots.TIME_SLOT_ID));
            }
            default: {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        LOGV(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        CHServiceTimeUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);

        final SelectionBuilder builder = buildSimpleSelection(uri);
//        if (matchingUriEnum == CHServiceTimeUriEnum.MY_SCHEDULE) {
//            values.remove(MySchedule.MY_SCHEDULE_ACCOUNT_NAME);
//            builder.where(MySchedule.MY_SCHEDULE_ACCOUNT_NAME + "=?", accountName);
//        }

        int retVal = builder.where(selection, selectionArgs).update(db, values);
        notifyChange(uri);
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        LOGV(TAG, "delete(uri=" + uri + ")");
        if (uri == CHServiceTimeContract.BASE_CONTENT_URI) {
            // Handle whole database deletes (e.g. when signing out)
            deleteDatabase();
            notifyChange(uri);
            return 1;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        CHServiceTimeUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
//        if (matchingUriEnum == CHServiceTimeUriEnum.MY_SCHEDULE) {
//            builder.where(MySchedule.MY_SCHEDULE_ACCOUNT_NAME + "=?", accountName);
//        }

        int retVal = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri);
        return retVal;
    }

    /**
     * Notifies the system that the given {@code uri} data has changed.
     * <p/>
     * We only notify changes if the uri wasn't called by the sync adapter, to avoid issuing a large
     * amount of notifications while doing a sync. The
     * {link com.mycompany.chstock.sync.ConferenceDataHandler} notifies all top level
     * conference paths once the conference data sync is done, and the
     * {link com.mycompany.chstock.sync.userdata.AbstractUserDataSyncHelper} notifies all
     * user data related paths once the user data sync is done.
     */
    private void notifyChange(Uri uri) {
        //if (!CHServiceTimeContractHelper.isUriCalledFromSyncAdapter(uri)) {
        Context context = getContext();
        context.getContentResolver().notifyChange(uri, null);

        // Widgets can't register content observers so we refresh widgets separately.
        //    context.sendBroadcast(ScheduleWidgetProvider.getRefreshBroadcastIntent(context,
        //        false));
        //}
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually enough to support {@link #insert},
     * {@link #update}, and {@link #delete} operations.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        CHServiceTimeUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        // The main Uris, corresponding to the root of each type of Uri, do not have any selection
        // criteria so the full table is used. The others apply a selection criteria.
        switch (matchingUriEnum) {
            case TIMESLOTS:
                return builder.table(matchingUriEnum.table);
            case TIMESLOTS_ID: {
                final String timeSlotId = TimeSlots.getTimeSlotlId(uri);
                return builder.table(Tables.TIME_SLOTS_TABLE)
                        .where(TimeSlots.TIME_SLOT_ID + "=?", timeSlotId);
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri for " + uri);
            }
        }
    }

    /**
     * Build an advanced {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually only used by {@link #query}, since it
     * performs table joins useful for {@link Cursor} data.
     */
    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        CHServiceTimeUriEnum matchingUriEnum = mUriMatcher.matchCode(match);
        if (matchingUriEnum == null) {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        switch (matchingUriEnum) {
            case TIMESLOTS: {
                return builder.table(Tables.TIME_SLOTS_TABLE);
            }
//            case BLOCKS_BETWEEN: {
//                final List<String> segments = uri.getPathSegments();
//                final String startTime = segments.get(2);
//                final String endTime = segments.get(3);
//                return builder.table(Tables.BLOCKS)
//                        .where(Blocks.BLOCK_START + ">=?", startTime)
//                        .where(Blocks.BLOCK_START + "<=?", endTime);
//            }
            case TIMESLOTS_ID: {
                final String timeSlotId = TimeSlots.getTimeSlotlId(uri);
                return builder.table(Tables.TIME_SLOTS_TABLE)
                        .where(TimeSlots.TIME_SLOT_ID + "=?", timeSlotId);
            }
//            case SESSIONS_UNSCHEDULED: {
//                final long[] interval = Sessions.getInterval(uri);
//                return builder.table(Tables.SESSIONS_JOIN_ROOMS_TAGS_FEEDBACK_MYSCHEDULE,
//                        getCurrentAccountName(uri, true))
//                        .mapToTable(Sessions._ID, Tables.SESSIONS)
//                        .mapToTable(Sessions.ROOM_ID, Tables.SESSIONS)
//                        .mapToTable(Sessions.SESSION_ID, Tables.SESSIONS)
//                        .map(Sessions.SESSION_IN_MY_SCHEDULE, "IFNULL(in_schedule, 0)")
//                        .where(Sessions.SESSION_IN_MY_SCHEDULE + "=0")
//                        .where(Sessions.SESSION_START + ">=?", String.valueOf(interval[0]))
//                        .where(Sessions.SESSION_START + "<?", String.valueOf(interval[1]))
//                        .groupBy(Qualified.SESSIONS_SESSION_ID);
//            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        throw new UnsupportedOperationException("openFile is not supported for " + uri);
    }

    private interface Subquery {

    }

    /**
     * {@link CHServiceTimeContract} fields that are fully qualified with a specific
     * parent {@link Tables}. Used when needed to work around SQL ambiguity.
     */
    private interface Qualified {

    }
}
