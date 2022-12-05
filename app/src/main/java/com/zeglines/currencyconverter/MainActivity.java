package com.zeglines.currencyconverter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Spinner from_value_spinner;
    Spinner to_value_spinner;
    EditText amount_text;

    ExchangeRateDatabase forexDb = new ExchangeRateDatabase();

    TextView result_text_view;

    ShareActionProvider shareActionProvider;

    // Initialize the custom adapter
    CurrencyItemAdapter adapter = new CurrencyItemAdapter(forexDb);

    SharedPreferences prefs;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu obj from resource file
        getMenuInflater().inflate(R.menu.my_menu, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        setShareText(null);

        //return true so we can create menu content
        return true;
    }

    private void setShareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (text != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        }
        shareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // When a button gets clicked, find out which one
        switch (item.getItemId()) {
            // Currency list button
            case R.id.currency_list_menu_entry:
                Log.i("Currency Converter", "Clicked on the menu option currency list");

                //start the currency list activity through intent
                Intent currencyListIntent = new Intent(MainActivity.this, CurrencyListActivity.class);
                startActivity(currencyListIntent);

                return true;

                // Get rates from ECB button
            case R.id.refresh_rates_menu_entry:
                Log.i("Currency Converter", "Clicked on REFRESH RATES FROM ECB");
                CurrencyUpdater.updateCurrencies(forexDb);

                // instantiate a shared preferences editor and store the currency rates as preferences starting with 'rate'
                SharedPreferences.Editor editor = prefs.edit();
                for (String c : forexDb.getCurrencies()) {
                    double rate = forexDb.getExchangeRate(c);
                    editor.putString("rate" + c, String.valueOf(rate));
                    Log.i("CurrencyConvertor", c + " " + String.valueOf(rate));
                }
                editor.apply();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getPreferences(Context.MODE_PRIVATE);

        // TEMP Bypass thread policy and do networking on the GUI Main thread
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        //initialize a string array with currency names
        String[] currencies = forexDb.getCurrencies();

        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get spinner views
        from_value_spinner = findViewById(R.id.from_value_spinner);
        to_value_spinner = findViewById(R.id.to_value_spinner);

        // Get edit text views
        amount_text = findViewById(R.id.edit_text_value);
        result_text_view = findViewById(R.id.result_text_view);

        // Set the created adapter to the spinners
        from_value_spinner.setAdapter(adapter);
        to_value_spinner.setAdapter(adapter);

        // Update values of rates if they are in the preferences
        // (have been previously updated from the ECB API)

        for (String c : forexDb.getCurrencies()) {
            String newValue = prefs.getString("rate" + c, "NOT_EXIST");


            if (!newValue.equals("NOT_EXIST")) {
                Log.i("CurrencyConverter", "Updating rate from previous session for " + c);
                double newValueDouble = Double.parseDouble(newValue);
                forexDb.setExchangeRate(c, newValueDouble);
            }
        }
    }

    public void calculateConversion(View v) {
        Log.i("Converter", "Calculate Button Clicked!");

        // Get currency names from spinners
        String from_cur = (String) from_value_spinner.getSelectedItem();
        String to_cur = (String) to_value_spinner.getSelectedItem();
        Log.i("Converter", "From " + from_cur);
        Log.i("Converter", "To " + to_cur);

        // Get given value
        double value = Double.parseDouble(amount_text.getText().toString());
        Log.i("Converter", "Value is " + value);

        // Calculate result using the forexDB class
        double result = forexDb.convert(value, from_cur, to_cur);
        Log.i("Converter", "Result is " + result);


        // Update result textview
        result_text_view.setText(String.valueOf(result));

        // Update the share text
        setShareText(amount_text.getText().toString() + " " + from_cur + " is " + String.valueOf(result) + " " + to_cur);

    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("FromCurrency", (String) from_value_spinner.getSelectedItem());
        editor.putString("ToCurrency", (String) to_value_spinner.getSelectedItem());
        editor.putString("ValueToConvert", amount_text.getText().toString());

        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

        String valueToConvert = prefs.getString("ValueToConvert", "10.0");
        amount_text.setText(valueToConvert);
        Log.i("Currency Converter", "Resumed VALUETOCONVERT is " + valueToConvert);

        String fromCurrency = prefs.getString("FromCurrency", "EUR");
        from_value_spinner.setSelection(getIndex(from_value_spinner, fromCurrency));
        Log.i("Currency Converter", "Resumed FROMVALUE is " + fromCurrency);


        String toCurrency = prefs.getString("ToCurrency", "AUD");
        to_value_spinner.setSelection(getIndex(to_value_spinner, toCurrency));
        Log.i("Currency Converter", "Resumed TOVALUE is " + toCurrency);

    }

    private int getIndex(Spinner spinner, String mystr) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(mystr)) {
                index = i;
            }
        }
        return index;
    }
}