package cs310.fidgetspinners.grubmate.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.model.Request;
import cs310.fidgetspinners.grubmate.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailedPostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailedPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailedPostFragment extends Fragment {
    private static final String POSITION = "fidgetspinners.grubmate.post.snippet.position";
    private static final String SCREEN_NAME = "fidgetspinners.grubmate.post.screen.name";
    public static final String CURRENT_USER = "fidgetspinners.detailedpostfragment.currentuser";
    public static final String OTHER_USER = "fidgetspinners.detailedpostfragment.otheruser";

    // TODO: Rename and change types of parameters
    private int position;
    private String currentName;
    private Post postitem;
    private String screenName;

    private static NotificationFragment.RequestSnippetAdapter requestAdapter;

    private final String TAG = "DEATILED POST:";

    private ArrayList<Request> displayRequests;

    private OnFragmentInteractionListener mListener;

    public DetailedPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailedPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailedPostFragment newInstance(int position, String screenName) {
        DetailedPostFragment fragment = new DetailedPostFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putString(SCREEN_NAME, screenName);
        fragment.setArguments(args);
        return (DetailedPostFragment) fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION);
            screenName = getArguments().getString(SCREEN_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detailed_post, container, false);

        final Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Bold.ttf");
        final Typeface light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");
        final Typeface italic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Italic.ttf");

        //TODO position specifies which post we need to load here.
        if(screenName.equals("Feed_Fragment")) {
            postitem = FeedFragment.posts.get(position);
        }
        else if(screenName.equals("Profile_Fragment")) {
            postitem = ProfileFragment.orderHistory.get(position);
        }
        else if(screenName.equals("Notification_Fragment")) {
            postitem = NotificationFragment.subscriptionPostsSearched.get(position);
        }

        //Toast.makeText(getActivity(), postitem.getPostName(), Toast.LENGTH_SHORT).show();


        //fill in post details

        final TextView viewPostName = v.findViewById(R.id.detailedPostName);
        final TextView posterRating = v.findViewById(R.id.poster_rating);
        viewPostName.setTypeface(bold);
        posterRating.setTypeface(light);
        FirebaseDatabase.getInstance().getReference().child("users").child(postitem.getOriginalPoster())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User tempUser = dataSnapshot.getValue(User.class);
                        String authorName = tempUser.getName();
                        viewPostName.setText(postitem.getPostName()+ " (by "+authorName+")");

                        viewPostName.setTextColor(Color.BLUE);
                        SpannableString content = new SpannableString(viewPostName.getText());
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        viewPostName.setText(content);


                        DecimalFormat df = new DecimalFormat();
                        df.setMaximumFractionDigits(2);
                        posterRating.setText("Poster Rating: " + df.format(tempUser.getAverageRating()) + " / 5");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        viewPostName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = ProfileFragment.newInstance(postitem.getOriginalPoster(), "");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.post_frame_detailed, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        // disable button if after range

        TextView description = v.findViewById(R.id.detailedPostDescription);
        description.setText(postitem.getDescription());
        description.setTypeface(italic);

        Button type = v.findViewById(R.id.detailedPostType);
        type.setText(postitem.getType());
        type.setClickable(false);
        type.setTypeface(light);

        Button category = v.findViewById(R.id.detailedPostCategory);
        category.setText(postitem.getCategory());
        category.setClickable(false);
        category.setTypeface(light);

        Button price = v.findViewById(R.id.detailedPostPrice);
        price.setText("$"+postitem.getPrice());
        price.setClickable(false);
        price.setTypeface(light);

        LinearLayout tagLayout = v.findViewById(R.id.detailedPostTagsLayout);
        LinearLayout row = null;

        if (postitem.getTags() != null) {
            for (int i = 0; i < postitem.getTags().size()-2; i++) {
                if (i % 4 == 0) {
                    row = new LinearLayout(this.getContext());
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setGravity(Gravity.CENTER);
                    tagLayout.addView(row);
                }
                //Button currTag = new Button(this.getContext());
                // here we can inflate a button we design using a custom layout file TODO
                Button currTag = (Button) this.getLayoutInflater().inflate(R.layout.tag, null);
                currTag.setTypeface(light);
                //currTag.setClickable(false);
                currTag.setText(postitem.getTags().get(i));
                row.addView(currTag);
            }
        }

        TextView location = v.findViewById(R.id.detailedPostLocation);
        location.setText(postitem.getLocation());
        location.setTypeface(italic);

        TextView availablePortions = v.findViewById(R.id.detailedPostPortions);
        availablePortions.setText(""+postitem.getPortionsAvailable());
        availablePortions.setTypeface(italic);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yy");

        TextView startRange = v.findViewById(R.id.detailedPostRange);
        startRange.setText("Time:   "+sdf.format(postitem.getStartRange())+ "     to     " +
                sdf.format(postitem.getEndRange()));

        startRange.setTypeface(italic);
        
        //if user id isn't the same, allow requests
        String posterName = postitem.getOriginalPoster();

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage mStorage = FirebaseStorage.getInstance();

        FloatingActionButton messageButton = (FloatingActionButton) v.findViewById(R.id.message_user);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUser.getUid().equals(postitem.getOriginalPoster())) {
                    Intent i = new Intent(getContext(), MessageRoomActivity.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(getContext(), MessageActivity.class);
                    i.putExtra(CURRENT_USER, currentUser.getUid());
                    i.putExtra(OTHER_USER, postitem.getOriginalPoster());
                    startActivity(i);
                }
            }
        });

        final DatabaseReference mRef = mDatabase.getReference("users").child(currentUser.getUid());

        LinearLayout sv = (LinearLayout) v.findViewById(R.id.view_images);

        if(postitem.getPictures() != null) {
            for(int i=0; i< postitem.getPictures().size(); i++) {
                int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                int height = Resources.getSystem().getDisplayMetrics().heightPixels;

                ImageView imageView = new ImageView (this.getContext());
                imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height/3));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(mStorage.getReference().child(postitem.getPictures().get(i)))
                        .into(imageView);


                sv.addView(imageView);
            }
        } else {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            int height = Resources.getSystem().getDisplayMetrics().heightPixels;
            ImageView imageView = new ImageView (this.getContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height/3));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(R.drawable.grubmate_logo); // default grubmate logo if no picture was uploaded
            sv.addView(imageView);
        }


        Button requestButton = (Button) v.findViewById(R.id.sendRequest);
        Button editButton = (Button) v.findViewById(R.id.editPost);
        Button deleteButton = (Button) v.findViewById(R.id.deletePost);

        Date currentTime = Calendar.getInstance().getTime();
        if (currentTime.after(postitem.getEndRange())) {
            requestButton.setEnabled(false);
            requestButton.setText("Expired Request");
        }

        if ((currentUser.getUid()).equals(posterName)) {
            //set to invisible
            requestButton.setVisibility(View.GONE);
        } else {
            deleteButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
        }
        requestButton.setTypeface(bold);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // grab the requests, check if one of them is confirmed
                ArrayList<Request> requests = postitem.getRequests();

                if (requests != null) {
                    if (requests.isEmpty()) {
                        deletePost(postitem);
                    } else {
                        for (int i=0; i < requests.size(); i++) {
                            if (requests.get(i).getStatus() == Request.BOTH_CONFIRMED) {
                                deletePost(postitem);
                                return;
                            }
                        }
                        Toast.makeText(getContext(), "Someone already requested your " +
                                "item.", Toast.LENGTH_LONG);
                        return;
                    }
                } else {
                    deletePost(postitem);
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postitem.getRequests() != null) {
                    if (postitem.getRequests().isEmpty()) {
                        deletePost(postitem);
                        Intent i = new Intent(getContext(), PostActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(getContext(), "Someone already requested your " +
                                "item.", Toast.LENGTH_LONG);
                        return;
                    }
                } else {
                    deletePost(postitem);
                    Intent i = new Intent(getContext(), PostActivity.class);
                    startActivity(i);
                }
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestItem(v);
            }
        });

        // configure the listview
        final ListView listView = (ListView) v.findViewById(R.id.request_list_from_detailed);
        boolean isMyPost = postitem.getOriginalPoster().equals(mAuth.getCurrentUser().getUid());

        // configure adapters
        displayRequests = new ArrayList<Request>();
        requestAdapter =
                new NotificationFragment.RequestSnippetAdapter(getContext(), displayRequests, !isMyPost, this.getActivity(), true, true);
        listView.setAdapter(requestAdapter);

        // grab the requests
        if (isMyPost) {
            DatabaseReference myPostRef = FirebaseDatabase.getInstance().getReference()
                    .child("posts").child(mAuth.getCurrentUser().getUid()); //.child()

            myPostRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Post tempPost = dataSnapshot.getValue(Post.class);
                    if (tempPost.getPostName().equals(postitem.getPostName())
                            && tempPost.getCategory().equals(postitem.getCategory())) {
                        // grab all the requests
                        if(tempPost.getRequests() != null) {
                            for (int i = 0; i < tempPost.getRequests().size(); i++) {
                                displayRequests.add(tempPost.getRequests().get(i));
                            }
                            System.out.println("new size of display: " + displayRequests.size());
                            requestAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            DatabaseReference myPostRef = FirebaseDatabase.getInstance().getReference()
                    .child("posts").child(postitem.getOriginalPoster()); //.child()

            myPostRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    System.out.println("received item!");

                    Post tempPost = dataSnapshot.getValue(Post.class);
                    if (tempPost.getPostName().equals(postitem.getPostName())
                            && tempPost.getCategory().equals(postitem.getCategory())) {
                        // grab all the requests
                        if (tempPost.getRequests() == null) return;

                        for (int i=0; i < tempPost.getRequests().size(); i++) {
                            // check if its yours!!
                            if (tempPost.getRequests().get(i).getRequestingUser()
                                    .equals(mAuth.getCurrentUser().getUid())) {
                                displayRequests.add(tempPost.getRequests().get(i));
                            }
                        }
                        requestAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string) {
        if (mListener != null) {
            mListener.onFragmentInteraction(string);
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

    public void requestItem(View view) {
        Fragment fragment = RequestFragment.newInstance(postitem);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.post_frame_detailed, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void deletePost(final Post post) {
        System.out.println("called delete post");
        FirebaseDatabase DB = FirebaseDatabase.getInstance();
        // delete from activeposts
        DatabaseReference postRef = DB.getReference("users")
                .child(post.getOriginalPoster()).child("activePosts");

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post iteratePost = postSnapshot.getValue(Post.class);
                    if (iteratePost.getPostName().equals(post.getPostName())
                            && post.getLatitude() == iteratePost.getLatitude()) {
                        postSnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // delete from posts
        DatabaseReference postRef2 = DB.getReference("posts").child(post.getOriginalPoster());

        postRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post iteratePost = postSnapshot.getValue(Post.class);
                    if (iteratePost.getPostName().equals(post.getPostName())
                            && post.getLatitude() == iteratePost.getLatitude()) {
                        postSnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // delete from feed
        DatabaseReference feedRef = DB.getReference("feed");
        feedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : userSnapshot.getChildren()) {
                        Post iteratePost = postSnapshot.getValue(Post.class);
                        if (iteratePost.getPostName().equals(post.getPostName())
                                && post.getLatitude() == iteratePost.getLatitude()) {
                            postSnapshot.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        System.out.println("deleted everything");

        // transition out of the activity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}