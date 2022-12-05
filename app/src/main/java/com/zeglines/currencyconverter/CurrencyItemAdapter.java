package com.zeglines.currencyconverter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class CurrencyItemAdapter extends BaseAdapter {
    ExchangeRateDatabase rateDb;

    public CurrencyItemAdapter(ExchangeRateDatabase db) {
        rateDb = db;
    }

    @Override
    public int getCount() {
        return rateDb.getCurrencies().length;
    }

    @Override
    public Object getItem(int i) {
        return rateDb.getCurrencies()[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();
        String currencyName = rateDb.getCurrencies()[position];
        // TODO: (Create and) Initialize view with text and image

        int imageId = context.getResources().getIdentifier("flag_" + currencyName.toLowerCase(),
        "drawable", context.getPackageName());

        //View v = parent.findViewById(R.id.spinner_element_lin_layout);
        LayoutInflater li = LayoutInflater.from(context);
        View v = li.inflate(R.layout.spinner_element_view, null);

        ImageView iv = v.findViewById(R.id.spinner_flag_element);
        TextView tv = v.findViewById(R.id.spinner_text_element);
        Log.i("CURRENCYClogs", String.valueOf(imageId));

        iv.setImageResource(imageId);
        tv.setText(currencyName);

        return v;
    }
}
