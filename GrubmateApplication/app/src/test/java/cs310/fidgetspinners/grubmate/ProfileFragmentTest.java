package cs310.fidgetspinners.grubmate;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
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

import cs310.fidgetspinners.grubmate.model.User;
import cs310.fidgetspinners.grubmate.ui.ProfileFragment;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
/**
 * Created by anish on 10/30/17.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ FirebaseDatabase.class, FirebaseAuth.class})
public class ProfileFragmentTest {

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

        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);
    }

    @Test
    public void getProfile() throws Exception {
        DataSnapshot dataSnapshot = mock(DataSnapshot.class);
        User u = mock(User.class);
        u.setAverageRating(3);
        u.setName("Anish");
        u.setProfilephoto("Photo URL");

        when(dataSnapshot.getValue(User.class)).thenReturn(u);

        TextView name = mock(TextView.class);
        String result = ProfileFragment.getProfile("userID", mock(RatingBar.class),
                name, mock(ImageView.class), mock(Context.class));

        assertEquals("NotNull", result);
    }

}