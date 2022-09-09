package com.example.weathertab;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.weathertab.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.opencsv.CSVReader;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity  {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    NavController navController;


    SimpleCursorAdapter searchAdapter;
    SharedPreferences preferences;
    SearchView searchView;
    List<String> airports;
    static Context context;
    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }
    public static Context getContext(){
        return context;
    }

    private FragmentRefreshListener fragmentRefreshListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_first);
        context = this.getApplicationContext();
        airports = new ArrayList<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);


        searchView = findViewById(R.id.search_view);


         navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);




        final String[] from = new String[] {"id","cityName", "country"};
        final int[] to = new int[] {R.id.airport_id,R.id.stringName, R.id.country};
        searchAdapter = new SimpleCursorAdapter(this,
                R.layout.search_item,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //searchAdapter.setDropDownViewResource(R.color.white);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.unit_menu);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(navController.getCurrentDestination().getId() == R.id.FirstFragment)
                    navController.navigate(R.id.action_FirstFragment_to_Units);
                else if(navController.getCurrentDestination().getId() == R.id.AirportDetail)
                    navController.navigate(R.id.action_Detail_to_UnitSelector);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        searchView.setSuggestionsAdapter(searchAdapter);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }
            @Override
            public boolean onSuggestionClick(int i) {
                Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(i);
                String clicked = cursor.getString(1);
                searchView.setQuery("",false);
                airports.clear();
                String set = preferences.getString("airports","[KPNS]");
                Gson gson = new Gson();
                airports = gson.fromJson(set, ArrayList.class);
                getSupportActionBar().setTitle("Stations");

                searchView.setIconified(true);
                if(!airports.contains(clicked))
                    airports.add(clicked);

                for(String s: airports)
                    System.out.println(s);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor edit = prefs.edit();
                String map = gson.toJson(airports);
                edit.putString("airports",map);
                edit.commit();
                getFragmentRefreshListener().onRefresh();

                return true;
            }
        });
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populate(s);
                return false;
            }
        });

        return true;
    }

    private void populate(String s) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "id","cityName","country" });
        if(s != null && s.length() >0) {

            List<Integer> res = AirportData.find(s);
            for(int i : res){
                String id = AirportData.getID(i);
                Locale loc = new Locale("",AirportData.getLoc(id));
                c.addRow(new Object[]{i, AirportData.getID(i), AirportData.getCity(id),loc.getDisplayCountry()});
            }
        }
            searchAdapter.changeCursor(c);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);


        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public interface FragmentRefreshListener{
        void onRefresh();
    }

}