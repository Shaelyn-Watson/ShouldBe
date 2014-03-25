package app.there.shouldbe;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.SupportMapFragment;

public class TapActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	//Log.d("play_services", "is google play services available = " +  GooglePlayServicesUtil.isGooglePlayServicesAvailable(null));
        super.onCreate(savedInstanceState);
        //GoogleMap mMap = ((SupportMapFragment) getSupportFragmentManager()
                //.findFragmentById(R.id.map)).getMap();
        setContentView(R.layout.activity_tap_location);
    }
}
