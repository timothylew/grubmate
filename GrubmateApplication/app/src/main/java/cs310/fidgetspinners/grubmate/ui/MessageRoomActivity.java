package cs310.fidgetspinners.grubmate.ui;

/**
 * Created by timothylew on 11/12/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.constraint.solver.SolverVariable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.model.Chat;
import cs310.fidgetspinners.grubmate.model.User;


public class MessageRoomActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    public MessageRoomActivity() {
        // Required empty public constructor

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_room);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Get references to UI components.
        ListView messageOptions = (ListView) findViewById(R.id.messageOptions);
        TextView yourMessages = (TextView) findViewById(R.id.yourMessages);

        // Set font for UI components.
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-SemiBoldItalic.ttf");
        yourMessages.setTypeface(typeface);

        // Set up the adapter for listview, and create an arraylist for that adapter.
        final ArrayList<User> toAdd = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("chatMap")
                .child(currentUser.getUid());
        final MessageListAdapter mAdapter = new MessageListAdapter(MessageRoomActivity.this, String.class, R.layout.message_display_item, ref);
        messageOptions.setAdapter(mAdapter);

        messageOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String itemRef = mAdapter.getItem(i);
                String[] ids = itemRef.split("_");
                String id1;
                String id2;
                if(currentUser.getUid().equals(ids[0])) {
                    id1 = ids[0];
                    id2 = ids[1];
                }
                else {
                    id1 = ids[1];
                    id2 = ids[0];
                }
                Intent intent = new Intent(MessageRoomActivity.this, MessageActivity.class);
                intent.putExtra(DetailedPostFragment.CURRENT_USER, id1);
                intent.putExtra(DetailedPostFragment.OTHER_USER, id2);
                startActivity(intent);
            }
        });

        // Set a firebase listener to get relevant data (people you have open message channels with).
        // This will let us know which chats exist with you already.
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        FirebaseDatabase.getInstance().getReference().child("chatMap").child(user.getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        // For each chat channel, add the other party into user arraylist in singleton
//                        for(DataSnapshot individualItem : dataSnapshot.getChildren()){
//                            String key = individualItem.getKey();
//                            FirebaseDatabase.getInstance().getReference().child("users")
//                                    .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    User user = dataSnapshot.getValue(User.class);
//                                    toAdd.add(user);
////                                    if(!RelevantUserSingleton.getInstance(getActivity()).messagingUserExists(user)) {
////                                        RelevantUserSingleton.getInstance(getActivity()).addMessagingUser(user);
////                                    }
//                                    mAdapter.notifyDataSetChanged();
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
    }

    // Firebase Adapter for all the chat objects under a certain child.
    private class MessageListAdapter extends FirebaseListAdapter<String> {
        public MessageListAdapter(Activity activity, Class<String> modelClass, int modelLayout, DatabaseReference ref) {
            super(activity, modelClass, modelLayout, ref);
        }
        protected void populateView(View v, String model, int positionNumber) {

            // Get references to UI components.
            final TextView username = (TextView) v.findViewById(R.id.userNameForMessaging);
           // Button viewMessages = (Button) v.findViewById(R.id.continueMessenger);

            // Set fonts.
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-SemiBold.ttf");
            username.setTypeface(typeface);

            String[] ids = model.split("_");
            String idToUse;
            if(currentUser.getUid().equals(ids[0])) {
                idToUse = ids[1];
            }
            else {
                idToUse = ids[0];
            }

            FirebaseDatabase.getInstance().getReference().child("users").child(idToUse).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    username.setText(user.getName());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            //Typeface typeface1 = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.ttf");
            //viewMessages.setTypeface(typeface1);

            // Set text according to the Chat Message object.

        }
    }
//    // This adapter gets all the users with open messaging channels with you, and puts it in the list view.
//    public class MessageListAdapter extends ArrayAdapter<User> {
//
//        private ArrayList<User> openChannels;
//
//        // Constructor takes in an ArrayList to use for the tutors.
//        public MessageListAdapter(Context context, ArrayList<User> openChannels) {
//            super(context, 0, openChannels);
//            this.openChannels = openChannels;
//        }
//
//        public View getView(int position, View convertView, ViewGroup parent) {
//            // Inflates the layout.
//            if(convertView == null) {
//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_display_item, null);
//            }
//
//            // Get the current user for the specific row we're on.
//            User user = openChannels.get(position);
//
//            // Get references to UI components.
//            TextView name = (TextView) convertView.findViewById(R.id.userNameForMessaging);
//            TextView topicsAvailable = (TextView) convertView.findViewById(R.id.topicsAvailable);
//            Button respond = (Button) convertView.findViewById(R.id.continueMessenger);
//
//            // Set the fonts for the UI components.
//            Typeface typeface1 = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-SemiBold.ttf");
//            name.setTypeface(typeface1);
//            Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.ttf");
//            topicsAvailable.setTypeface(typeface2);
//            respond.setTypeface(typeface2);
//
//
//            // Set the text for the UI Components accordingly to the position we're at.
//            name.setText(user.getName());
//
//            // Set the biography text of a user to contain all the topics a user tutors.
//            String bioText = "Topics: ";
//
//            // Declare int tagPosition that will be our tag for the respond button.
//            int tagPosition = -1;
//
//            // Go through arraylist of messaging users to find the position TODO
////            ArrayList<User> tempArrayList = RelevantUserSingleton.getInstance(getActivity()).getMessagingUsers();
////            for(int i = 0; i < tempArrayList.size(); i++) {
////                if(user.getUid().trim().equals(tempArrayList.get(i).getUid().trim())) {
////                    tagPosition = i;
////                }
////            }
//            // The position should be the index the user is at in the Relevant user array
//
//            respond.setTag(tagPosition);
//
//            // Set on click listener for the learnMore button.
//            respond.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    // We are retrieving the tag we set earlier here.
//                    int clickedPosition = (int) v.getTag();
//
//                }
//            });
//
//            return convertView;
//        }
//    }

}