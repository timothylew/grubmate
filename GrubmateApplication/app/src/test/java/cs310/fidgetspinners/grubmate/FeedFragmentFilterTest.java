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
import cs310.fidgetspinners.grubmate.ui.FeedFragment;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by andyg on 10/30/2017.
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ FirebaseDatabase.class, DataSnapshot.class, FirebaseUser.class, FirebaseAuth.class})
public class FeedFragmentFilterTest {

    private DatabaseReference mockedDatabaseReference;
    private FirebaseDatabase mockedFirebaseDatabase;
    private FirebaseAuth mockedFirebaseAuth;
    private FirebaseUser firebaseUser;

    @Before
    public void before() {
        mockedDatabaseReference = mock(DatabaseReference.class);
        mockedFirebaseDatabase = mock(FirebaseDatabase.class);
        mockedFirebaseAuth = mock(FirebaseAuth.class);
        PowerMockito.mockStatic(FirebaseAuth.class);

        when(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth);

        when(mockedFirebaseDatabase.getReference(anyString())).thenReturn(mockedDatabaseReference);
        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);
        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);
        when(mockedDatabaseReference.setValue(ArgumentMatchers.any())).thenReturn(null);

        PowerMockito.mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);

        firebaseUser = mock(FirebaseUser.class);
        when(mockedFirebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn("TEST");
    }

    @Test
    public void testFeedFragmentFilter() {
        ArrayList<Post> result = FeedFragment.filterPosts("");
        assertEquals(result, null);
    }

    @Test
    public void testFeedFragmentBaseCase() {
        Post a = mock(Post.class);
        Post b = mock(Post.class);

        ArrayList<Post> postArray = new ArrayList<Post>();
        postArray.add(a);
        postArray.add(b);

        FeedFragment.setPosts(postArray);
        FeedFragment.filterPosts("Chinese");
        ArrayList<Post> returnedPosts = FeedFragment.GetPosts();

        assertEquals(returnedPosts.size(), 0);
    }

    @Test
    public void testFeedFragmentFilterWrong() {
        Post a = mock(Post.class);
        Post b = mock(Post.class);

        ArrayList<Post> postArray = new ArrayList<Post>();
        postArray.add(a);
        postArray.add(b);

        FeedFragment.setPosts(postArray);
        FeedFragment.filterPosts("");
        ArrayList<Post> returnedPosts = FeedFragment.GetPosts();

        assertEquals(returnedPosts.size(), 2);
    }
}
