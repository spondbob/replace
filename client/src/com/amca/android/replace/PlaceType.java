package com.amca.android.replace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PlaceType extends ListActivity {
	private String userId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_type);
		
		HTTPPlaceType http = new HTTPPlaceType(this);
		http.execute("type/listAll");
		
		Intent intent = getIntent();
		this.userId = intent.getStringExtra("userId");
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		String[] s = item.split(" - ");
		if(Integer.parseInt(s[2]) > 0){
			Toast.makeText(this, s[1] + " selected, go ahead", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(PlaceType.this, PlaceSelector.class);
        	intent.putExtra("userId", this.userId);
        	intent.putExtra("typeName", s[1]);
        	startActivity(intent);
		}else{
			Toast.makeText(this, "no data available for " + s[1], Toast.LENGTH_SHORT).show();
		}
	}
	
	class HTTPPlaceType extends AsyncTask<String, String, String>{
		
		private Context mContext;
		private String serverUrl = "http://10.151.36.36/replace/server/";
		private ProgressDialog progressDialog;
		
		public HTTPPlaceType(Context context){
			this.mContext = context;
		}
		
		@Override
	    protected void onPreExecute() {
			progressDialog = new ProgressDialog(mContext);
		    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		    progressDialog.setMessage("Loading...");
		    progressDialog.setCancelable(false);
		    progressDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			this.serverUrl += params[0];
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(this.serverUrl);
				
				HttpResponse httpRespose = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpRespose.getEntity();
				InputStream in = httpEntity.getContent();
				BufferedReader read = new BufferedReader(new InputStreamReader(in));

				String isi= "";
				String baris= "";
				while((baris = read.readLine())!=null){
					isi+= baris;
				} 

				return isi;

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//parse json data
			try{
				List<String> list = new ArrayList<String>();
				JSONArray jArray = new JSONArray(result);
				for(int i=0;i<jArray.length();i++){
					JSONObject json_data = jArray.getJSONObject(i);
					list.add(json_data.getString("typeId") + " - " + json_data.getString("typeName") + " - " + json_data.getString("typeTotal"));
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
				setListAdapter(adapter);
			}catch(JSONException e){
				Toast.makeText(getApplicationContext(), "Error parsing data "+e.toString(), Toast.LENGTH_SHORT).show();
			}
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}