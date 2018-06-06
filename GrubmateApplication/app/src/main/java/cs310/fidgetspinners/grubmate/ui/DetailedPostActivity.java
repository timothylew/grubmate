package cs310.fidgetspinners.grubmate.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import cs310.fidgetspinners.grubmate.R;


public class DetailedPostActivity extends AppCompatActivity implements
        RequestFragment.OnFragmentInteractionListener, DetailedPostFragment.OnFragmentInteractionListener,
        RatingFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener{

    private Intent i;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Fragment f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_post);

        i = getIntent();
        int position = i.getIntExtra(FeedFragment.FEED_POSITION, -1);
        String screenName = i.getStringExtra(FeedFragment.SCREEN_NAME);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        f = DetailedPostFragment.newInstance(position, screenName);
        ft.replace(R.id.post_frame_detailed, f);

        ft.commit();
    }

    @Override
    public void onFragmentInteraction(String request) {

        if(request.equals("itemRequested")) {
            Toast.makeText(this, "Your request was successful!", Toast.LENGTH_SHORT).show();
            finish();
        }
     }

     @Override
    public void onFragmentInteractionRatingFragment(Uri uri){}

    public void onFragmentInteractionProfile(String interaction) {}


}
