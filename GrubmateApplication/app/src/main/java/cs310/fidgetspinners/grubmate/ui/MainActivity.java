package cs310.fidgetspinners.grubmate.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import cs310.fidgetspinners.grubmate.R;

import static android.R.attr.action;

public class MainActivity extends AppCompatActivity
        implements FeedFragment.OnFragmentInteractionListener, GroupFragment.OnFragmentInteractionListener,
        NotificationFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener,
        SubscriptionFragment.OnFragmentInteractionListener, RatingFragment.OnFragmentInteractionListener {


    FragmentManager fm;
    FragmentTransaction ft;
    Fragment f;

    public static final String MODIFY_GROUP_ORIGIN = "fidgetspinners.csci310.grubmate.modifygrouporigin";

    // Potential add to mainActivity
    private Toolbar mToolbar;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    public BottomNavigationView navigationBar;

    // Logging
    private final String TAG = "MainActivity: ";


    //TODO if auto import fails or something, make sure to switch to support fragment in the Fragment class
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // This shows the edit text by default but it's potentially not what we want.  Think about this TODO
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(R.layout.search_bar);//add the custom view
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        // Add feed fragment to the main activity
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        f = new FeedFragment();
        ft.replace(R.id.main_frame, f, "feed").addToBackStack("feed");
        ft.commit();

        navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if(itemId == R.id.bottom_feed) {
                    f = fm.findFragmentByTag("feed");
                    if(f == null) {
                        System.out.println("created");
                        ft = fm.beginTransaction();
                        f = new FeedFragment();
                        ft.replace(R.id.main_frame, f).addToBackStack("feed");
                        ft.commit();
                    }
                    else {
                        FeedFragment.pullPostFeed();
                        System.out.println("replaced");
                        ft = fm.beginTransaction();
                        ft.replace(R.id.main_frame, f);
                        ft.commit();
                    }
                    getSupportActionBar().show();
                }
                else if(itemId == R.id.bottom_groups) {
                    f = fm.findFragmentByTag("groups");
                    if(f == null) {
                        System.out.println("created");
                        ft = fm.beginTransaction();
                        f = new GroupFragment();
                        ft.replace(R.id.main_frame, f, "groups").addToBackStack("groups");
                        ft.commit();
                    }
                    else {
                        System.out.println("replaced");
                        ft = fm.beginTransaction();
                        ft.replace(R.id.main_frame, f);
                        ft.commit();
                    }
                    getSupportActionBar().hide();
                }
                else if(itemId == R.id.bottom_notifications) {
                    f = fm.findFragmentByTag("notifications");
                    if(f == null) {
                        System.out.println("created");
                        ft = fm.beginTransaction();
                        f = new NotificationFragment();
                        ft.replace(R.id.main_frame, f, "notifications").addToBackStack("notifications");
                        ft.commit();
                    }
                    else {
                        System.out.println("replaced");
                        ft = fm.beginTransaction();
                        ft.replace(R.id.main_frame, f);
                        ft.commit();
                    }
                    getSupportActionBar().hide();
                }
                else if(itemId == R.id.bottom_profile) {
                    f = fm.findFragmentByTag("profile");
                    if(f == null) {
                        System.out.println("created");
                        ft = fm.beginTransaction();
                        f = new ProfileFragment();
                        ft.replace(R.id.main_frame, f, "profile").addToBackStack("profile");
                        ft.commit();
                    }
                    else {
                        System.out.println("replaced");
                        ft = fm.beginTransaction();
                        ft.replace(R.id.main_frame, f);
                        ft.commit();
                    }
                    getSupportActionBar().hide();
                }
                else if(itemId == R.id.bottom_subscriptions) {
                    f = fm.findFragmentByTag("subscriptions");
                    if(f == null) {
                        System.out.println("created");
                        ft = fm.beginTransaction();
                        f = new SubscriptionFragment();
                        ft.replace(R.id.main_frame, f, "subscriptions").addToBackStack("subscriptions");
                        ft.commit();
                    }
                    else {
                        System.out.println("replaced");
                        ft = fm.beginTransaction();
                        ft.replace(R.id.main_frame, f);
                        ft.commit();
                    }
                    getSupportActionBar().hide();
                }

                return true;
            }
        });

        // add functionality to search query
        final EditText searchTextField = (EditText) findViewById(R.id.searchbar); //the text editor
        searchTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    FeedFragment.searchForPost(searchTextField.getText().toString());
                    return true;
                }
                return false;

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Returned from an activity");
    }

    // TODO do research on if this implementation is required.  if not, remove from all fragments.
    public void onFragmentInteraction(String interaction) {
        if(interaction.equals("create")) {
            Intent i = new Intent(MainActivity.this, ManageGroupActivity.class);
            i.putExtra(MODIFY_GROUP_ORIGIN, "create");
            startActivity(i);
        }
        else if(interaction.equals("edit")) {
            Intent i = new Intent(MainActivity.this, ManageGroupActivity.class);
            i.putExtra(MODIFY_GROUP_ORIGIN, "edit");
            startActivity(i);
        }
        else if(interaction.equals("delete")) {
            Intent i = new Intent(MainActivity.this, ManageGroupActivity.class);
            i.putExtra(MODIFY_GROUP_ORIGIN, "delete");
            startActivity(i);
        }

    }

    // All menu code taken from rhesoft.com Android menu bar tutorial. (Aron bordin)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_filter:
                handleActionFilter();
                return true;
            case R.id.create_post:
                handleCreatePost();
                return true;
            case R.id.action_search:
                doSearch();
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void handleActionFilter() {
        Intent i = new Intent(MainActivity.this, FilterActivity.class);
        //TODO do we want to do this for result
        startActivityForResult(i, 1); //TODO remember we set result code 1 here
    }

    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open

            action.setDisplayShowCustomEnabled(true); //disable a custom view inside the actionbar
            //action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);
            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.search_white));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText) action.getCustomView().findViewById(R.id.searchbar); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch();

                        return true;
                    }
                    return false;
                }
            });


            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.search_white));

            isSearchOpened = true;
        }
    }

    protected void handleCreatePost() {
        Intent i = new Intent(MainActivity.this, PostActivity.class);
        //TODO do we want to do this for result
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }
       // super.onBackPressed();
    }

    private void doSearch() {
        System.out.println("do search executed");
        final EditText searchTextField = (EditText) findViewById(R.id.searchbar); //the text editor

        // get and clear search
        String query = searchTextField.getText().toString();

        // close keybaord
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        FeedFragment.searchForPost(searchTextField.getText().toString());
        searchTextField.setText("");

    }

    public void onFragmentInteractionRatingFragment(Uri uri) {

    }

    public void onFragmentInteractionProfile(String interaction) {

    }
}
