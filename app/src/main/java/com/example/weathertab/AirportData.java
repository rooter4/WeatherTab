package com.example.weathertab;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import androidx.core.content.ContextCompat;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final HashMap<String, String> airportLat;
    private static final HashMap<String, String> airportLong;
    private static final List<String> wxLat;
    private static final List<String> wxLong;
    private static List<String> wxStat;
    private static  List<String> ids;
    private static List<String> cities;
    private static List<String> wxStations = Arrays.asList(new String[]{"KABR", "KABX", "KAKQ", "KAMA", "KAMX", "KAPX", "KARX", "KATX", "KBBX", "KBGM", "KBHX", "KBIS", "KBLX", "KBMX", "KBOX", "KBRO", "KBUF", "KBYX", "KCAE", "KCBW", "KCBX", "KCCX", "KCLE", "KCLX", "KCRI", "KCRP", "KCXX", "KCYS", "KDAX", "KDDC", "KDFX", "KDGX", "KDIX", "KDLH", "KDMX", "KDOX", "KDTX", "KDVN", "KDYX", "KEAX", "KEMX", "KENX", "KEOX", "KEPZ", "KESX", "KEVX", "KEWX", "KEYX", "KFCX", "KFDR", "KFDX", "KFFC", "KFSD", "KFSX", "KFTG", "KFWS", "KGGW", "KGJX", "KGLD", "KGRB", "KGRK", "KGRR", "KGSP", "KGWX", "KGYX", "KHDX", "KHGX", "KHNX", "KHPX", "KHTX", "KICT", "KICX", "KILN", "KILX", "KIND", "KINX", "KIWA", "KIWX", "KJAX", "KJGX", "KJKL", "KLBB", "KLCH", "KLIX", "KLNX", "KLOT", "KLRX", "KLSX", "KLTX", "KLVX", "KLZK", "KMAF", "KMAX", "KMBX", "KMHX", "KMKX", "KMLB", "KMOB", "KMPX", "KMQT", "KMRX", "KMSX", "KMTX", "KMUX", "KMVX", "KMXX", "KNKX", "KNQA", "KOAX", "KOHX", "KOKX", "KOTX", "KPAH", "KPBZ", "KPDT", "KPOE", "KPUX", "KRAX", "KRGX", "KRIW", "KRLX", "KRTX", "KSFX", "KSGF", "KSHV", "KSJT", "KSOX", "KSRX", "KTBW", "KTFX", "KTLH", "KTLX", "KTWX", "KTYX", "KUDX", "KUEX", "KVAX", "KVBX", "KVNX", "KVTX", "KVWX", "KYUX", "PACG", "PAEC", "PAHG", "PAIH", "PAKC", "PAPD", "PGUA", "PHKI", "PHKM", "PHMO", "PHWA", "TJUA", "KLWX", "PABC", "TADW", "TATL", "TBNA", "TBOS", "TBWI", "TCLT", "TCMH", "TCVG", "TDAL", "TDAY", "TDCA", "TDEN", "TDFW", "TDTW", "TEWR", "TFLL", "THOU", "TIAD", "TIAH", "TICH", "TIDS", "TJFK", "TLAS", "TLVE", "TMCI", "TMCO", "TMDW", "TMEM", "TMIA", "TMKE", "TMSP", "TMSY", "TOKC", "TORD", "TPBI", "TPHL", "TPHX", "TPIT", "TRDU", "TSDF", "TSJU", "TSLC", "TSTL", "TTPA", "TTUL"});
    static {
        data = new ArrayList<>();
        airportIDs = new HashMap<>();
        airportCity = new HashMap<>();
        airportElev = new HashMap<>();
        airportLocale = new HashMap<>();
        airportLat = new HashMap<>();
        airportLong = new HashMap<>();
        wxLong = new ArrayList<>();
        wxLat = new ArrayList<>();
        ids = new ArrayList<>();
        cities = new ArrayList<>();
        wxStat = new ArrayList<>();
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
                airportLat.put(nextLine[1],nextLine[4]);
                airportLong.put(nextLine[1],nextLine[5]);
            }
            ids = new ArrayList<>(airportIDs.keySet());
            cities = new ArrayList<>(airportCity.values());
            stream = MainActivity.getContext().getAssets().open("nexrad_site_list_with_utm.csv");
            reader = new CSVReader(new InputStreamReader(stream));
            String[] newline;
            while((newline = reader.readNext())!=null){
                wxLat.add(newline[6]);
                wxLong.add(newline[7]);
                wxStat.add(newline[0]);
            }


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
    public static Boolean hasWX(String name){
        if(wxStations.contains(name))
            return true;
        else if (wxStations.contains(name.substring(0,name.length()-1)+"X")) {
            System.out.println("with X");
            return true;
        }

            return false;
    }
    public static String getClosestStation(String name){
        int closest = 100000;
        int index = 0;
        int r = 6371;

        double aLat = Double.parseDouble(airportLat.get(name));
        double aLon = Double.parseDouble(airportLong.get(name));
        for(int i = 0; i < wxStat.size(); i ++){
            double wLon = Double.parseDouble(wxLong.get(i));
            double wlat = Double.parseDouble(wxLat.get(i));
            double dLat = deg2rad(aLat-wlat);
            double dLon = deg2rad(aLon-wLon);

            double a = sin(dLat/2) * sin(dLat/2) +
                    Math.cos(deg2rad(wlat)) * Math.cos(deg2rad(aLat)) *
                            sin(dLon/2) * sin(dLon/2);
            double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
            double d = r * c;

            if(d<closest){
                closest = (int) d;
                index = i;
            }

            double X = Math.cos(wlat) * Math.sin(wLon-aLon);
            double Y = Math.cos(aLat) * Math.sin(wlat) - Math.sin(aLat) * Math.cos(wlat) * cos(wLon-aLon);
            double angle = Math.atan2(X,Y);
            System.out.println(wxStat.get(i) + " Distance = " + Math.toDegrees(angle) +" @ " + d + "m");

        }
        return wxStat.get(index);
    }
    private static double deg2rad(double deg){
        return deg *(Math.PI/180);
    }




}
