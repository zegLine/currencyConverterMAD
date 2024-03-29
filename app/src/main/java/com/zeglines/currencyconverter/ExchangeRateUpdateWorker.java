package com.zeglines.currencyconverter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ExchangeRateUpdateWorker extends Worker {

    public ExchangeRateUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("CurrencyConverterWORKER", "Started Work!");

        updateCurrencies();

        Log.i("CurrencyConverterWORKER", "Finished Work!");
        return Result.success();
    }

    private static final String ECB_API = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    public static void updateCurrencies() {

        try {

            // Create the URL from the API string
            URL url = new URL(ECB_API);

            // Initiate the connection
            Log.i("CurrencyConverterWORKER", "Initiate connection with ECB");
            URLConnection conn = url.openConnection();

            // Get the input stream
            Log.i("CurrencyConverterWORKER", "Getting input stream");
            InputStream inputStream = conn.getInputStream();

            // Get Encoding
            String encoding = conn.getContentEncoding();

            // Create pull-parser object using the XMLPullParserFactory
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();

            // Pass input stream and encoding to the parser
            parser.setInput(inputStream, encoding);

            //get first event type
            int eventType = parser.getEventType();

            // Move parser head until the end of the XML document
            Log.i("CurrencyConverterWORKER", "Parsing XML data...");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // If it found a starting tag inspect
                if (eventType == XmlPullParser.START_TAG) {
                    // If element is Cube and has two attributes, it is a data point
                    if ("Cube".equals(parser.getName()) && parser.getAttributeCount() == 2) {
                        // Get the currency name and value as the first and second attribute of the XML element
                        String currency = parser.getAttributeValue(0);
                        String newValue = parser.getAttributeValue(1);
                        Log.i("CurrencyConverterWORKER", "Found data point for currency " + currency + " with value + " + newValue);


                        // Set exchange rate using public method
                        ExchangeRateDatabase.setExchangeRate(currency, Double.parseDouble(newValue));
                    }
                }

                // Move the parser head forward
                eventType = parser.next();
            }

        } catch (Exception exception) {
            Log.e("Currency Converter", "Error when calling API! Stack trace below");
            exception.printStackTrace();
        }

    }

}
