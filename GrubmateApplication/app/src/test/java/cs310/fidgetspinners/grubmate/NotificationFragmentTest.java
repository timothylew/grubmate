package cs310.fidgetspinners.grubmate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.model.Request;
import cs310.fidgetspinners.grubmate.ui.NotificationFragment;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by andyg on 10/30/2017.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ FirebaseAuth.class, FirebaseDatabase.class, DataSnapshot.class, FirebaseUser.class})
public class NotificationFragmentTest {

    @Before
    public void before() {
    }

    @Test
    public void updatePostNotificationsHandlerNoChange() {
        final DatabaseReference mockedDatabaseReference = mock(DatabaseReference.class);
        final FirebaseDatabase mockedFirebaseDatabase = mock(FirebaseDatabase.class);
        final FirebaseAuth mockedFirebaseAuth = mock(FirebaseAuth.class);
        PowerMockito.mockStatic(FirebaseAuth.class);

        when(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth);

        when(mockedFirebaseDatabase.getReference(anyString())).thenReturn(mockedDatabaseReference);
        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);
        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);
        when(mockedDatabaseReference.setValue(ArgumentMatchers.any())).thenReturn(null);

        PowerMockito.mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);

        final FirebaseUser firebaseUser = mock(FirebaseUser.class);
        when(mockedFirebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("TEST");

        final Request selectedRequest = mock(Request.class);
        when(selectedRequest.getStatus()).thenReturn(0);

        final Post selectedPost = mock(Post.class);
        when(selectedRequest.getOriginalPost()).thenReturn(selectedPost);
        when(selectedPost.getOriginalPoster()).thenReturn("TEST");

        final boolean alreadyConfirmed = false;

        int result = NotificationFragment.RequestSnippetAdapter.updatePostObject(mockedDatabaseReference, selectedRequest,
            selectedPost, alreadyConfirmed, mockedFirebaseAuth);
        assertEquals(result, 0);
    }

    @Test
    public void checkDeniedChecksValid() {
        int result = Util.checkDeniedStatusValid(Request.BOTH_CONFIRMED);
        int result2 = Util.checkDeniedStatusValid(Request.REQUESTER_CONFIRMED);
        int result3 = Util.checkDeniedStatusValid(Request.INITIALIZED);

        assertEquals(result, -1);
        assertEquals(result2, -2);
        assertEquals(result3, 0);
    }
}
