package com.amca.android.replace;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

public class PlaceSelectorMap extends FragmentActivity implements OnInfoWindowClickListener  {

	private GoogleMap map = null;
	private Float currentLat, currentLng;
	private String jsonValue;
	private List<Integer> listMarker = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_selector_map);
		Intent intent = getIntent();
		this.currentLat = intent.getFloatExtra("currentLat", 0);
		this.currentLng = intent.getFloatExtra("currentLng", 0);
		this.jsonValue = intent.getStringExtra("jsonValue");
		
		setUpMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.place_selector_map, menu);
		return true;
	}

	@Override
    public void onInfoWindowClick(Marker marker) {
		String id = marker.getId();
		String[] s = id.split("m");
		
		Intent intent = new Intent(PlaceSelectorMap.this, PlaceDetail.class);
    	intent.putExtra("placeId", listMarker.get(Integer.parseInt(s[1])).toString());
    	startActivity(intent);
	} 
	
	private void setUpMap() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (map == null) {
	    	map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
	                            .getMap();
	        // Check if we were successful in obtaining the map.
	        if (map != null) {
	            // The Map is verified. It is now safe to manipulate the map.
	        	map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.currentLat, this.currentLng), 14));
	    		map.animateCamera(CameraUpdateFactory.zoomIn());
	    		map.setOnInfoWindowClickListener(this);
	    		
	    		try{
					JSONArray jArray = new JSONArray(this.jsonValue);
					for(int i=0;i<jArray.length();i++){
						JSONObject json_data = jArray.getJSONObject(i);
						listMarker.add(json_data.getInt("placeId"));
						map.addMarker(new MarkerOptions()
							.position(new LatLng(json_data.getDouble("placeLat"), json_data.getDouble("placeLng")))
							.title(json_data.getString("placeName"))
							.snippet(json_data.getString("placeDesc")));
					}
				}catch(JSONException e){
					Toast.makeText(getApplicationContext(), "Error parsing data "+e.toString(), Toast.LENGTH_SHORT).show();
				}
	    		
	    		map.addMarker(new MarkerOptions()
					.position(new LatLng(this.currentLat, this.currentLng))
					.title("You are here")
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	        }
	    }
	}

}
