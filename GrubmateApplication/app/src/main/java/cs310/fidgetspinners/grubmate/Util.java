package cs310.fidgetspinners.grubmate;

import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cs310.fidgetspinners.grubmate.model.Request;

/**
 * Created by andyg on 10/16/2017.
 */

public class Util {

    public static int checkDeniedStatusValid(int status) {
        if (status == Request.BOTH_CONFIRMED) {
            return -1;
        } else if (status == Request.ACCEPTED
                || status == Request.OWNER_CONFIRMED
                || status == Request.REQUESTER_CONFIRMED) {
            return -2;
        }
        return 0;
    }

    // SEND REQUEST GIVEN A USER'S FIREBASE ID AND A MESSAGE
    public static void sendRequest(String userID, final String message) {

        // find the user ID
        FirebaseDatabase.getInstance().getReference().child("tokens")
                .child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String deviceID = dataSnapshot.getValue(String.class);

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try  {
                                    // grab the URL and message
                                    String baseURL = "https://us-central1-grubmate-c4429.cloudfunctions.net/addMessage?text=";
                                    String payload = message;

                                    try {
                                        URL url = new URL(baseURL + payload + "&ID=" + deviceID);
                                        System.out.println("THE URL IS " + url.toString());

                                        String token = FirebaseInstanceId.getInstance().getToken();
                                        System.out.println("THE TOKEN IS " + token);

                                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                        urlConnection.setRequestMethod("GET");
                                        urlConnection.setConnectTimeout(5000);
                                        int responseCode = urlConnection.getResponseCode();

                                    } catch (MalformedURLException mURLe) {
                                        mURLe.printStackTrace();
                                    } catch (IOException IOe) {
                                        IOe.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        thread.start();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
