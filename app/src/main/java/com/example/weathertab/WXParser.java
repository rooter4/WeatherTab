package com.example.weathertab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@SuppressLint("HandlerLeak")
public final class WXParser {
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
    public static WeatherHolder airport;
    private static Handler handler;

    static {
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
            }
        };
        doc = null;
        tafDoc = null;
        airport = null;


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
                Refresh(handler);

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
                Refresh(handler);
            }
        }
        return tafDoc;
    }

    public static boolean Refresh(Handler handler) {

        Thread thread = new Thread(() -> {
            try {
                String set = preferences.getString("airports", "[KPNS]");
                Gson gson = new Gson();
                List<String> iData = gson.fromJson(set, ArrayList.class);


                String icao = "";
                for (String s : iData)
                    icao += s + "+";

                System.out.println(icao);
                URL url = new URL(urlString + icao);
                URL taf = new URL(tafUrl + icao);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                System.out.println(System.getProperty("http.agent"));
                conn.setRequestProperty("User-Agent", System.getProperty("http.agent"));
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

    public static String getTemp() {
        return airport.fields.get("temp_c");
    }

    public static String getDp() {
        return airport.fields.get("dp_c");
    }

    public static String getPxMb() {
        return airport.fields.get("px_mb");
    }

    public static String getPxIn() {
        return airport.fields.get("px_in");
    }

    public static String getWx() {
        return airport.fields.get("wx");
    }

    public static String getVis() {
        return airport.fields.get("vis");
    }

    public static String[] getTafTime() {
        return new String[]{airport.fields.get("fcst_from"), airport.fields.get("fcst_to")};
    }

    public static String getRawMetar() {
        return airport.fields.get("raw");
    }

    public static String getTime() {
        return airport.fields.get("time");
    }

    public static String[] getWind() {
        return new String[]{airport.fields.get("wind_dir"), airport.fields.get("wind_spd"), airport.fields.get("wind_gst")};
    }

    public static String getSky() {
        return airport.fields.get("sky");
    }


    public static void setMetar(String name) {
        airport = new WeatherHolder();
        airport.fields.put("name", name);
        Document locDoc = getDoc();
        if (locDoc != null) {
            NodeList e = locDoc.getElementsByTagName("METAR");
            for (int i = 0; i < e.getLength(); i++) {
                Node cur = e.item(i);
                if (cur.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) cur;
                    String aName = el.getElementsByTagName("station_id").item(0).getTextContent();
                    if (name.equalsIgnoreCase(aName)) {

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
                                if (!s.equals("raw")) {
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


    public static String getPa() {
        try {
            String px = airport.fields.get("px_in");
            if (px.equals(""))
                px = UNITS.convertToIn(airport.fields.get("px_mb"));
            double std = 29.92;
            double alt = Double.parseDouble(AirportData.getAlt(airport.fields.get("name")));
            double p = Double.parseDouble(px);


            int res = (int) (((std - p) * 1000) + alt);
            return res + "' PA";
        } catch (NumberFormatException | NullPointerException e) {
            return "";
        }
    }

    public static String getDa() {
        try {
            String temp = airport.fields.get("temp_c");
            String dp = airport.fields.get("dp_c");
            String px = airport.fields.get("px_in");
            if (px.equals(""))
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
        } catch (NumberFormatException | NullPointerException e) {
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

    public static String getChange() { return airport.fields.get("type");
    }


    public static class WeatherHolder {
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
            put("px_mb", "");
            put("cat", "");
            put("raw", "");
            put("name", "");
            put("id", "");
            put("time", "");
            put("fcst_to", "");
            put("fcst_from", "");
            put("type", "FM");
            put("lat", "");
            put("long", "");
            put("px_chng", "");
            put("maxT", "");
            put("minT", "");
            put("maxT24", "");
            put("minT24", "");
            put("precip", "");
            put("pcp3", "");
            put("pcp6", "");
            put("pcp24", "");
            put("snow", "");
            put("vv", "");

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
            put("lat", "latitude");
            put("long", "longitude");
            put("px_chng", "three_hr_pressure_tendency_mb");
            put("maxT", "maxT_c");
            put("minT", "minT_c");
            put("maxT24", "maxT24hr_c");
            put("minT24", "minT24hr_c");
            put("precip", "precip_in");
            put("pcp3", "pcp3hr_in");
            put("pcp6", "pcp6hr_in");
            put("pcp24", "pcp24_in");
            put("snow", "snow_in");
            put("vv", "vert_vis_ft");

        }};

        public WeatherHolder() {
        }

    }


}
