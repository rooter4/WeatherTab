package com.example.weathertab.Formatter;

import android.graphics.Color;

import com.example.weathertab.R;
import com.example.weathertab.UNITS;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MetarFormatter  implements WXFormatter{

    int color;
    public MetarFormatter(){
        color = R.color.error;
    }

    public String getVis(String vis){

        String res = vis;
        if(!res.equals("") && UNITS.DIST == UNITS.NM)
            res+= " NM";
        else if(!res.isEmpty() && UNITS.DIST == UNITS.KM)
            res = UNITS.convertToKm(res) + " KM";
        else
            res = res + " sm";

        return res;

    }
    public  String getWind(String dir, String spd, String gust) {


        if (dir.isEmpty())
            return "";
        if (dir.length() == 2)
            dir = "0" + dir;

        if (spd.equals("0") && dir.equals("0")) {
            dir = "CALM";
            spd = "";
        } else if (dir.equals("0")) {
            dir = "VRB\n";
            spd += " kts";
        }
        else {
            dir += "\u00B0 \n";
            spd += " kts";
        }

        if (!gust.isEmpty())
            gust = "\nG " + gust + " kts";

        return dir + spd  + gust;

    }

  public String getMetar(String raw){

      String[] sep = {"FM", "BM", "PROB", "TEMPO", "BECMG", "PROB30", "PROB40"};
      String res = raw;
      String fin = "";
      String finFin = "";
      for (String s : sep) {
          while (res.toUpperCase().contains(s.toUpperCase())) {
              System.out.println(s);
              fin = (res.substring(res.toUpperCase().lastIndexOf(s.toUpperCase())) + "\n" + fin);
              res = res.substring(0, res.toUpperCase().lastIndexOf(s.toUpperCase()));
          }
      }
      for (String s : sep) {
          while (fin.toUpperCase().contains(s.toUpperCase())) {
              finFin = fin.substring(fin.toUpperCase().lastIndexOf(s.toUpperCase())) + "\n" + finFin;
              fin = fin.substring(0, fin.toUpperCase().lastIndexOf(s.toUpperCase()));
          }
      }


      return res + "\n" + fin + finFin;
  }

    public String getTime(String temp) {
        int time = 999;

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
        if(time<60)
            color = R.color.VFR;
        else
            color = R.color.error;
        return String.valueOf(time)+ " mins";
    }
    public int getColor(){
        return color;
    }
    public  String getTemp_Dp(String temp, String dp) {
        if (UNITS.TEMP.equals(UNITS.temp_f))
            return UNITS.convertToF(temp) + "\u00B0F" + "\n" + UNITS.convertToF(dp) + "\u00B0F";
        return temp + "\u00B0C" + "\n" + dp+ "\u00B0C";
    }

}
