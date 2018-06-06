package cs310.fidgetspinners.grubmate.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PROFILE FRAGMENT: ";
    public static final String FEED_POSITION = "fidgetspinners.grubmate.feed.position";
    public static final String SCREEN_NAME = "fidgetspinners.grubmate.screen.name";

    private ImageView profilePicture;
    private RatingBar mRating;
    private TextView profileName;
    private Button reportUserButton;
    private Button viewMessages;
    //display posts
    public static ArrayList<Post> orderHistory;
    private static ListView ordersView;
    private ProfileFragment.PostSnippetAdapter postSnippetAdapter;
    private ProfileFragment.ReviewSnippetAdapter reviewSnippetAdapter;

    // access token for fb
    private AccessTokenTracker accessTokenTracker;

    // Database members
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private boolean viewPosts = true;
    private boolean viewReviews = false;

    Typeface bold = null;
    Typeface light = null;
    Typeface italic = null;

    private String userQuery = "";
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.out.println("WHAT WAS PASSED WAS : " + mParam1);
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        ordersView = (ListView) v.findViewById(R.id.profile_list_view);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Bold.ttf");
        light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");
        italic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Italic.ttf");


        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;

        profilePicture = v.findViewById(R.id.profilePicture);
        viewMessages = v.findViewById(R.id.profile_viewMessages);
        final EditText phoneNumber = v.findViewById(R.id.changenumber);
        //profilePicture.setLayoutParams(new LinearLayout.LayoutParams(width, width));
        //profilePicture.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mRating = v.findViewById(R.id.profileRatingBar);
        profileName = v.findViewById(R.id.profile_name);
        profileName.setTypeface(bold);
        reportUserButton = v.findViewById(R.id.report_user_button);

        mDatabase.getReference().child("numbers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneNumber.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newString = editable.toString();
                mDatabase.getReference().child("numbers")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(newString);
            }
        });

        if (mParam1 != null) {
            userQuery = mParam1;
        } else {
            userQuery = mAuth.getCurrentUser().getUid();
        }

//        FirebaseDatabase.getInstance().getReference().child("users").child(userQuery)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User currUser = dataSnapshot.getValue(User.class);
//                Glide.with(getContext()).load(currUser.getProfilephoto()).into(profilePicture);
//                System.out.println(currUser.getAverageRating());
//                mRating.setRating(currUser.getAverageRating());
//                profileName.setText(currUser.getName());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        getProfile(userQuery, mRating, profileName, profilePicture, getContext());

        viewMessages.setTypeface(italic);
        viewMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MessageRoomActivity.class);
                startActivity(i);
            }
        });

        reportUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"grubmate.reports@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Reporting: " + profileName.getText().toString());
                i.putExtra(Intent.EXTRA_TEXT   , "");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        orderHistory = new ArrayList<Post>();

        // set the toggle
        final Button posts = v.findViewById(R.id.toggleposts);
        final Button reviews = v.findViewById(R.id.toggleReviews);
        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPosts = true;
                viewReviews = false;
                posts.setBackgroundColor(getResources().getColor(R.color.white));
                reviews.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                viewHistory();
            }
        });
        posts.setTypeface(italic);

        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPosts = false;
                viewReviews = true;
                posts.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                reviews.setBackgroundColor(getResources().getColor(R.color.white));
                viewReviews();
            }
        });
        reviews.setTypeface(italic);

        if (viewPosts) viewHistory();
        if (viewReviews) viewReviews();

	    //Move to the Main Activity if you're already logged in
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                if (!isLoggedIn()) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
        return v;
    }

    final boolean orders = false;

    public static String getProfile(String userId, final RatingBar mRating, final TextView profileName,
                                    final ImageView profilePicture, final Context currContext){

        FirebaseDatabase.getInstance().getReference().child("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User currUser = dataSnapshot.getValue(User.class);
                        Glide.with(currContext).load(currUser.getProfilephoto()).into(profilePicture);
                        mRating.setRating(currUser.getAverageRating());
                        profileName.setText(currUser.getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return "NotNull";
    }
    public void viewReviews() {
        final DatabaseReference mRef = mDatabase.getReference("users").child(userQuery).child("reviews");

        final ArrayList<String> reviews = new ArrayList<String>();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    reviews.add(postSnapshot.getValue(String.class));
                }
                reviewSnippetAdapter = new ReviewSnippetAdapter(getActivity(), reviews);
                ordersView.setAdapter(reviewSnippetAdapter);
                reviewSnippetAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public ArrayList<Post> viewHistory() {
        if (!userQuery.equals(mAuth.getCurrentUser().getUid())) {return new ArrayList<Post>();}
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final DatabaseReference mRef = mDatabase.getReference("posts").child(currentUser.getUid());
        orderHistory.clear();
        // Code taken from Firebase tutorial:
        // https://firebase.google.com/docs/database/android/lists-of-data
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    final Post temporaryPost = postSnapshot.getValue(Post.class);
                    // take and add objects
                    orderHistory.add(temporaryPost);
                }

                postSnippetAdapter = new ProfileFragment.PostSnippetAdapter(getActivity(), orderHistory, orders);
                ordersView.setAdapter(postSnippetAdapter);
                postSnippetAdapter.notifyDataSetChanged();
                // I know this is ugly, but lets see if it works okay
                ordersView.invalidateViews();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        //TODO: GET THE LIST VIEW AND UPDATE VIEW
        return orderHistory;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String interaction) {
        if (mListener != null) {
            mListener.onFragmentInteractionProfile(interaction);
        }
    }

    // Adapter for Review Snippets
    public class ReviewSnippetAdapter extends ArrayAdapter<String> {

        private ArrayList<String> reviews;

        // Constructor takes in an ArrayList to use for the tutors.
        public ReviewSnippetAdapter(Context context, ArrayList<String> reviews) {
            super(context, 0, reviews);
            this.reviews = reviews;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // Inflates the layout.
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_snippet, null);
            }

            String selectedReview = reviews.get(position);

            TextView titleView = convertView.findViewById(R.id.reviewText);
            titleView.setText(selectedReview);
            titleView.setTypeface(light);

            return convertView;
        }
    }

    // Adapter for Post Snippets
    public class PostSnippetAdapter extends ArrayAdapter<Post> {

        private ArrayList<Post> posts;
        private Button viewDetails;
        private boolean profileList;

        // Constructor takes in an ArrayList to use for the tutors.
        public PostSnippetAdapter(Context context, ArrayList<Post> posts, boolean profileList) {
            super(context, 0, posts);
            this.posts = posts;
            this.profileList = profileList;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // Inflates the layout.
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.feed_post_snippet, null);
            }

            Post selectedPost = posts.get(position);


            TextView titleView = convertView.findViewById(R.id.postName);
            titleView.setText(selectedPost.getPostName());
            titleView.setTypeface(bold);

            final TextView usernameView = convertView.findViewById(R.id.userName);
            final TextView rating = convertView.findViewById(R.id.rating_snippet);
            usernameView.setText("");
            usernameView.setTypeface(italic);
            rating.setText("");
            rating.setTypeface(italic);
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child("users").child(selectedPost.getOriginalPoster()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User tempUser = dataSnapshot.getValue(User.class);
                    usernameView.setText(tempUser.getName());
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);

                    rating.setText("Rating:  " + df.format(tempUser.getAverageRating()) + " / 5");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            TextView locationView = convertView.findViewById(R.id.snippet_location);
            locationView.setText(selectedPost.getLocation());
            locationView.setTypeface(italic);

            viewDetails = (Button) convertView.findViewById(R.id.view_snippet_details);
            viewDetails.setTypeface(bold);
            if (profileList == false) {
                //viewDetails.setEnabled(false);
            }

            viewDetails.setTag(position);

            viewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // We are retrieving the tag we set earlier here.
                    int clickedPosition = (int) v.getTag();
                    Intent i = new Intent(getActivity(), DetailedPostActivity.class);
                    i.putExtra(FEED_POSITION, clickedPosition);
                    i.putExtra(SCREEN_NAME, "Profile_Fragment");

                    startActivity(i);
                }
            });

            return convertView;
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
        void onFragmentInteractionProfile(String interaction);
    }
}
