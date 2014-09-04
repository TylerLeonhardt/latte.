package com.wearbucks.app;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class BarcodeAsyncTask extends AsyncTask<Void, Void, Void>{

	private int barcode;
	private Bitmap barcodeImage;
	Context main;
	
	public BarcodeAsyncTask(int b, Context context) {
		// TODO Auto-generated constructor stub
		barcode = b;
		main = context;
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		InputStream inputStream = null;
		Bitmap result = null;
		
		String url = "http://www.voindo.eu/UltimateBarcodeGenerator/barcode/barcode.processor.php?encode=QRCODE&qrdata_type=text&qr_btext_text=" + barcode + "&qr_link_link=&qr_sms_phone=&qr_sms_msg=&qr_phone_phone=&qr_vc_N=&qr_vc_C=&qr_vc_J=&qr_vc_W=&qr_vc_H=&qr_vc_AA=&qr_vc_ACI=&qr_vc_AP=&qr_vc_ACO=&qr_vc_E=&qr_vc_U=&qr_mec_N=&qr_mec_P=&qr_mec_E=&qr_mec_U=&qr_email_add=&qr_email_sub=&qr_email_msg=&qr_wifi_ssid=&qr_wifi_type=wep&qr_wifi_pass=&qr_geo_lat=&qr_geo_lon=&bdata_matrix=123&bdata_pdf=123&bdata=123&height=500&scale=1&bgcolor=%23ffffff&color=%23000000&file=&folder=";
		
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if(inputStream != null)
				result = BitmapFactory.decodeStream(inputStream);
			else
				result = null;

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		
		barcodeImage = result;
		
		return null;
	}
	
	@Override
    protected void onPostExecute(Void aVoid) {
		MainActivity.sendNotification(barcode, barcodeImage);
	}
	

}
