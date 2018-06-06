package cs310.fidgetspinners.grubmate;

import android.test.InstrumentationTestCase;
import android.widget.CheckBox;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.exceptions.ExceptionIncludingMockitoWarnings;

import java.util.ArrayList;
import java.util.List;

import cs310.fidgetspinners.grubmate.ui.CreateGroupFragment;

import static com.google.firebase.database.DatabaseReference.goOffline;
import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

/**
 * Created by andyg on 10/25/2017.
 */

public class GroupCreationTest {
    // tests if a null group is accepted
    @Test
    public void nullGroupTest() throws Exception {
        // make sure that a null group is not allowed
        String result = CreateGroupFragment.createGroup("uid", "", null, null, null, false);
        assertEquals(result, "nameerror");
    }

    // tests if a group with no members is valid
    @Test
    public void nullMemberTest() throws Exception {
        String result = CreateGroupFragment.createGroup("uid", "testname", new JSONArray(), new ArrayList<CheckBox>()
            , null, false);
        assertEquals(result, "frienderror");
    }

    // tests normal case, if a group with 1 member is valid
    @Test
    public void nameTest() throws Exception {

        JSONObject friend = new JSONObject();
        friend.put("name", "andy");
        friend.put("id", "0000");

        JSONArray array = new JSONArray();
        array.put(friend);

        CheckBox testBox = mock(CheckBox.class, Mockito.RETURNS_DEEP_STUBS);

        Mockito.when(testBox.getText()).thenReturn("andy");
        Mockito.when(testBox.isChecked()).thenReturn(true);

        ArrayList<CheckBox> checkBoxes = new ArrayList<CheckBox>();
        checkBoxes.add(testBox);

        FirebaseDatabase mockDB = mock(FirebaseDatabase.class);

        String result = CreateGroupFragment.createGroup("TESTUSERID", "testname", array, checkBoxes, mockDB, true);
        assertEquals(result, "testname");
    }

}
