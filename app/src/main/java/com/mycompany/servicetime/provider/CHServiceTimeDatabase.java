/*
 * Copyright 2014 Google Inc. All rights reserved.
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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;

import com.mycompany.servicetime.SystemConstants;
import com.mycompany.servicetime.provider.CHServiceTimeContract.*;
import com.mycompany.servicetime.support.AppSystemSupport;

import java.io.File;

import static com.mycompany.servicetime.util.LogUtils.LOGD;
import static com.mycompany.servicetime.util.LogUtils.makeLogTag;

/**
 * Helper for managing {@link SQLiteDatabase} that stores data for
 * {@link CHServiceTimeProvider}.
 */
public class CHServiceTimeDatabase extends SQLiteOpenHelper {
    private static final String TAG = makeLogTag(CHServiceTimeDatabase.class);

    private static final String DATABASE_NAME;
    private static final int VER_INIT_RELEASE = 1; // app version 1.0

    // NOTE: carefully update onUpgrade() when bumping database versions to make
    // sure user data is saved.
    private static final int CUR_DATABASE_VERSION = VER_INIT_RELEASE;

    static {
        // Database locates on SD card if it exist. The sensitive data must be encrypted.
        if (AppSystemSupport.isSDCardPresent()) {
            DATABASE_NAME = Environment.getExternalStorageDirectory() + File.separator
                    + SystemConstants.App_Dir_In_SDCard + File.separator
                    + SystemConstants.DATABASE_FILE_NAME;
        } else {
            DATABASE_NAME = SystemConstants.DATABASE_FILE_NAME;
        }
        LOGD(TAG, "Database=" + DATABASE_NAME);
    }

    interface Tables {
        String TIME_SLOTS_TABLE = "time_slots";
    }

    private final Context mContext;

    public CHServiceTimeDatabase(Context context) {
        super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Tables.TIME_SLOTS_TABLE + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TimeSlots.TIME_SLOT_ID + " TEXT NOT NULL,"
                + TimeSlots.NAME + " TEXT NOT NULL,"
                + TimeSlots.SERVICE_FLAG + " INTEGER NOT NULL DEFAULT 0,"
                + TimeSlots.BEGIN_TIME_HOUR + " INTEGER NOT NULL,"
                + TimeSlots.BEGIN_TIME_MINUTE + " INTEGER NOT NULL,"
                + TimeSlots.END_TIME_HOUR + " INTEGER NOT NULL,"
                + TimeSlots.END_TIME_MINUTE + " INTEGER NOT NULL,"
                + TimeSlots.DAYS + " TEXT NOT NULL,"
                + TimeSlots.REPEAT_FLAG + " INTEGER NOT NULL DEFAULT 0,"
                + "UNIQUE (" + TimeSlots.TIME_SLOT_ID + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGD(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
    }


    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
