package com.ifreedomlife.picturesaver;

import java.io.File;
import java.net.URI;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;

public class MainActivity extends PreferenceActivity {

	int PICK_REQUEST_CODE = 0;
	final String TAG = "PictureSaver";

	static public final String PREFS_NAME = "com.ifreedomlife.picturesaver";
	
	Preference download_dir_pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		setupPreferences();
	}
	
	private void setupPreferences() {
		Preference pref;
		
		// about preference
		pref = findPreference("pref_about");
		pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showAbout();
				return true;
			}
		});
		
		// directory preference
		pref = findPreference("pref_download_dir");
		pref.setSummary(getDownloadDirectory());
		pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary((String)newValue);
				return true;
			}
		});
		pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				pickDirectory();
				return true;
			}
		});
		download_dir_pref = pref;
	}
	
	private void showFallbackDirectoryPicker() {
		SimpleDirectoryChooser chooser = new SimpleDirectoryChooser(this, getDownloadDirectory());
		
		chooser.setTitle(getString(R.string.fallback_dir_chooser_dialog_title));
		chooser.setButtonLabel(getString(R.string.yes), getString(R.string.no));

		chooser.setOnResultListener(new SimpleDirectoryChooser.OnResultListener() {
			@Override
			public void onResult(SimpleDirectoryChooser chooser, String result) {
				if (result != null) {
					notifyDownloadDirectoryChanged(result);
				}
			}
		});

		chooser.show();
	}
	
	private boolean showEstrongsDirectoryPicker() {
		Intent intent = new Intent();
		intent.setAction("com.estrongs.action.PICK_DIRECTORY");
		intent.putExtra("com.estrongs.intent.extra.TITLE", "Select Directory");
		
		try {
			startActivityForResult(intent, PICK_REQUEST_CODE);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}
	
	private void pickDirectory () {
		boolean ret = false;
		
		ret = ret || showEstrongsDirectoryPicker();
		// other directory picker...
		
		// fallback to simple directory input.
		if (!ret) showFallbackDirectoryPicker();
	}

	private String getDownloadDirectory() {
		SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
		String dir = prefs.getString("pref_download_dir", null);
		
		if (dir != null) return dir;
		
		return Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES).getPath();
	}
	
	private void setDownloadDirectory(String path) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("pref_download_dir", path);
        editor.commit();
	}
	
	private void notifyDownloadDirectoryChanged(String path) {
        Preference pref = download_dir_pref;
        Preference.OnPreferenceChangeListener listener = pref.getOnPreferenceChangeListener();
        if (listener != null) listener.onPreferenceChange(pref, path);

        setDownloadDirectory(path);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
	   if (requestCode == PICK_REQUEST_CODE)
	   {
		   if (resultCode == RESULT_OK)
		   {
		      Uri uri = intent.getData();
		      if (uri != null)
		      {
		         String path = uri.toString();
		         if (path.startsWith("file://"))
		         {
		            path = (new File(URI.create(path))).getAbsolutePath();
		            
		            Log.i(TAG, "AbsolutePath " + path);

		            notifyDownloadDirectoryChanged(path);
		         }
	
		      }
		   }
		   else Log.i(TAG, "Back from pick with cancel status");
	   }
	}
	
	private String getVersion() {
		try {
			return "v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
		
	}
	
	private void showAbout() {
		WebView web = new WebView(this);
		web.loadUrl("file:///android_asset/pages/about.html");
		web.setWebViewClient(new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		        return true;
		    }
		});
		
		
		new AlertDialog.Builder(this)
		    .setTitle(getString(R.string.app_name) + " " + getVersion())
		    .setCancelable(false)
			.setNegativeButton(getString(R.string.ok_iknow), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}).setView(web).create().show();		
	}
}
