package cs310.fidgetspinners.grubmate.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import cs310.fidgetspinners.grubmate.ProfanityFilter;
import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.model.Post;
import cs310.fidgetspinners.grubmate.model.Request;
import cs310.fidgetspinners.grubmate.model.User;

import static android.app.Activity.RESULT_OK;
import static cs310.fidgetspinners.grubmate.R.id.imageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreatePostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreatePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<Post> postHistory;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FloatingActionButton sendPost;
    private EditText name;
    private EditText description;
    private Spinner type;
    private Spinner category;
    private EditText tags;
    private Button locationButton;
    private TextView location;
    private Button increaseMaxPortions;
    private Button decreaseMaxPortions;
    private TextView maxPortions;
    private EditText price;
    private Button startDateButton;
    private Button startTimeButton;
    private Button endDateButton;
    private Button endTimeButton;
    private Button cameraButton;
    private Button uploadButton;
    private ImageView[] imageViews;

    private LatLng locationLatLng;
    private double lat;
    private double lon;

    private int numImages  = 0;
    private final int maxImages  = 5;
    private int currentMaximumPortions = 1;
    private int google_image = 0;

    private int mYear, mMonth, mDay, mHour, mMinute;

    private static final int REQUEST_PLACE_PICKER = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreatePostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreatePostFragment newInstance(String param1, String param2) {
        CreatePostFragment fragment = new CreatePostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_post, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        sendPost = (FloatingActionButton) v.findViewById(R.id.create_post);
        name = (EditText) v.findViewById(R.id.create_post_name);
        description = (EditText) v.findViewById(R.id.create_post_description);
        type = (Spinner) v.findViewById(R.id.create_post_type);
        category = (Spinner) v.findViewById(R.id.create_post_category);
        tags = (EditText) v.findViewById(R.id.create_post_tags);
        locationButton = (Button) v.findViewById(R.id.choose_location_button);
        location = (TextView) v.findViewById(R.id.choose_location_view);
        increaseMaxPortions = (Button) v.findViewById(R.id.create_post_incMaxPortions);
        decreaseMaxPortions = (Button) v.findViewById(R.id.create_post_decMaxPortions);
        maxPortions = (TextView) v.findViewById(R.id.create_post_maxPortions);
        price = (EditText) v.findViewById(R.id.create_post_price);
        startDateButton = (Button) v.findViewById(R.id.create_post_startDate_button);
        startTimeButton = (Button) v.findViewById(R.id.create_post_startTime_button);
        endDateButton = (Button) v.findViewById(R.id.create_post_endDate_button);
        endTimeButton = (Button) v.findViewById(R.id.create_post_endTime_button);
        cameraButton = (Button) v.findViewById(R.id.camera_button);
        uploadButton = (Button) v.findViewById(R.id.upload_button);

        imageViews = new ImageView[maxImages];
        for(int i =0; i<maxImages; i++)
            imageViews[i] = (ImageView) v.findViewById(imageView+i);


        maxPortions.setText(Integer.toString(currentMaximumPortions));

        // use google places API, place picker, to take location information
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    intentBuilder.setLatLngBounds(new LatLngBounds(
                            new LatLng(34.017454, -118.293056),
                            new LatLng(34.026761, -118.279182)
                            ));
                    Intent intent = intentBuilder.build(getActivity());
                    startActivityForResult(intent, REQUEST_PLACE_PICKER);
                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil
                            .getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(getActivity(), "Google Play Services is not available.",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        increaseMaxPortions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMaximumPortions++;
                maxPortions.setText(Integer.toString(currentMaximumPortions));
            }
        });

        decreaseMaxPortions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentMaximumPortions != 1) {
                    currentMaximumPortions--;
                    maxPortions.setText(Integer.toString(currentMaximumPortions));
                }
            }
        });

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        endTimeButton.setText(hour + ":" + minute);
                    }
                }, mHour, mMinute, false);

                timePickerDialog.show();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numImages == maxImages) {
                    Toast.makeText(getActivity(), "Maximum 5 Images!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                }
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(numImages == maxImages) {
                    Toast.makeText(getActivity(), "Maximum 5 Images!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String [] postTypes = getResources().getStringArray(R.array.types);
                final String [] postCategories = getResources().getStringArray(R.array.categories);
                String [] postTags = tags.getText().toString().trim().toLowerCase().split("\\s*,\\s*");

                final ArrayList<String> postTagsArrayList = new ArrayList<String>(Arrays.asList(postTags));
                System.out.println(postTags.length);

                final String startRange = startDateButton.getText().toString().trim()
                        + " "+ startTimeButton.getText().toString().trim();

                final String endRange = endDateButton.getText().toString().trim()
                        + " "+ endTimeButton.getText().toString().trim();

                if(locationLatLng == null)
                    locationLatLng =  new LatLng(0,0);
                //Upload images
                final ArrayList<String> imagesList = new ArrayList<String>();
                System.out.println("Size first"+imagesList.size());
                System.out.println("Size num"+numImages);

                FirebaseDatabase.getInstance().getReference().child("users")
                        .child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);
                        ArrayList<Post> existingPosts = u.getActivePosts();
                        int numPosts = 0;
                        if(existingPosts != null)
                            numPosts = existingPosts.size();

                        for(int i=0; i<numImages;i++) {
                            StorageReference imageRef = mStorage.getReference()
                                    .child(mUser.getUid() + "/" + numPosts + "/"+i+".jpg");

                            imageViews[i].setDrawingCacheEnabled(true);
                            imageViews[i].buildDrawingCache();
                            Bitmap bitmap = imageViews[i].getDrawingCache();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = imageRef.putBytes(data);
                            imagesList.add(mUser.getUid() + "/" + numPosts + "/"+i+".jpg");
                            System.out.println("Size inin"+imagesList.size());

                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getActivity(), "Error in Uploading Image", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                }
                            });
                        }
                        System.out.println("Size second"+imagesList.size());

                        Post returnedPost = new Post();
                        String result = CreatePost(mUser.getUid(), name.getText().toString().trim(), postTypes[type.getSelectedItemPosition()],
                                postCategories[category.getSelectedItemPosition()], location.getText().toString().trim(),
                                postTagsArrayList, description.getText().toString().trim(), price.getText().toString().trim(),
                                currentMaximumPortions, currentMaximumPortions, startRange, endRange, imagesList, null,
                                locationLatLng.latitude, locationLatLng.longitude, returnedPost);

                        if (result.equals("profane")) {
                            Toast.makeText(getActivity(), "You cannot make a profane post", Toast.LENGTH_LONG).show();
                            return;
                        } else if(result.equals("postNameBlank")){
                            Toast.makeText(getActivity(), "Post name cannot be blank", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(result.equals("postNameLong")) {
                            Toast.makeText(getActivity(), "Post name cannot be longer than 50 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(result.equals("postDescriptionBlank")){
                            Toast.makeText(getActivity(), "Description cannot be blank", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(result.equals("postLocationBlank")){
                            Toast.makeText(getActivity(), "Please select a location", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(result.equals("postPriceBlank")){ //price required for homemade
                            Toast.makeText(getActivity(), "Price field cannot be blank", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(result.equals("startRangeBlank")){
                            Toast.makeText(getActivity(), "Please select a Start Date and Time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(result.equals("endRangeBlank")){
                            Toast.makeText(getActivity(), "Please select a End Date and Time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(result.equals("dateMismatchError")){
                            Toast.makeText(getActivity(), "End time should be after Start time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(result.equals("dateParsingError")){ //image required for homemade
                            Toast.makeText(getActivity(), "Error in Parsing Date", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(result.equals("postNoImage")){ //image required for homemade
                            Toast.makeText(getActivity(), "Please add at least 1 Image", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(result.equals("NoErrors")){
                            if(returnedPost == null)
                                System.out.println("returned is null");
                            else
                                onButtonPressed("create", returnedPost);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                //Parse Date
//                Date startRange = null, endRange = null;
//                try {
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
//                    startRange = dateFormat.parse(startDateButton.getText().toString().trim()
//                            + " "+ startTimeButton.getText().toString().trim());
//
//                    endRange = dateFormat.parse(endDateButton.getText().toString().trim()
//                            + " "+ endTimeButton.getText().toString().trim());
//
//                    Date currDate = new Date();
//                    if(startRange.compareTo(endRange) > 0){
//                        Toast.makeText(getActivity(), "End time should be after Start time", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                }catch(ParseException pex){
//                    Toast.makeText(getActivity(), "Error in Parsing Date", Toast.LENGTH_SHORT).show();
//                }



                //create post
//                Post post = new Post(mUser.getUid(), name.getText().toString().trim(), postTypes[type.getSelectedItemPosition()],
//                        postCategories[category.getSelectedItemPosition()], location.getText().toString().trim(),
//                        postTagsArrayList, description.getText().toString().trim(), price.getText().toString().trim(),
//                        currentMaximumPortions, currentMaximumPortions, startRange, endRange, imagesList, null,
//                        locationLatLng.latitude, locationLatLng.longitude);

//                onButtonPressed("create", post);


            }
        });

        // use google places API, place picker, to take location information

        return v;

    }

    public static String CreatePost(String originalPoster, String postName, String type, String category, String location,
                                  ArrayList<String> tags, String description, String price, int maxPortions, int portionsAvailable,
                                  String startRange, String endRange, ArrayList<String> pictures, ArrayList<Request> requests,
                                  double latitude, double longitude, Post returnPost){
        ProfanityFilter pF = new ProfanityFilter();
        ArrayList<String> resultsName = pF.badWordsFound(postName);
        ArrayList<String> resultsDesc = pF.badWordsFound(description);

        System.out.println("PROFANE RESULT: " + resultsName + "FOR NAME: " + postName);
        System.out.println("PROFANE RESULT2: " + resultsDesc);
        if (!resultsName.isEmpty() || !resultsDesc.isEmpty()) {
            return "profane";
        }

        if(postName.length() == 0){
//            Toast.makeText(getActivity(), "Post name cannot be blank", Toast.LENGTH_SHORT).show();
            return "postNameBlank";
        }
        else if(postName.length() > 50) {
//            Toast.makeText(getActivity(), "Post name cannot be longer than 50 characters", Toast.LENGTH_SHORT).show();
            return "postNameLong";
        }
        else if(description.length() == 0){
//            Toast.makeText(getActivity(), "Description cannot be blank", Toast.LENGTH_SHORT).show();
            return "postDescriptionBlank";
        }
        else if(location.length() == 0){
//            Toast.makeText(getActivity(), "Please select a location", Toast.LENGTH_SHORT).show();
            return "postLocationBlank";
        }
        else if(price.length() == 0 && type.equals("Homemade")){ //price required for homemade
//            Toast.makeText(getActivity(), "Price field cannot be blank", Toast.LENGTH_SHORT).show();
            return "postPriceBlank";
        }
        else if(startRange.length() == 0){
//            Toast.makeText(getActivity(), "Please select a Start Date", Toast.LENGTH_SHORT).show();
            return "postStartRangeBlank";
        }
        else if(endRange.length() == 0){
//            Toast.makeText(getActivity(), "Please select a Start Date", Toast.LENGTH_SHORT).show();
            return "postEndRangeBlank";
        }
//        else if(startTimeButton.getText().toString().trim().length() == 0
//                || startTimeButton.getText().toString().trim().equals("Select Start Time")){
//            Toast.makeText(getActivity(), "Please select a Start Time", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        else if(endDateButton.getText().toString().trim().length() == 0
//                || endDateButton.getText().toString().trim().equals("Select End Date")){
//            Toast.makeText(getActivity(), "Please select a End Date", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        else if(endTimeButton.getText().toString().trim().length() == 0
//                || endTimeButton.getText().toString().trim().equals("Select End Time")){
//            Toast.makeText(getActivity(), "Please select a End Time", Toast.LENGTH_SHORT).show();
//            return;
//        }
        else if(pictures.size() == 0 && type.equals("Homemade")){ //image required for homemade
//            Toast.makeText(getActivity(), "Please add at least 1 Image", Toast.LENGTH_SHORT).show();
            System.out.println("Size"+pictures.size());
            return "postNoImage";
        }

        //Parse Date
        Date startDate = null, endDate = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            startDate = dateFormat.parse(startRange);

            endDate = dateFormat.parse(endRange);

            if(startDate.compareTo(endDate) > 0){
                //Toast.makeText(getActivity(), "End time should be after Start time", Toast.LENGTH_SHORT).show();
                return "dateMismatchError";
            }

        }catch(ParseException pex){
           // Toast.makeText(getActivity(), "Error in Parsing Date", Toast.LENGTH_SHORT).show();
            return "dateParsingError";
        }

        tags.addAll(Arrays.asList(postName.toLowerCase().split(" ")));
        tags.add(category.toLowerCase());
        tags.add(type.toLowerCase());

//      create post
        returnPost.initializePost(originalPoster, postName, type,
                    category, location,
                    tags, description, price,
                    maxPortions, portionsAvailable, startDate, endDate, pictures, null,
                    latitude, longitude);

//                onButtonPressed("create", post);
        return "NoErrors";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode != RESULT_OK) {
            return;
        }
        // Check which request we're responding to
        if (requestCode == REQUEST_PLACE_PICKER) {
                Place place = PlacePicker.getPlace(data, getActivity());
                CharSequence address = place.getAddress();
                locationLatLng = place.getLatLng();
                lat = locationLatLng.latitude;
                lon = locationLatLng.longitude;
                location.setText(address);

            //get photos
            //ref: https://developers.google.com/places/android-api/photos
            if(type.getSelectedItemPosition() == 1) {

                final String placeId = place.getId();
                final GeoDataClient mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
                final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
                photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                        try {
                            // Get the list of photos.
                            PlacePhotoMetadataResponse photos = task.getResult();
                            // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                            PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                            // Get the first photo in the list.
                            PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                            // Get a full-size bitmap for the photo.
                            Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                            photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                    PlacePhotoResponse photo = task.getResult();
                                    Bitmap bitmap = photo.getBitmap();
                                    int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                                    int height = Resources.getSystem().getDisplayMetrics().heightPixels;
                                    imageViews[numImages].setLayoutParams(new LinearLayout.LayoutParams(width / 3, height / 5));
                                    imageViews[numImages].setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    imageViews[numImages].setImageBitmap(bitmap);
                                    google_image = numImages;
                                    numImages++;
                                }
                            });
                        }catch (IllegalStateException ise){
                            System.out.println("No Photos");
                        }
                    }
                });
            }
//            else
//                imageViews[google_image].setImageDrawable(null);
        }
        else {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            int height = Resources.getSystem().getDisplayMetrics().heightPixels;
            Bitmap imageBitmap = null;

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
            }
            else if (requestCode == PICK_IMAGE) {
                try {
                    InputStream imageStream = getContext().getContentResolver().openInputStream(data.getData());
                    imageBitmap = BitmapFactory.decodeStream(imageStream);
                } catch (Exception ex) {}
            }

            if(imageBitmap != null){
                imageViews[numImages].setLayoutParams(new LinearLayout.LayoutParams(width/3, height/5));
                imageViews[numImages].setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageViews[numImages].setImageBitmap(imageBitmap);
                numImages++;
            }
        }
    }

    public void onButtonPressed(String interaction, Post post) {
        if (mListener != null) {
            mListener.onFragmentInteraction(interaction, post);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String interaction, Post post);
    }
}
