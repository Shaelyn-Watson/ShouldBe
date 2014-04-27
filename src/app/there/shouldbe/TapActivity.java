package app.there.shouldbe;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
	
	/* 
	 * TODO: replace hashmaps with database information
	 */
	//Marker mapped to number of likes
	private HashMap<Marker, Integer> pins = new HashMap<Marker, Integer>();
	private HashMap<Marker, TextView> likeCounts = new HashMap<Marker, TextView>();
	//Marker mapped to positions
	private HashMap<Marker, LatLng> markerPositions = new HashMap<Marker, LatLng>();
	//Marker mapped to infoWindow view instances
	private HashMap<Marker, ViewGroup> markerWindows = new HashMap<Marker, ViewGroup>();
	
	//info window global elements
    private Button likeButton;      //like the ShouldBe *TODO facebook
    private OnInfoWindowElemTouchListener likeButtonListener; 
    private Button whatShouldBe;
    
    private EditText mapSearchBox;
    
    //private DBConnection dbConn;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_location);
        
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        /* 
         * Setup Google Map
         */
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
        mLocationClient = new LocationClient(this, this, this);
        setUpMapIfNeeded();  // Check to make sure map loads
        mMap.getUiSettings().setZoomControlsEnabled(false);   // Remove +/- zoom controls since pinching is enabled
        
        //pin info window
        final ViewGroup emptyInfoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.map_info_window_empty, null);
        //this.likeButton = (Button)infoWindow.findViewById(R.id.button);
        this.whatShouldBe = (Button)emptyInfoWindow.findViewById(R.id.shouldBeButton);
        
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) { 
            	// hide virtual keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mapSearchBox.getWindowToken(), 0);
                
                Marker marker = null;
                marker = mMap.addMarker(new MarkerOptions().position(point)
                	.icon(BitmapDescriptorFactory.fromResource(R.drawable.shouldbepin))
                	.title("There should be:")  //not used
                	);
                //new marker is presented with simple add ShouldBe window
                markerWindows.put(marker, emptyInfoWindow);
                // Move camera to position of new marker
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15)); 
                pins.put(marker, 0);  //Init like count
                
                marker.showInfoWindow(); 
                }

        });
        
        /* 
         * Setup map search bar
         */
        mapSearchBox = (EditText) findViewById(R.id.mapSearchBox);
        //mapSearchBox.setImeActionLabel("Custom text", KeyEvent.KEYCODE_SEARCH);
        //mapSearchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mapSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	             if (actionId == EditorInfo.IME_ACTION_SEARCH ||
	                    //actionId == EditorInfo.IME_ACTION_DONE ||
	                    //actionId == EditorInfo.IME_ACTION_GO ||
	                    event.getAction() == KeyEvent.ACTION_DOWN &&
	                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

	                // hide virtual keyboard
	                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	                imm.hideSoftInputFromWindow(mapSearchBox.getWindowToken(), 0);

	                new SearchClicked(mapSearchBox.getText().toString()).execute();
	                mapSearchBox.setText("", TextView.BufferType.EDITABLE);
	                return true;
	            }
	            return false;
	        }
		});
        
        
        /* 
         * Setup pin infowindow
         */
        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge 
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, 39 + 20)); 
        
//        // Setting custom OnTouchListener which deals with the pressed state 
//        this.infoButtonListener = new OnInfoWindowElemTouchListener(likeButton,
//                getResources().getDrawable(R.drawable.like1),
//                getResources().getDrawable(R.drawable.like2)) {
//            @Override
//            protected void onClickConfirmed(View v, Marker marker) {
//                // *** TODO register click as a "like" counting towards the ShouldBe
//            	int pastLikes = (Integer) pins.get(marker);
//            	pins.put(marker, pastLikes+1);
//            	//if ( marker exists)
////            	TextView likeCount = likeCounts.get(marker);
////            	likeCount.setText(String.valueOf(pins.get(marker)));
//            	marker.showInfoWindow();
//           }
//        }; 
//        this.likeButton.setOnTouchListener(infoButtonListener);
        
        whatShouldBe.setOnTouchListener(new OnInfoWindowElemTouchListener(whatShouldBe) {
			
			@Override
			protected void onClickConfirmed(View v, Marker marker) {
				Intent intent = new Intent(TapActivity.this, WhatShouldBeActivity.class);
				startActivity(intent);
			}
		});

        mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                //likeButtonListener.setMarker(marker);
//                TextView likeCount = likeCounts.get(marker);
//            	likeCount.setText(String.valueOf(pins.get(marker)));

                // We must call this to set the current marker and infoWindow references
                mapWrapperLayout.setMarkerWithInfoWindow(marker, markerWindows.get(marker));
                return markerWindows.get(marker);
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
    
    /**
     * Method to check if the map is null, if so, setup the map
     */
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        }
    }
    
    /**
     * Converts dp to px
     * @param context	this context
     * @param dp	dp to convert
     * @return	returns dp in pixels
     */
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
    	super.onStop();
    	if (mLocationClient != null)
    		mLocationClient.disconnect();
    }

    private void zoomToUserLocation() {
    	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 15));
    }
    
    private void zoomToLatLngLocation(LatLng point) {
    	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
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
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    // Try request again TODO
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
		Toast.makeText(this, "Connection Error!", Toast.LENGTH_LONG).show();
		
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
		
	}
	
	private class SearchClicked extends AsyncTask<Void, Void, Boolean> {
		private String toSearch;
		private Address address;
		
		public SearchClicked(String toSearch) {
			this.toSearch = toSearch;
		}
		
		@Override
		protected Boolean doInBackground(Void... voids) {
			Boolean result = false;
			try {
				// Locale.US to only search addresses in US
				Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.US); 
				List<Address> results = geocoder.getFromLocationName(toSearch, 1);
				
				if (results.size() > 0) {
					address = results.get(0);
					LatLng l = new LatLng((int) (address.getLatitude() * 1E6), (int) (address.getLongitude() * 1E6));
					zoomToLatLngLocation(l);
					result = true;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
	}
	
}