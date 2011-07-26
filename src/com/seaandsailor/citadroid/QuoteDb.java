package com.seaandsailor.citadroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class QuoteDb {
	private ArrayList<String> quotes;
	private Random rnd;

	public QuoteDb(InputStream txt) {
		quotes = new ArrayList<String>();
		rnd = new Random(System.currentTimeMillis());
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
	}
	
	public String getQuote() {
		return quotes.get(rnd.nextInt(quotes.size()));
	}
	
}
