package com.amca.android.replace.review;

import java.text.DecimalFormat;
import java.util.List;
import com.amca.android.replace.R;
import com.amca.android.replace.model.Review;
import com.amca.android.replace.model.User;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceReviewsArrayAdapter extends ArrayAdapter<Review> {
	private final Context context;
	private final List<Review> values;

	public PlaceReviewsArrayAdapter(Context context, List<Review> values) {
		super(context, R.layout.activity_place_reviews_row, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.activity_place_reviews_row,
				parent, false);
		TextView reviewUser = (TextView) rowView.findViewById(R.id.reviewUser);
		TextView reviewPoint = (TextView) rowView.findViewById(R.id.reviewPoint);
		TextView reviewPointPrice = (TextView) rowView.findViewById(R.id.textViewPointPrice);
		TextView reviewPointService = (TextView) rowView.findViewById(R.id.textViewPointService);
		TextView reviewPointLocation = (TextView) rowView.findViewById(R.id.textViewPointLocation);
		TextView reviewPointCondition = (TextView) rowView.findViewById(R.id.textViewPointCondition);
		TextView reviewPointComfort = (TextView) rowView.findViewById(R.id.textViewPointComfort);
		TextView similarity = (TextView) rowView.findViewById(R.id.similarity);
		TextView reviewText = (TextView) rowView.findViewById(R.id.reviewText);
		ImageView userIcon = (ImageView) rowView.findViewById(R.id.userIcon);
		
		DecimalFormat df = new DecimalFormat("#0.##");

		User user = new User();
		Review review = new Review();
		review = values.get(position);
		user = review.getReviewUser();

		String simText = null;
		switch(review.getSimilarityFlag()){
			case 0:
				simText = "not\nsimilar"; break;
			case 1:
				simText = "less\nsimilar"; break;
			case 2:
				simText = "quite\nsimilar"; break;
			case 3:
				simText = "similar"; break;
			case 4:
				simText = "very\nsimilar"; break;
		}
		
		reviewUser.setText(user.getUserAlias());
		reviewPointPrice.setText(reviewPointPrice.getText() + " : " + review.getReviewPointPrice() + "/10");
		reviewPointService.setText(reviewPointService.getText() + " : " + review.getReviewPointService() + "/10");
		reviewPointLocation.setText(reviewPointLocation.getText() + " : " + review.getReviewPointLocation() + "/10");
		reviewPointCondition.setText(reviewPointCondition.getText() + " : " + review.getReviewPointCondition() + "/10");
		reviewPointComfort.setText(reviewPointComfort.getText() + " : " + review.getReviewPointComfort() + "/10");
		similarity.setText(simText);
		reviewPoint.setText(df.format(review.getAveragePoint()) + "/10");
		reviewText.setText(review.getReviewText());

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		String serverUrl = preferences.getString("serverUrl", "");
		
		UrlImageViewHelper.setUrlDrawable(userIcon, serverUrl + "static/user/" + user.getUserId() + ".jpg", R.drawable.gif_loader);
		
		return rowView;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
	public void sort(int key) {
	    super.sort(new PlaceReviewsComparator(key));
	}
}
