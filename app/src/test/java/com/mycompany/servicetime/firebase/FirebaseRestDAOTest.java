package com.mycompany.servicetime.firebase;

import android.os.Build;

import com.mycompany.servicetime.BuildConfig;
import com.mycompany.servicetime.firebase.model.TimeSlotItem;
import com.mycompany.servicetime.firebase.model.TimeSlotList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by szhx on 3/24/2016.
 * <p/>
 * This test class accesses the real Firebase. For running successful, we need to prohibit the
 * security rules on Firebase, and set authToken to null.
 */
//@RunWith(RobolectricGradleTestRunner.class)
//@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class FirebaseRestDAOTest {

    //@Before
    public void setup() {
        ShadowLog.stream = System.out;
    }

    //@Test
    public void testAddTimeSlotList() throws Exception {
        String encodedUserEmail = FirebaseUtils.encodeEmail("test@my.com");
        String authToken = null;

        TimeSlotList response = FirebaseRestDAO.create()
                .addTimeSlotList(encodedUserEmail, authToken);

        assertThat(response, is(notNullValue()));
    }

    //@Test
    public void testRestoreTimeSlotItemList() throws Exception {
        String encodedUserEmail = FirebaseUtils.encodeEmail("a@a.com");
        String authToken = null;

        Collection<TimeSlotItem> response = FirebaseRestDAO.create()
                .restoreTimeSlotItemList(encodedUserEmail, authToken);

        assertThat(response, is(notNullValue()));
        assertThat(response.size(), is(2));
    }

    //@Test
    public void testBackupTimeSlotItemList() throws Exception {
        String encodedUserEmail = FirebaseUtils.encodeEmail("test@my.com");
        String authToken = null;
        ArrayList<TimeSlotItem> tsItems = new ArrayList<TimeSlotItem>();
        tsItems.add(
                new TimeSlotItem(9, 10, "0011001", 10, 10, "Test Item", false, false, "129303432"));
        tsItems.add(new TimeSlotItem());

        boolean response = FirebaseRestDAO.create()
                .backupTimeSlotItemList(encodedUserEmail, authToken, tsItems);

        assertThat(response, is(true));
    }
}
