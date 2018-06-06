package cs310.fidgetspinners.grubmate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import cs310.fidgetspinners.grubmate.ui.RatingFragment;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by andyg on 10/27/2017.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ FirebaseDatabase.class, FirebaseAuth.class})
public class RatingTest {

    private DatabaseReference mockedDatabaseReference;

    @Before
    public void before() {
        mockedDatabaseReference = mock(DatabaseReference.class);
        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);
        when(mockedDatabaseReference.push()).thenReturn(mockedDatabaseReference);
        when(mockedDatabaseReference.setValue(anyString())).thenReturn(null);
        when(mockedDatabaseReference.setValue(anyInt())).thenReturn(null);
    }

    @Test
    public void testRateUserFirstRating() {
        double averageRating = 0;
        int numRatings = 0;
        float numStars = 5;
        String review = "hi";
        String otheruser = "iruWvC3Cf0azSiTF9EGCIo6WClY2";
        double result = RatingFragment.RateUser(averageRating, numRatings,
                numStars, review, mockedDatabaseReference, otheruser);

        assertEquals(result, 5.0);
    }

    @Test
    public void testRateUserAverageFunction() {
        double averageRating = 0;
        int numRatings = 1;
        float numStars = 5;
        String review = "hi";
        String otheruser = "iruWvC3Cf0azSiTF9EGCIo6WClY2";
        double result = RatingFragment.RateUser(averageRating, numRatings,
                numStars, review, mockedDatabaseReference, otheruser);

        assertEquals(result, 2.5);
    }

    @Test
    public void testNullReview() {
        double averageRating = 0;
        int numRatings = 1;
        float numStars = 5;
        String review = "";
        String otheruser = "iruWvC3Cf0azSiTF9EGCIo6WClY2";
        double result = RatingFragment.RateUser(averageRating, numRatings,
                numStars, review, mockedDatabaseReference, otheruser);

        assertEquals(result, -1.0);
    }

    @Test
    public void testNullUser() {
        double averageRating = 0;
        int numRatings = 1;
        float numStars = 5;
        String review = "hi";
        String otheruser = "";
        double result = RatingFragment.RateUser(averageRating, numRatings,
                numStars, review, mockedDatabaseReference, otheruser);

        assertEquals(result, -1.0);
    }

}