package com.example.weathertab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public final class WXParser {

    public static final HashMap OBSCURATION = new HashMap() {{


        put("FG", "Fog");
        put("BR", "Mist");
        put("HZ", "Haze");
        put("VA", "Volcanic Ash");
        put("DU", "Dust");
        put("FU", "Smoke");
        put("SA", "Sand");
        put("SY", "Spray");

    }};
    private static final HashMap BADSTUFF = new HashMap() {{
        put("PO", "Dust Whirls");
        put("DS", "Duststorm");
        put("SS", "Sandstorm");
        put("FC", "Funnel Cloud");
        put("+FC", "Funnel Cloud");
        put("SQ", "Squall");
    }};
    private static final HashMap QUALIFIER = new HashMap() {{
        put("BL", "Blowing");
        put("PR", "Partial");
        put("DR", "Low Drifting");
        put("TS", "Thunderstorms");
        put("SH", "Showers");
        put("FZ", "Freezing");
        put("MI", "Shallow");
        put("BC", "Patchy");
    }};
    private static final HashMap PRECIP = new HashMap() {{
        put("RA", "Rain");

        put("DZ", "Drizzle");
        put("SN", "Snow");
        put("SG", "Snow Grains");
        put("IC", "Ice Crystals");
        put("PL", "Ice Pellets");
        put("GR", "Hail");
        put("GS", "Small Hail");
        put("UP", "Unknown");
    }};

    private static final HashMap INTENSITY = new HashMap() {{
        put("+", "Heavy");
        put("-", "Light");
        put("VC", "in the vicinity");
        put("DSNT", "Distant");

    }};

    private static final HashMap wxString = new HashMap() {{
        put("SCT", "Scattered");
        put("OVC", "Overcast");
        put("BKN", "Broken");
        put("FEW", "Few");
        put("CLR", "Clear");
        put("SKC", "Clear");
        put("FM", "From");
        put("BM", "Becoming");
        put("PROB", "Probably");
        put("TEMPO", "Temporary");
        put("VV", "Vertical Visibility");
        put("BECMG", "Becoming");
    }};

    static String urlString = "https://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&hoursBeforeNow=36&mostRecentForEachStation=true&stationString=";
    static String tafUrl = "https://www.aviationweather.gov/adds/dataserver_current/httpparam?datasource=tafs&requestType=retrieve&format=xml&mostRecentForEachStation=true&hoursBeforeNow=24&stationString=";
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor prefEditor;
    private static Document doc;
    private static Document tafDoc;
    private static WeatherHolder airport;

    static {
        doc = null;
        tafDoc = null;
        airport = new WeatherHolder();

    }


    public WXParser() {

    }

    public static void init(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        prefEditor = preferences.edit();

    }

    public static Document getDoc() {

        if (doc == null) {
            try {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();

                File f = new File(path + "/recent.xml");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                doc = builder.parse(f);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return doc;
    }

    public static Document getTafDoc() {

        if (tafDoc == null) {
            try {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();

                File f = new File(path + "/taf.xml");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                tafDoc = builder.parse(f);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tafDoc;
    }

    public static boolean Refresh(SmallRVAdapter smallRVAdapter, List<String> iData, Handler handler) {

        Thread thread = new Thread(() -> {
            try {
                String icao = "";
                for (String s : iData)
                    icao += s + "+";

                System.out.println(icao);
                URL url = new URL(urlString + icao);
                URL taf = new URL(tafUrl + icao);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                System.out.println("RESPONSE:  " + conn.getResponseCode());
                conn.connect();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();

                doc = builder.parse(conn.getInputStream());
                doc.getDocumentElement().normalize();

                File f = new File(path + "/recent.xml");
                f.delete();
                File fnew = new File(path + "/recent.xml");
                DOMSource source = new DOMSource(doc);
                FileWriter writer = new FileWriter(fnew);
                StreamResult result = new StreamResult(writer);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.transform(source, result);


                conn = (HttpURLConnection) taf.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                System.out.println("TAF RESPONSE:  " + conn.getResponseCode());
                conn.connect();

                factory = DocumentBuilderFactory.newInstance();
                builder = factory.newDocumentBuilder();

                //TAF DOC

                tafDoc = builder.parse(conn.getInputStream());
                tafDoc.getDocumentElement().normalize();

                f = new File(path + "/taf.xml");
                f.delete();
                File fnnew = new File(path + "/taf.xml");

                source = new DOMSource(tafDoc);
                writer = new FileWriter(fnnew);
                result = new StreamResult(writer);

                transformerFactory = TransformerFactory.newInstance();
                transformer = transformerFactory.newTransformer();
                transformer.transform(source, result);

                smallRVAdapter.updateData(iData);
                handler.sendEmptyMessage(1);


            } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
                e.printStackTrace();
            }
        });

        thread.start();

        return true;
    }

    public static String getCondition() {
        return airport.fields.get("cat");
    }

    public static String getTemp_Dp() {
        if(UNITS.TEMP == UNITS.temp_f)
            return UNITS.convertToF(airport.fields.get("temp_c"))+ "\u00B0F" + "\n" + UNITS.convertToF(airport.fields.get("dp_c")) + "\u00B0F";
        return airport.fields.get("temp_c") + "\u00B0C" + "\n" + airport.fields.get("dp_c") + "\u00B0C";
    }

    public static String getPx() {
        String res = "";
        if(UNITS.PX == UNITS.px_mb) {
            res = airport.fields.get("px_mb");
            if(res.equals("")){
                res = UNITS.convertToMb(airport.fields.get("px_in"));
            }
        }
        else if(UNITS.PX == UNITS.px_in ) {
            res = airport.fields.get("px_in");
        }

        if (res.length() > 5)
            res = res.substring(0, 5);
        return res;
    }

    public static String getWx() {
        return airport.fields.get("wx");
    }

    public static String getRawMetar() {
        String[] sep = {"FM","BM","PROB","TEMPO","BECMG","PROB30", "PROB40"};
        String res =airport.fields.get("raw");
        System.out.println(res);
        String fin = "";
        String finFin = "";
        for(String s: sep){
            while(res.toUpperCase().contains(s.toUpperCase())){
                System.out.println(s);
                fin = (res.substring(res.toUpperCase().lastIndexOf(s.toUpperCase())) + "\n" + fin);
                res = res.substring(0,res.toUpperCase().lastIndexOf(s.toUpperCase()));
            }
        }
        for(String s: sep){
            while(fin.toUpperCase().contains(s.toUpperCase())){
                finFin = fin.substring(fin.toUpperCase().lastIndexOf(s.toUpperCase())) + "\n" + finFin;
                fin = fin.substring(0,fin.toUpperCase().lastIndexOf(s.toUpperCase()));
            }
        }



        return res +"\n" + fin + finFin;
    }

    public static void setMetar(String name) {
        airport = new WeatherHolder();
        airport.fields.put("name", name);
        Document locDoc = getDoc();
        NodeList e = locDoc.getElementsByTagName("METAR");
        for (int i = 0; i < e.getLength(); i++) {
            Node cur = e.item(i);
            if (cur.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) cur;
                String aName = el.getElementsByTagName("station_id").item(0).getTextContent();
                if (name.equalsIgnoreCase(aName)) {
                    if (el != null) {
                        for (String s : airport.fields.keySet()) {
                            try {
                                airport.fields.put(s, el.getElementsByTagName(airport.req.get(s)).item(0).getTextContent());
                                if (s.equals("sky"))
                                    airport.fields.put(s, parseClouds(el));
                            } catch (NullPointerException exception) {
                            }
                        }
                    }
                }
            }
        }
    }

    public static int setTaf(String name, int index) {
        airport = new WeatherHolder();
        airport.fields.put("name", name);
        int counter = 0;
        Document tafDoc = getTafDoc();
        if (hasTaf()) {
            NodeList e = tafDoc.getElementsByTagName("TAF");
            for (int i = 0; i < e.getLength(); i++) {
                Node cur = e.item(i);
                if (cur.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) cur;
                    String aName = el.getElementsByTagName("station_id").item(0).getTextContent();
                    if (aName.equals(name)) {
                        airport.fields.put("raw", el.getElementsByTagName("raw_text").item(0).getTextContent());
                        NodeList fork = el.getElementsByTagName("forecast");
                        Element r = (Element) fork.item(index);
                        counter = fork.getLength();
                        for (String s : airport.fields.keySet()) {
                            try {
                                if (s != "raw") {
                                    airport.fields.put(s, r.getElementsByTagName(airport.req.get(s)).item(0).getTextContent());
                                    if (s.equals("sky"))
                                        airport.fields.put(s, parseClouds(r));
                                }
                            } catch (NullPointerException exception) {
                            }

                        }
                    }
                }
            }
        }
        return counter;
    }

    private static String parseClouds(Element e) {
        String sky = "";
        NodeList n = e.getElementsByTagName("sky_condition");
        for (int j = 0; j < n.getLength(); j++) {
            Element ex = (Element) n.item(j);
            sky += wxString.get(ex.getAttribute("sky_cover"));
            String base = ex.getAttribute("cloud_base_ft_agl");
            String type = ex.getAttribute("cloud_type");
            if (!base.isEmpty() && !type.isEmpty())
                sky += " cumulonibus clouds at " + ex.getAttribute("cloud_base_ft_agl") + "'  \n";
            else if (!base.isEmpty())
                sky += " clouds at " + ex.getAttribute("cloud_base_ft_agl") + "'  \n";
        }
        return sky;
    }

    public static int getTime() {
        int time = 999;
        String temp = airport.fields.get("time");


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = new Date();  // From your code above
        Date now = new Date();
        try {
            d = df.parse(temp);
            long diffmills = Math.abs(now.getTime() - d.getTime());
            long dif = TimeUnit.MINUTES.convert(diffmills, TimeUnit.MILLISECONDS);
            time = (int) dif;
        } catch (ParseException e) {
            time = 999;
        }
        if (time > 999)
            time = 999;
        return time;
    }

    public static String getWind() {
        String dir = airport.fields.get("wind_dir");
        String spd = airport.fields.get("wind_spd");
        String gust = airport.fields.get("wind_gst");

        if (dir.isEmpty())
            return "";
        if (dir.length() == 2)
            dir = "0" + dir;

        if (spd.equals("0") && dir.equals("0")) {
            dir = "CALM";
            spd = "";
        } else if (dir.equals("0"))
            dir = "VRB - ";
        else
            dir += "\u00B0 - ";

        if (!gust.isEmpty())
            gust = " G " + gust;


        return dir + spd + gust;
    }

    public static String getSky() {
        return airport.fields.get("sky");
    }

    public static String getPa() {
        try {
            String px = airport.fields.get("px_in");
            if(px.equals(""))
                px = UNITS.convertToIn(airport.fields.get("px_mb"));
            double std = 29.92;
            double alt = Double.valueOf(AirportData.getAlt(airport.fields.get("name")));
            double p = Double.valueOf(px);


            int res = (int) (((std - p) * 1000) + alt);
            return res + "' PA";
        } catch (NumberFormatException e) {
            return "";
        }
    }

    public static String getDa() {
        try {
            String temp = airport.fields.get("temp_c");
            String dp = airport.fields.get("dp_c");
            String px = airport.fields.get("px_in");
            if(px.equals(""))
                px = UNITS.convertToIn(airport.fields.get("px_mb"));
            double std = 29.92;
            double alt = Double.parseDouble(AirportData.getAlt(airport.fields.get("name")));
            double p = Double.parseDouble(px);
            double t = Double.parseDouble(temp);
            double d = Double.parseDouble(dp);
            double isaT = 15 - (.002 * (alt));

            double pxAlt = ((std - p) * 1000) + alt;
            double rh = 100 * (Math.exp((17.625 * d) / (243.04 + d)) / Math.exp((17.625 * t) / (243.04 + t)));
            int da = (int) (pxAlt + (100 * (rh / 10)));
            return da + "' DA";
        } catch (NumberFormatException e) {
            return "";
        }

    }

    public static boolean hasTaf() {
        Document tafDoc = getTafDoc();
        if (tafDoc != null) {
            NodeList e = tafDoc.getElementsByTagName("TAF");
            for (int i = 0; i < e.getLength(); i++) {
                Node cur = e.item(i);
                if (cur.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) cur;
                    String aName = el.getElementsByTagName("station_id").item(0).getTextContent();
                    if (aName.equals(airport.fields.get("name"))) {
                        String date = el.getElementsByTagName("valid_time_to").item(0).getTextContent();

                        return isValid(date);
                    }
                }
            }
        }
        return false;
    }

    private static boolean isValid(String time) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date d = df.parse(time);
            Date now = new Date();
            if (now.before(d))
                return true;


        } catch (ParseException e) {
            return false;
        }
        return false;
    }

    public static String getVis() {
        String res = airport.fields.get("vis");
        if(!res.equals("") && UNITS.DIST == UNITS.NM)
            res+= " NM";
        if(!res.isEmpty() && UNITS.DIST == UNITS.KM)
            res = UNITS.convertToKm(res) + "KM";
        return  res;
    }

    public static String getDetailedWx() {
        String res = getWx();
        String wxString = "";
        String[] temp = res.split(" ");
        for (String w : temp) {
            if (w.startsWith("+") || w.startsWith("-"))
                wxString = (String) INTENSITY.get(w.substring(0, 1)) + " ";
            for (int i = 0; i < w.length() - 1; i++) {

                if (QUALIFIER.containsKey(w.substring(i, i + 2))) {
                    wxString += QUALIFIER.get(w.substring(i, i + 2)) + " ";
                }
                if (PRECIP.containsKey(w.substring(i, i + 2))) {
                    wxString += PRECIP.get(w.substring(i, i + 2)) + " ";
                }
                if (OBSCURATION.containsKey(w.substring(i, i + 2))) {
                    wxString += OBSCURATION.get(w.substring(i, i + 2)) + " ";
                }
                if (BADSTUFF.containsKey(w.substring(i, i + 2))) {
                    wxString += BADSTUFF.get(w.substring(i, i + 2)) + " ";
                }

            }
            if (w.contains("VC") || w.contains("DSNT")) {
                wxString += INTENSITY.get((w.contains("VC") ? "VC" : "DSNT"));
            }
        }
        return wxString;
    }

    public static String getTafTime() {

        String from = airport.fields.get("fcst_from");
        String to = airport.fields.get("fcst_to");

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateFormat dft = new SimpleDateFormat("HH:mm' 'MM/dd");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = new Date();  // From your code above
        Date n = new Date();

        try {
            d = df.parse(from);
            n = df.parse(to);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return wxString.get(airport.fields.get("type")) + ": " + dft.format(d) + " until " + dft.format(n);
    }

    private static class WeatherHolder {
        public HashMap<String, String> fields = new HashMap() {{
            put("wind_dir", "");
            put("wind_spd", "");
            put("wind_gst", "");
            put("vis", "");
            put("wx", "");
            put("temp_c", "n/a");
            put("dp_c", "n/a");
            put("sky", "");
            put("px_in", "");
            put("px_mb","");
            put("cat", "");
            put("raw", "");
            put("name", "");
            put("id", "");
            put("time", "");
            put("fcst_to", "");
            put("fcst_from", "");
            put("type", "FM");

        }};
        public HashMap<String, String> req = new HashMap() {{
            put("wind_dir", "wind_dir_degrees");
            put("wind_spd", "wind_speed_kt");
            put("wind_gst", "wind_gust_kt");
            put("vis", "visibility_statute_mi");
            put("wx", "wx_string");
            put("temp_c", "temp_c");
            put("dp_c", "dewpoint_c");
            put("sky", "sky_condition");
            put("px_in", "altim_in_hg");
            put("px_mb", "sea_level_pressure_mb");
            put("cat", "flight_category");
            put("raw", "raw_text");
            put("id", "station_id");
            put("time", "observation_time");
            put("fcst_from", "fcst_time_from");
            put("fcst_to", "fcst_time_to");
            put("type", "change_indicator");

        }};

        public WeatherHolder() {
        }

        public void setData() {

        }
    }


}
