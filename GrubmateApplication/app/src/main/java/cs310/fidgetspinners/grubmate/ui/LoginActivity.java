package cs310.fidgetspinners.grubmate.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.model.User;

public class LoginActivity extends AppCompatActivity {

    // TODO:  Facebook login implementation will go inside this file

    // placeholder button to let us log in.i
    private Button loginButton;
    private String TAG = "FACEBOOK LOGIN:";
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private AccessTokenTracker accessTokenTracker;

    static int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This is crossed out because im using a deprecated API. Delete this comment before Yixue sees

        // initialize ads
        MobileAds.initialize(this, "ca-app-pub-8862166713476817~4751609012");

        // check if user is already logged in
        if (counter == 0) {
            Log.i("FBBB ID", Integer.toString(R.string.facebook_app_id));
            FacebookSdk.setApplicationId("118723615485029");
            FacebookSdk.sdkInitialize(getApplicationContext());

            counter++;
        }

        //Move to the Main Activity if you're already logged in
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                if (isLoggedIn()) {
                    ProgressBar spinner = (ProgressBar)findViewById(R.id.loginspinner);
                    System.out.println("logging in!");
                    handleFacebookAccessToken(newAccessToken);
                }
            }
        };

        setContentView(R.layout.activity_login);
        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        LinearLayout ll = (LinearLayout) findViewById(R.id.loginView);

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-8862166713476817/6803057282");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        ll.addView(adView);

        LoginButton loginButton = findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        ProgressBar spinner = (ProgressBar)findViewById(R.id.loginspinner);
        spinner.setVisibility(View.VISIBLE);

        firebaseSignInWithCredential(token);
    }

    private void firebaseSignInWithCredential(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        System.out.println("signing in with credential: " + mAuth.getCurrentUser());
        updateUserDatabase(mAuth.getCurrentUser());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUserDatabase(user);

                            // update Facebook ID
                            if (AccessToken.getCurrentAccessToken().getUserId() != null) {
                                FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid())
                                        .child("FacebookID").setValue(AccessToken.getCurrentAccessToken().getUserId());
                            }

                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a mLessage to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void updateUserDatabase(final FirebaseUser firebaseUser) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        if (firebaseUser == null) {
            Log.d(TAG, "firebaseUser:null");
            System.out.println("This is a null user");
            return;
        }

        RefreshToken(firebaseUser, false);

        Log.d(TAG, "firebaseUser:nonNull");
        final User newUser = new User(firebaseUser);
        myRef.child(firebaseUser.getUid()).child("name").setValue(newUser.getName());
        myRef.child(firebaseUser.getUid()).child("email").setValue(newUser.getEmail());
        if (AccessToken.getCurrentAccessToken().getUserId() != null) {
            myRef.child(firebaseUser.getUid()).child("FacebookID").setValue(AccessToken.getCurrentAccessToken().getUserId());

            String facebookUserId = "";

            for(UserInfo profile : firebaseUser.getProviderData()) {
                // check if the provider id matches "facebook.com"
                if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                    facebookUserId = profile.getUid();
                }
            }

            // construct the URL to the profile picture, with a custom height
            // alternatively, use '?type=small|medium|large' instead of ?height=
            String photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?type=large";
            myRef.child(firebaseUser.getUid()).child("profilephoto").setValue(photoUrl);
        }

        // if ratings exists gtfo
        myRef.child(firebaseUser.getUid()).child("ratingsCount")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    database.getReference("users").child(firebaseUser.getUid())
                            .child("ratingsCount").setValue(0);
                    return;
                }
                int value = dataSnapshot.getValue(Integer.class);
                System.out.println("value of ratingsCount: " + String.valueOf(value));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("failed to read value");
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // if ratings exists gtfo
        myRef.child(firebaseUser.getUid()).child("averageRating")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            database.getReference("users").child(firebaseUser.getUid())
                                    .child("averageRating").setValue(0);
                            return;
                        }
                        int value = dataSnapshot.getValue(Integer.class);
                        System.out.println("value of ratingsCount: " + String.valueOf(value));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        System.out.println("failed to read value");
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }

    public static int RefreshToken(final FirebaseUser firebaseUser, boolean dryrun) {
        if (firebaseUser == null) return -1;

        String token = FirebaseInstanceId.getInstance().getToken();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myTokenRef = database.getReference("tokens");

        myTokenRef.child(firebaseUser.getUid()).setValue(token);

        if (!dryrun) {
            if (AccessToken.getCurrentAccessToken().getUserId() != null) {
                FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid())
                        .child("FacebookID").setValue(AccessToken.getCurrentAccessToken().getUserId());
            }
            return 1;
        } else {
            FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid())
                    .child("FacebookID").setValue("AAAAA");
            return 1;
        }
    }

    /*@Override
    public void onTokenRefresh() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myTokenRef = database.getReference("tokens");
        // Get updated InstanceID token.

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);


    }*/
}
