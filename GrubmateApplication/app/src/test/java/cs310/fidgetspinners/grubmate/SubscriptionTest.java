package cs310.fidgetspinners.grubmate;

import org.junit.Test;

import java.util.Date;

import cs310.fidgetspinners.grubmate.ui.SubscriptionFragment;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by andyg on 10/26/2017.
 */

public class SubscriptionTest {

    // test if checkers for valid subscription ranges work
    @Test
    public void rangeisInvalidTest() throws Exception {
        Date start = mock(Date.class);
        Date end = mock(Date.class);
        Date curr = mock(Date.class);

        when(start.compareTo(end)).thenReturn(1);
        when(end.compareTo(curr)).thenReturn(1);

        boolean result = SubscriptionFragment.rangeIsInvalid(start, end, curr);
        assertEquals(result, true);

        Date start2 = mock(Date.class);
        Date end2 = mock(Date.class);
        Date curr2 = mock(Date.class);

        when(start2.compareTo(end2)).thenReturn(0);
        when(end2.compareTo(curr2)).thenReturn(0);

        // this triggers when end is after curr date

        boolean result2 = SubscriptionFragment.rangeIsInvalid(start2, end2, curr2);
        assertEquals(result2, true);
    }

    // test if checkers for valid subscription ranges work
    @Test
    public void rangeisValidTest() throws Exception {
        Date start = mock(Date.class);
        Date end = mock(Date.class);
        Date curr = mock(Date.class);

        when(start.compareTo(end)).thenReturn(0);
        when(end.compareTo(curr)).thenReturn(500);

        boolean result = SubscriptionFragment.rangeIsInvalid(start, end, curr);
        assertEquals(result, false);
    }
}
