package cs310.fidgetspinners.grubmate.ui;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.ui.PlacePicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.model.Post;

public class PostActivity extends AppCompatActivity implements CreatePostFragment.OnFragmentInteractionListener,
    SendPostFragment.OnFragmentInteractionListener {



    FragmentManager fm;
    FragmentTransaction ft;
    Fragment f;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        i = getIntent();

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        f = new CreatePostFragment();
        ft.add(R.id.post_frame, f);
        ft.commit();

        //CreatePostFragment fragment = (CreatePostFragment) fm.findFragmentByTag(CreatePostFragment);
        // refer to this website to implement the rest of postactivity for placepicker intent
        // https://github.com/googlesamples/android-play-places/blob/master/PlacePicker/Application/src/main/java/com/example/google/playservices/placepicker/MainActivity.java

    }

    public void onFragmentInteraction(String interaction, Post post) {
        if(interaction.equals("create")) {
            ft = fm.beginTransaction();
            f = SendPostFragment.newInstance(post);
            ft.replace(R.id.post_frame, f);
            ft.commit();
        }
        else if(interaction.equals("send")) {
            Toast.makeText(this, "Your post has been sent!", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK, i);
            finish();
        }
    }

}
