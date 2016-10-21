package com.appisode.currencyconverter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

import Adapter.Adapter_conversion_listview;
import Data.Currency_Names;
import HttpService.ServiceHandler;


public class Activity_conversion_listview extends Activity
{

    ListView                            listview        ;
    Adapter_conversion_listview         adaptr_listview ;
    String                              temp = null     ;


    public static String url_currency_id_name = "http://free.currencyconverterapi.com/api/v3/currencies";
    public JSONObject jsonObj_name_id = null;
    public static ArrayList<Currency_Names> currences_names  ;
    String s_rtes , s_names,s_ids_names;
    private ProgressDialog pDialog               ;

    String split[];
    StringTokenizer stok;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion_listview);

        listview    =   (ListView)findViewById(R.id.listView);
        pDialog = new ProgressDialog(this);
        currences_names= new ArrayList<>();



        new GetExchangeRates().execute();

    }

    private class GetExchangeRates extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            ServiceHandler sh = new ServiceHandler();

            String json_curncy_id_name = sh.makeServiceCall(url_currency_id_name, ServiceHandler.GET);

            try
            {

                jsonObj_name_id = new JSONObject(json_curncy_id_name);
                s_ids_names = jsonObj_name_id.getJSONObject("results").toString();

            }catch (JSONException e)
            {
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            add_country_ids_names();


        }

    }

    public void add_country_ids_names()  {

        s_ids_names=s_ids_names.replace("{","");
        s_ids_names=s_ids_names.replace("}","");
        s_ids_names=s_ids_names.replace("\"","");

        stok= new StringTokenizer(s_ids_names,",");


        while(stok.hasMoreElements())
        {
            temp= stok.nextElement().toString();

            if(temp.indexOf("currencySymbol") != -1){
                temp= stok.nextElement().toString();
            }

            String split[]= temp.split(":");

            temp= stok.nextElement().toString();

            if(temp.indexOf("currencySymbol") != -1){
                temp= stok.nextElement().toString();


            }

            String split2[]= temp.split(":");

            temp = null;

            currences_names.add(new Currency_Names(split[2], split2[1]));


        }

        Collections.sort(currences_names, new Comparator<Currency_Names>() {
            @Override
            public int compare(Currency_Names n1, Currency_Names n2) {

                return n1.short_name.compareTo(n2.short_name);
            }
        });

        adaptr_listview = new Adapter_conversion_listview(Activity_conversion_listview.this, currences_names);
        listview.setAdapter(adaptr_listview);
        pDialog.dismiss();
    }
}
