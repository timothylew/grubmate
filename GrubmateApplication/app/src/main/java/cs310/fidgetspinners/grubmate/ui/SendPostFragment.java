package cs310.fidgetspinners.grubmate.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.model.Subscription;
import cs310.fidgetspinners.grubmate.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SendPostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SendPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendPostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String POST = "fidgetspinners.grubmate.csci310.post.sendpostfragment";

    // TODO: Rename and change types of parameters
    private Post post = null;
    private String mParam2;

    private FloatingActionButton sendPost;
    private Button facebookButton;
    private TextView testView;
    static private ArrayList<Post> existingPosts = null;
    static private ArrayList<Post> existingFeed = null;
    private FirebaseUser user = null;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    private JSONArray friendArray;

    private final String TAG = "SEND POST:";

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private OnFragmentInteractionListener mListener;


    public SendPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment SendPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendPostFragment newInstance(Post post) {
        SendPostFragment fragment = new SendPostFragment();
        Bundle args = new Bundle();
        args.putSerializable(POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            post = (Post) getArguments().getSerializable(POST);
        }
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
    }

    public static int SendPost(DataSnapshot dataSnapshot, String UID, Post post) {
        User u = dataSnapshot.getValue(User.class);
        existingPosts = u.getActivePosts();
        if(existingPosts == null) {
            existingPosts = new ArrayList<Post>();
        }
        int newIndex = existingPosts.size();

        // dont add posts wtice
        if (existingPosts.contains(post)) return -1;

        FirebaseDatabase.getInstance().getReference().child("posts")
                .child(UID).child(Integer.toString(newIndex)).setValue(post);

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(UID).child("activePosts")
                .child(Integer.toString(newIndex)).setValue(post);

        return 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_post, container, false);

        final ArrayList<CheckBox> checkBoxes = new ArrayList<CheckBox>();

        // Load Groups
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("groups")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        final LinearLayout groupLayout = v.findViewById(R.id.send_post_checklist);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // only trigger on visible
                if (getView() == null || !getView().isShown()) {
                    return;
                }

                checkBoxes.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    final String groupName = postSnapshot.getKey();

                    CheckBox groupButton= new CheckBox(getContext());
                    groupButton.setText(groupName);

                    checkBoxes.add(groupButton);

                    groupLayout.addView(groupButton);
                }

                final String groupName = "All friends";
                CheckBox groupButton = new CheckBox(getContext());
                groupButton.setText(groupName);
                checkBoxes.add(groupButton);
                groupLayout.addView(groupButton);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        // Button to submit
        sendPost = (FloatingActionButton) v.findViewById(R.id.send_post);

        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add to user's actives and posts
                user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase.getInstance().getReference().child("users").
                        child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        SendPost(dataSnapshot, user.getUid(), post);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // Send this post to other people's feeds
                // pull all the users out


                if (checkBoxes.get(checkBoxes.size() - 1).isChecked()) {
                    /* make the API call */
                    String URI = "/" + AccessToken.getCurrentAccessToken().getUserId() + "/friends";

                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            URI,
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    try {
                                        friendArray = response.getJSONObject().getJSONArray("data");
                                        System.out.println("DATA: " + friendArray.toString());

                                        for (int i=0; i < friendArray.length(); i++) {

                                            JSONObject friend = friendArray.getJSONObject(i);
                                            SendFeed(friend.get("id").toString(), post);

                                        }
                                    } catch (JSONException JSONe) {
                                        System.out.println("exception: " + JSONe.toString());
                                    }
                                }
                            }
                    ).executeAsync();
                } else {
                    final Set<String> usersPostedTo = new HashSet<String>();
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        if (checkBoxes.get(i).isChecked()) {
                            DatabaseReference userRef = FirebaseDatabase.getInstance()
                                    .getReference("groups")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(checkBoxes.get(i).getText().toString())
                                    .child("groupMembers");

                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        final String userID = postSnapshot.child("userID").getValue(String.class);
                                        if (!usersPostedTo.contains(userID)) {
                                            SendFeed(userID, post);
                                            usersPostedTo.add(userID);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Getting Post failed, log a message
                                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                }

                            });
                        }
                    }
                }
                onButtonPressed("send", null);
            }
        });

        //ref: https://developers.facebook.com/docs/sharing/android/
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://sunset.usc.edu/classes/cs310_2017b/hw/CS310%20Assignment%201%20-%20Requirements%20Specification.pdf"))
                .build();

        ShareButton shareButton = v.findViewById(R.id.share_button);
        shareButton.setShareContent(content);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String interaction, Post post) {
        if (mListener != null) {
            mListener.onFragmentInteraction(interaction, post);
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
        void onFragmentInteraction(String interaction, Post post);
    }

    public void SendFeed(String UserID, final Post post) {
        System.out.println("Calling send feed");

        FirebaseDatabase.getInstance().getReference().child("users")
            .orderByChild("FacebookID").equalTo(UserID)
            .addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    System.out.println("FOUND CHILD: " + dataSnapshot.getKey());
                    SendSubscriptions(dataSnapshot.getKey(),post);
                    PullOldFeed(dataSnapshot.getKey());
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

    public void PullOldFeed(final String UserID) {
        // pull old feed
        FirebaseDatabase.getInstance().getReference().child("feed")
                .child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Post> aOP = (ArrayList<Post>) dataSnapshot.getValue();

                if(aOP == null) {
                    existingFeed = new ArrayList<Post>();
                } else {
                    existingFeed = aOP;
                }

                if (post != null) {
                    existingFeed.add(post);
                    FirebaseDatabase.getInstance().getReference().child("feed")
                            .child(UserID).setValue(existingFeed);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void SendSubscriptions(final String UserID, final Post post) {
        System.out.println("Calling send subscriptions");
        // push to subscription data
        DatabaseReference susRef = FirebaseDatabase.getInstance()
                .getReference("subscriptions").child(UserID);
        // first, pull subscription data and grab valid subscriptions
        susRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("data key grabbed:" + dataSnapshot.getKey());
                for (DataSnapshot susSnapshot : dataSnapshot.getChildren()) {
                    System.out.println("suscription key:" + susSnapshot.getKey());
                    final Subscription subscription = susSnapshot.getValue(Subscription.class);
//                    Date currentDate  = new Date(System.currentTimeMillis());

                    // check if the tags match
                    List<String> postTags = post.getTags();
                    List<String> susTags
                            = new ArrayList<String>(Arrays.asList(subscription.getSubscriptionName().toLowerCase()
                            .trim().split(" ")));

                    System.out.println("this posts tags:" + postTags);
                    System.out.println("subscription posts tags:" + susTags);

                    susTags.retainAll(postTags);

                    System.out.println("common tags:" + susTags);

                    // check if the date is correct
                    if (!susTags.isEmpty() &&
                            post.getEndRange().after(subscription.getStartTime()) &&
                             post.getStartRange().before(subscription.getEndTime())) {
                        System.out.println("PUSHING THIS SHIT");
                        System.out.println("TO THE DB OF " + UserID);
                        FirebaseDatabase.getInstance().getReference("subscriptionData")
                                .child(UserID).push()
                                .setValue(post);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
