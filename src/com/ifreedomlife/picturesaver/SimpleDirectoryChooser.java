package com.ifreedomlife.picturesaver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.TextView.BufferType;

public class SimpleDirectoryChooser implements DialogInterface.OnClickListener {

	public interface OnResultListener {
		void onResult(SimpleDirectoryChooser chooser, String result);
	}
	
	private Context context;
	private EditText editText;
	private OnResultListener listener;
	private String title = "";
	private String positiveButtonLabel = "";
	private String negativeButtonLabel = "";
	
	public SimpleDirectoryChooser(Context context, String dir) {
		this.context = context;
		editText = new EditText(context);
		editText.setText(dir, BufferType.EDITABLE);
	}
	
	public void setOnResultListener(OnResultListener listener) {
		this.listener = listener;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setButtonLabel(String positive, String negative) {
		this.positiveButtonLabel = positive;
		this.negativeButtonLabel = negative;
	}
	
	public void show() {
		new AlertDialog.Builder(context)
	    .setTitle(title)
	    .setCancelable(false)
	    .setPositiveButton(positiveButtonLabel, this)
		.setNegativeButton(negativeButtonLabel, this)
		.setView(editText).create().show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.cancel();
		
		if (which == DialogInterface.BUTTON_POSITIVE)
			listener.onResult(this, editText.getText().toString());
		
		if (which == DialogInterface.BUTTON_NEGATIVE)
			listener.onResult(this, null);
	}
}
