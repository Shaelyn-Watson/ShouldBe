package app.there.shouldbe;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.MapActivity;

public class TapActivity extends MapActivity {
	
	private GoogleMap mMap;
	private static final LatLng GDC = new LatLng(30.286336,-97.736693);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_location);
        
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        setUpMapIfNeeded();
        manipulateMap();
        
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                //lstLatLngs.add(point);
                mMap.addMarker(new MarkerOptions().position(point)
                	.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
            }
        });

    }
    
    
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                                .getMap();
        }
    }


    private void manipulateMap() {
    	// Move the camera instantly to campus with a zoom of 15.
    	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GDC, 15));

	}


	@Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    

	
}