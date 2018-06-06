package cs310.fidgetspinners.grubmate.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import cs310.fidgetspinners.grubmate.R;
import cs310.fidgetspinners.grubmate.model.Post;

import static cs310.fidgetspinners.grubmate.R.id.map;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {

    ArrayList<Post> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map_view);

        markers = (ArrayList<Post>) getIntent().getSerializableExtra("MARKERS");

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("in map ready");

        for(Post curr_marker : markers){
            LatLng curr_location = new LatLng(curr_marker.getLatitude(),curr_marker.getLongitude());

            googleMap.addMarker(new MarkerOptions().position(curr_location)
                    .title(curr_marker.getPostName()).snippet(curr_marker.getDescription()));
        }


        if(markers.size() > 0){
            LatLng start_location = new LatLng(markers.get(0).getLatitude(),markers.get(0).getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start_location, 15));
        }
        else
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.0223519,-118.285117), 10));
    }
}
