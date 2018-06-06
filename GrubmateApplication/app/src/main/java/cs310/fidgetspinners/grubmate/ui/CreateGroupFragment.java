package cs310.fidgetspinners.grubmate.ui;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.params.Face;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cs310.fidgetspinners.grubmate.MyFirebaseMessagingService;
import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGroupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    static public class FacebookFriend {
        public String username;
        public String userID;
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private JSONArray friendArray;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static ArrayList<CheckBox> boxArrayList = new ArrayList<>();
    public static ArrayList<FacebookFriend> facebookFriendArrayList = new ArrayList<FacebookFriend>();

    private OnFragmentInteractionListener mListener;

    public CreateGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateGroupFragment newInstance(String param1, String param2) {
        CreateGroupFragment fragment = new CreateGroupFragment();
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
        final View v = inflater.inflate(R.layout.fragment_create_group, container, false);
        final LinearLayout addItem = (LinearLayout) v.findViewById(R.id.groupLinearLayout);

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

                            boxArrayList.clear();
                            for (int i=0; i < friendArray.length(); i++) {
                                JSONObject friend = friendArray.getJSONObject(i);

                                CheckBox checkBox= new CheckBox(getContext());
                                checkBox.setText(friend.get("name").toString());

                                boxArrayList.add(checkBox);

                                addItem.addView(checkBox);
                            }

                        } catch (JSONException JSONe) {
                            System.out.println("exception: " + JSONe.toString());
                        }

                        //System.out.println("result: " + response.toString()'');

                    }
                }
        ).executeAsync();

        final EditText groupNameEdit = v.findViewById(R.id.group_name_edit);
        Button submitGroupButton = v.findViewById(R.id.submit_group_button);
        submitGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                String result = createGroup(FirebaseAuth.getInstance().getCurrentUser().getUid()
                        , groupNameEdit.getText().toString(), friendArray, boxArrayList, database, false);
                System.out.println("CREATE GROUP RESULT: " + result);

                if (result.equals("nameerror")) {
                    Toast.makeText(getContext(), "Your group name is invalid!", Toast.LENGTH_LONG);
                    return;
                } else if (result.equals("frienderror")) {
                    Toast.makeText(getContext(), "You need a member in your group!", Toast.LENGTH_LONG);
                    return;
                }

                // transition to next screen
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }

        });


        return v;
    }

    static public String createGroup(String UID, String groupName, JSONArray friendJSON,
                                      ArrayList<CheckBox> userBox, FirebaseDatabase database, boolean test
                                     ) {
        if (groupName.equals("")) return "nameerror";
        facebookFriendArrayList.clear();
        for (int i=0; i < userBox.size(); i++) {
            if (userBox.get(i).isChecked()) {
                try {
                    JSONObject friend = friendJSON.getJSONObject(i);

                    FacebookFriend facebookFriend = new FacebookFriend();
                    facebookFriend.username = friend.get("name").toString();
                    facebookFriend.userID = friend.get("id").toString();

                    facebookFriendArrayList.add(facebookFriend);

                } catch (JSONException jsonE) {
                    System.out.println(jsonE.toString());
                    return "true";
                }
            }
        }

        if (facebookFriendArrayList.isEmpty()) return "frienderror";

        if (!test) {
            DatabaseReference myRef = database.getReference("groups");
            myRef = myRef.child(UID);
            myRef = myRef.child(groupName);

            myRef.child("groupMembers").setValue(facebookFriendArrayList);
            myRef.child("groupName").setValue(groupName);
        }

        return groupName;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }
}
