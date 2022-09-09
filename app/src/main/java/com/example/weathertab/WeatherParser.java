package com.example.weathertab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class WeatherParser {
    public final HashMap OBSCURATION = new HashMap() {{


        put("FG", "Fog");
        put("BR", "Mist");
        put("HZ", "Haze");
        put("VA", "Volcanic Ash");
        put("DU", "Dust");
        put("FU", "Smoke");
        put("SA", "Sand");
        put("SY", "Spray");

    }};
    private final HashMap BADSTUFF = new HashMap() {{
        put("PO", "Dust Whirls");
        put("DS", "Duststorm");
        put("SS", "Sandstorm");
        put("FC", "Funnel Cloud");
        put("+FC", "Funnel Cloud");
        put("SQ", "Squall");
    }};
    private final HashMap QUALIFIER = new HashMap() {{
        put("BL", "Blowing");
        put("PR", "Partial");
        put("DR", "Low Drifting");
        put("TS", "Thunderstorms");
        put("SH", "Showers");
        put("FZ", "Freezing");
        put("MI", "Shallow");
        put("BC", "Patchy");
    }};
    private final HashMap PRECIP = new HashMap() {{
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

    private final HashMap INTENSITY = new HashMap() {{
        put("+", "Heavy");
        put("-", "Light");
        put("VC", "in the vicinity");
        put("DSNT", "Distant");

    }};

    private final HashMap wxString = new HashMap() {{
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
    }};
    String urlString = "https://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&hoursBeforeNow=36&mostRecentForEachStation=true&stationString=";
    String tafUrl = "https://www.aviationweather.gov/adds/dataserver_current/httpparam?datasource=tafs&requestType=retrieve&format=xml&mostRecentForEachStation=true&hoursBeforeNow=24&stationString=";
    List<String> stationIDs;
    List<String> data;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    Context context;
    Document doc;


    WeatherParser(Context context) {
        data = new ArrayList<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        prefEditor = preferences.edit();
        this.context = context;
        doc = null;


    }


    public Document getTafDoc() {
        Document loc = null;
        if (loc == null) {
            try {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();

                File f = new File(path + "/taf.xml");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                loc = builder.parse(f);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return loc;
    }

    public boolean Refresh(SmallRVAdapter smallRVAdapter, List<String> iData, Handler handler) {
        this.data = iData;


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String icao = "";
                    for (String s : data)
                        icao += s + "+";

                    System.out.println(icao);
                    URL taf = new URL(tafUrl + icao);


                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();

                    HttpURLConnection conn = (HttpURLConnection) taf.openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                    System.out.println("TAF RESPONSE:  " + conn.getResponseCode());
                    conn.connect();

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    InputStream stream = conn.getInputStream();

                    //TAF DOC
                    Document tafDoc;
                    tafDoc = builder.parse(conn.getInputStream());
                    tafDoc.getDocumentElement().normalize();

                    File f = new File(path + "/taf.xml");
                    DOMSource source = new DOMSource(tafDoc);
                    FileWriter writer = new FileWriter(f);
                    StreamResult result = new StreamResult(writer);

                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.transform(source, result);

                    smallRVAdapter.updateData(data);
                    handler.sendEmptyMessage(1);


                } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        return true;
    }





    public boolean hasTaf(String name) {
        Document tafDoc = getTafDoc();
        if (tafDoc != null) {
            NodeList e = tafDoc.getElementsByTagName("TAF");
            for (int i = 0; i < e.getLength(); i++) {
                Node cur = e.item(i);
                if (cur.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) cur;
                    String aName = el.getElementsByTagName("station_id").item(0).getTextContent();
                    if (aName.equals(name)) {
                        String date = el.getElementsByTagName("valid_time_to").item(0).getTextContent();
                        System.out.println(date);
                        return isValid(date);
                    }
                }
            }
        }
        return false;
    }

    public String getTaf(String name) {
        Document tafDoc = getTafDoc();
        String res = "none";
        if (hasTaf(name)) {
            {
                NodeList e = tafDoc.getElementsByTagName("TAF");
                for (int i = 0; i < e.getLength(); i++) {
                    Node cur = e.item(i);
                    if (cur.getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element) cur;
                        String aName = el.getElementsByTagName("station_id").item(0).getTextContent();
                        if (aName.equals(name)) {


                            Transformer transformer = null;
                            try {
                                transformer = TransformerFactory.newInstance().newTransformer();
                            } catch (TransformerConfigurationException exception) {
                                exception.printStackTrace();
                            }
                            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
// initialize StreamResult with File object to save to file
                            StreamResult result = new StreamResult(new StringWriter());
                            DOMSource source = new DOMSource(el);
                            try {
                                transformer.transform(source, result);
                            } catch (TransformerException exception) {
                                exception.printStackTrace();
                            }
                            res = result.getWriter().toString();

                        }
                    }

                }
            }
        }

        return res;
    }

    public int getTafSize(String name) {
        Document tafDoc = getTafDoc();
        int size = 0;
        if (hasTaf(name)) {
            NodeList e = tafDoc.getElementsByTagName("TAF");
            for (int i = 0; i < e.getLength(); i++) {
                Node cur = e.item(i);
                if (cur.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) cur;
                    String aName = el.getElementsByTagName("station_id").item(0).getTextContent();
                    if (aName.equals(name)) {
                        NodeList s = el.getElementsByTagName("forecast");
                        size = s.getLength();

                    }
                }
            }
        }
        return size;
    }
    private boolean isValid(String time){
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date d = df.parse(time);
            Date now = new Date();
            //System.out.println(now.toString() + " : " + d.toString());
            if(now.before(d))
                return true;


        } catch (ParseException e) {
            return false;
        }
        return false;


    }

    public String getTafTime(String name, int index) {

        String from = getTafTag(name, "fcst_time_from", index, "N/A");
        String to = getTafTag(name, "fcst_time_to", index, "N/A");

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


        return getTafType(name, index) + ": " + dft.format(d) + " until " + dft.format(n);
    }





    private String getTafTag(String name, String attr, int index, String def) {
        String res = def;
        Document tafDoc = getTafDoc();
        if (hasTaf(name)) {
            {
                NodeList e = tafDoc.getElementsByTagName("TAF");
                for (int i = 0; i < e.getLength(); i++) {
                    Node cur = e.item(i);
                    if (cur.getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element) cur;
                        String aName = el.getElementsByTagName("station_id").item(0).getTextContent();
                        if (aName.equals(name)) {
                            try {
                                NodeList s = el.getElementsByTagName("forecast");
                                Element r = (Element) s.item(index);

                                res = r.getElementsByTagName(attr).item(0).getTextContent();
                            } catch (NullPointerException exception) {

                            }
                        }
                    }
                }
            }
        }
        return res;
    }



    public String getTafType(String name, int position) {
        String res = getTafTag(name, "change_indicator", position, "FM");
        res = (String) wxString.get(res);
        return res;
    }


}
