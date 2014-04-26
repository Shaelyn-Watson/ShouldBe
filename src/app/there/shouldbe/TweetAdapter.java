package app.there.shouldbe;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class TweetAdapter<T> extends BaseAdapter {

	private Activity activity;
	private ArrayList<T> data;
	private static LayoutInflater inflater = null;
	Tweet tempValues = null;
	int i = 0;

	public TweetAdapter(Activity a, ArrayList<T> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/******** What is the size of Passed Arraylist Size ************/
	@Override
	public int getCount() {

		if(data.size() <= 0)
			return 1;
		return data.size();
	}

	@Override
	public T getItem(int position) {
		return data.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	/********* Create a holder Class to contain inflated xml file elements *********/
	public static class ViewHolder {

		public TextView date;
		public TextView message;
		public TextView name;
		public Button likeButton;
		public TextView likeCount;

	}

	/****** Depends upon data size called for each row , Create each ListView row *****/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;

		if(convertView == null) {

			/****** Inflate file for each row *******/
			vi = inflater.inflate(R.layout.wall_list_item, null);

			holder = new ViewHolder();
			holder.date = (TextView) vi.findViewById(R.id.tweet_date);
			holder.message = (TextView) vi.findViewById(R.id.tweet_message);
			holder.name = (TextView) vi.findViewById(R.id.tweet_name);
			holder.likeCount = (TextView) vi.findViewById(R.id.wall_like_count);

			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		if(data.size() <= 0) {
			holder.date.setText("No Data");

		} else {
			/***** Get each Model object from Arraylist ********/
			tempValues = (Tweet) data.get(position);

			/************ Set Model values in Holder elements ***********/

			holder.date.setText(tempValues.getDate());
			holder.message.setText(tempValues.getMessage());
			holder.name.setText("@" + tempValues.getName());
			holder.likeCount.setText("like count");
		}
		return vi;
	}
}