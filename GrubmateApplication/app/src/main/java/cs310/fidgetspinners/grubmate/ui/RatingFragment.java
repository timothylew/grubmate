package cs310.fidgetspinners.grubmate.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cs310.fidgetspinners.grubmate.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RatingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RatingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "RATINGUSER:";

    private static int numRatings = 0;
    private static double averageRating = 0;

    // TODO: Rename and change types of parameters
    private String otherUser;

    private OnFragmentInteractionListener mListener;

    public RatingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param otherUser User to be rated.
     * @return A new instance of fragment RatingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingFragment newInstance(String otherUser) {
        RatingFragment fragment = new RatingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, otherUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // I have no idea what this code does
        if (getArguments() != null) {
            otherUser = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rating, container, false);

        // Initialize firebase user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

        // Add hook for rating button
        final RatingBar ratingBar = v.findViewById(R.id.ratingBar);

        // Grab the user's current rating information
        myRef.child(otherUser).child("ratingsCount")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            myRef.child(otherUser)
                                    .child("ratingsCount").setValue(0);
                            return;
                        }
                        numRatings = dataSnapshot.getValue(Integer.class);
                        System.out.println("value of ratingsCount: " + String.valueOf(numRatings));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        System.out.println("failed to read value");
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

        // Grab the user's current rating information
        myRef.child(otherUser).child("averageRating")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            myRef.child(otherUser)
                                    .child("averageRating").setValue(0);
                            return;
                        }
                        averageRating = dataSnapshot.getValue(Double.class);
                        System.out.println("value of averageRating: " + String.valueOf(numRatings));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        System.out.println("failed to read value");
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

        final EditText reviewText = v.findViewById(R.id.review_input);
        // Hook button response
        Button submitButton = v.findViewById(R.id.ratingSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RateUser(ratingBar.getRating(), numRatings, ratingBar.getRating()
                , reviewText.getText().toString(), myRef, otherUser);

                // Go back to the OG activity
                // TODO this code might have to go back in the onFragmentInteraction method.
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });

        Button cancelButton = v.findViewById(R.id.cancel_rating);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    public static double RateUser(double averageRating, int numRatings, float numStars,
             String review, DatabaseReference myRef, String otherUser) {

        if (review.equals("")) {
            return -1;
        } else if (otherUser.equals("")) {
            return -1;
        }

        double averageRatingSum = averageRating * numRatings + numStars;
        numRatings++;
        double finalRating = averageRatingSum / numRatings;
        System.out.println("value of finalsum: " + String.valueOf(averageRatingSum));
        System.out.println("value of finalaverage: " + String.valueOf(finalRating));
        System.out.println("value of uid: " + otherUser);

        // Put value back
        myRef.child(otherUser)
                .child("averageRating").setValue(finalRating);
        myRef.child(otherUser)
                .child("ratingsCount").setValue(numRatings);

        myRef.child(otherUser)
                .child("reviews").push().setValue(review);

        return finalRating;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionRatingFragment(uri);
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
        void onFragmentInteractionRatingFragment(Uri uri);
    }

}
