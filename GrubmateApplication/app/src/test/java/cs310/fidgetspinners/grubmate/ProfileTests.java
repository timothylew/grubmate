package cs310.fidgetspinners.grubmate;

import android.media.Image;
import android.widget.CheckBox;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.Mockito;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.ui.CreateGroupFragment;
import cs310.fidgetspinners.grubmate.ui.FilterActivity;
import cs310.fidgetspinners.grubmate.model.User;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by sarahwu on 10/25/2017.
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ FirebaseDatabase.class, DataSnapshot.class, FirebaseUser.class})

public class ProfileTests {

    private DatabaseReference mockedDatabaseReference;
    private FirebaseDatabase mockedFirebaseDatabase;

    @Before
    public void before() {
        mockedDatabaseReference = mock(DatabaseReference.class);
        mockedFirebaseDatabase = mock(FirebaseDatabase.class);
        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);
        PowerMockito.mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);
    }


    @Test
    public void requiredFields() throws Exception {
        DataSnapshot dataSnapshot = mock(DataSnapshot.class);
        User u = mock(User.class);
        when(dataSnapshot.getValue(User.class)).thenReturn(u);

        String UID = "iruWvC3Cf0azSiTF9EGCIo6WClY2";
//        BufferedImage propic = new BufferedImage(100, 50,
//                        BufferedImage.TYPE_INT_ARGB);
        Image propic = PowerMockito.mock(Image.class);
        Float r = new Float(1);
        when(u.getName()).thenReturn(UID);
        when(u.getProfilePicture()).thenReturn(propic);
        when(u.getAverageRating()).thenReturn(r);

        String name = u.getName();
        Image profilePicture = u.getProfilePicture();
        Float rating = u.getAverageRating();

        assertNotNull(name);
        assertNotNull(profilePicture);
        assertNotNull(rating);
    }

    @Test
    public void postHistory() throws Exception {
        DataSnapshot dataSnapshot = mock(DataSnapshot.class);
        User u = mock(User.class);

        when(dataSnapshot.getValue(User.class)).thenReturn(u);

        Post test = mock(Post.class);
        ArrayList<Post> results = new ArrayList<Post>();
        results.add(test);

        String posterName = u.getName();

        when(u.getPostHistory()).thenReturn(results);

        u.getPostHistory();

        for (Post result : results) {
            assertEquals(posterName, result.getOriginalPoster());
        }
    }
}
