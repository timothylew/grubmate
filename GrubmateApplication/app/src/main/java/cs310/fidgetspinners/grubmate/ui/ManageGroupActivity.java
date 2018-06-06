package cs310.fidgetspinners.grubmate.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import cs310.fidgetspinners.grubmate.R;

public class ManageGroupActivity extends AppCompatActivity implements CreateGroupFragment.OnFragmentInteractionListener,
        DeleteGroupFragment.OnFragmentInteractionListener,
        GroupSelectionFragment.OnFragmentInteractionListener,
        ModifyGroupFragment.OnFragmentInteractionListener {

    FragmentManager fm;
    FragmentTransaction ft;
    Fragment f;
    Intent i;
    String origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_group);

        i = getIntent();

        origin = i.getStringExtra(MainActivity.MODIFY_GROUP_ORIGIN);
        if(origin.equals("create")) {
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            f = new CreateGroupFragment();
            ft.replace(R.id.manage_group_frame, f);
            ft.commit();
        }
        else if (origin.equals("delete")) {
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            f = new DeleteGroupFragment();
            ft.replace(R.id.manage_group_frame, f);
            ft.commit();
        }
        // The group selection fragment allows a user to select which group to edit.
        else if(origin.equals("edit")) {
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            f = new GroupSelectionFragment();
            ft.replace(R.id.manage_group_frame, f);
            ft.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri Uri) {

    }

    @Override
    public void onFragmentInteractionSelection(Uri uri) {

    }

    @Override
    public void onFragmentInteractionModify(Uri uri) {

    }

    @Override
    public void onFragmentInteractionDelete(Uri uri) {

    }

}
