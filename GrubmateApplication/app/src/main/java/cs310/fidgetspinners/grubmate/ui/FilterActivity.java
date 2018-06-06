package cs310.fidgetspinners.grubmate.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.Util;
import cs310.fidgetspinners.grubmate.model.Post;

import static cs310.fidgetspinners.grubmate.ui.FeedFragment.filterOn;
import static cs310.fidgetspinners.grubmate.ui.FeedFragment.postSnippetAdapter;
import static cs310.fidgetspinners.grubmate.ui.FeedFragment.posts;
import static cs310.fidgetspinners.grubmate.ui.FeedFragment.pullPostFeed;

public class FilterActivity extends AppCompatActivity {

    private Button applyFilters;
    private RadioGroup filters;
    private Button closestLocation;
    private Button earliestTime;
    private Button latestTime;
    private Button noDairy;
    private Button noNuts;
    private Button vegetarian;
    private Button timeRange;

    private Button startDateButton;
    private Button startTimeButton;
    private Button endDateButton;
    private Button endTimeButton;
    private int mYear, mMonth, mDay, mHour, mMinute;

    Intent i;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private boolean firstCall = false;

    private static ArrayList<String> dairy;
    private static ArrayList<String> nuts;
    private static ArrayList<String> meats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        i = getIntent();
        FeedFragment.pullPostFeed();

        dairy = new ArrayList<>();
        nuts = new ArrayList<>();
        meats = new ArrayList<>();

        dairy.add("egg");
        dairy.add("milk");
        dairy.add("butter");
        dairy.add("cheese");
        dairy.add("cream");
        dairy.add("yogurt");

        nuts.add("nut");
        nuts.add("cashew");
        nuts.add("almond");
        nuts.add("pistachio");
        nuts.add("pecan");
        nuts.add("macadamia");

        meats.add("beef");
        meats.add("chicken");
        meats.add("pork");
        meats.add("lamb");
        meats.add("fish");
        meats.add("salmon");
        meats.add("tuna");
        meats.add("cod");
        meats.add("egg");

        applyFilters = (Button) findViewById(R.id.apply_filters);
        closestLocation = (Button) findViewById(R.id.location);
        earliestTime = (Button) findViewById(R.id.earliest);
        latestTime = (Button) findViewById(R.id.latest);
        noDairy = (Button) findViewById(R.id.dairy);
        noNuts = (Button) findViewById(R.id.nuts);
        vegetarian = (Button) findViewById(R.id.vege);
        timeRange = (Button) findViewById(R.id.timeRange);

        startDateButton = (Button) findViewById(R.id.create_post_startDate_button);
        startTimeButton = (Button) findViewById(R.id.create_post_startTime_button);
        endDateButton = (Button) findViewById(R.id.create_post_endDate_button);
        endTimeButton = (Button) findViewById(R.id.create_post_endTime_button);
        startDateButton.setEnabled(false);
        startTimeButton.setEnabled(false);
        endDateButton.setEnabled(false);
        endTimeButton.setEnabled(false);
        startDateButton.setVisibility(View.INVISIBLE);
        startTimeButton.setVisibility(View.INVISIBLE);
        endDateButton.setVisibility(View.INVISIBLE);
        endTimeButton.setVisibility(View.INVISIBLE);

        filters = (RadioGroup) findViewById(R.id.group);
        filters.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) { //sets button as "checked"
                    case R.id.location:
                        filters.check(R.id.location);
                        startDateButton.setEnabled(false);
                        startTimeButton.setEnabled(false);
                        endDateButton.setEnabled(false);
                        endTimeButton.setEnabled(false);
                        startDateButton.setVisibility(View.INVISIBLE);
                        startTimeButton.setVisibility(View.INVISIBLE);
                        endDateButton.setVisibility(View.INVISIBLE);
                        endTimeButton.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.earliest:
                        filters.check(R.id.earliest);
                        startDateButton.setEnabled(false);
                        startTimeButton.setEnabled(false);
                        endDateButton.setEnabled(false);
                        endTimeButton.setEnabled(false);
                        startDateButton.setVisibility(View.INVISIBLE);
                        startTimeButton.setVisibility(View.INVISIBLE);
                        endDateButton.setVisibility(View.INVISIBLE);
                        endTimeButton.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.latest:
                        filters.check(R.id.latest);
                        startDateButton.setEnabled(false);
                        startTimeButton.setEnabled(false);
                        endDateButton.setEnabled(false);
                        endTimeButton.setEnabled(false);
                        startDateButton.setVisibility(View.INVISIBLE);
                        startTimeButton.setVisibility(View.INVISIBLE);
                        endDateButton.setVisibility(View.INVISIBLE);
                        endTimeButton.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.dairy:
                        filters.check(R.id.dairy);
                        startDateButton.setEnabled(false);
                        startTimeButton.setEnabled(false);
                        endDateButton.setEnabled(false);
                        endTimeButton.setEnabled(false);
                        startDateButton.setVisibility(View.INVISIBLE);
                        startTimeButton.setVisibility(View.INVISIBLE);
                        endDateButton.setVisibility(View.INVISIBLE);
                        endTimeButton.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.nuts:
                        filters.check(R.id.nuts);
                        startDateButton.setEnabled(false);
                        startTimeButton.setEnabled(false);
                        endDateButton.setEnabled(false);
                        endTimeButton.setEnabled(false);
                        startDateButton.setVisibility(View.INVISIBLE);
                        startTimeButton.setVisibility(View.INVISIBLE);
                        endDateButton.setVisibility(View.INVISIBLE);
                        endTimeButton.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.vege:
                        filters.check(R.id.vege);
                        startDateButton.setEnabled(false);
                        startTimeButton.setEnabled(false);
                        endDateButton.setEnabled(false);
                        endTimeButton.setEnabled(false);
                        startDateButton.setVisibility(View.INVISIBLE);
                        startTimeButton.setVisibility(View.INVISIBLE);
                        endDateButton.setVisibility(View.INVISIBLE);
                        endTimeButton.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.timeRange:
                        filters.check(R.id.timeRange);
                        startDateButton.setEnabled(true);
                        startTimeButton.setEnabled(true);
                        endDateButton.setEnabled(true);
                        endTimeButton.setEnabled(true);
                        startDateButton.setVisibility(View.VISIBLE);
                        startTimeButton.setVisibility(View.VISIBLE);
                        endDateButton.setVisibility(View.VISIBLE);
                        endTimeButton.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });

        applyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // after filtering, disable pullpost refresh
                FeedFragment.pullPosts = false;
                FeedFragment.filterOn = true;

                //pullPostFeed(); //get latest version of feed (reset post array)
                onRadioButtonClicked();

                // check if filters invalid

                FeedFragment.postSnippetAdapter.notifyDataSetChanged();
//                FeedFragment.feedView.invalidate();

                Toast.makeText(getApplicationContext(), "Your filters have been applied!", Toast.LENGTH_SHORT).show();
               // i.putExtra()
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        startDateButton.setText((month + 1) + "-" + day + "-" + year);
                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(FilterActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        startTimeButton.setText(hour + ":" + minute);
                    }
                }, mHour, mMinute, false);

                timePickerDialog.show();
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        endDateButton.setText((month + 1) + "-" + day + "-" + year);
                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(FilterActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        endTimeButton.setText(hour + ":" + minute);
                    }
                }, mHour, mMinute, false);

                timePickerDialog.show();
            }
        });

    }

    public static ArrayList<Post> setEarliest(ArrayList<Post> toFilter, FeedFragment.PostSnippetAdapter psa) {
        ArrayList<Post> temp = new ArrayList<Post>();
        Collections.sort(toFilter, new Comparator<Post>() {
            @Override
            public int compare(Post lhs, Post rhs) {
                return lhs.getStartRange().compareTo(rhs.getStartRange());
            }
        });
        //updates post arraylist in feedview

        temp.addAll(toFilter);
        psa.swapItems(temp);
        FeedFragment.setPosts(temp);

        return temp;

    }

    public static ArrayList<Post> setLatest(ArrayList<Post> toFilter, FeedFragment.PostSnippetAdapter psa) {
        ArrayList<Post> temp = new ArrayList<Post>();
        Collections.sort(toFilter, new Comparator<Post>() {
            @Override
            public int compare(Post lhs, Post rhs) {
                return rhs.getStartRange().compareTo(lhs.getStartRange());
            }
        });

        temp.addAll(toFilter);
        psa.swapItems(temp);
        FeedFragment.setPosts(temp);

        return temp;
    }

    public static ArrayList<Post> setDairy(ArrayList<Post> toFilter, FeedFragment.PostSnippetAdapter psa) {
        ArrayList<Post> temp = new ArrayList<Post>();

        for (int i = 0; i < toFilter.size(); i++) {
            temp.add(toFilter.get(i));
        }

        for (int i = 0; i <temp.size(); i++) {
            Post p =  temp.get(i);

            filters:
            for (String filter : dairy) {
                if (p.getPostName().contains(filter) || p.getDescription().contains(filter)) {
                    temp.remove(p);
                    break filters;
                }
                for (String tag : p.getTags()) {
                    if (tag.contains(filter)) {
                        temp.remove(p);
                        break filters;
                    }
                }
            }
        }
        psa.swapItems(temp);
        FeedFragment.setPosts(temp);

        return temp;
    }

    public static ArrayList<Post> setNuts(ArrayList<Post> toFilter, FeedFragment.PostSnippetAdapter psa) {
        ArrayList<Post> temp = new ArrayList<Post>();

        for (int i = 0; i < toFilter.size(); i++) {
            temp.add(toFilter.get(i));
        }

        for (int i = 0; i <temp.size(); i++) {
            Post p =  temp.get(i);

            filters:
            for (String filter : nuts) {
                if (p.getPostName().contains(filter) || p.getDescription().contains(filter)) {
                    temp.remove(p);
                    break filters;
                }
                for (String tag : p.getTags()) {
                    if (tag.contains(filter)) {
                        temp.remove(p);
                        break filters;
                    }
                }
            }
        }
        psa.swapItems(temp);
        FeedFragment.setPosts(temp);

        return temp;
    }

    public static ArrayList<Post> setVege(ArrayList<Post> toFilter, FeedFragment.PostSnippetAdapter psa) {
        ArrayList<Post> temp = new ArrayList<Post>();

        for (int i = 0; i < toFilter.size(); i++) {
            temp.add(toFilter.get(i));
        }

        for (int i = 0; i <temp.size(); i++) {
            Post p =  temp.get(i);

            filters:
            for (String filter : meats) {
                if (p.getPostName().contains(filter) || p.getDescription().contains(filter)) {
                    temp.remove(p);
                    break filters;
                }
                for (String tag : p.getTags()) {
                    if (tag.contains(filter)) {
                        temp.remove(p);
                        break filters;
                    }
                }
            }
        }
        psa.swapItems(temp);
        FeedFragment.setPosts(temp);

        return temp;
    }

    private void setLocation(ArrayList<Post> toFilter, FeedFragment.PostSnippetAdapter psa) {
        //compare latlong of everyone
        LocationManager locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
        Location place = null;
        try {
            place = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException sE) {
            sE.printStackTrace();
            Toast.makeText(getApplicationContext(), "We need permissions to view location!", Toast.LENGTH_LONG);
            return;
        }

        if (place == null) return;

        final LatLng currentLatLng = new LatLng(place.getLatitude(), place.getLongitude());
        System.out.println("current location: " + currentLatLng.longitude + " and " + currentLatLng.latitude);

        Collections.sort(toFilter, new Comparator<Post>() {
            @Override
            public int compare(Post lhs, Post rhs) {
                if (lhs.getLatitude() == 0.0 || rhs.getLatitude() == 0.0) {
                    System.out.println("Playing with a nul value!");
                    return 0;
                }

                LatLng lhsLatLng = new LatLng(lhs.getLatitude(), lhs.getLongitude());
                LatLng rhsLatLng = new LatLng(rhs.getLatitude(), rhs.getLongitude());

                System.out.println("comparing: " + lhsLatLng.toString());
                System.out.println("comparing: " + rhsLatLng.toString());

                return distanceFrom(lhsLatLng, rhsLatLng, currentLatLng);
            }
        });

        ArrayList<Post> temp = new ArrayList<>();
        temp.addAll(toFilter);

        psa.swapItems(temp);
    }

    public static ArrayList<Post> sortPostsLatLng(ArrayList<Post> postitems, final LatLng current) {
        Collections.sort(postitems, new Comparator<Post>() {
            @Override
            public int compare(Post lhs, Post rhs) {
                if (lhs.getLatitude() == 0.0 || rhs.getLatitude() == 0.0) {
                    System.out.println("Playing with a nul value!");
                    return 0;
                }

                LatLng lhsLatLng = new LatLng(lhs.getLatitude(), lhs.getLongitude());
                LatLng rhsLatLng = new LatLng(rhs.getLatitude(), rhs.getLongitude());

                System.out.println("comparing: " + lhsLatLng.toString());
                System.out.println("comparing: " + rhsLatLng.toString());

                return distanceFrom(lhsLatLng, rhsLatLng, current);
            }
        });

        return postitems;
    }

    private void setRange(ArrayList<Post> toFilter, FeedFragment.PostSnippetAdapter psa) {
        System.out.println("filtering by time range1");
        try {
            System.out.println("filtering by time range2");
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            Date startRange = dateFormat.parse(startDateButton.getText().toString().trim()
                    + " "+ startTimeButton.getText().toString().trim());

            Date endRange = dateFormat.parse(endDateButton.getText().toString().trim()
                    + " "+ endTimeButton.getText().toString().trim());

            ArrayList<Post> newPosts = new ArrayList<Post>();
            for (Post individualPost : toFilter) {
                if ((startRange.compareTo(individualPost.getStartRange()) <= 0) &&
                        (endRange.compareTo(individualPost.getStartRange()) >= 0)) {
                    //this is the post we want
                    System.out.println("Start ranges: " + startRange + " " + individualPost.getStartRange());
                    System.out.println("End ranges: " + endRange + " " + individualPost.getStartRange());
                    newPosts.add(individualPost);
                }
            }

//            postSnippetAdapter.swapItems(newPosts);
            FeedFragment.setPosts(newPosts);
            psa.swapItems(newPosts);

        }catch(Exception ex){}
    }

    public void onRadioButtonClicked() {
        int radioButtonID = filters.getCheckedRadioButtonId();

        // Check which radio button was clicked
        switch(radioButtonID) {
            case R.id.location:
                System.out.println("setting location");
                setLocation(FeedFragment.posts, postSnippetAdapter);
                break;
            case R.id.earliest:
                setEarliest(FeedFragment.posts, postSnippetAdapter);
                break;
            case R.id.latest:
                setLatest(FeedFragment.posts, postSnippetAdapter);
                break;
            case R.id.dairy:
                setDairy(FeedFragment.posts, postSnippetAdapter);
                break;
            case R.id.nuts:
                setNuts(FeedFragment.posts, postSnippetAdapter);
                break;
            case R.id.vege:
                setVege(FeedFragment.posts, postSnippetAdapter);
                break;
            case R.id.timeRange:
                setRange(FeedFragment.posts, postSnippetAdapter);
                break;
        }
    }

    public static int distanceFrom(LatLng lhs, LatLng rhs, LatLng compare) {
        System.out.println("implementing the distance operator");
        double distanceOne = Math.sqrt(Math.pow(lhs.latitude - compare.latitude, 2)
                + Math.pow(lhs.longitude - compare.longitude, 2));
        double distanceTwo = Math.sqrt(Math.pow(rhs.latitude - compare.latitude, 2)
                + Math.pow(rhs.longitude - compare.longitude, 2));

        if (distanceOne < distanceTwo) {
            return 0;
        } else {
            return 1;
        }
    }
}