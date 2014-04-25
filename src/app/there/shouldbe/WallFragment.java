package app.there.shouldbe;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WallFragment extends ListFragment {
	
	String[] numbers_text = new String[] { "one", "two", "three", "four",  
		    "five", "six", "seven", "eight", "nine", "ten", "eleven",  
		    "twelve", "thirteen", "fourteen", "fifteen" };  
		  String[] numbers_digits = new String[] { "1", "2", "3", "4", "5", "6", "7",  
		    "8", "9", "10", "11", "12", "13", "14", "15" };  
		  
		  @Override  
		  public void onListItemClick(ListView l, View v, int position, long id) {  
		   //new CustomToast(getActivity(), numbers_digits[(int) id]);     
		  }  
		  
		  @Override  
		  public View onCreateView(LayoutInflater inflater, ViewGroup container,  
		    Bundle savedInstanceState) {  
		   ArrayAdapter<String> adapter = new ArrayAdapter<String>(  
		     inflater.getContext(), android.R.layout.simple_list_item_1,  
		     numbers_text);  
		   setListAdapter(adapter);  
		   return super.onCreateView(inflater, container, savedInstanceState);  
		  }  
		   
	
//	  @Override
//	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//	    View view = inflater.inflate(R.layout.activity_twitter_feed, null);
//	    return view;
//	  }
}
