package cs310.fidgetspinners.grubmate.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import cs310.fidgetspinners.grubmate.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeleteGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeleteGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeleteGroupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "DELETE GROUP: ";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private OnFragmentInteractionListener mListener;

    public DeleteGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeleteGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeleteGroupFragment newInstance(String param1, String param2) {
        DeleteGroupFragment fragment = new DeleteGroupFragment();
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
        View v = inflater.inflate(R.layout.fragment_delete_group, container, false);

        final DatabaseReference mRef = mDatabase.getReference("groups")
                .child(mAuth.getCurrentUser().getUid());

        final LinearLayout groupLayout = v.findViewById(R.id.group_selection_delete_layout);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // only trigger on visible
                if (getView() == null || !getView().isShown()) {
                    return;
                }

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    final String groupName = postSnapshot.getKey();

                    Button groupButton= new Button(getContext());
                    groupButton.setText(groupName);

                    groupButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteGroup(mRef, groupName);
                            Toast.makeText(getContext(), "Group deleted.", Toast.LENGTH_LONG);

                            Intent transition = new Intent(getActivity(), MainActivity.class);
                            startActivity(transition);

                        }
                    });

                    groupLayout.addView(groupButton);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        // Code taken from Firebase tutorial:
        // https://firebase.google.com/docs/database/android/lists-of-data

        return v;
    }

    public String deleteGroup(DatabaseReference mRef, String groupName) {
        if (groupName.equals(null)) {
            return "Group must be selected.";
        }
        mRef.child(groupName).removeValue();
        return "Group removed!";
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionDelete(uri);
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
        void onFragmentInteractionDelete(Uri uri);
    }
}
