package cs310.fidgetspinners.grubmate;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.ui.FilterActivity;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by andyg on 10/29/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(LatLng.class)
public class FilterActivityTest {

    @Test
    public void testSortPostsSizeAndIntegrity() {
        Post a = mock(Post.class);
        Post b = mock(Post.class);
        when(a.getLatitude()).thenReturn(1.0);
        when(a.getLongitude()).thenReturn(1.0);
        when(b.getLatitude()).thenReturn(2.0);
        when(b.getLongitude()).thenReturn(2.0);

        ArrayList<Post> ab = new ArrayList<Post>();
        ab.add(a);
        ab.add(b);

        LatLng current = new LatLng(5, 5);

        ArrayList<Post> result = FilterActivity.sortPostsLatLng(ab, current);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getLatitude(), 1.0);
        assertEquals(result.get(1).getLatitude(), 2.0);
    }

    @Test
    public void testSortPostsSizeAndIntegrityNegative() {
        Post a = mock(Post.class);
        Post b = mock(Post.class);
        when(a.getLatitude()).thenReturn(-1.0);
        when(a.getLongitude()).thenReturn(-1.0);
        when(b.getLatitude()).thenReturn(20.0);
        when(b.getLongitude()).thenReturn(20.0);

        ArrayList<Post> ab = new ArrayList<Post>();
        ab.add(a);
        ab.add(b);

        LatLng current = new LatLng(5, 5);

        ArrayList<Post> result = FilterActivity.sortPostsLatLng(ab, current);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getLatitude(), -1.0);
        assertEquals(result.get(1).getLatitude(), 20.0);
    }

    @Test
    public void dontMoveIfZero() {
        Post a = mock(Post.class);
        Post b = mock(Post.class);
        when(a.getLatitude()).thenReturn(200.0);
        when(a.getLongitude()).thenReturn(200.0);
        when(b.getLatitude()).thenReturn(20.0);
        when(b.getLongitude()).thenReturn(20.0);

        ArrayList<Post> ab = new ArrayList<Post>();
        ab.add(a);
        ab.add(b);

        LatLng current = new LatLng(0, 0);

        ArrayList<Post> result = FilterActivity.sortPostsLatLng(ab, current);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getLatitude(), 200.0);
        assertEquals(result.get(1).getLatitude(), 20.0);
    }


}
