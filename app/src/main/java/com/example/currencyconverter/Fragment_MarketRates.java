package com.example.currencyconverter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.StringTokenizer;

import Adapter.Adapter_listview;
import Data.Currency_Names;
import Data.Currency_Rates;
import Data.Currency_id_name;
import HttpService.ServiceHandler;


public class Fragment_MarketRates extends Fragment
{

    private ProgressDialog  pDialog                                            ;
    public JSONObject       jsonObj_rates=null , jsonObj_names=null            ;
    String                  s_rtes , s_names,s_ids_names ,temp=null            ;
    TextView                fixPrice                                           ;
    ListView                listview                                           ;
    Adapter_listview        adapter_listview                                   ;
    RelativeLayout          root_layout                                        ;
    View                    v                                                  ;
    AdView                  mAdView	                        		 		   ;
    public static final String BASE_URL = "http://apilayer.net/api/";
    public static final String ENDPOINT = "live";

    public static   ArrayList<Currency_Rates> list_currency_rates_data         ;
    public static   ArrayList<Currency_Names> list_currency_names_data         ;
    public static   ArrayList<Currency_id_name> list_currency_id_name          ;

    String url_currency_rates;
    String ulr_curency_namees;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       v  = inflater.inflate(R.layout.fragment_marketrates,null);

        //apilayer api key
       String key= getActivity().getResources().getString(R.string.Currencylayer_Key);

         url_currency_rates = BASE_URL + ENDPOINT + "?access_key=" + key;
         ulr_curency_namees="http://www.apilayer.net/api/list?access_key="+key+"&format=1";


        listview        = (ListView)v.findViewById(R.id.listview);
        root_layout     = (RelativeLayout)v.findViewById(R.id.parent_relative);

        list_currency_id_name = new ArrayList<>();
        list_currency_rates_data = new ArrayList<>();
        list_currency_names_data= new ArrayList<>();


        mAdView = (AdView)v.findViewById(R.id.adView);
        AdRequest adr= new AdRequest.Builder().build();
        mAdView.loadAd(adr);



        pDialog = new ProgressDialog(getActivity());
        if(isNetworkAvailable())
        {
            new GetExchangeRates().execute();
        }
        else
        {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Internet Connection")
                    .setMessage("Please check your internet connection")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("LOGOUT", true);
                            startActivity(intent);

                            getActivity().finish();
                        }
                    })

                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

       }





        return v;

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
            String json_curny_rates = sh.makeServiceCall(url_currency_rates, ServiceHandler.GET);
            String json_curncy_names = sh.makeServiceCall(ulr_curency_namees, ServiceHandler.GET);


            try
            {

                jsonObj_rates = new JSONObject(json_curny_rates);

                jsonObj_names = new JSONObject(json_curncy_names);

                s_rtes = jsonObj_rates.getJSONObject("quotes").toString();
                Log.d("s_rates ",""+s_rtes);
                s_names= jsonObj_names.getJSONObject("currencies").toString();
            }catch (JSONException e)
            {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            add_currency_rates();
            add_country_names();

        }

    }

    public void add_country_ids_names()  {

        s_ids_names=s_ids_names.replace("{","");
        s_ids_names=s_ids_names.replace("}","");
        s_ids_names=s_ids_names.replace("\"","");

        StringTokenizer stok= new StringTokenizer(s_ids_names,",");

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

            Log.d("Split 1",""+split[2]);
            Log.d("Split 2",""+split2[1]);

            temp = null;

            list_currency_id_name.add(new Currency_id_name(split[2], split2[1]));

        }

        Collections.sort(list_currency_id_name, new Comparator<Currency_id_name>() {
            @Override
            public int compare(Currency_id_name n1, Currency_id_name n2) {

                return n1.currency_id.compareTo(n2.currency_id);
            }
        });


    }

    public void add_currency_rates()
    {
        s_rtes=s_rtes.replace("{","");
        s_rtes=s_rtes.replace("}","");
        s_rtes=s_rtes.replace("\"","");

        StringTokenizer stok= new StringTokenizer(s_rtes,",");

        while(stok.hasMoreElements())
        {

            String temp= stok.nextElement().toString();

            String split[]= temp.split(":");


            double amount = Double.parseDouble(split[1]);

            DecimalFormat df1 = new DecimalFormat("#.###",new DecimalFormatSymbols(Locale.US));

            String refinedNumber = df1.format(amount);

            split[1] = String.valueOf(refinedNumber);


            if(split[0].contentEquals("USDUSD"))
            {

                fixPrice = (TextView)v.findViewById(R.id.priceTextFix);
                String s = split[1];
                fixPrice.setText(s);
            }

            list_currency_rates_data.add(new Currency_Rates(split[0], split[1]));



        }

        Collections.sort(list_currency_rates_data, new Comparator<Currency_Rates>() {
            @Override
            public int compare(Currency_Rates r1,Currency_Rates r2) {
                return r1.title.compareTo(r2.title);
            }
        });


    }

    public void add_country_names()
    {
        s_names=s_names.replace("{","");
        s_names=s_names.replace("}","");
        s_names=s_names.replace("\"","");

        StringTokenizer stoke= new StringTokenizer(s_names,",");

        while(stoke.hasMoreElements())
        {

            String temp= stoke.nextElement().toString();
            String split[]= temp.split(":");

            list_currency_names_data.add(new Currency_Names(split[0], split[1]));



        }


        Collections.sort(list_currency_names_data, new Comparator<Currency_Names>() {
            @Override
            public int compare(Currency_Names n1, Currency_Names n2) {
                return n1.short_name.compareTo(n2.short_name);
            }
        });


        adapter_listview= new Adapter_listview(getActivity(),list_currency_names_data,list_currency_rates_data);
        listview.setAdapter(adapter_listview);

        pDialog.dismiss();


    }




}
