package com.example.weathertab.Formatter;

import com.example.weathertab.UNITS;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public  class TafFormatter implements WXFormatter{

   public TafFormatter(){

   }

   public String getWind(String dir, String spd, String gust){

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
         dir += "\u00B0 at ";

      if (!gust.isEmpty())
         gust = " gusting " + gust + " kts";


      return dir + spd + " kts "+ gust;
   }


   public String getVis(String vis){

      String res = vis;
      if(!res.equals("") && UNITS.DIST == UNITS.NM)
         res+= " NM";
      else if(!res.isEmpty() && UNITS.DIST == UNITS.KM)
         res = UNITS.convertToKm(res) + " KM";
      else
         res = res + " sm";

      return  "Visibility : "+ res;

   }
   public String getTafTime(String[] input, String type) {
      String from = input[0];
      String to = input[1];
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      DateFormat dft = new SimpleDateFormat("HH:mm' 'MM/dd");
      DateFormat simple = new SimpleDateFormat("HH:mm' /'dd");
      df.setTimeZone(TimeZone.getTimeZone("UTC"));
      Date d = new Date();  // From your code above
      Date n = new Date();

      try {
         d = df.parse(from);
         n = df.parse(to);

      } catch (ParseException e) {
         e.printStackTrace();
      }

      String change = wxString.get(type).toString();

      return change +": " + simple.format(d) + " until " + simple.format(n);
   }

}
