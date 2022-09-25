package com.example.weathertab;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weathertab.databinding.FragmentFirstBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    RecyclerView rv;
    SmallRVAdapter adapter;
    SharedPreferences preferences;
    List<String> data;
    private FloatingActionButton refresh;
    Handler handler = null;
    SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(container.getContext());
        data = new ArrayList<>();
        String set = preferences.getString("airports","[KPNS]");
        Gson gson = new Gson();
        data = gson.fromJson(set, ArrayList.class);
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        WXParser.init(getContext());
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                adapter.updateData(data);
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
                super.handleMessage(msg);
            }


        };


        ((MainActivity)getActivity()).setFragmentRefreshListener(new MainActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh() {

                data.clear();
                String set = preferences.getString("airports","KPNS");
                Gson gson = new Gson();
                data = gson.fromJson(set, ArrayList.class);
                refresh(getView());

            }
        });


        return binding.getRoot();

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.airport_recycler);
        refresh = view.findViewById(R.id.refresh);
        DividerItemDecoration decoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
       rv.addItemDecoration(decoration);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        rv.setLayoutManager(llm);
        refreshLayout = view.findViewById(R.id.swipeRefreshLayout);


        adapter = new SmallRVAdapter(view.getContext(),data,true);

        ItemTouchHelper ith = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    System.out.println("touched");
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE )
                {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;
                    int height = itemView.getHeight();
                    int width = itemView.getWidth();
                    int top = itemView.getTop();
                    int bottom = itemView.getBottom();

                    Paint p = new Paint();
                    if (dX > 0) {
                        /* Set your color for positive displacement */
                        p.setColor(ContextCompat.getColor(getContext(),R.color.error));
                        // Draw Rect with varying right side, equal to displacement dX
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);
                        Drawable d = getContext().getDrawable(R.drawable.delete_48px);

                        d.setBounds(itemView.getLeft(), top + height/3, itemView.getLeft()+ width/10,bottom - height/3);
                        d.draw(c);

                    } else {
                        /* Set your color for negative displacement */
                        p.setColor(ContextCompat.getColor(getContext(), R.color.error));
                        // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);
                        Drawable d = getContext().getDrawable(R.drawable.delete_48px);

                        d.setBounds(itemView.getRight() - width/10 , top + height / 3, itemView.getRight() , bottom - height / 3);
                        d.draw(c);
                    }

                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // get the viewHolder's and target's positions in your adapter data, swap them
                Collections.swap(data, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                updateData();
                // and notify the adapter that its dataset has changed
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                System.out.println("remove");
                int position = viewHolder.getAdapterPosition();
                String removed= data.remove(position);
                updateData();
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                // below line is to display our snackbar with action.
                Snackbar.make(rv, removed + " removed", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.add(position, removed);
                        adapter.notifyItemInserted(position);
                        updateData();
                    }
                }).show();

            }
        });


        ith.attachToRecyclerView(rv);
        rv.setAdapter(adapter);



        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
                WXParser.Refresh(handler);
                refreshLayout.setRefreshing(true);


            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
                WXParser.Refresh(handler);



            }

        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void refresh(View view){
       adapter.updateData( data);
    }
    public void updateData(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor edit = prefs.edit();
        Gson gson = new Gson();
        String map = gson.toJson(data);
        edit.putString("airports",map);
        edit.commit();
    }

}