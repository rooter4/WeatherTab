package com.example.weathertab.Formatter;

import com.example.weathertab.UNITS;

import java.util.HashMap;

public interface  WXFormatter {

   HashMap OBSCURATION = new HashMap() {{
    put("FG", "Fog");
    put("BR", "Mist");
    put("HZ", "Haze");
    put("VA", "Volcanic Ash");
    put("DU", "Dust");
    put("FU", "Smoke");
    put("SA", "Sand");
    put("SY", "Spray");

  }};
  HashMap<String, String> COMMON = new HashMap() {{
    put("wind_dir", "Wind Direction:");
    put("wind_spd", "Wind Speed (kts):");
    put("wind_gst", "Wind Gust:");
    put("vis", "Visibility:");
    put("wx", "Weather: ");
    put("sky", "Sky Cover: ");
    put("cat", "Current Flight Condition: ");
    put("temp_c", "Temperature: ");
    put("dp_c", "Dewpoint: ");
    put("px_in", "Pressure (inHg): ");
    put("px_mb", "Sea Level Pressure (mb): ");;
    put("lat","Latitude: ");
    put("long","Longitude: ");
    put("px_chng","Pressure Change (mb) ~3hrs: ");
    put("maxT","Max Temp: ");
    put("minT","Min Temp: ");
    put("maxT24","Max Observed Temp ~24hrs: ");
    put("minT24","Min Observed Temo ~24hrs: ");
    put("precip","Precipitaion Amount: ");
    put("pcp3","Precipitation Amount ~3hrs (in): ");
    put("pcp6","Precipitation Amount ~6hrs (in): ");
    put("pcp24","Precipitation Amount  ~24hrs (in): ");
    put("snow","Snowfall (in):");
    put("vv","Vertical Visibility (ft): ");

  }};
   HashMap BADSTUFF = new HashMap() {{
    put("PO", "Dust Whirls");
    put("DS", "Duststorm");
    put("SS", "Sandstorm");
    put("FC", "Funnel Cloud");
    put("+FC", "Funnel Cloud");
    put("SQ", "Squall");
  }};
   HashMap QUALIFIER = new HashMap() {{
    put("BL", "Blowing");
    put("PR", "Partial");
    put("DR", "Low Drifting");
    put("TS", "Thunderstorms");
    put("SH", "Showers");
    put("FZ", "Freezing");
    put("MI", "Shallow");
    put("BC", "Patchy");
  }};
  HashMap PRECIP = new HashMap() {{
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
  HashMap INTENSITY = new HashMap() {{
    put("+", "Heavy");
    put("-", "Light");
    put("VC", "in the vicinity");
    put("DSNT", "Distant");

  }};
  HashMap wxString = new HashMap() {{
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

  String getWind(String dir, String spd, String gust);
  String getVis(String vis);
  default String getSky(String sky){
    return sky;
  }
  default String getWX(String res){

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
    default String getPx(String mb, String in) {
        String res = "";
        if (UNITS.PX == UNITS.px_mb) {
            res = mb;
            if (res.equals("")) {
                res = UNITS.convertToMb(in);
            }
        } else if (UNITS.PX == UNITS.px_in) {
            res = in;
        }
        if (res.length() > 5)
            res = res.substring(0, 5);

        return res;
    }


}
