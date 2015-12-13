package com.mycompany.servicetime.ui;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mycompany.servicetime.R;
import com.mycompany.servicetime.provider.CHServiceTimeContract;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Activity mActivity;
    private char[] days = "0000000".toCharArray();
    private TimePicker beginTimeTP;
    private TimePicker endTimeTP;


    public TimeSlotFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimeSlotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimeSlotFragment newInstance(String param1, String param2) {
        TimeSlotFragment fragment = new TimeSlotFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        beginTimeTP = (TimePicker) mActivity.findViewById(R.id.beginTimePicker);
        endTimeTP = (TimePicker) mActivity.findViewById(R.id.endTimePicker);
        beginTimeTP.setIs24HourView(true);
        endTimeTP.setIs24HourView(true);
        ((ToggleButton) mActivity.findViewById(R.id.day0InWeekToggleButton))
                .setOnCheckedChangeListener(new daysOnCheckedChangeListener(0));
        ((ToggleButton) mActivity.findViewById(R.id.day1InWeekToggleButton))
                .setOnCheckedChangeListener(new daysOnCheckedChangeListener(1));
        ((ToggleButton) mActivity.findViewById(R.id.day2InWeekToggleButton))
                .setOnCheckedChangeListener(new daysOnCheckedChangeListener(2));
        ((ToggleButton) mActivity.findViewById(R.id.day3InWeekToggleButton))
                .setOnCheckedChangeListener(new daysOnCheckedChangeListener(3));
        ((ToggleButton) mActivity.findViewById(R.id.day4InWeekToggleButton))
                .setOnCheckedChangeListener(new daysOnCheckedChangeListener(4));
        ((ToggleButton) mActivity.findViewById(R.id.day5InWeekToggleButton))
                .setOnCheckedChangeListener(new daysOnCheckedChangeListener(5));
        ((ToggleButton) mActivity.findViewById(R.id.day6InWeekToggleButton))
                .setOnCheckedChangeListener(new daysOnCheckedChangeListener(6));

        Button saveButton = (Button) mActivity.findViewById(R.id.saveButton);
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
