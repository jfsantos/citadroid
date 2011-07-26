package com.seaandsailor.citadroid;

import java.io.InputStream;
import com.seaandsailor.citadroid.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CitaDroid extends Activity {
	/** Called when the activity is first created. */
	private Button generate;
	private TextView text;
	private QuoteDb quotes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		InputStream txt = getResources().openRawResource(R.raw.batema);
		quotes = new QuoteDb(txt);
		generate = (Button) this.findViewById(R.id.button);
		text = (TextView) this.findViewById(R.id.text);
		text.setText(quotes.getQuote());
		this.generate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				text.setText(quotes.getQuote());
			}
		});
	}
}