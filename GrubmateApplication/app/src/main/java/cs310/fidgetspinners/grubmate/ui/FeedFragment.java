package cs310.fidgetspinners.grubmate.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.model.Request;
import cs310.fidgetspinners.grubmate.model.User;
import cs310.fidgetspinners.grubmate.utility.GlideDrawableViewBackgroundTarget;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */

public class FeedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String FEED_POSITION = "fidgetspinners.grubmate.feed.position";
    public static final String SCREEN_NAME = "fidgetspinners.grubmate.screen.name";
    private static final String TAG = "FEEDFRAGMENT: ";
    public static List<String> currentFilter = null;
    private static ArrayList<String> friendArray = new ArrayList<String>();
    public static boolean pullPosts = true;

    private ImageButton chineseFood;
    private ImageButton japaneseFood;
    private ImageButton mexicanFood;
    private ImageButton koreanFood;
    private ImageButton americanFood;
    private ImageButton hawaiianFood;
    private ImageButton italianFood;

    private FloatingActionButton mapButton;

    public static boolean filterOn = false;

    public static ArrayList<Post> posts;
    public static ArrayList<Post> filterposts;
    public static ArrayList<Post> backupposts;
    public static ArrayList<Request> activeRequests;
    //TODO: rename to displayedPosts to correspond with design doc

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static ListView feedView;
    public static ListView activePostView;
    public static PostSnippetAdapter postSnippetAdapter;
    public static NotificationFragment.RequestSnippetAdapter activeRequestSnippetAdapter;

    // Database members
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    private OnFragmentInteractionListener mListener;

    public FeedFragment() {
        // Required empty public constructoy
    }

    public static ArrayList<Post> GetPosts() {
        return posts;
    }

    public static void SetPosts(ArrayList<Post> newPosts) {
        posts.clear();
        for (int i=0; i < newPosts.size(); i++) {
            posts.add(newPosts.get(i));
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
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
        View v = inflater.inflate(R.layout.fragment_feed, container, false);

        feedView = (ListView) v.findViewById(R.id.feedView);
        activePostView = (ListView) v.findViewById(R.id.feed_activePosts);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        chineseFood = (ImageButton) v.findViewById(R.id.chinesefood);
        japaneseFood = (ImageButton) v.findViewById(R.id.japanesefood);
        mexicanFood = (ImageButton) v.findViewById(R.id.mexicanfood);
        koreanFood = (ImageButton) v.findViewById(R.id.koreanfood);
        americanFood = (ImageButton) v.findViewById(R.id.americanfood);
        hawaiianFood = (ImageButton) v.findViewById(R.id.hawaiianfood);
        italianFood = (ImageButton) v.findViewById(R.id.italianfood);

        chineseFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterPosts("Chinese");
            }
        });

        japaneseFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterPosts("Japanese");
            }
        });

        mexicanFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterPosts("Mexican");
            }
        });

        koreanFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterPosts("Korean");
            }
        });

        americanFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterPosts("American");
            }
        });

        hawaiianFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterPosts("Hawaiian");
            }
        });

        italianFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterPosts("Italian");
            }
        });

        mapButton = (FloatingActionButton) v.findViewById(R.id.map_button);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MapViewActivity.class);

                ArrayList<Post> markers = new ArrayList<Post>(5);

                for(int j = 0; j<5 && j<posts.size();j++){
                    //continue if post is expired
                    Date currentTime = Calendar.getInstance().getTime();
                    if (currentTime.after(posts.get(j).getEndRange()))
                        continue;

                    //add posts to array list
                    markers.add(posts.get(j));
                }

                //show error if no posts to display
                if(markers.size()< 1) {
                    Toast.makeText(getActivity(), "No posts to display.",
                            Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                i.putExtra("MARKERS", markers);
                startActivity(i);
            }
        });

        // Set up the adapter for this screen.
        System.out.println("Creating Feed Fragment view");
        posts = new ArrayList<Post>(); //TODO we need to grab the shit here...
        filterposts = new ArrayList<Post>();
        backupposts = new ArrayList<Post>();

        pullPostFeed();

        postSnippetAdapter = new PostSnippetAdapter(getActivity(), posts);
        feedView.setAdapter(postSnippetAdapter);
        postSnippetAdapter.notifyDataSetChanged();

        activeRequests = new ArrayList<Request>();

        activeRequestSnippetAdapter = new NotificationFragment.RequestSnippetAdapter(getActivity(), activeRequests, false, getActivity(), false, true);
        activePostView.setAdapter(activeRequestSnippetAdapter);
        activeRequestSnippetAdapter.notifyDataSetChanged();

        final TextView recentTransView = v.findViewById(R.id.recentTransactionsLabel);

        Query mRef = FirebaseDatabase.getInstance().getReference()
                .child("incomingRequests").child(mAuth.getCurrentUser().getUid()).limitToLast(3);

        ChildEventListener requestsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                Request request = dataSnapshot.getValue(Request.class);

                activeRequests.add(request);
                activeRequestSnippetAdapter.notifyDataSetChanged();

                if (activeRequests.size() == 0) {
                    activePostView.setVisibility(View.GONE);
                    recentTransView.setVisibility(View.GONE);
                } else {
                    activePostView.setVisibility(View.VISIBLE);
                    recentTransView.setVisibility(View.VISIBLE);
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

        System.out.println("Adapter set");

        return v;
    }

    public static ArrayList<Post> filterPosts(String categoryName) {
//        filterOn = true;
//        pullPosts = false;
//        pullPostFeed(); //get fresh post list
//        pullPosts = true;
        if (categoryName.isEmpty()) return null;

        for (Post individualPost : backupposts) {
            System.out.println(individualPost.getPostName());
        }

        ArrayList<Post> toAdd = new ArrayList<Post>();
        for (Post individualPost : backupposts) {
            if (categoryName.equals(individualPost.getCategory())) {
                toAdd.add(individualPost);
            }
        }

        posts.clear();
        posts.addAll(toAdd);

        if (postSnippetAdapter != null) postSnippetAdapter.notifyDataSetChanged();

        return posts;
    }

    public void sortByCategory(String category) {
        //TODO
    }


    static void searchForPost(String query) {
        if (query.replaceAll("\\s","").equals("")) { currentFilter = null; pullPostFeed(); return; }
        currentFilter = Arrays.asList(query.toLowerCase().split(" "));
        pullPostFeed();
    }

    static public void setPosts(ArrayList<Post> newPosts) {
        if (posts == null) {
            posts = new ArrayList<Post>();
            backupposts = new ArrayList<Post>();
            filterposts = new ArrayList<Post>();
        }
        posts.clear();
        posts.addAll(newPosts);

        //if (filterposts.size() > 0) filterposts.clear();
        filterposts.clear();
        filterposts.addAll(newPosts);

        for (int i=0; i < filterposts.size(); i++) {
            System.out.println("SET:" + posts.get(i).getPostName());
            System.out.println("SET:" + filterposts.get(i).getPostName());
        }

        backupposts.clear();
        backupposts.addAll(newPosts);
        if (postSnippetAdapter != null) postSnippetAdapter.notifyDataSetChanged();
    }

    // Pull post feed
    static public void pullPostFeed() {
//        if(!pullPosts) {
//            pullPosts = true;
//            return;
//        }

        System.out.println("CALLING PULLPOST FEED WITH FILTER: " + currentFilter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        final DatabaseReference mRef = mDatabase.getReference("feed")
                .child(mAuth.getCurrentUser().getUid());

        // Code taken from Firebase tutorial:
        // https://firebase.google.com/docs/database/android/lists-of-data
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();
                backupposts.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Post temporaryPost = postSnapshot.getValue(Post.class);
                    if(temporaryPost.getPortionsAvailable() > 0) {
                        List<String> temporaryPostTagList = temporaryPost.getTags();
                        List<String> temporaryPostNameList =
                                new ArrayList<String>(Arrays.asList(temporaryPost.getPostName().split(" ")));
                        List<String> temporaryPostDescList =
                                new ArrayList<String>(Arrays.asList(temporaryPost.getDescription().split(" ")));

                        // set tags to all lowercase to maek string comp easy
                        if (temporaryPostTagList != null) {
                            for (int i = 0; i < temporaryPostTagList.size(); i++) {
                                temporaryPostTagList.set(i, temporaryPostTagList.get(i).toLowerCase());
                            }
                        }

                        // only search if the filter contains something
                        if (currentFilter != null) {
                            if (!currentFilter.isEmpty()) {
                                if (temporaryPostTagList != null) {
                                    temporaryPostTagList.retainAll(currentFilter);
                                }
                                if (temporaryPostNameList != null) {
                                    temporaryPostNameList.retainAll(currentFilter);
                                }
                                if (temporaryPostDescList != null) {
                                    temporaryPostDescList.retainAll(currentFilter);
                                }
                                if (!temporaryPostTagList.isEmpty() || !temporaryPostNameList.isEmpty()
                                        || !temporaryPostDescList.isEmpty()) {
                                    posts.add(temporaryPost);
                                    backupposts.add(temporaryPost);
                                }

                                // check if description fits


                            } else {
                                // CHECK IF THE CURRENT FILTER IS SANE.
                                posts.add(temporaryPost);
                                backupposts.add(temporaryPost);
                            }
                        } else {
                            // CHECK IF THE CURRENT FILTER IS SANE.
                            posts.add(temporaryPost);
                            backupposts.add(temporaryPost);
                        }
                    }
                }

                // sort backup posts hcronologically
                Collections.sort(backupposts, new Comparator<Post>() {
                    @Override
                    public int compare(Post post, Post t1) {
                        Date one = post.getStartRange();
                        Date two = post.getEndRange();
                        if (one.before(two)) {
                            return -1;
                        } else if (two.before(one)) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });

                // sort posts by chronological order
                Collections.sort(posts, new Comparator<Post>() {
                    @Override
                    public int compare(Post post, Post t1) {
                        Date one = post.getStartRange();
                        Date two = post.getEndRange();
                        if (one.before(two)) {
                            return -1;
                        } else if (two.before(one)) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
                postSnippetAdapter.notifyDataSetChanged();

//                if (filterOn) {
//                    System.out.println("filter on");
//                    filterOn = false;
//                    return;
//                }
//                else {
//                    feedView.invalidateViews();
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        //TODO: GET THE LIST VIEW AND UPDATE VIEW
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

    // Adapter for Post Snippets
    public class PostSnippetAdapter extends ArrayAdapter<Post> {

        private ArrayList<Post> posts;
        private Button viewDetails;

        FirebaseStorage mStorage = FirebaseStorage.getInstance();

        Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Bold.ttf");
        Typeface light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.ttf");

        // Constructor takes in an ArrayList to use for the tutors.
        public PostSnippetAdapter(Context context, ArrayList<Post> posts) {
            super(context, 0, posts);
            this.posts = posts;
        }

        public void swapItems(ArrayList<Post> posts) {
            this.posts = posts;

            notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // Inflates the layout.
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.feed_post_snippet, null);
            }

            Post selectedPost = null;
            try {
                selectedPost = posts.get(position);
                System.out.println("position: " + position);
                System.out.println("name: " + selectedPost.getPostName());

                // check if the post is expired
                Date currentTime = Calendar.getInstance().getTime();

                TextView titleView = convertView.findViewById(R.id.postName);
                titleView.setTypeface(bold);
                titleView.setText(selectedPost.getPostName());

                if (currentTime.after(selectedPost.getEndRange())) {
                    titleView.setText(titleView.getText() + " [EXPIRED]");
                }

                final TextView usernameView = convertView.findViewById(R.id.userName);
                final TextView rating = convertView.findViewById(R.id.rating_snippet);
                usernameView.setText("");
                rating.setText("");
                usernameView.setTypeface(light);
                rating.setTypeface(light);
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
                locationView.setTypeface(light);
            } catch (IndexOutOfBoundsException ioobe) {

            }

            if (selectedPost != null) {
                System.out.println(selectedPost.getPostName());
            } else System.out.println("not here");

            viewDetails = (Button) convertView.findViewById(R.id.view_snippet_details);
            viewDetails.setTag(position);
            viewDetails.setTypeface(bold);
            viewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // We are retrieving the tag we set earlier here.
                    int clickedPosition = (int) v.getTag();
                    Intent i = new Intent(getActivity(), DetailedPostActivity.class);
                    i.putExtra(FEED_POSITION, clickedPosition);
                    i.putExtra(SCREEN_NAME, "Feed_Fragment");
                    startActivity(i);
                }
            });


            if (selectedPost == null) return convertView;
            if(selectedPost.getPictures() != null) {
                if (selectedPost.getPictures().size() > 0) {
                    View image_container = (View) convertView.findViewById(R.id.post_snippet_image);
                    Glide.with(getContext())
                            .using(new FirebaseImageLoader())
                            .load(mStorage.getReference().child(selectedPost.getPictures().get(0)))
                            .fitCenter().into(new GlideDrawableViewBackgroundTarget(image_container));
                }
            }
            return convertView;
        }
    }
}
