package cs310.fidgetspinners.grubmate.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.Util;
import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.model.Request;
import cs310.fidgetspinners.grubmate.model.User;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String POST = "grubmate.requestfragment.post.newinstance";

    // TODO: Rename and change types of parameters
    private Post requestFromPost;

    private OnFragmentInteractionListener mListener;
    private EditText phoneNumber;
    private Button locationButton;
    private TextView location;
    private EditText reqdetails;
    private Button incrementPortions;
    private Button decrementPortions;
    private TextView displayPortions;
    private Button submitRequest;
    private int portionsRemaining;

    private int numPortions = 1;
    private Request request;

    private static final int REQUEST_PLACE_PICKER = 0;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser mUser = mAuth.getCurrentUser();
    private final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestFragment newInstance(Post post) {
        RequestFragment fragment = new RequestFragment();
        Bundle args = new Bundle();
        args.putSerializable(POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestFromPost = (Post) getArguments().getSerializable(POST);
        }
    }

    public static final long parsephoneNumber(String phoneNumber) {
        try {
            Long phoneNumberLong = Long.parseLong(phoneNumber);
            System.out.println("phone input: " + phoneNumber + " " + phoneNumberLong);
            if ((phoneNumberLong >= 1000000000) && (phoneNumberLong <=9999999999L))
                return phoneNumberLong;
            else return -1;
        }
        catch(NumberFormatException nfe) {
            return -1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_request, container, false);

        portionsRemaining = requestFromPost.getPortionsAvailable();

        phoneNumber = v.findViewById(R.id.request_phone_number);
        locationButton = (Button) v.findViewById(R.id.request_location_button);
        location = (TextView) v.findViewById(R.id.request_location_view);
        reqdetails = (EditText) v.findViewById(R.id.requestDetails);

        // autopopulate phone number
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("numbers")
                .child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneNumber.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


                System.out.println("THE TYPE IS: " + requestFromPost.getType());
        if (!requestFromPost.getType().contains("estaur")) {
            reqdetails.setVisibility(View.GONE);
        }

        incrementPortions = v.findViewById(R.id.request_addPortions);
        decrementPortions = v.findViewById(R.id.request_reducePortions);
        displayPortions = v.findViewById(R.id.request_numPortions);
        submitRequest = v.findViewById(R.id.request_submit);

        incrementPortions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numPortions < portionsRemaining) {
                    numPortions++;
                    displayPortions.setText(Integer.toString(numPortions));
                }
            }
        });

        decrementPortions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numPortions > 1) {
                    numPortions--;
                    displayPortions.setText(Integer.toString(numPortions));
                }
            }
        });

        // use google places API, place picker, to take location information
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    intentBuilder.setLatLngBounds(new LatLngBounds(
                            new LatLng(34.017454, -118.293056),
                            new LatLng(34.026761, -118.279182)
                    ));
                    Intent intent = intentBuilder.build(getActivity());
                    startActivityForResult(intent, REQUEST_PLACE_PICKER);
                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil
                            .getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(getActivity(), "Google Play Services is not available.",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        submitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final FirebaseUser mUser = mAuth.getCurrentUser();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                portionsRemaining = portionsRemaining - numPortions;

                // save #
                databaseReference.child("numbers")
                        .child(mUser.getUid()).setValue(phoneNumber.getText().toString().trim());

                databaseReference.child("users")
                        .child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        System.out.println("grabbing value of this key: " + dataSnapshot.getKey());
                        final User currUser = dataSnapshot.getValue(User.class);
                        boolean validPhone = true;
                        long phoneNumberLong = 0;
                        long isValidPhone = RequestFragment.parsephoneNumber(phoneNumber.getText().toString().trim());
                        if (isValidPhone == -1) {
                            Toast.makeText(getActivity(), "Phone number is not valid.", Toast.LENGTH_SHORT).show();
                            validPhone = false;
                        }
                        else {
                            validPhone = true;
                            phoneNumberLong = isValidPhone;
                        }

                        if(validPhone) {
                            if(location.getText().toString().trim().length() == 0) {
                                Toast.makeText(getActivity(), "Please select a location", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            System.out.println("phone number supposed to be: " + phoneNumberLong);
                            request = new Request(mUser.getUid(), requestFromPost,
                                phoneNumberLong, location.getText().toString().trim(), numPortions,
                                    reqdetails.getText().toString());
                            ArrayList<Request> requests = currUser.getActiveRequests();
                            if(requests == null) {
                                requests = new ArrayList<Request>();
                            }
                            //requests.add(request);
                            //currUser.setActiveRequests(requests);
                            String size = Integer.toString(requests.size());

                            databaseReference.child("users")
                                    .child(mUser.getUid()).child("activeRequests").child(size).setValue(request);

                            databaseReference.child("posts")
                                    .child(requestFromPost.getOriginalPoster()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    GenericTypeIndicator<ArrayList<Post>> t = new GenericTypeIndicator<ArrayList<Post>>() {};
                                    ArrayList<Post> posts = dataSnapshot.getValue(t);
                                    for(int i = 0; i < posts.size(); i++) {
                                        if(posts.get(i).getPostName().equals(requestFromPost.getPostName())
                                                && posts.get(i).getDescription().equals(requestFromPost.getDescription())) {
                                            final String index = Integer.toString(i);

                                            // grab the requests
                                            final DatabaseReference databaseReferenceReq = databaseReference.child("posts")
                                                    .child(requestFromPost.getOriginalPoster())
                                                    .child(index).child("requests");
                                            databaseReferenceReq.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    GenericTypeIndicator<ArrayList<Request>> t
                                                            = new GenericTypeIndicator<ArrayList<Request>>() {};

                                                    ArrayList<Request> requests = dataSnapshot.getValue(t);

                                                    if(requests == null) {
                                                        requests = new ArrayList<Request>();
                                                    }
                                                    System.out.println("REQUESTS: " + requests);
                                                    //requestFromPost.setRequests(requests);

                                                    databaseReference.child("posts")
                                                            .child(request.getOriginalPost().getOriginalPoster())
                                                            .child(index).child("requests")
                                                            .child(Integer.toString(requests.size())).setValue(request);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    updateOutgoingNotifications(request);
                                    updateIncomingNotifications(request);

                                    // send notification to user
                                    String payload = "Your have a request for " + request.getOriginalPost().getPostName();
                                    Util.sendRequest(request.getOriginalPost().getOriginalPoster(), payload);

                                    onButtonPressed("itemRequested");
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "A database error has occurred.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return v;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode != RESULT_OK) {
            return;
        }
        // Check which request we're responding to
        if (requestCode == REQUEST_PLACE_PICKER) {
            Place place = PlacePicker.getPlace(data, getActivity());
            CharSequence address = place.getAddress();
            location.setText(address);
        }
    }
    private void updateOutgoingNotifications(final Request request) {
        System.out.println("updating notifs");
        mRef.child("outgoingRequests").child(mUser.getUid())
                .push().setValue(request);
    }

    private void updateIncomingNotifications(final Request request) {
        // get the other user's request, then update it
        mRef.child("incomingRequests").child(request.getOriginalPost().getOriginalPoster())
                .push().setValue(request);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String request) {
        if (mListener != null) {
            mListener.onFragmentInteraction(request);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(String string);
    }
}
