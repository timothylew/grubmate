package cs310.fidgetspinners.grubmate;

import android.widget.CheckBox;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;

import cs310.fidgetspinners.grubmate.ui.CreateGroupFragment;
import cs310.fidgetspinners.grubmate.ui.ModifyGroupFragment;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by andyg on 10/27/2017.
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ FirebaseDatabase.class, FirebaseAuth.class})
public class ModifyGroupTest {

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
    public void failGracefullyOnNull() {
        //ModifyGroupFragment.ModifyGroup(ArrayList<CheckBox> groupSelectionList,
        //                          ArrayList<CreateGroupFragment.FacebookFriend> facebookFriends,
        //                          String newGroupName, String groupName);
        int result = ModifyGroupFragment.ModifyGroup(null, null, "new", "old");
        assertEquals(result, -1);
    }

    @Test
    public void failGracefullyOnEmpty() {
        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);

        ArrayList<CheckBox> groupSelectionList = new ArrayList<CheckBox>();
        ArrayList<CreateGroupFragment.FacebookFriend> facebookFriends =
                new ArrayList<CreateGroupFragment.FacebookFriend>();

        int result = ModifyGroupFragment.ModifyGroup(groupSelectionList, facebookFriends, "new", "old");
        assertEquals(result, 1);
    }

    @Test
    public void baseCase() {
        FirebaseUser firebaseUser = mock(FirebaseUser.class);
        FirebaseDatabase firebaseDatabase = mock(FirebaseDatabase.class);

        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);
        when(mockedDatabaseReference.child("TEST")).thenReturn(mockedDatabaseReference);

        when(mockedDatabaseReference.removeValue()).thenReturn(null);
        when(mockedFirebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("TEST");

        ArrayList<CheckBox> groupSelectionList = mock(ArrayList.class);
        ArrayList<CreateGroupFragment.FacebookFriend> facebookFriends
                = mock(ArrayList.class);

        CheckBox tempBox = mock(CheckBox.class);
        groupSelectionList.add(tempBox);

        when(groupSelectionList.size()).thenReturn(1);

        int result = ModifyGroupFragment.ModifyGroup(groupSelectionList, facebookFriends, "new", "old");
        assertEquals(result, 0);
    }


}
