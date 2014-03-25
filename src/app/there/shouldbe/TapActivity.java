package app.there.shouldbe;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class TapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_location);
    }


    public void startMap(View v) {

        // lat and long for Gates Computer Science Complex:
        double latitude = 30.286336;
        double longitude = -97.736693;


        String oldAddress = "geo:0,0?q=713+n+duchesne+st.+charles+mo";
        String gatesAddress = "geo:0,0?q=2317+speedway+austin+tx";
        
         // default zoom, no label
//	         String uri = String.format(Locale.ENGLISH,"geo:%f,%f",latitude,longitude);
//	         Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(oldAddress));
//	         startActivity(intent);



        // with label and given zoom
        String label = "Bill and Melinda Gates Computer Science Complex";
        String uriBegin = "geo:" + latitude + "," + longitude;
        String query = latitude + "," + longitude + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=18";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        this.startActivity(intent);
    }

	
}