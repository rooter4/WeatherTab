package com.example.weathertab;

import androidx.core.content.ContextCompat;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AirportData {
    private static final List<String[]> data;
    private static final HashMap<String,String> airportIDs;
    private static final HashMap<String,String> airportCity;
    private static final HashMap<String, String> airportElev;
    private static final HashMap<String, String> airportLocale;
    private static  List<String> ids;
    private static List<String> cities;
    static {
        data = new ArrayList<>();
        airportIDs = new HashMap<>();
        airportCity = new HashMap<>();
        airportElev = new HashMap<>();
        airportLocale = new HashMap<>();
        ids = new ArrayList<>();
        cities = new ArrayList<>();
        try {


            InputStream stream = MainActivity.getContext().getAssets().open("airports.csv");

            CSVReader reader = new CSVReader(new InputStreamReader(stream));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                data.add(nextLine);
                airportIDs.put(nextLine[1],nextLine[3]);
                airportCity.put(nextLine[1],nextLine[10]);
                airportElev.put(nextLine[1], nextLine[6]);
                airportLocale.put(nextLine[1],nextLine[8]);
            }
            ids = new ArrayList<>(airportIDs.keySet());
            cities = new ArrayList<>(airportCity.values());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static String getName(String id){
        return airportIDs.get(id);
    }
    public static String getCity(String id){
        return airportCity.get(id);
    }
    public static String getAlt(String id){return airportElev.get(id);}
    public static String getID(int pos){return ids.get(pos);}
    public static String getLoc(String id){return airportLocale.get(id);}

    public static List<Integer> find(CharSequence c){
        List<Integer> values = new ArrayList<>();
       for(int i = 0; i < ids.size(); i++){
           if(ids.get(i).toUpperCase().contains(c.toString().toUpperCase()))
           {
            values.add(i);
           }
           if(cities.get(i).toUpperCase().contains(c.toString().toUpperCase())){
               values.add(i);


           }
       }
       return values;


    }

}
