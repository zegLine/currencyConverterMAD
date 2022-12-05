package com.zeglines.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CurrencyListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        // instantiate the forex database in order to pass it as a parameter to our custom adapter
        ExchangeRateDatabase forexDb = new ExchangeRateDatabase();

        /* OLD CODE: this was used before we had the custom adapter, and it did not show images

        String[] currencies = forexDb.getCurrencies();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_element_view,
                R.id.spinner_text_element,
                currencies
        );
        */

        // create an instance of our custom adapter
        CurrencyItemAdapter adapter = new CurrencyItemAdapter(forexDb);

        // instantiate the list view and set its adapter to the one we just created
        ListView lv = findViewById(R.id.list_view_currencies);
        lv.setAdapter(adapter);

        // se the on click listener for our list view entries
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //todo start google maps intent

                String currency = (String) adapter.getItem(i);
                String address = forexDb.getCapital(currency);

                Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

    }
}