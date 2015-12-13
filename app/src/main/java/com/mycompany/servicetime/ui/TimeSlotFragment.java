package com.mycompany.servicetime.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mycompany.servicetime.R;
import com.mycompany.servicetime.model.TimeSlot;
import com.mycompany.servicetime.provider.CHServiceTimeDAO;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimeSlotFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimeSlotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeSlotFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TIME_SLOT_ID = "time_slot_id";

    private String mTimeSlotId;

    private OnFragmentInteractionListener mListener;
    private Activity mActivity;
    private char[] days = "0000000".toCharArray();

    private EditText nameEditText;
    private TimePicker beginTimeTP;
    private TimePicker endTimeTP;
    private CheckBox repeatFlagCheckBox;
    private ToggleButton day0ToggleButton;
    private ToggleButton day1ToggleButton;
    private ToggleButton day2ToggleButton;
    private ToggleButton day3ToggleButton;
    private ToggleButton day4ToggleButton;
    private ToggleButton day5ToggleButton;
    private ToggleButton day6ToggleButton;
    private Button deleteButton;
    private Button saveButton;

    public TimeSlotFragment() {
        // Required empty public constructor
    }

    public static TimeSlotFragment newInstance(String timeSlotId) {
        TimeSlotFragment fragment = new TimeSlotFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TIME_SLOT_ID, timeSlotId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTimeSlotId = getArguments().getString(ARG_TIME_SLOT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_slot, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = this.getActivity();
        initViews();
    }

    private void initViews() {
        nameEditText = (EditText) mActivity.findViewById(R.id.timeSlotNameEditText);
        beginTimeTP = (TimePicker) mActivity.findViewById(R.id.beginTimePicker);
        endTimeTP = (TimePicker) mActivity.findViewById(R.id.endTimePicker);
        beginTimeTP.setIs24HourView(true);
        endTimeTP.setIs24HourView(true);
        day0ToggleButton = (ToggleButton) mActivity.findViewById(R.id.day0InWeekToggleButton);
        day1ToggleButton = (ToggleButton) mActivity.findViewById(R.id.day1InWeekToggleButton);
        day2ToggleButton = (ToggleButton) mActivity.findViewById(R.id.day2InWeekToggleButton);
        day3ToggleButton = (ToggleButton) mActivity.findViewById(R.id.day3InWeekToggleButton);
        day4ToggleButton = (ToggleButton) mActivity.findViewById(R.id.day4InWeekToggleButton);
        day5ToggleButton = (ToggleButton) mActivity.findViewById(R.id.day5InWeekToggleButton);
        day6ToggleButton = (ToggleButton) mActivity.findViewById(R.id.day6InWeekToggleButton);
        repeatFlagCheckBox = (CheckBox) mActivity.findViewById(R.id.repeatWeeklyCheckBox);

        deleteButton = (Button) mActivity.findViewById(R.id.deleteButton);
        saveButton = (Button) mActivity.findViewById(R.id.saveButton);

        if (!TextUtils.isEmpty(mTimeSlotId)) {
            TimeSlot timeSlot = CHServiceTimeDAO.create(getContext()).getTimeSlot(mTimeSlotId);
            nameEditText.setText(timeSlot.name);
            beginTimeTP.setCurrentHour(Integer.parseInt(timeSlot.beginTime.split(":")[0]));
            beginTimeTP.setCurrentMinute(Integer.parseInt(timeSlot.beginTime.split(":")[1]));
            endTimeTP.setCurrentHour(Integer.parseInt(timeSlot.beginTime.split(":")[0]));
            endTimeTP.setCurrentMinute(Integer.parseInt(timeSlot.beginTime.split(":")[1]));
            days = timeSlot.days.toCharArray();
            day0ToggleButton.setChecked(days[0] == '1');
            day1ToggleButton.setChecked(days[1] == '1');
            day2ToggleButton.setChecked(days[2] == '1');
            day3ToggleButton.setChecked(days[3] == '1');
            day4ToggleButton.setChecked(days[4] == '1');
            day5ToggleButton.setChecked(days[5] == '1');
            day6ToggleButton.setChecked(days[6] == '1');
            repeatFlagCheckBox.setChecked(timeSlot.repeatFlag);

            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.INVISIBLE);
        }

        day0ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(0));
        day1ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(1));
        day2ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(2));
        day3ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(3));
        day4ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(4));
        day5ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(5));
        day6ToggleButton.setOnCheckedChangeListener(new daysOnCheckedChangeListener(6));

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTimeSlot();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTimeSlot();
            }
        });
    }

    private void saveTimeSlot() {

        String name = ((TextView) mActivity.findViewById(R.id.timeSlotNameEditText)).getText()
                .toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "You must input a Name.", Toast.LENGTH_LONG).show();
            return;
        }
        String beginTime = beginTimeTP.getCurrentHour() + ":" + beginTimeTP.getCurrentMinute();
        String endTime = endTimeTP.getCurrentHour() + ":" + endTimeTP.getCurrentMinute();
        int repeatFlag = ((CheckBox) mActivity.findViewById(R.id.repeatWeeklyCheckBox))
                .isChecked() ? 1 : 0;

        CHServiceTimeDAO.create(getContext()).addTimeSlot(name, beginTime, endTime,
                String.copyValueOf(days), repeatFlag);


    }

    private void deleteTimeSlot() {
        CHServiceTimeDAO.create(getContext()).deleteTimeSlot(mTimeSlotId);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class daysOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        int currentDayIndex;

        daysOnCheckedChangeListener(int dayIndex) {
            currentDayIndex = dayIndex;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
                days[currentDayIndex] = '1';
            } else {
                days[currentDayIndex] = '0';
            }

        }
    }
}
