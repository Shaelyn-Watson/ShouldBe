package app.there.shouldbe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.parse.Parse;
import com.parse.ParseObject;

public class TapActivity extends MapActivity implements 
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener {
	
	private GoogleMap mMap;
	private static final LatLng GDC = new LatLng(30.286336,-97.736693);  //Yay UT
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	private LatLng mCurrentLatLng;
	
	private boolean posted;
	
	/* 
	 * TODO: replace hashmaps with database information
	 */
	//Marker mapped to number of likes
	private HashMap<Marker, Integer> pins = new HashMap<Marker, Integer>();
	private HashMap<Marker, TextView> likeCounts = new HashMap<Marker, TextView>();
	//Marker mapped to positions
	private HashMap<Marker, LatLng> markerPositions = new HashMap<Marker, LatLng>();
	//Marker mapped to infoWindow view instances
	private HashMap<Marker, ViewGroup> markers2Windows = new HashMap<Marker, ViewGroup>();
	private HashMap<ViewGroup, Marker> windows2Markers = new HashMap<ViewGroup, Marker>();
	
	//marker to statuses
	private HashMap<Marker, String> markers2Statuses = new HashMap<Marker, String>();
	
	//super temp struct to keep our most recently used marker
	private ArrayList<Marker> markerArray = new ArrayList<Marker>();
	
	//info window global elements
	private ViewGroup infoWindow;
    private Button likeButton;      //like the ShouldBe *TODO facebook
    private OnInfoWindowElemTouchListener likeButtonListener; 
    private Button whatShouldBe;
    
    private EditText mapSearchBox;
    private ImageButton searchButton;
    private String searchString;
    private ProgressDialog pDialog;
    private LatLng mostRecentMarkerPosition;
    
    private OnInfoWindowElemTouchListener infoButtonListener;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_location);
        
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        //Log.d("**TaponCreate", "new tap activity");
        posted = false;
        
        // Setup Google Map
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
        mLocationClient = new LocationClient(this, this, this);
        setUpMapIfNeeded();  // Check to make sure map loads
        mMap.getUiSettings().setZoomControlsEnabled(false);   // Remove +/- zoom controls since pinching is enabled
        
        //pin info window
        final ViewGroup emptyInfoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.map_info_window_empty, null);
        infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.map_info_window, null);
        this.whatShouldBe = (Button)emptyInfoWindow.findViewById(R.id.shouldBeButton);
        likeButton = (Button)infoWindow.findViewById(R.id.button);
    	likeButtonListener = new OnInfoWindowElemTouchListener(likeButton,
                getResources().getDrawable(R.drawable.like1),
                getResources().getDrawable(R.drawable.like2)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // *** TODO register click as a "like" counting towards the ShouldBe
            	Log.d("likebutton", "clicked!");
            	int pastLikes = (Integer) pins.get(marker);
            	pins.put(marker, pastLikes+1);
            	Log.d("likebutton", String.valueOf(pins.get(marker)));
            	marker.showInfoWindow();
                //Toast.makeText(TapActivity.this, marker.getTitle() + "'s button clicked! " + pins.get(marker), Toast.LENGTH_SHORT).show();
            }
        }; 
        
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) { 
            	// hide virtual keyboard
                //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(mapSearchBox.getWindowToken(), 0);
                
                Marker marker = null;
                marker = mMap.addMarker(new MarkerOptions().position(point)
                	.icon(BitmapDescriptorFactory.fromResource(R.drawable.shouldbepin))
                	.title("There should be:")  //not used
                	);
                //new marker is presented with simple add ShouldBe window
                markers2Windows.put(marker, emptyInfoWindow);
                // Move camera to position of new marker
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15)); 
                pins.put(marker, 0);  //Init like count
                markerArray.add(marker);
                marker.showInfoWindow(); 
            }

        });
        
        // Setup map search bar  (hidden for now)
//        mapSearchBox = (EditText) findViewById(R.id.mapSearchBox);
//        mapSearchBox.addTextChangedListener(new EditTextChanged());
//        searchButton = (ImageButton) findViewById(R.id.searchButton);
//        searchButton.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if (!searchString.isEmpty()) {
//					new SearchClicked(mapSearchBox.getText().toString()).execute();
//				}
//				return false;
//			}
//		});
        
        
        /* 
         * Setup pin infowindow
         */
        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge 
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, 39 + 20)); 

        
        whatShouldBe.setOnTouchListener(new OnInfoWindowElemTouchListener(whatShouldBe) {
			@Override
			protected void onClickConfirmed(View v, Marker marker) {
				Intent intent = new Intent(TapActivity.this, WhatShouldBeActivity.class);
				startActivityForResult(intent, 1);
			}
		});

        mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            private void giveInfoWindowTweet(Marker marker) {
            	
            	TextView postedTweet = (TextView)infoWindow.findViewById(R.id.posted_tweet);
            	postedTweet.setText(String.valueOf(markers2Statuses.get(marker)));
            	TextView likeCount = (TextView)infoWindow.findViewById(R.id.like_count);
            	likeCount.setText(String.valueOf(pins.get(marker)));
            	// Setting custom OnTouchListener which deals with the pressed state 
            	
                likeButtonListener.setMarker(marker);
                likeButton.setOnTouchListener(likeButtonListener);
                
            	markers2Windows.put(marker, infoWindow);
			}

			@Override
            public View getInfoContents(Marker marker) {
                // We must call this to set the current marker and infoWindow references
				//markers2Statuses.put(marker, "test tweet");
            	if(markers2Statuses.get(marker) != null){
            		giveInfoWindowTweet(marker);
	            	
            	}
            	Log.d("getInfoContents", "getInfoContents");
                mapWrapperLayout.setMarkerWithInfoWindow(marker, markers2Windows.get(marker));
                return markers2Windows.get(marker);
            }
        });  
        
        // ========================
        // ======== Parse =========
        // ========================
        Parse.initialize(this, "3wJJsTSZovcJZreXDjYVeJi3e1AOqAZEA8e2S860", "y1SZ9RtY8wuv9sOaXTIHrapLK5uk6LrehEEYylZd");

    }
    
    public void shouldBeUpdate (String status) {
    	posted = true;
		if (markerArray.get(markerArray.size()-1) != null){
			//Make new marker and display updated infowindow
            Marker marker = markerArray.get(markerArray.size()-1);
            
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15)); 
            
            markers2Statuses.put(markerArray.get(markerArray.size()-1), status);
            marker.showInfoWindow();
            zoomToLatLngLocation(marker.getPosition());
            Log.d("**sbUpdate", "showInfoWindow called");
		}
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
                    break;
                    default:
                    	break;
                }
            case 1:
            	switch (resultCode) {
            	case Activity.RESULT_OK:
            		if (!data.getStringExtra("status").isEmpty()) {
                    	String shouldBeText = data.getStringExtra("status");
                    	Log.d("**onActivityResult", "**status text = " + shouldBeText);
                    	shouldBeUpdate(shouldBeText);
                	}
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
        
        if (!posted){
	        mCurrentLocation = mLocationClient.getLastLocation();
	        mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
	        zoomToUserLocation();
        }

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
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TapActivity.this);
            pDialog.setMessage("Searching...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
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
					
					result = true;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(Boolean b) {
			if (b) {
				LatLng l = new LatLng((int) (address.getLatitude() * 1E6), (int) (address.getLongitude() * 1E6));
				zoomToLatLngLocation(l);
			}
			pDialog.dismiss();
		}
	}
	
	private class EditTextChanged implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// do nothing
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// do nothing
		}

		@Override
		public void afterTextChanged(Editable s) {
			// save string
			searchString = s.toString();
			//Log.d("TapActivity.EditTextChanged.afterTextChanged", "s.toString() = " + s.toString());
		}
		
	}
	
}