package cs310.fidgetspinners.grubmate.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Array;
import java.util.ArrayList;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.model.User;

import static cs310.fidgetspinners.grubmate.R.id.feedView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ModifyGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ModifyGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModifyGroupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String GROUP_PARAM = "groupname";

    private static final String TAG = "GROUP MODIFY: ";
    private static final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // TODO: Rename and change types of parameters
    private String groupName;

    private ArrayList<User> groupMembers;
    private ArrayList<CreateGroupFragment.FacebookFriend> facebookFriends
            = new ArrayList<CreateGroupFragment.FacebookFriend>();

    private OnFragmentInteractionListener mListener;

    private ArrayList<CheckBox> groupSelectionList = new ArrayList<CheckBox>();

    public ModifyGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ModifyGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ModifyGroupFragment newInstance(String param1) {
        ModifyGroupFragment fragment = new ModifyGroupFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_PARAM, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupName = getArguments().getString(GROUP_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_modify_group, container, false);
        final DatabaseReference mRef = mDatabase.getReference("groups")
                .child(mAuth.getCurrentUser().getUid()).child(groupName).child("groupMembers");

        final LinearLayout userListLayout = v.findViewById(R.id.modify_list_layout);
        // layout to grab the group users
        final ArrayList<String> addedUsers = new ArrayList<String>();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                groupSelectionList.clear();
                userListLayout.removeAllViews();
                facebookFriends.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String userID = postSnapshot.child("userID").getValue(String.class);
                    final String userName = postSnapshot.child("username").getValue(String.class);

                    addedUsers.add(userID);

                    CheckBox userButton = new CheckBox(getContext());
                    userButton.setText(userName);

                    userListLayout.addView(userButton);

                    //Update models accordingly
                    groupSelectionList.add(userButton);
                    CreateGroupFragment.FacebookFriend temp
                            = new CreateGroupFragment.FacebookFriend();
                    temp.username = userName;
                    temp.userID = userID;

                    facebookFriends.add(temp);

                }

                String URI = "/" + AccessToken.getCurrentAccessToken().getUserId() + "/friends";
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        URI,
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {

                                try {
                                    JSONArray friendArray = response.getJSONObject().getJSONArray("data");

                                    for (int i=0; i < friendArray.length(); i++) {
                                        JSONObject friend = friendArray.getJSONObject(i);

                                        if (!addedUsers.contains(friend.get("id"))) {

                                            CheckBox checkBox= new CheckBox(getContext());
                                            checkBox.setText(friend.get("name").toString());

                                            userListLayout.addView(checkBox);

                                            //Update models accordingly
                                            groupSelectionList.add(checkBox);
                                            CreateGroupFragment.FacebookFriend temp
                                                    = new CreateGroupFragment.FacebookFriend();
                                            temp.username = friend.get("name").toString();
                                            temp.userID = friend.get("id").toString();

                                            facebookFriends.add(temp);
                                        }
                                    }

                                } catch (JSONException JSONe) {
                                    System.out.println("exception: " + JSONe.toString());
                                }
                                //System.out.println("result: " + response.toString()'');
                            }
                        }
                ).executeAsync();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }

        });


        // make name editable, but set it default to the old one
        final EditText newGroupName = v.findViewById(R.id.new_title);
        newGroupName.setText(groupName);

        // add button functionality to modify the group
        Button reconfigureButton = v.findViewById(R.id.button_reconfigure_group);
        reconfigureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<CreateGroupFragment.FacebookFriend> newFacebookFriends
                        = new ArrayList<CreateGroupFragment.FacebookFriend>();

                for (int i=0; i < groupSelectionList.size(); i++) {
                    if (groupSelectionList.get(i).isChecked()) {
                        newFacebookFriends.add(facebookFriends.get(i));
                    }
                }

                int result = ModifyGroup(groupSelectionList, newFacebookFriends, newGroupName.getText().toString(), groupName);

                if (result == 1) {
                    Toast.makeText(getContext(), "Your group name or users selected are invalid!", Toast.LENGTH_LONG);
                    return;
                } else if (result == -1) {
                    Toast.makeText(getContext(), "ERROR: Null Values!", Toast.LENGTH_LONG);
                    return;
                }

                // notify the user
                Toast.makeText(getActivity(), "Group modified",
                        Toast.LENGTH_LONG).show();

                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });

        return v;
    }

    static public int ModifyGroup(ArrayList<CheckBox> groupSelectionList,
                                  ArrayList<CreateGroupFragment.FacebookFriend> newFacebookFriends,
                                  String newGroupName, String groupName) {
        if (groupSelectionList == null || newFacebookFriends == null) return -1;

        // null check
        if (newFacebookFriends.isEmpty() || newGroupName.equals("")) {
            System.out.println(newGroupName);
            System.out.println(newFacebookFriends);
            return 1;
        }

        // grab ref
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        //clear old values
        DatabaseReference dbr1 = mDatabase.getReference("groups");
        String uid = firebaseUser.getUid();
        DatabaseReference dbr2 = dbr1.child(uid);
        DatabaseReference dbr3 = dbr2.child(groupName);
        dbr3.removeValue();

        //reset the new values
        DatabaseReference myRef = mDatabase.getReference("groups")
                .child(firebaseUser.getUid()).child(groupName);

        myRef.child("groupMembers").setValue(newFacebookFriends);
        myRef.child("groupName").setValue(newGroupName);

        return 0;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionModify(uri);
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
        void onFragmentInteractionModify(Uri uri);
    }
}
