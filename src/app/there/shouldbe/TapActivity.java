package app.there.shouldbe;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.MapActivity;

public class TapActivity extends MapActivity implements 
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener {
	
	private GoogleMap mMap;
	private static final LatLng GDC = new LatLng(30.286336,-97.736693);  //Yay UT
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	private LatLng mCurrentLatLng;
	
	//Marker mapped to number of likes
	private HashMap<Marker, Integer> pins = new HashMap<Marker, Integer>();
	private HashMap<Marker, TextView> likeCounts = new HashMap<Marker, TextView>();
	//Marker mapped to positions
	private HashMap<Marker, LatLng> markerPositions = new HashMap<Marker, LatLng>();
	
	//info window global elements
	private ViewGroup infoWindow;
    private TextView thereShouldBe;    //"There should be:"
    //private TextView shouldBeText;   //Twitter display/input
    private Button likeButton;      //like the ShouldBe *TODO facebook
    //private TextView likeCount;     //display current number of likes
    private OnInfoWindowElemTouchListener infoButtonListener;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_location);
        
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        /* 
         * Load google map 
         * */
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
        mLocationClient = new LocationClient(this, this, this);
        setUpMapIfNeeded(); //ensure map loads
        
        /*
         *Click on map = show existing pin's info window or create new pin waiting for input 
         * */
        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.map_info_window, null);
        this.thereShouldBe = (TextView)infoWindow.findViewById(R.id.there_should_be);
        this.likeButton = (Button)infoWindow.findViewById(R.id.button);
        
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) { 
                Marker marker = null;
                marker = mMap.addMarker(new MarkerOptions().position(point) //**point has LatLng info ALEXIS
                	.icon(BitmapDescriptorFactory.fromResource(R.drawable.shouldbepin))
                	.title("There should be:")
                	);
                
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15)); //position of new marker
                TextView likeCount = (TextView)infoWindow.findViewById(R.id.like_count);
                
                pins.put(marker, 0);   //put in hashmap 
                likeCount.setText(String.valueOf(pins.get(marker)));
                likeCounts.put(marker, likeCount);
                marker.showInfoWindow(); //**need this line?
                Toast.makeText(TapActivity.this, marker.getTitle() + " is created " + pins.get(marker), Toast.LENGTH_SHORT).show();
            }
        });
        
        /*
         * Click on a marker's infowindow 
         * */
        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge 
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, 39 + 20)); 


        // Setting custom OnTouchListener which deals with the pressed state 
        this.infoButtonListener = new OnInfoWindowElemTouchListener(likeButton,
                getResources().getDrawable(R.drawable.like1),
                getResources().getDrawable(R.drawable.like2)) 
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // *** TODO register click as a "like" counting towards the ShouldBe
            	int pastLikes = (Integer) pins.get(marker);
            	pins.put(marker, pastLikes+1);
            	TextView likeCount = likeCounts.get(marker);
            	likeCount.setText(String.valueOf(pins.get(marker)));
            	marker.showInfoWindow();
                Toast.makeText(TapActivity.this, marker.getTitle() + "'s button clicked! " + pins.get(marker), Toast.LENGTH_SHORT).show();
            }
        }; 
        this.likeButton.setOnTouchListener(infoButtonListener);

        mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                thereShouldBe.setText(marker.getTitle());
                infoButtonListener.setMarker(marker);
                TextView likeCount = likeCounts.get(marker);
            	likeCount.setText(String.valueOf(pins.get(marker)));

                // We must call this to set the current marker and infoWindow references
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });  

    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        }
    }
    
    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    @Override
    protected void onStart() {
    	super.onStart();
    	mLocationClient.connect();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mLocationClient.connect();
    }
    
    
    @Override 
    protected void onStop() {
    	if (mLocationClient != null)
    		mLocationClient.disconnect();
    	super.onStop();
    }

    private void zoomToUserLocation() {
    	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 15));
    }

	@Override
    protected boolean isRouteDisplayed() {
        return false;
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                    break;
                    default:
                    	break;
                }
        }
     }
	
	@Override
	public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        
        mCurrentLocation = mLocationClient.getLastLocation();
        mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        zoomToUserLocation();
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
		
	}
	
	
}