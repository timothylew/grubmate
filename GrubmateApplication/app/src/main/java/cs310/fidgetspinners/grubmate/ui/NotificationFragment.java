package cs310.fidgetspinners.grubmate.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.ArrayList;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.Util;
import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.model.Request;
import cs310.fidgetspinners.grubmate.model.User;
import cs310.fidgetspinners.grubmate.utility.GlideDrawableViewBackgroundTarget;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String TAG = "NOTIFICATION: ";

    private String mUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public static ArrayList<Post> subscriptionPosts = new ArrayList<Post>();
    public static final ArrayList<Post> subscriptionPostsSearched = new ArrayList<Post>();

    //TOggle for listviews
    private static String INCOMINGREQUESTS = "incomingRequests";
    private static String OUTGOINGREQUESTS = "outgoingRequests";
    private static String SUBSCRIPTIONS = "subscriptionData";

    private static String toggle = INCOMINGREQUESTS;

    private static String lastClicked = "";

    private static final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private OnFragmentInteractionListener mListener;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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
        final View v = inflater.inflate(R.layout.fragment_notification, container, false);
        final Button incoming = v.findViewById(R.id.incoming);
        final Button outgoing = v.findViewById(R.id.outgoing);
        final Button subscriptions = v.findViewById(R.id.subscribe);
        final Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Bold.ttf");
        final Typeface light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");
        final Typeface italic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Italic.ttf");

        if (lastClicked.isEmpty() || lastClicked == INCOMINGREQUESTS) {
            incoming.setTypeface(light);
            outgoing.setTypeface(italic);
            subscriptions.setTypeface(italic);
            lastClicked = INCOMINGREQUESTS;
        } else if (lastClicked == OUTGOINGREQUESTS) {
            incoming.setTypeface(italic);
            outgoing.setTypeface(light);
            subscriptions.setTypeface(italic);
            lastClicked = OUTGOINGREQUESTS;
        } else if (lastClicked == SUBSCRIPTIONS) {
            incoming.setTypeface(italic);
            outgoing.setTypeface(italic);
            subscriptions.setTypeface(light);
            lastClicked = SUBSCRIPTIONS;
        }
        refresh(v);


        incoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incoming.setTypeface(light);
                outgoing.setTypeface(italic);
                subscriptions.setTypeface(italic);
                incoming.setBackgroundColor(getResources().getColor(R.color.white));
                outgoing.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                subscriptions.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                toggle = INCOMINGREQUESTS;
                lastClicked = INCOMINGREQUESTS;
                refresh(v);
            }
        });

        outgoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incoming.setTypeface(italic);
                outgoing.setTypeface(light);
                subscriptions.setTypeface(italic);
                incoming.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                outgoing.setBackgroundColor(getResources().getColor(R.color.white));
                subscriptions.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                toggle = OUTGOINGREQUESTS;
                lastClicked = OUTGOINGREQUESTS;

                refresh(v);
            }
        });

        subscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incoming.setTypeface(italic);
                outgoing.setTypeface(italic);
                subscriptions.setTypeface(light);
                incoming.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                outgoing.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                subscriptions.setBackgroundColor(getResources().getColor(R.color.white));
                toggle = SUBSCRIPTIONS;
                lastClicked = SUBSCRIPTIONS;

                refresh(v);
            }
        });

        return v;
    }

    private void refresh(View v) {
        DatabaseReference mRef = null;

        final ArrayList<Request> notifyingRequests = new ArrayList<Request>();
        subscriptionPosts = new ArrayList<Post>();

        mRef = FirebaseDatabase.getInstance().getReference()
                .child(toggle).child(mUserID);

        // the outgoing boolean controlls the enabling of the button
        boolean outgoing = false;
        if (toggle.equals(OUTGOINGREQUESTS)) {
            outgoing = true;
        }

        // hook up the adapter : note, every time u change data, u need to notify the adapter
        ListView dynamicListView = v.findViewById(R.id.dynamic_list);

        // taken from the firebase childlistener tutorial
        if (!toggle.equals(SUBSCRIPTIONS)) {
            final RequestSnippetAdapter requestSnippetAdapter
                    = new RequestSnippetAdapter(getActivity(), notifyingRequests, outgoing
                        , getActivity(), false, false);
            dynamicListView.setAdapter(requestSnippetAdapter);

            ChildEventListener requestsListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    Request request = dataSnapshot.getValue(Request.class);
                    notifyingRequests.add(request);

                    requestSnippetAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                }
            };
            mRef.addChildEventListener(requestsListener);
        } else {
            final PostSnippetAdapter subscriptionSnipppetAdapter
                    = new PostSnippetAdapter(getActivity(), subscriptionPosts, true);
            dynamicListView.setAdapter(subscriptionSnipppetAdapter);

            ChildEventListener requestsListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                    // this is going to be a post, not a request
                    Post subscribedPost = dataSnapshot.getValue(Post.class);
                    if (!mUserID.equals(subscribedPost.getOriginalPoster())) {
                        subscriptionPosts.add(subscribedPost);

                        subscriptionSnipppetAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                }
            };
            mRef.addChildEventListener(requestsListener);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        // TODO: Update argument type and name
        void onFragmentInteraction(String interaction);
    }

    // ADAPTERS FOR THE SUBSCRIPTION AND REQUEST LISTS
    // Adapter for Post Snippets
    public static class RequestSnippetAdapter extends ArrayAdapter<Request> {

        private ArrayList<Request> requests;
        private boolean outgoing;
        private static FragmentActivity viewActivity;
        private boolean isFromDetailedView;
        private boolean disableButtons;

        // Constructor takes in an ArrayList to use for the tutors.
        public RequestSnippetAdapter(Context context, ArrayList<Request> requests
                , boolean outgoing, FragmentActivity viewActivity, boolean isFromDetailedView,
                                     boolean disableButtons) {
            super(context, 0, requests);
            this.requests = requests;
            this.outgoing = outgoing;
            this.viewActivity = viewActivity;
            this.isFromDetailedView = isFromDetailedView;
            this.disableButtons = disableButtons;
        }

        public void refreshButtonView(Button acceptRequestButton, Request selectedRequest) {
            if (outgoing) {
                if (selectedRequest.getStatus() == Request.ACCEPTED
                        || selectedRequest.getStatus() == Request.OWNER_CONFIRMED) {
                    acceptRequestButton.setText("Confirm Request");
                    acceptRequestButton.setEnabled(true);
                } else if (selectedRequest.getStatus() == Request.REQUESTER_CONFIRMED) {
                    acceptRequestButton.setText("Confirmed");
                    acceptRequestButton.setEnabled(false);
                } else if (selectedRequest.getStatus() == Request.INITIALIZED) {
                    acceptRequestButton.setText("Your Request");
                    acceptRequestButton.setEnabled(false);
                } else if (selectedRequest.getStatus() == Request.BOTH_CONFIRMED) {
                    acceptRequestButton.setText("Complete");
                    acceptRequestButton.setEnabled(false);
                }

            } else {

                boolean isOwner = selectedRequest.getOriginalPost()
                        .getOriginalPoster()
                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid());

                if (selectedRequest.getStatus() == Request.ACCEPTED) {
                    acceptRequestButton.setText("Confirm Request");
                    acceptRequestButton.setEnabled(true);
                } else if (selectedRequest.getStatus() == Request.OWNER_CONFIRMED) {
                    if (isOwner) {
                        acceptRequestButton.setText("Confirmed");
                        acceptRequestButton.setEnabled(false);
                    } else {
                        acceptRequestButton.setText("Confirm Request");
                        acceptRequestButton.setEnabled(true);
                    }
                } else if (selectedRequest.getStatus() == Request.REQUESTER_CONFIRMED) {
                    if (isOwner) {
                        acceptRequestButton.setText("Confirm Request");
                        acceptRequestButton.setEnabled(true);
                    } else {
                        acceptRequestButton.setText("Confirmed");
                        acceptRequestButton.setEnabled(false);
                    }
                } else if (selectedRequest.getStatus() == Request.BOTH_CONFIRMED) {
                    acceptRequestButton.setText("Complete");
                    acceptRequestButton.setEnabled(false);
                } else if (selectedRequest.getStatus() == Request.INITIALIZED) {
                    acceptRequestButton.setText("Accept Request");
                    acceptRequestButton.setEnabled(true);
                }
            }

            if (disableButtons) {
                try {
                    acceptRequestButton.setBackgroundResource(0);
                    acceptRequestButton.setTextColor(Color.BLUE);
                } catch (NullPointerException nPE) {
                    nPE.printStackTrace();
                }
            }
        }

        public void updateOutgoingNotifications(final DatabaseReference databaseReference, final Request selectedRequest,
                                                final boolean alreadyConfirmed, final FirebaseAuth mAuth) {
            databaseReference.child("outgoingRequests").child(selectedRequest.getRequestingUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        String key = ds.getKey();
                        Request request = ds.getValue(Request.class);
                        if(selectedRequest.getOriginalPost().getPostName().equals(request.getOriginalPost().getPostName())
                                && selectedRequest.getOriginalPost().getStartRange().equals(request.getOriginalPost().getStartRange())
                                && selectedRequest.getRequestingUser().equals(request.getRequestingUser())
                                && selectedRequest.getPhoneNumber() == request.getPhoneNumber()
                                && selectedRequest.getNumShares() == request.getNumShares()) {
                            if (!alreadyConfirmed) {
                                databaseReference.child("outgoingRequests").child(selectedRequest.getRequestingUser())
                                        .child(key).child("status").setValue(Request.ACCEPTED);
                            } else {

                                System.out.println("CONFIRMING REQUEST AT KEY: " + selectedRequest.getRequestingUser());
                                boolean isOwner = selectedRequest.getOriginalPost()
                                        .getOriginalPoster().equals(mAuth.getCurrentUser().getUid());

                                        /*int temp = -1;
                                        if (selectedRequestOldSTateTwo != selectedRequest.getStatus()) {
                                            // save old state in a temp
                                            temp = selectedRequest.getStatus();
                                            selectedRequest.setStatus(selectedRequestOldSTateTwo);
                                        }*/

                                if (selectedRequest.getStatus() == Request.ACCEPTED) {
                                    System.out.println("STATUS IS ACCEPTED");
                                    if (isOwner) {
                                        databaseReference.child("outgoingRequests").child(selectedRequest.getRequestingUser())
                                                .child(key).child("status").setValue(Request.OWNER_CONFIRMED);
                                    } else {
                                        databaseReference.child("outgoingRequests").child(selectedRequest.getRequestingUser())
                                                .child(key).child("status").setValue(Request.REQUESTER_CONFIRMED);
                                    }
                                } else if (selectedRequest.getStatus() == Request.OWNER_CONFIRMED) {
                                    if (!isOwner) {
                                        databaseReference.child("outgoingRequests").child(selectedRequest.getRequestingUser())
                                                .child(key).child("status").setValue(Request.BOTH_CONFIRMED);
                                    }
                                } else if (selectedRequest.getStatus() == Request.REQUESTER_CONFIRMED) {
                                    if (isOwner) {
                                        databaseReference.child("outgoingRequests").child(selectedRequest.getRequestingUser())
                                                .child(key).child("status").setValue(Request.BOTH_CONFIRMED);
                                    }
                                } else if (selectedRequest.getStatus() == Request.BOTH_CONFIRMED) {
                                    // you should probably throw an exception here because this should not happen
                                }
                            }
                        }
                    }

                    updatePostObject(databaseReference,
                            selectedRequest, selectedRequest.getOriginalPost(), alreadyConfirmed, mAuth);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public static int updatePostObject(final DatabaseReference databaseReference, final Request selectedRequest,
                                     final Post post, final boolean alreadyConfirmed, final FirebaseAuth mAuth) {

            databaseReference.child("posts").child(selectedRequest.getOriginalPost().getOriginalPoster()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("What we want: " + post.toString());
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        final String key = ds.getKey();
                        Post postData = ds.getValue(Post.class);
                        System.out.println("COMPARING TO: " + postData.toString());
                        if(postData.getPostName().equals(post.getPostName())) System.out.println("1match");
                        if (postData.getDescription().equals(post.getDescription())) System.out.println("3match");

                        if(postData.getPostName().equals(post.getPostName())
                                && postData.getOriginalPoster().equals(post.getOriginalPoster())
                                && postData.getDescription().equals(post.getDescription())) {

                            System.out.println("Found the correct post.");
                            databaseReference.child("posts").child(post.getOriginalPoster()).child(key).child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                        String key1 = ds.getKey();
                                        Request request = ds.getValue(Request.class);
                                        if(request.getRequestingUser().equals(selectedRequest.getRequestingUser())
                                                && request.getLocation().equals(selectedRequest.getLocation())){

                                            System.out.println("found a request to update.");
                                            DatabaseReference requestReference = databaseReference.child("posts").child(post.getOriginalPoster()).child(key)
                                                    .child("requests").child(key1).child("status");

                                            if (!alreadyConfirmed) {
                                                System.out.println("SET STATUS TO ACCEPTED ");
                                                requestReference.setValue(Request.ACCEPTED);
                                                selectedRequest.setStatus(Request.ACCEPTED);
                                            } else {
                                                boolean isOwner = selectedRequest.getOriginalPost()
                                                        .getOriginalPoster().equals(mAuth.getCurrentUser().getUid());

                                                if (selectedRequest.getStatus() == Request.ACCEPTED) {
                                                    if (isOwner) {
                                                        requestReference.setValue(Request.OWNER_CONFIRMED);
                                                        selectedRequest.setStatus(Request.OWNER_CONFIRMED);
                                                        System.out.println("SET STATUS TO OWNER CONFIRMED");
                                                    } else {
                                                        requestReference.setValue(Request.REQUESTER_CONFIRMED);
                                                        selectedRequest.setStatus(Request.REQUESTER_CONFIRMED);
                                                        System.out.println("SET STATUS TO req CONFIRMED");
                                                    }
                                                } else if (selectedRequest.getStatus() == Request.OWNER_CONFIRMED) {
                                                    if (!isOwner) {
                                                        requestReference.setValue(Request.BOTH_CONFIRMED);
                                                        selectedRequest.setStatus(Request.BOTH_CONFIRMED);
                                                        System.out.println("SET STATUS TO DONE ");

                                                    }
                                                } else if (selectedRequest.getStatus() == Request.REQUESTER_CONFIRMED) {
                                                    if (isOwner) {
                                                        requestReference.setValue(Request.BOTH_CONFIRMED);
                                                        selectedRequest.setStatus(Request.BOTH_CONFIRMED);
                                                        System.out.println("SET STATUS TO DONE ");
                                                    }
                                                } else if (selectedRequest.getStatus() == Request.BOTH_CONFIRMED) {
                                                    // you should probably throw an exception here because this should not happen
                                                }

                                                // send a notification to requestee if you are the owner
                                                if (isOwner) {
                                                    String payload = "Your request for " + selectedRequest.getOriginalPost().getPostName()
                                                            + " was finalized!";
                                                    Util.sendRequest(selectedRequest.getRequestingUser(), payload);
                                                } else {
                                                    String payload = "Your request for " + selectedRequest.getOriginalPost().getPostName()
                                                            + " was finalized!";
                                                    Util.sendRequest(selectedRequest.getOriginalPost().getOriginalPoster(), payload);
                                                    // send notification to requester if you are the requester
                                                }



                                                //update button here, might as well
                                                /*if (selectedRequest.getStatus() == Request.REQUESTER_CONFIRMED
                                                        || selectedRequest.getStatus() == Request.OWNER_CONFIRMED) {
                                                    acceptRequestButton.setText("Confirmed");
                                                    acceptRequestButton.setEnabled(false);
                                                }*/

                                                // you need to do some updates if the request is both confirmed
                                                if (selectedRequest.getStatus() == Request.BOTH_CONFIRMED) {

                                                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("feed")
                                                            .child(selectedRequest.getRequestingUser());

                                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                                                Post postToRemove = postSnapshot.getValue(Post.class);
                                                                String index = postSnapshot.getKey();

                                                                if (postToRemove.getPostName().equals(selectedRequest.getOriginalPost().getPostName())
                                                                        && postToRemove.getLatitude() == selectedRequest.getOriginalPost().getLatitude()
                                                                        && postToRemove.getDescription().equals(selectedRequest.getOriginalPost().getDescription())
                                                                        && postToRemove.getPrice().equals(selectedRequest.getOriginalPost().getPrice())) {
                                                                    System.out.println("Removing the post from the " +
                                                                            "requesting user's feed");
                                                                    FirebaseDatabase.getInstance().getReference().child("feed")
                                                                            .child(selectedRequest.getRequestingUser())
                                                                            .child(index).removeValue();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                }

                                            }
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return selectedRequest.getStatus();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // Inflates the layout.
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.notification_request_snippet, null);
            }

            final Request selectedRequest = requests.get(position);
            final int selectedRequestOldState = selectedRequest.getStatus();
            final int selectedRequestOldSTateTwo = selectedRequest.getStatus();
            final TextView titleView = convertView.findViewById(R.id.requestAuthor);
            titleView.setText("");

            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child("users").child(selectedRequest.getRequestingUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User tempUser = dataSnapshot.getValue(User.class);
                    titleView.setText(tempUser.getName());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            TextView servingsView = convertView.findViewById(R.id.numServings);
            servingsView.setText("Number of Portions: " +
                    Integer.toString(selectedRequest.getNumShares()));

            TextView locationView = convertView.findViewById(R.id.snippetLocation);
            locationView.setText("Location: "+ selectedRequest.getLocation());

            TextView phoneView = convertView.findViewById(R.id.snippetPhoneNumber);
            phoneView.setText("Phone Number: "+ selectedRequest.getPhoneNumber());

            TextView detailView = convertView.findViewById(R.id.snippetDetails);
            detailView.setText("Details: " + selectedRequest.getRequestDetails());

            final Button acceptRequestButton = (Button) convertView.findViewById(R.id.acceptRequestButton);
            final Button denyRequestButton = (Button) convertView.findViewById(R.id.denyRequestButton);

            if (outgoing) denyRequestButton.setVisibility(View.GONE);
            denyRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Util.checkDeniedStatusValid(selectedRequest.getStatus()) == -1) {
                        Toast.makeText(getContext(), "You cannot deny a completed request", Toast.LENGTH_LONG);
                    } else if (Util.checkDeniedStatusValid(selectedRequest.getStatus()) == -2) {
                        Toast.makeText(getContext(), "You cannot deny an accepted request", Toast.LENGTH_LONG);
                    } else {
                        denyRequestButton.setText("Denied");
                        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        final FirebaseUser mUser = mAuth.getCurrentUser();
                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        // remove post from incoming
                        databaseReference.child("incomingRequests").child(selectedRequest.getOriginalPost().getOriginalPoster()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String key = ds.getKey();
                                    Request request = ds.getValue(Request.class);
                                    if(selectedRequest.getOriginalPost().getPostName().equals(request.getOriginalPost().getPostName())
                                            && selectedRequest.getOriginalPost().getStartRange().equals(request.getOriginalPost().getStartRange())
                                            && selectedRequest.getRequestingUser().equals(request.getRequestingUser())
                                            && selectedRequest.getPhoneNumber() == request.getPhoneNumber()) {
                                            databaseReference.child("incomingRequests").child(selectedRequest.getOriginalPost().getOriginalPoster())
                                                    .child(key).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        // remove post from outgoing
                        databaseReference.child("outgoingRequests").child(selectedRequest.getRequestingUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String key = ds.getKey();
                                    Request request = ds.getValue(Request.class);
                                    if(selectedRequest.getOriginalPost().getPostName().equals(request.getOriginalPost().getPostName())
                                            && selectedRequest.getOriginalPost().getStartRange().equals(request.getOriginalPost().getStartRange())
                                            && selectedRequest.getRequestingUser().equals(request.getRequestingUser())
                                            && selectedRequest.getPhoneNumber() == request.getPhoneNumber()) {
                                        databaseReference.child("outgoingRequests").child(selectedRequest.getRequestingUser())
                                                .child(key).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //remove request from post object
                        final Post post = selectedRequest.getOriginalPost();
                        databaseReference.child("posts").child(selectedRequest.getOriginalPost().getOriginalPoster()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                    final String key = ds.getKey();
                                    Post postData = ds.getValue(Post.class);
                                    if(postData.getPostName().equals(post.getPostName())
                                            && postData.getOriginalPoster().equals(post.getOriginalPoster())
                                            && postData.getDescription().equals(post.getDescription())) {

                                        databaseReference.child("posts").child(post.getOriginalPoster()).child(key).child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    String key1 = ds.getKey();
                                                    Request request = ds.getValue(Request.class);
                                                    if(request.getRequestingUser().equals(selectedRequest.getRequestingUser())
                                                            && request.getLocation().equals(selectedRequest.getLocation())){

                                                        databaseReference.child("posts").child(post.getOriginalPoster()).child(key)
                                                                .child("requests").child(key1).removeValue();
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        String payload = "Your request for " + selectedRequest.getOriginalPost().getPostName()
                                + " was denied";
                        Util.sendRequest(selectedRequest.getRequestingUser(), payload);

                        refreshButtonView(acceptRequestButton, selectedRequest);
                    }
                }
            });

            refreshButtonView(acceptRequestButton, selectedRequest);

            acceptRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // update button view
                    if (acceptRequestButton.getText().toString().startsWith("Your")) {
                        return;
                    }
                    refreshButtonView(acceptRequestButton, selectedRequest);

                    if (selectedRequest.getStatus() == Request.BOTH_CONFIRMED) { return; }
                    final boolean alreadyConfirmed = acceptRequestButton.getText().toString().trim().contains("Confirm");

                    if (alreadyConfirmed) {
                        String userID = "";
                        if(outgoing)
                            userID=selectedRequest.getOriginalPost().getOriginalPoster();
                        else
                            userID = selectedRequest.getRequestingUser();

                        RatingFragment nextFrag= RatingFragment.newInstance(userID);

                        if (isFromDetailedView) {
                            viewActivity.getFragmentManager().beginTransaction()
                                    .replace(R.id.main_frame_post_detailed, nextFrag)
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            viewActivity.getFragmentManager().beginTransaction()
                                    .replace(R.id.main_frame, nextFrag)
                                    .addToBackStack(null)
                                    .commit();
                            try {
                                MainActivity ma = (MainActivity) getContext();
                                ma.navigationBar.setVisibility(View.INVISIBLE);
                            } catch(Exception e) {

                            }
                        }
                    }

                    final Post post = selectedRequest.getOriginalPost();
                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    final FirebaseUser mUser = mAuth.getCurrentUser();
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    final int newNumPortions = post.getPortionsAvailable() - selectedRequest.getNumShares();

                    if (!alreadyConfirmed) {
                        databaseReference.child("posts").child(selectedRequest.getOriginalPost().getOriginalPoster())
                                .addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            String index = ds.getKey();
                                            Post postData = ds.getValue(Post.class);
                                            if (postData.getPostName().equals(post.getPostName())
                                                    && postData.getCategory().equals(post.getCategory())
                                                    && postData.getDescription().equals(post.getDescription())
                                                    && postData.getOriginalPoster().equals(post.getOriginalPoster())
                                                    && postData.getStartRange().equals(post.getStartRange())) {
                                                databaseReference.child("posts").child(selectedRequest.getOriginalPost().getOriginalPoster())
                                                        .child(index).child("portionsAvailable").setValue(newNumPortions);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                        databaseReference.child("feed").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String currentKey;
                                String currentIndex;
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    currentKey = ds.getKey();
                                    for (DataSnapshot ds1 : ds.getChildren()) {
                                        currentIndex = ds1.getKey();
                                        Post postData = ds1.getValue(Post.class);
                                        if (postData.getPostName().equals(post.getPostName())
                                                && postData.getCategory().equals(post.getCategory())
                                                && postData.getDescription().equals(post.getDescription())
                                                && postData.getOriginalPoster().equals(post.getOriginalPoster())
                                                && postData.getStartRange().equals(post.getStartRange())) {
                                            // do your shit here.
                                            databaseReference.child("feed").child(currentKey).child(currentIndex).
                                                    child("portionsAvailable").setValue(newNumPortions);
                                            selectedRequest.setNumShares(newNumPortions);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    System.out.println("testing incoming Requests");
                    System.out.println("what we want to match: " + selectedRequest.toString());
                    databaseReference.child("incomingRequests").child(selectedRequest.getOriginalPost().getOriginalPoster()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                String key = ds.getKey();
                                Request request = ds.getValue(Request.class);
                                if (selectedRequest.getOriginalPost().getPostName().equals(request.getOriginalPost().getPostName())) System.out.println("match1");
                                if (selectedRequest.getOriginalPost().getStartRange().equals(request.getOriginalPost().getStartRange())) System.out.println("match2");
                                if (selectedRequest.getRequestingUser().equals(request.getRequestingUser())) System.out.println("match3");
                                if (selectedRequest.getPhoneNumber() == request.getPhoneNumber()) System.out.println("match4");
                                if(selectedRequest.getOriginalPost().getPostName().equals(request.getOriginalPost().getPostName())
                                        && selectedRequest.getOriginalPost().getStartRange().equals(request.getOriginalPost().getStartRange())
                                        && selectedRequest.getRequestingUser().equals(request.getRequestingUser())
                                        && selectedRequest.getPhoneNumber() == request.getPhoneNumber()) {
                                    if (!alreadyConfirmed) {
                                        databaseReference.child("incomingRequests").child(selectedRequest.getOriginalPost().getOriginalPoster())
                                                .child(key).child("status").setValue(1);

                                        selectedRequest.setStatus(1);

                                        acceptRequestButton.setText("Confirm Request");
                                        acceptRequestButton.setEnabled(true);

                                    } else if (alreadyConfirmed) {
                                        // check if you are the owner
                                        boolean isOwner = selectedRequest.getOriginalPost()
                                                .getOriginalPoster().equals(mAuth.getCurrentUser().getUid());

                                        System.out.println("current status is : " + selectedRequest.getStatus());

                                        System.out.println("status to set is: " + selectedRequest.getStatus());
                                        if (selectedRequest.getStatus() == Request.ACCEPTED) {
                                            System.out.println("GONNA SET SHIT, OWNER IS : " + isOwner);
                                            if (isOwner) {
                                                databaseReference.child("incomingRequests").child(selectedRequest.getOriginalPost().getOriginalPoster())
                                                      .child(key).child("status").setValue(Request.OWNER_CONFIRMED);
                                            } else {
                                                databaseReference.child("incomingRequests").child(selectedRequest.getOriginalPost().getOriginalPoster())
                                                      .child(key).child("status").setValue(Request.REQUESTER_CONFIRMED);
                                            }
                                        } else if (selectedRequest.getStatus() == Request.OWNER_CONFIRMED) {
                                            if (!isOwner) {
                                                databaseReference.child("incomingRequests").child(selectedRequest.getOriginalPost().getOriginalPoster())
                                                        .child(key).child("status").setValue(Request.BOTH_CONFIRMED);
                                            }
                                        } else if (selectedRequest.getStatus() == Request.REQUESTER_CONFIRMED) {
                                            if (isOwner) {
                                                databaseReference.child("incomingRequests").child(selectedRequest.getOriginalPost().getOriginalPoster())
                                                        .child(key).child("status").setValue(Request.BOTH_CONFIRMED);
                                            }
                                        } else if (selectedRequest.getStatus() == Request.BOTH_CONFIRMED) {
                                            // you should probably throw an exception here because this should not happen
                                        }
                                    }
                                }
                            }

                            // do outgoing at the end
                            updateOutgoingNotifications(databaseReference, selectedRequest, alreadyConfirmed, mAuth);
                            refreshButtonView(acceptRequestButton, selectedRequest);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    // send notification to user
                    if (!alreadyConfirmed) {
                        String payload = "Your request for " + selectedRequest.getOriginalPost().getPostName()
                                + " was accepted!";
                        Util.sendRequest(selectedRequest.getRequestingUser(), payload);
                    }
                }
            });

            return convertView;
        }
    }

    // Adapter for Post Snippets
    public class PostSnippetAdapter extends ArrayAdapter<Post> {

        private ArrayList<Post> posts;
        private Button viewDetails;

        private boolean subscriptionValue;

        // Constructor takes in an ArrayList to use for the tutors.
        public PostSnippetAdapter(Context context, ArrayList<Post> posts, boolean subscriptionValue) {
            super(context, 0, posts);
            this.posts = posts;
            this.subscriptionValue = subscriptionValue;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // Inflates the layout.
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.feed_post_snippet, null);
            }

            Post selectedPost = posts.get(position);

            TextView titleView = convertView.findViewById(R.id.postName);
            titleView.setText(selectedPost.getPostName());

            final TextView usernameView = convertView.findViewById(R.id.userName);
            final TextView rating = convertView.findViewById(R.id.rating_snippet);
            usernameView.setText("");
            rating.setText("");
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

            viewDetails = (Button) convertView.findViewById(R.id.view_snippet_details);
            viewDetails.setTag(position);
            viewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // We are retrieving the tag we set earlier here.
                    int clickedPosition = (int) v.getTag();
                    if (subscriptionValue) {
                        final Post searchPost = subscriptionPosts.get(clickedPosition);

                        // grab the matching post in the posts object
                        // search for the corresponding post
                        DatabaseReference myPostRef = FirebaseDatabase.getInstance().getReference()
                                .child("posts").child(searchPost.getOriginalPoster());

                        myPostRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Post tempPost = dataSnapshot.getValue(Post.class);
                                System.out.println("TEMP POST SEARCH VAL: " + tempPost.getPostName());
                                if (tempPost.getPostName().equals(searchPost.getPostName())
                                        && tempPost.getCategory().equals(searchPost.getCategory())
                                        && tempPost.getLocation().equals(searchPost.getLocation())
                                        && tempPost.getStartRange().equals(searchPost.getStartRange())
                                        && tempPost.getPostName().equals(searchPost.getPostName())) {
                                    // grab all the requests
                                    System.out.println("found the post!");
                                    subscriptionPostsSearched.add(tempPost);
                                }

                                Intent i = new Intent(getActivity(), DetailedPostActivity.class);
                                i.putExtra(FeedFragment.FEED_POSITION, 0);
                                i.putExtra(FeedFragment.SCREEN_NAME, "Notification_Fragment");
                                startActivity(i);
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
                        Intent i = new Intent(getActivity(), DetailedPostActivity.class);
                        i.putExtra(FeedFragment.FEED_POSITION, clickedPosition);
                        i.putExtra(FeedFragment.SCREEN_NAME, "Notification_Fragment");
                        startActivity(i);
                    }
                }
            });

            if(selectedPost.getPictures() != null && selectedPost.getPictures().size() > 0) {
                View image_container = (View) convertView.findViewById(R.id.post_snippet_image);
                Glide.with(getContext())
                        .using(new FirebaseImageLoader())
                        .load(FirebaseStorage.getInstance().getReference().child(selectedPost.getPictures().get(0)))
                        .fitCenter().into(new GlideDrawableViewBackgroundTarget(image_container));
            }

            return convertView;
        }
    }
}
