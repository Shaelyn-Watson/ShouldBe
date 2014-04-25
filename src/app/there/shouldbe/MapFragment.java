package app.there.shouldbe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends Fragment {
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		  ViewGroup rootView = (ViewGroup) inflater.inflate(
	                R.layout.activity_tap_location, container, false);

		  return rootView;	
	  }
}
