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

import android.net.Uri;
import android.provider.BaseColumns;

import com.mycompany.servicetime.util.ParserUtils;

/**
 * Contract class for interacting with {@link CHServiceTimeProvider}. Unless otherwise noted, all
 * time-based fields are milliseconds since epoch and can be compared against
 * {@link System#currentTimeMillis()}.
 * <p/>
 * The backing {@link android.content.ContentProvider} assumes that {@link Uri}
 * are generated using stronger {@link String} identifiers, instead of
 * {@code int} {@link BaseColumns#_ID} values, which are prone to shuffle during
 * sync.
 */
public final class CHServiceTimeContract {

    public static final String CONTENT_TYPE_APP_BASE = "chservicetime.";

    public static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/vnd."
            + CONTENT_TYPE_APP_BASE;

    public static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/vnd."
            + CONTENT_TYPE_APP_BASE;

    interface TimeSlotsColumns {
        String TIMESTAMP = "timestamp";
        String TIME_SLOT_ID = "time_slot_id";
        String NAME = "name";
        String SERVICE_FLAG = "service_flag"; // true means runing.
        String BEGIN_TIME = "begin_time";
        String END_TIME = "end_time";
        String DAYS = "days";
        String REPEAT_FLAG = "repeat_flag";
    }

    public static final String CONTENT_AUTHORITY = "com.mycompany.chservicetime";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_TIME_SLOTS_TABLE = "time_slots";

    public static final String[] TOP_LEVEL_PATHS = {
            PATH_TIME_SLOTS_TABLE,
    };


    public static String makeContentType(String id) {
        if (id != null) {
            return CONTENT_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static String makeContentItemType(String id) {
        if (id != null) {
            return CONTENT_ITEM_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static class TimeSlots implements TimeSlotsColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIME_SLOTS_TABLE).build();

        public static final String CONTENT_TYPE_ID = "time_slot";

        /**
         * Build {@link Uri} that references all watch symbols.
         */
        public static Uri buildTimeSlotsUri() {
            return CONTENT_URI;
        }

        /**
         * Build a {@link Uri} that references a given watch symbol.
         */
        public static Uri buildTimeSlotUri(String timeSlotId) {
            return CONTENT_URI.buildUpon().appendPath(timeSlotId).build();
        }

        public static String getTimeSlotlId(Uri uri) {
            return uri.getPathSegments().get(1);
        }


        public static String generateTimeSlotId(String name) {
            return ParserUtils.sanitizeId(name);
        }

        public static final String[] DEFAULT_PROJECTION = new String[]{
                BaseColumns._ID,
                TIME_SLOT_ID,
                NAME,
                SERVICE_FLAG,
                BEGIN_TIME,
                END_TIME,
                DAYS,
                REPEAT_FLAG
        };
    }

    private CHServiceTimeContract() {
    }
}
