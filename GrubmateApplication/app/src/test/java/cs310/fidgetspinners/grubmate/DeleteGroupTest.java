package cs310.fidgetspinners.grubmate;

import android.widget.CheckBox;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;

import cs310.fidgetspinners.grubmate.model.Group;
import cs310.fidgetspinners.grubmate.model.User;
import cs310.fidgetspinners.grubmate.ui.CreateGroupFragment;
import cs310.fidgetspinners.grubmate.ui.DeleteGroupFragment;
import cs310.fidgetspinners.grubmate.ui.ModifyGroupFragment;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by sarahwu on 10/30/2017.
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ FirebaseDatabase.class, FirebaseAuth.class})
public class DeleteGroupTest {

    private DatabaseReference mockedDatabaseReference;
    private FirebaseAuth mockedFirebaseAuth;

    @Before
    public void before() {
        mockedDatabaseReference = mock(DatabaseReference.class);
        mockedFirebaseAuth = mock(FirebaseAuth.class);

        PowerMockito.mockStatic(FirebaseAuth.class);

        when(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth);

        FirebaseDatabase mockedFirebaseDatabase = mock(FirebaseDatabase.class);
        when(mockedFirebaseDatabase.getReference(anyString())).thenReturn(mockedDatabaseReference);
        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);

        PowerMockito.mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);
    }

    @Test
    public void baseCase() {
        FirebaseUser firebaseUser = mock(FirebaseUser.class);
        FirebaseDatabase firebaseDatabase = mock(FirebaseDatabase.class);

        DataSnapshot dataSnapshot = mock(DataSnapshot.class);
        User u = mock(User.class);
        when(dataSnapshot.getValue(User.class)).thenReturn(u);

        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);
        when(mockedDatabaseReference.child("TEST")).thenReturn(mockedDatabaseReference);

        when(mockedFirebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("TEST");

        Group g = mock(Group.class);
        String groupName = "test remove";
        g.setGroupName(groupName);
        ArrayList<Group> groupList = new ArrayList<Group>();
        groupList.add(g);

        u.setGroups(groupList);

//        JSONObject friend = new JSONObject();
//        friend.put("name", "andy");
//        friend.put("id", "0000");
//
//        JSONArray array = new JSONArray();
//        array.put(friend);
//        CreateGroupFragment.createGroup("TESTUSERID", groupName, array, checkBoxes, mockedDatabaseReference, true);
//
//        String result = DeleteGroupFragment.deleteGroup(mockedDatabaseReference, groupName);

//        ArrayList<Group> afterGroup = mockedDatabaseReference.child();

//        when(afterGroup.size()).thenReturn(0);
//        int result = afterGroup.size();

//        assertEquals(result, "Group removed!");
    }


}
