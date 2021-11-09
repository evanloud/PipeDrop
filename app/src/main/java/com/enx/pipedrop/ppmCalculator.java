package com.enx.pipedrop;

import static com.enx.pipedrop.MainActivity.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ppmCalculator extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static double dilutionRate = 1.0 / 25.0;

    private EditText ppmOrificeTemp;
    private EditText ppmOrificePressure;
    private EditText ppmAmmoniaConc;
    private EditText numberOfZones;
    private EditText stackOxygen;
    private Spinner ppmPipeDiameter;
    private EditText ppmOrificeDiameter;
    private EditText fuelFlow;
    private EditText dpA;
    private EditText dpB;
    private EditText ppmA;
    private EditText ppmB;

    private Button dpACalc, dpBCalc, ppmACalc, ppmBCalc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppm_calculator);

        ppmOrificeTemp = (EditText) findViewById(R.id.ppmOrificeTemp);
        ppmOrificePressure = (EditText) findViewById(R.id.ppmOrificePressure);
        ppmAmmoniaConc = (EditText) findViewById(R.id.ppmAmmoniaConc);
        numberOfZones = (EditText) findViewById(R.id.numberOfZones);
        stackOxygen = (EditText) findViewById(R.id.stackOxygen);
        ppmPipeDiameter = (Spinner) findViewById(R.id.ppmPipeDiameter);
        ppmPipeDiameter.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pipeSizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ppmPipeDiameter.setAdapter(adapter);
        ppmOrificeDiameter = (EditText) findViewById(R.id.ppmOrificeDiameter);
        fuelFlow = (EditText) findViewById(R.id.fuelFlow);
        dpA = (EditText) findViewById(R.id.dpA);
        dpB = (EditText) findViewById(R.id.dpB);
        ppmA = (EditText) findViewById(R.id.ppmA);
        ppmB = (EditText) findViewById(R.id.ppmB);

        dpACalc = (Button) findViewById(R.id.dpACalcBtn);
        dpACalc.setOnClickListener(this::onDPClick);
        dpBCalc = (Button) findViewById(R.id.dpBCalcBtn);
        dpBCalc.setOnClickListener(this::onDPClick);
        ppmACalc = (Button) findViewById(R.id.ppmACalcBtn);
        ppmACalc.setOnClickListener(this::onPpmClick);
        ppmBCalc = (Button) findViewById(R.id.ppmBCalcBtn);
        ppmBCalc.setOnClickListener(this::onPpmClick);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onDPClick(View view) {
        boolean err = false;

        if (ppmOrificeTemp.getText().toString().length() == 0) {
            ppmOrificeTemp.setError("Enter a temperature");
            err = true;
            ppmOrificeTemp.setText("");
        }
        if (ppmOrificePressure.getText().toString().length() == 0) {
            ppmOrificePressure.setError("Enter a pressure");
            err = true;
            ppmOrificePressure.setText("");
        }
        if (ppmAmmoniaConc.getText().toString().length() == 0) {
            ppmAmmoniaConc.setError("Enter ammonia concentration");
            err = true;
            ppmAmmoniaConc.setText("");
        }
        if (pipeHelper(ppmPipeDiameter.getSelectedItem().toString()) <
                Double.parseDouble(ppmOrificeDiameter.getText().toString())) {
            ppmOrificeDiameter.setError("Orifice is larger than pipe");
            err = true;
        }

        if (!err) {

            double temp = Double.parseDouble(ppmOrificeTemp.getText().toString());
            double pressure = Double.parseDouble(ppmOrificePressure.getText().toString());
            double ammoniaConcentration = Double.parseDouble(ppmAmmoniaConc.getText().toString());
            double density = density(ammoniaConcentration,temp,pressure);
            double viscosity = mixtureViscosity(ammoniaConcentration,temp);
            double pipeID = pipeHelper(ppmPipeDiameter.getSelectedItem().toString());
            double diameter = Double.parseDouble(ppmOrificeDiameter.getText().toString());
            double fuelFlowRate = Double.parseDouble(fuelFlow.getText().toString());
            double oxygen = Double.parseDouble(stackOxygen.getText().toString());
            double zones = Double.parseDouble(numberOfZones.getText().toString());
            double ppmAmmonia;

            if (view.getId() == R.id.dpACalcBtn) {
                ppmAmmonia = Double.parseDouble(ppmA.getText().toString());
            }
            else {
                ppmAmmonia = Double.parseDouble(ppmB.getText().toString());
            }

            double ammoniaFlowRate = ammoniaFlowFromPPM(ppmAmmonia,
                    exhaustFlow(fuelFlowRate,oxygen) / zones, ammoniaConcentration);

            double beta = diameter / pipeID;
            double ReD = reynoldsNumber(ammoniaFlowRate,pipeID,viscosity);
            double Cd = dischargeCoeff(beta, ReD);
            double dp = pressureDrop(beta, ammoniaFlowRate, Cd, diameter, density);

            if (view.getId() == R.id.dpACalcBtn) {
                dpA.setText(String.format("%.2f",dp));
            }
            else {
                dpB.setText(String.format("%.2f",dp));
            }

        }
    }

    public void onPpmClick(View view) {
        // Calculate the ppm based on flow through an orifice

        double fuelFlowRate = Double.parseDouble(fuelFlow.getText().toString());
        double oxygen = Double.parseDouble(stackOxygen.getText().toString());
        double zones = Double.parseDouble(numberOfZones.getText().toString());
        double temp = Double.parseDouble(ppmOrificeTemp.getText().toString());
        double pressure = Double.parseDouble(ppmOrificePressure.getText().toString());
        double ammoniaConcentration = Double.parseDouble(ppmAmmoniaConc.getText().toString());
        double density = density(ammoniaConcentration,temp,pressure);
        double viscosity = mixtureViscosity(ammoniaConcentration,temp);
        double pipeID = pipeHelper(ppmPipeDiameter.getSelectedItem().toString());
        double diameter = Double.parseDouble(ppmOrificeDiameter.getText().toString());
        double dp;

        if (view.getId() == R.id.ppmACalcBtn) {
            dp = Double.parseDouble(dpA.getText().toString());
        }
        else {
            dp = Double.parseDouble(dpB.getText().toString());
        }

        double flowRate = 1.0;
        double exhaust = exhaustFlow(fuelFlowRate,oxygen) / zones;
        double beta = diameter / pipeID;
        double Cd;
        double ReD;
        double prevFlowRate;

        while (true) {
            prevFlowRate = flowRate;
            ReD = reynoldsNumber(flowRate, pipeID, viscosity);
            Cd = dischargeCoeff(beta, ReD);
            flowRate = Cd * 22.0 / 7.0 * Math.pow(diameter, 2) / (4 * Math.sqrt(1 - Math.pow(beta, 4))) *
                    Math.sqrt(2 * dp / 27.7076 * density * 32.2 / 144) * 3600;
            if (Math.abs((flowRate - prevFlowRate) / flowRate) < 0.0001) break;
        }

        double ppm = ammoniaPPMFromFlow(flowRate,exhaust, ammoniaConcentration);

        if (view.getId() == R.id.ppmACalcBtn) {
            ppmA.setText(String.format("%.1f",ppm));
        }
        else {
            ppmB.setText(String.format("%.1f",ppm));
        }

    }

    public double exhaustFlow(double fuelFlow, double stackOxygen) {
        // fuel flow as NG lb/hr, oxygen as integer %
        double exhaustFlow;

        exhaustFlow = fuelFlow * (379.0 / 16.8) * 1040.0 * 8710.0 / 1000000 * 21.0 / (21.0 - stackOxygen);

        return exhaustFlow;
    }

    public double ammoniaFlowFromPPM(double ppm, double exhaust, double ammoniaConc) {
        double flow;

        flow = ppm * (exhaust / 1000000.0) / (379.0 / ammMW * dilutionRate * ammoniaConc/100.0);

        return flow;
    }

    public double ammoniaPPMFromFlow(double flow, double exhaust, double ammoniaConc) {
        double ppm;

        ppm = flow * dilutionRate * (379.0 / ammMW * ammoniaConc/100.0) / (exhaust / 1000000.0);

        return ppm;
    }
}