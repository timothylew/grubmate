package cs310.fidgetspinners.grubmate;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import java.util.Date;

import cs310.fidgetspinners.grubmate.ui.RequestFragment;
import cs310.fidgetspinners.grubmate.ui.SubscriptionFragment;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by andyg on 10/26/2017.
 */

public class RequestNullTest {
    @Test
    public void NumberIsValidTest() throws Exception {
        long number = RequestFragment.parsephoneNumber("1231231234");
        assertEquals(number, 1231231234);

        long number2 = RequestFragment.parsephoneNumber("1231a31234");
        assertEquals(number2, -1);

    }

}
