package cs310.fidgetspinners.grubmate.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.model.Subscription;
import cs310.fidgetspinners.grubmate.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubscriptionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubscriptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubscriptionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText newSubscription;
    private Button addSubscription;

    private Button startDateButton;
    private Button startTimeButton;
    private Button endDateButton;
    private Button endTimeButton;

    private int mYear, mMonth, mDay, mHour, mMinute;

    private ListView subscriptionsList;
    private SubscriptionAdapter adapter;
    private ArrayList<Subscription> userSubscriptions;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private User currUser;

    public SubscriptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubscriptionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubscriptionFragment newInstance(String param1, String param2) {
        SubscriptionFragment fragment = new SubscriptionFragment();
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
        View v = inflater.inflate(R.layout.fragment_subscription, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Bold.ttf");
        final Typeface light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");
        final Typeface italic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Italic.ttf");

        newSubscription = (EditText) v.findViewById(R.id.new_subscription);
        addSubscription = (Button) v.findViewById(R.id.add_subscription);

        startDateButton = (Button) v.findViewById(R.id.startDate_button);
        startTimeButton = (Button) v.findViewById(R.id.startTime_button);
        endDateButton = (Button) v.findViewById(R.id.endDate_button);
        endTimeButton = (Button) v.findViewById(R.id.endTime_button);

        subscriptionsList = (ListView) v.findViewById(R.id.list_subscriptions);

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        startDateButton.setText((month + 1) + "-" + day + "-" + year);
                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        startTimeButton.setText(hour + ":" + minute);
                    }
                }, mHour, mMinute, false);

                timePickerDialog.show();
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        endDateButton.setText((month + 1) + "-" + day + "-" + year);
                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        endTimeButton.setText(hour + ":" + minute);
                    }
                }, mHour, mMinute, false);

                timePickerDialog.show();
            }
        });

        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                 .addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         currUser = dataSnapshot.getValue(User.class);
                         userSubscriptions = currUser.getSubscriptions();
                         if(userSubscriptions == null) {
                             userSubscriptions = new ArrayList<Subscription>();
                         }
                         for(int i = 0; i < userSubscriptions.size(); i++) {
                             Date endSubscription = userSubscriptions.get(i).getEndTime();
                             if(new Date().after(endSubscription)) {
                                 userSubscriptions.remove(i);
                             }
                         }
                         adapter = new SubscriptionAdapter(getActivity(), userSubscriptions);
                         subscriptionsList.setAdapter(adapter);
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });

        addSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subscriptionName = newSubscription.getText().toString().toLowerCase().trim();
                String startDateText = startDateButton.getText().toString().trim();
                String startTimeText = startTimeButton.getText().toString().trim();
                String endDateText = endDateButton.getText().toString().toLowerCase().trim();
                String endTimeText = endTimeButton.getText().toString().toLowerCase().trim();

                if(subscriptionName.equals("")){
                    newSubscription.setError("This field cannot be empty.");
                }
                else {
                    boolean success = true;

                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm");
                        SimpleDateFormat dayFormat = new SimpleDateFormat("MM-dd-yyyy");
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
                        Calendar cal = Calendar.getInstance();
                        Date currentDate = cal.getTime();
                        cal.add(Calendar.YEAR, 50);
                        Date laterDate = cal.getTime();

                        if (startDateText.equals("Select Start Date")) {
                            startDateText = dayFormat.format(currentDate);
                        }
                        if (startTimeText.equals("Select Start Time")) {
                            startTimeText = timeFormat.format(currentDate);

                        }
                        Date start = dateFormat.parse(startDateText+ " " + startTimeText);

                        if (endDateText.equals("select end date")) {
                            endDateText = dayFormat.format(laterDate);
                        }
                        if (endTimeText.equals("select end time")) {
                            endTimeText = timeFormat.format(laterDate);
                        }
                        Date end = dateFormat.parse(endDateText+ " " + endTimeText);

                        Date currDate = new Date();

                        if (rangeIsInvalid(start, end, currDate)) {
                            Toast.makeText(getActivity(), "Invalid Time Range", Toast.LENGTH_SHORT).show();
                            success = false;
                            return;
                        }

                        Subscription subscription = new Subscription(subscriptionName, start, end);
                        userSubscriptions.add(subscription);
                        currUser.setSubscriptions(userSubscriptions);
                        mDatabase.child("users")
                                .child(mUser.getUid()).child("subscriptions").setValue(userSubscriptions);
                        mDatabase.child("subscriptions").child(mUser.getUid()).setValue(userSubscriptions);

                        adapter.notifyDataSetChanged();
                    }
                    catch(ParseException exp){
                        Log.d("Error","Error in parsing date");
                        success = false;
                        Toast.makeText(getActivity(),"Error in DateTimeFormat", Toast.LENGTH_SHORT).show();
                    }

                    if(success) {
                        Toast.makeText(getActivity(), newSubscription.getText().toString().trim() +
                                " has been added to your list of subscriptions.", Toast.LENGTH_SHORT).show();
                    }

                }
                newSubscription.setText("");
                startDateButton.setText("Select Start Date");
                startTimeButton.setText("Select Start Time");
                endDateButton.setText("Select End Date");
                endTimeButton.setText("Select End Time");
            }
        });

        return v;
    }

    public static boolean rangeIsInvalid(Date start, Date end, Date currDate) {
        if(start.compareTo(end) > 0 || end.compareTo(currDate) <= 0){
            return true;
        }
        return false;
    }

    public void onButtonPressed(String interaction) {
        if (mListener != null) {
            mListener.onFragmentInteraction(interaction);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String interaction);
    }

    // Adapter for Post Snippets
    public class SubscriptionAdapter extends ArrayAdapter<Subscription> {

        private ArrayList<Subscription> subscriptions;
        private Button deleteSubscription;
        private TextView subscriptionName;
        private TextView subscriptionTime;

        // Constructor takes in an ArrayList to use for the tutors.
        public SubscriptionAdapter(Context context, ArrayList<Subscription> subscriptions) {
            super(context, 0, subscriptions);
            this.subscriptions = subscriptions;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // Inflates the layout.
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.subscription_list_item, null);
            }

            deleteSubscription = (Button) convertView.findViewById(R.id.delete_subscription);
            subscriptionName = (TextView) convertView.findViewById(R.id.subscription_item_name);
            subscriptionTime = (TextView) convertView.findViewById(R.id.subscription_item_timeFrame);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yy");

            String start = sdf.format(subscriptions.get(position).getStartTime());
            String end = sdf.format(subscriptions.get(position).getEndTime());

            if(subscriptions.get(position).getEndTime().getTime() == Long.MAX_VALUE)
                end = "sun explosion";

            subscriptionName.setText(subscriptions.get(position).getSubscriptionName().trim());
            subscriptionTime.setText(start + " to " + end);
            deleteSubscription.setTag(position);

            deleteSubscription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // We are retrieving the tag we set earlier here.
                    int clickedPosition = (int) v.getTag();

                    // Delete the item from the arraylist.
                    subscriptions.remove(position);
                    currUser.setSubscriptions(subscriptions);
                    mDatabase.child("users").child(mUser.getUid()).setValue(currUser);
                    mDatabase.child("subscriptions").child(mUser.getUid()).setValue(subscriptions);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }
}
