package app.there.shouldbe;



import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class IconizedWindowAdapter implements InfoWindowAdapter {
  LayoutInflater inflater=null;

  public IconizedWindowAdapter(LayoutInflater inflater) {
    this.inflater=inflater;
  }

  @Override
  public View getInfoWindow(Marker marker) {
    return(null);
  }

  @Override
  public View getInfoContents(Marker marker) {
    View popup=inflater.inflate(R.layout.map_info_window, null);
    TextView tv=(TextView)popup.findViewById(R.id.title);
    tv.setText(marker.getTitle());
    
    return(popup);
  }
} 