package com.enx.pipedrop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class Manual extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyRecyclerViewAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        ArrayList<String> displayText = new ArrayList<>();
        displayText.add("<b>Welcome to PipeDrop.</b>");
        displayText.add("This app is designed to calculate flow through an orifice plate on-the-go.");
        displayText.add("<b>Orifice Calculations</b>");
        displayText.add(
                "Once in the orifice calculator screen, enter the header temperature and pressure, " +
                "then enter the ammonia concentration as an integer (e.g. 19 for 19%).  Density " +
                "and viscosity will be calculated based on these inputs, and can be left blank.  " +
                "Next, select your pipe size from the drop down menu.");
        displayText.add("To complete the calculation, fill in the two relevant data fields in the " +
                "<b>Calculate</b> section, and press the appropriate calculate button.");
        displayText.add("<b>PPM Calculations</b>");
        displayText.add(
                "This feature is similar to the orifice calculator, but can be used to approximate " +
                "the concentration of ammonia through a zone based on ammonia flow and exhaust flow.  " +
                "PPM calculations are made assuming a 25:1 dilution air to ammonia dilution rate " +
                "and are not exact, but give a reasonable approximation.");

        recyclerView = findViewById(R.id.manualRecycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter = new MyRecyclerViewAdapter(this,displayText);
        recyclerView.setAdapter(myAdapter);

    }
}