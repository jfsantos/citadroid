package com.seaandsailor.citadroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

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
	private ArrayList<String> quotes;
	private Random rnd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		quotes = new ArrayList<String>();
		rnd = new Random(System.currentTimeMillis());
		InputStream txt = getResources().openRawResource(R.raw.batema);
		try {
			BufferedReader input = new BufferedReader(
					new InputStreamReader(txt));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					quotes.add(line);
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		generate = (Button) this.findViewById(R.id.button);
		text = (TextView) this.findViewById(R.id.text);
		this.generate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				text.setText(quotes.get(rnd.nextInt(quotes.size())));
			}
		});
	}
}