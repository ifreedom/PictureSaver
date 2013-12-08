package com.ifreedomlife.picturesaver;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		showAbout();
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
					finish();
				}
			}).setView(web).create().show();		
	}
}
