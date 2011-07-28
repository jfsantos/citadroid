package com.seaandsailor.citadroid;

import java.io.InputStream;

import com.seaandsailor.citadroid.R;
import com.seaandsailor.citadroid.Quote.Quotes;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CitaDroid extends Activity {
	/** Called when the activity is first created. */
	
	private static final String[] PROJECTION = new String[] {
        Quotes._ID, // 0
        Quotes.QUOTE, // 1
        Quotes.AUTHOR, // 2
	};
	private Button generate;
	private TextView text;
	private QuoteProvider quotes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		InputStream txt = getResources().openRawResource(R.raw.batema);
		quotes = new QuoteProvider();
		generate = (Button) this.findViewById(R.id.button);
		text = (TextView) this.findViewById(R.id.text);
		String[] quote = getQuote();
		text.setText(quote[0] + ": " + quote[1]);
		this.generate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String[] quote = getQuote();
				text.setText(quote[0] + ": " + quote[1]);
			}
		});
	}
	
	/**
	 * Retorna um array de strings onde o índice 0 indica o autor e o índice 1 a citação.
	 * @return
	 */
	private String[] getQuote() {
		Cursor q = managedQuery(Uri.parse("quotes"), PROJECTION, "", null, "");
		String[] res = {q.getString(1), q.getString(2)};
		return res;
	}
	
}