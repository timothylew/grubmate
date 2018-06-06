package cs310.fidgetspinners.grubmate.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.model.Chat;
import cs310.fidgetspinners.grubmate.model.User;


public class MessageActivity extends AppCompatActivity{

    private EditText messageInput;
    private Button sendMessage;
    private ListView messages;
    private TextView talkingTo;
    private String talkingToUID;
    private FirebaseListAdapter mAdapter;
    private String roomName;
    private int inputMode;
    private String roomName1;
    private String roomName2;
    private String nameOfRecipient;
    private String nameOfSender;
    private Chat chat;
    private TextView rating;

    String id1;
    String id2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent from = getIntent();
        id1 = (String) from.getSerializableExtra(DetailedPostFragment.CURRENT_USER);
        id2 = (String) from.getSerializableExtra(DetailedPostFragment.OTHER_USER);

        // Get references to UI Components
        messageInput = (EditText) findViewById(R.id.inputMessage);
        sendMessage = (Button) findViewById(R.id.sendMessage);
        messages = (ListView) findViewById(R.id.messages);
        talkingTo = (TextView) findViewById(R.id.talkingTo);
        rating = (TextView) findViewById(R.id.messageRatingDisplay);

        // Set fonts for UI Components
        Typeface typefaceBold = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-SemiBold.ttf");
        Typeface lightTypeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.ttf");
        messageInput.setTypeface(lightTypeface);
        talkingTo.setTypeface(typefaceBold);
        sendMessage.setTypeface(lightTypeface);
        rating.setTypeface(typefaceBold);

        // Make the message list view always auto scroll to the bottom (most recent messages).
        messages.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        messages.setStackFromBottom(true);

        // generate the appropriate possible room names for chat rooms.
        roomName1 = id1 + "_" + id2;
        roomName2 = id2 + "_" + id1;


        // Check which room name is the existing/valid one.  If it doesn't exist yet, just create the chat room.
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = firebaseDatabase.getReference().child("channels");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(roomName1)) {
                    roomName = roomName1;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("channels")
                            .child(roomName);
                    mAdapter = new ChatAdapter(MessageActivity.this, Chat.class, R.layout.message_from_user, ref);
                    messages.setAdapter(mAdapter);
                }
                else if (dataSnapshot.hasChild(roomName2)) {
                    roomName = roomName2;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("channels")
                            .child(roomName);
                    mAdapter = new ChatAdapter(MessageActivity.this, Chat.class, R.layout.message_from_user, ref);
                    messages.setAdapter(mAdapter);
                }
                else {
                    roomName = roomName1;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("channels")
                            .child(roomName);
                    mAdapter = new ChatAdapter(MessageActivity.this, Chat.class, R.layout.message_from_user, ref);
                    messages.setAdapter(mAdapter);
                }

                firebaseDatabase.getReference().child("chatMap").child(id1).child(id2).setValue(roomName);
                firebaseDatabase.getReference().child("chatMap").child(id2).child(id1).setValue(roomName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        firebaseDatabase.getReference().child("users").child(id2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameOfRecipient = user.getName();
                talkingTo.setText(nameOfRecipient);
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                rating.setText("Poster Rating: " + df.format(user.getAverageRating()) + " / 5");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebaseDatabase.getReference().child("users").child(id1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameOfSender = user.getName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // This sendMessage listener sends a Chat object over to the Firebase Database.
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ensure that the roomname is valid, and the message input is valid.
                if(roomName.trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please send that message again.", Toast.LENGTH_SHORT).show();
                }
                else if(messageInput.getText().toString().trim().equals("")) {
                    messageInput.setError("This field can't be empty!");
                }
                else {
                    // Construct the Chat object by creating the necessary components.
                    String message = messageInput.getText().toString().trim();
                    final String df = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                    chat = new Chat(message, df, nameOfRecipient, nameOfSender, id2, id1);

                    FirebaseDatabase.getInstance().getReference().child("channels").child(roomName).child(df).setValue(chat);
                    messageInput.setText("");
                }

            }
        });

    }

    // Firebase Adapter for all the chat objects under a certain child.
    private class ChatAdapter extends FirebaseListAdapter<Chat> {
        public ChatAdapter(Activity activity, Class<Chat> modelClass, int modelLayout, DatabaseReference ref) {
            super(activity, modelClass, modelLayout, ref);
        }
        protected void populateView(View v, Chat model, int positionNumber) {

            // Get references to UI components.
            TextView message = (TextView) v.findViewById(R.id.messageToDisplay);
            TextView sender = (TextView) v.findViewById(R.id.sentBy);
            TextView timestamp = (TextView) v.findViewById(R.id.messageTimestamp);

            // Set fonts.
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-SemiBold.ttf");
            message.setTypeface(typeface);
            Typeface typeface1 = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.ttf");
            sender.setTypeface(typeface1);
            timestamp.setTypeface(typeface1);

            // Set text according to the Chat Message object.
            message.setText(model.getMessageContents());
            sender.setText(model.getSenderName());
            timestamp.setText(model.getTimestamp());

        }
    }
}