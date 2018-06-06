package cs310.fidgetspinners.grubmate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.List;

import cs310.fidgetspinners.grubmate.ui.LoginActivity;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ FirebaseDatabase.class, FirebaseUser.class, FirebaseAuth.class, FirebaseInstanceId.class})
public class LoginTokenTest {
    private DatabaseReference mockedDatabaseReference;
    private FirebaseDatabase mockedFirebaseDatabase;
    private FirebaseAuth mockedFirebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseInstanceId firebaseInstanceId;

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

        firebaseInstanceId = mock(FirebaseInstanceId.class);
        when(firebaseInstanceId.getToken()).thenReturn("55555");

        PowerMockito.mockStatic(FirebaseInstanceId.class);
        when(FirebaseInstanceId.getInstance()).thenReturn(firebaseInstanceId);
    }

    @Test
    public void LoginTokenWorks() throws Exception {
        int result = LoginActivity.RefreshToken(firebaseUser, true);
        assertEquals(result, 1);
    }

    @Test
    public void LoginTokenFailsOnNull() throws Exception {
        int result = LoginActivity.RefreshToken(null, true);
        assertEquals(result, -1);
    }
}