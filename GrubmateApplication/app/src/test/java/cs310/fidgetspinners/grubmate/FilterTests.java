package cs310.fidgetspinners.grubmate;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.model.Request;
import cs310.fidgetspinners.grubmate.model.User;
import cs310.fidgetspinners.grubmate.ui.FeedFragment;
import cs310.fidgetspinners.grubmate.ui.FilterActivity;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by sarahwu on 10/25/2017.
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ FirebaseDatabase.class, DataSnapshot.class, FirebaseUser.class})

public class FilterTests {
//    Filter returns arraylist of posts that are only within time bounds (earliest, latest, range)
//    Filter returns arraylist of posts that are only in specified category
//    Search returns arraylist of posts that match tag

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
    public void earliestTime() throws Exception {
        //check to make sure that the filtered array of groups is correct
        DataSnapshot dataSnapshot = mock(DataSnapshot.class);
        User u = mock(User.class);
        when(dataSnapshot.getValue(User.class)).thenReturn(u);

        FeedFragment.PostSnippetAdapter psa = mock(FeedFragment.PostSnippetAdapter.class);

        ArrayList<String> tags = new ArrayList<String>();
        String earliest = "2000-01-01 00:00:00.0";
        Date earlier = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(earliest);

        String latest = "2010-01-01 00:00:00.0";
        Date later = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(latest);

        Post test1 = new Post("sarah", "test 1", "Homemade", "Chinese", "home",
                tags, "description", "price", 10, 15, earlier, earlier, tags, null, 0.0, 0.0);

        Post test2 = new Post("sarah", "test 2", "Homemade", "Chinese", "home",
                tags, "description", "price", 10, 15, later, later, tags, null, 0.0, 0.0);

        ArrayList<Post> results = new ArrayList<Post>();
        results.add(test1);
        results.add(test2);

        when(u.getViewablePosts()).thenReturn(results);

        ArrayList<Post> posts = u.getViewablePosts();

        //how to call method that calls function on main feedfragment?
        ArrayList<Post> filtered = FilterActivity.setEarliest(posts, psa);
        ArrayList<Date> startTimes = new ArrayList<>();
        for (Post result : filtered) {
            startTimes.add(result.getStartRange());
        }

        int minIndex = startTimes.indexOf(Collections.min(startTimes));

        assertEquals(minIndex, 0);
    }

    @Test
    public void latestTime() throws Exception {
        DataSnapshot dataSnapshot = mock(DataSnapshot.class);
        User u = mock(User.class);
        when(dataSnapshot.getValue(User.class)).thenReturn(u);

        FeedFragment.PostSnippetAdapter psa = mock(FeedFragment.PostSnippetAdapter.class);

        ArrayList<String> tags = new ArrayList<String>();
        String earliest = "2000-01-01 00:00:00.0";
        Date earlier = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(earliest);

        String latest = "2010-01-01 00:00:00.0";
        Date later = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(latest);

        Post test1 = new Post("sarah", "test 1", "Homemade", "Chinese", "home",
                tags, "description", "price", 10, 15, earlier, earlier, tags, null, 0.0, 0.0);

        Post test2 = new Post("sarah", "test 2", "Homemade", "Chinese", "home",
                tags, "description", "price", 10, 15, later, later, tags, null, 0.0, 0.0);

        ArrayList<Post> results = new ArrayList<Post>();
        results.add(test1);
        results.add(test2);

        when(u.getViewablePosts()).thenReturn(results);

        ArrayList<Post> posts = u.getViewablePosts();

        ArrayList<Post> filtered = FilterActivity.setLatest(posts, psa);
        ArrayList<Date> endTimes = new ArrayList<>();
        for (Post result : filtered) {
            endTimes.add(result.getEndRange());
        }

        int maxIndex = endTimes.indexOf(Collections.max(endTimes));

        assertEquals(maxIndex, 0);
    }

    @Test
    public void categoryFilter() throws Exception {
        DataSnapshot dataSnapshot = mock(DataSnapshot.class);
        User u = mock(User.class);
        when(dataSnapshot.getValue(User.class)).thenReturn(u);

        Post test = mock(Post.class);
        ArrayList<Post> results = new ArrayList<Post>();
        results.add(test);

        when(u.getViewablePosts()).thenReturn(results);

        ArrayList<Post> posts = u.getViewablePosts();

        ArrayList<Post> filtered = FeedFragment.filterPosts("Chinese");
        for (Post result : filtered) {
            assertEquals("Chinese", result.getCategory());
        }
    }

}
