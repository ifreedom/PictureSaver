package com.ifreedomlife.picturesaver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Locale;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SaverActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get intent, action and MIME type
	    Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();

	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if (type.startsWith("image/")) {
	            handleSendImage(intent);
	        }
	    }
	    
		finish();
	}
	
	void showLongToast(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
	}
	
	void showLongToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	Boolean CheckWriteable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) return true;
		return false;
	}
	
	void actionComplete(String filePath) {
		if (filePath == null) showLongToast(R.string.storgeError);
		else showLongToast(getString(R.string.saveComplete, filePath));
	}
	
	void handleSendImage(Intent intent) {
	    Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
	    if (imageUri != null) {
	    	new SavePictureTask(this, imageUri).execute();
	    }
	}

	private class SavePictureTask extends AsyncTask<Void, Void, String> {

		SaverActivity parent;
		Uri imageUri;
		
		public SavePictureTask(SaverActivity parent, Uri uri) {
			this.parent = parent;
			this.imageUri = uri;
		}
		
		@Override
		protected String doInBackground(Void... params) {
			return parent.savePicture(imageUri);
		}
		
		protected void onPostExecute(String result) {
			parent.actionComplete(result);
		}
	}
	
	static String detectImageExtension(byte[] bytes) {
		if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') return "gif";
		return "jpg";
	}

	static String formatFilename(String ext) {
		Calendar c = Calendar.getInstance();
	    return String.format(Locale.US, "%d-%02d-%02d-%08d.%s", 
	    		c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 
	    		c.getTimeInMillis() % 100000000, ext);
	}
	
	String savePicture(Uri imageUri) {
		File path = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);

		try {
	        // Make sure the Pictures directory exists.
	        path.mkdirs();

		    ContentResolver cr = getBaseContext().getContentResolver();
	        InputStream is = cr.openInputStream(imageUri);
	        
	        byte[] data = new byte[is.available()];
	        is.read(data);
	        
		    File file = new File(path, formatFilename(detectImageExtension(data)));
	        OutputStream os = new FileOutputStream(file);
	        os.write(data);
	        
	        is.close();
	        os.close();
	        
	        return file.getPath();
	        
	    } catch (IOException e) {
	        // Unable to create file, likely because external storage is
	        // not currently mounted.
	        Log.w("ExternalStorage", "Error writing " + path, e);
	        return null;
	    }	    
	}
}
