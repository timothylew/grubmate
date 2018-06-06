package cs310.fidgetspinners.grubmate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;

import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.model.User;
import cs310.fidgetspinners.grubmate.ui.SendPostFragment;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by andyg on 10/29/2017.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ FirebaseDatabase.class, DataSnapshot.class, FirebaseUser.class})
public class SendPostTest {

    private DatabaseReference mockedDatabaseReference;
    private FirebaseDatabase mockedFirebaseDatabase;

    @Before
    public void before() {
        mockedDatabaseReference = mock(DatabaseReference.class);
        mockedFirebaseDatabase = mock(FirebaseDatabase.class);
        when(mockedFirebaseDatabase.getReference(anyString())).thenReturn(mockedDatabaseReference);
        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);
        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);
        when(mockedDatabaseReference.setValue(ArgumentMatchers.any())).thenReturn(null);

        PowerMockito.mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);
    }

    @Test
    public void sendPostTestNull() {
        DataSnapshot dataSnapshot = mock(DataSnapshot.class);

        User u = mock(User.class);

        when(dataSnapshot.getValue(User.class)).thenReturn(u);
        when(u.getActivePosts()).thenReturn(null);

        String UID = "iruWvC3Cf0azSiTF9EGCIo6WClY2";
        Post post = mock(Post.class);

        int result = SendPostFragment.SendPost(dataSnapshot, UID, post);
        assertEquals(result, 1);
    }

    @Test
    public void sendPostTestNonNull() {
        DataSnapshot dataSnapshot = mock(DataSnapshot.class);

        User u = mock(User.class);

        Post post2 = mock(Post.class);
        ArrayList<Post> postArray = new ArrayList<Post>();
        postArray.add(post2);

        when(dataSnapshot.getValue(User.class)).thenReturn(u);
        when(u.getActivePosts()).thenReturn(postArray);

        String UID = "iruWvC3Cf0azSiTF9EGCIo6WClY2";
        Post post = mock(Post.class);

        int result = SendPostFragment.SendPost(dataSnapshot, UID, post);
        assertEquals(result, 1);
    }

    @Test
    public void sendPostTestDupes() {
        DataSnapshot dataSnapshot = mock(DataSnapshot.class);

        User u = mock(User.class);

        Post post2 = mock(Post.class);
        ArrayList<Post> postArray = new ArrayList<Post>();

        when(dataSnapshot.getValue(User.class)).thenReturn(u);
        when(u.getActivePosts()).thenReturn(postArray);

        String UID = "iruWvC3Cf0azSiTF9EGCIo6WClY2";
        Post post = mock(Post.class);

        postArray.add(post);

        int result = SendPostFragment.SendPost(dataSnapshot, UID, post);
        assertEquals(result, -1);
    }

}

