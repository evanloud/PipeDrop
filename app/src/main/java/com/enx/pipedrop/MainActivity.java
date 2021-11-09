package com.enx.pipedrop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    static final double airMW = 28.97;
    static final double ammMW = 17.03;

    private EditText orificeTemp;
    private EditText orificePressure;
    private EditText orificeDensity;
    private EditText orificeViscosity;
    private EditText ammoniaConc;
    private Spinner orificePipeDiameter;
    private EditText orificeFlowRate;
    private EditText orificeDP;
    private EditText orificeDiameter;
    private Button calcFlowRate, calcDP, calcOrificeDiameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orificeTemp = (EditText) findViewById(R.id.orificeTemp);
        orificePressure = (EditText) findViewById(R.id.orificePressure);
        ammoniaConc = (EditText) findViewById(R.id.ammoniaConc);
        orificeDensity = (EditText) findViewById(R.id.orificeDensity);
        orificeViscosity = (EditText) findViewById(R.id.orificeViscosity);
        orificePipeDiameter = (Spinner) findViewById(R.id.orificePipeDiameter);
        orificePipeDiameter.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pipeSizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orificePipeDiameter.setAdapter(adapter);
        orificeFlowRate = (EditText) findViewById(R.id.orificeFlowRate);
        orificeDP = (EditText) findViewById(R.id.orificeDP);
        orificeDiameter = (EditText) findViewById(R.id.orificeDiameter);
        calcFlowRate = (Button) findViewById(R.id.calcFlowRate);
        calcFlowRate.setOnClickListener(this::onFlowRateClick);
        calcDP = (Button) findViewById(R.id.calcDP);
        calcDP.setOnClickListener(this::onDPClick);
        calcOrificeDiameter = (Button) findViewById(R.id.calcOrificeDiameter);
        calcOrificeDiameter.setOnClickListener(this::onDiameterClick);

    }

    @Override
    public void onItemSelected(AdapterView arg0, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView arg0) {

    }

    public void onFlowRateClick(View view) {

        boolean err = false;

        if (orificeTemp.getText().toString().length() == 0) {
            orificeTemp.setError("Enter a temperature");
            err = true;
            orificeTemp.setText("");
        }
        if (orificePressure.getText().toString().length() == 0) {
            orificePressure.setError("Enter a pressure");
            err = true;
            orificePressure.setText("");
        }
        if (ammoniaConc.getText().toString().length() == 0) {
            ammoniaConc.setError("Enter ammonia concentration");
            err = true;
            ammoniaConc.setText("");
        }
        if (pipeHelper(orificePipeDiameter.getSelectedItem().toString()) <
                Double.parseDouble(orificeDiameter.getText().toString())) {
            orificeDiameter.setError("Orifice is larger than pipe");
            err = true;
        }

        if (!err) {

            double temp = Double.parseDouble(orificeTemp.getText().toString());
            double pressure = Double.parseDouble(orificePressure.getText().toString());
            double ammoniaConcentration = Double.parseDouble(ammoniaConc.getText().toString());
            double density = density(ammoniaConcentration,temp,pressure);
            orificeDensity.setText(String.format("%.4f",density));
            double viscosity = mixtureViscosity(ammoniaConcentration,temp);
            orificeViscosity.setText(String.format("%.4f",viscosity));
            double pipeID = pipeHelper(orificePipeDiameter.getSelectedItem().toString());
            double dp = Double.parseDouble(orificeDP.getText().toString());
            double diameter = Double.parseDouble(orificeDiameter.getText().toString());

            double beta = diameter / pipeID;
            double flowRate = 1.0;
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

            orificeFlowRate.setText(String.format("%.2f", flowRate));
        }

    }

    public void onDPClick(View view) {

        boolean err = false;

        if (orificeTemp.getText().toString().length() == 0) {
            orificeTemp.setError("Enter a temperature");
            err = true;
            orificeTemp.setText("");
        }
        if (orificePressure.getText().toString().length() == 0) {
            orificePressure.setError("Enter a pressure");
            err = true;
            orificePressure.setText("");
        }
        if (ammoniaConc.getText().toString().length() == 0) {
            ammoniaConc.setError("Enter ammonia concentration");
            err = true;
            ammoniaConc.setText("");
        }
        if (pipeHelper(orificePipeDiameter.getSelectedItem().toString()) <
                Double.parseDouble(orificeDiameter.getText().toString())) {
            orificeDiameter.setError("Orifice is larger than pipe");
            err = true;
        }

        if (!err) {

            double temp = Double.parseDouble(orificeTemp.getText().toString());
            double pressure = Double.parseDouble(orificePressure.getText().toString());
            double ammoniaConcentration = Double.parseDouble(ammoniaConc.getText().toString());
            double density = density(ammoniaConcentration,temp,pressure);
            orificeDensity.setText(String.format("%.4f",density));
            double viscosity = mixtureViscosity(ammoniaConcentration,temp);
            orificeViscosity.setText(String.format("%.4f",viscosity));
            double pipeID = pipeHelper(orificePipeDiameter.getSelectedItem().toString());
            double flowRate = Double.parseDouble(orificeFlowRate.getText().toString());
            double diameter = Double.parseDouble(orificeDiameter.getText().toString());


            double beta = diameter / pipeID;
            double ReD = reynoldsNumber(flowRate,pipeID,viscosity);
            double Cd = dischargeCoeff(beta, ReD);
            double dp = pressureDrop(beta, flowRate, Cd, diameter, density);

            orificeDP.setText(String.format("%.2f",dp));
        }
    }

    public void onDiameterClick(View view) {

        boolean err = false;

        if (orificeTemp.getText().toString().length() == 0) {
            orificeTemp.setError("Enter a temperature");
            err = true;
            orificeTemp.setText("");
        }
        if (orificePressure.getText().toString().length() == 0) {
            orificePressure.setError("Enter a pressure");
            err = true;
            orificePressure.setText("");
        }
        if (ammoniaConc.getText().toString().length() == 0) {
            ammoniaConc.setError("Enter ammonia concentration");
            err = true;
            ammoniaConc.setText("");
        }


        if (!err) {

            double temp = Double.parseDouble(orificeTemp.getText().toString());
            double pressure = Double.parseDouble(orificePressure.getText().toString());
            double ammoniaConcentration = Double.parseDouble(ammoniaConc.getText().toString());
            double density = density(ammoniaConcentration,temp,pressure);
            orificeDensity.setText(String.format("%.4f",density));
            double viscosity = mixtureViscosity(ammoniaConcentration,temp);
            orificeViscosity.setText(String.format("%.4f",viscosity));
            double pipeID = pipeHelper(orificePipeDiameter.getSelectedItem().toString());
            double dp = Double.parseDouble(orificeDP.getText().toString());
            double flowRate = Double.parseDouble(orificeFlowRate.getText().toString());

            double diameter = pipeID / 2;
            double beta;
            double ReD = reynoldsNumber(flowRate, pipeID, viscosity);
            double Cd;
            double prevDiameter;

            while (true) {
                prevDiameter = diameter;
                beta = diameter / pipeID;
                Cd = dischargeCoeff(beta, ReD);
                diameter = Math.pow(0.5 * Math.pow((4.0 * flowRate / 3600.0) * Math.sqrt(1 - Math.pow(beta,4)) /
                        (Cd * 22.0 / 7.0), 2) /
                        (density * dp / 27.7076 * Math.pow(1.0/12,3) * 32.2 * 12), (0.25));
                if (Math.abs((diameter-prevDiameter)/diameter) < 0.0001) break;
            }

            orificeDiameter.setText(String.format("%.2f", diameter));
        }
    }

    public static double dischargeCoeff(double beta, double ReD) {
        double Cd = (0.5961 + (0.0261 * Math.pow(beta,2)) + (-0.216 * Math.pow(beta,8)) +
                (0.000521 * Math.pow(1000000 * beta / ReD, 0.7)) +
                ((0.0188 + 0.0063 * Math.pow(19000*beta/ReD,0.8)) * Math.pow(beta,3.5) * Math.pow(1000000/ReD,0.3)) +
                (0.043+0.08*Math.exp(-10*1)-0.123*Math.exp(-7*1)) *
                        (1-0.11*Math.pow(19000*beta/ReD,0.8)) *Math.pow(beta,4)/(1-Math.pow(beta,4)) +
                (-0.031 * (2 * 0.47/(1-beta) - 0.8*Math.pow(2 * 0.47/(1-beta),1.1))*Math.pow(beta,1.3)));
        return Cd;
    }

    public static double reynoldsNumber(double flowRate, double pipeID, double viscosity) {
        // flowRate in lb/hr, pipeID in inches, viscosity in cP

        double ReD;
        ReD = (4.0 * flowRate * (1.0/3600.0) / (22.0/7 * (pipeID / 12.0) * (viscosity / 47880.26 * 32.2)));
        return ReD;
    }

    public static double pressureDrop (double beta, double flowRate, double Cd, double diameter, double density) {
        // flowRate in lbm/hr, diameter in inches, density in lbm/ft^3
        double dp;
        dp = 8.0 * (1.0 - Math.pow(beta,4)) * Math.pow(flowRate / 3600.0 / (Cd * 22.0 / 7.0 * Math.pow(diameter,2)),2) /
                (density * Math.pow(1.0/12.0,3)) * (1.0 / (32.2 * 12.0)) * 27.7076;
        return dp;
    }

    public static double density(double ammoniaConc, double temperature, double pressure) {
        double dens;
        double MW;

        MW = molecularWeight(ammoniaConc);
        dens = MW / (33411.0 * ((temperature - 32.0) * 5.0 / 9.0 + 273.15) / (pressure + 407.19)) *
                0.0022046 * 28316.98028;
        return dens;
    }


    public static double molecularWeight(double ammoniaConc) {
        double ammMoleConc;
        double MW;

        ammMoleConc = ammMolePercent(ammoniaConc);
        MW = ammMoleConc * ammMW + (1 - ammMoleConc) * airMW;
        return MW;
    }

    public static double ammMolePercent(double ammoniaConc) {
        double ammMolePercent;
        ammoniaConc = ammoniaConc / 100;
        ammMolePercent = ( (ammoniaConc) / ammMW ) / ( ((1 - ammoniaConc) / airMW) + ((ammoniaConc) / ammMW ));
        return ammMolePercent;
    }

    public static double omega(double temperature) {
        double lj2, kte, omega;

        lj2 = 97.0;
        kte = (5.0/9.0) * (temperature + 459.67) / lj2;
        if (kte > 5) {
            omega = 1.2144 * Math.pow(kte,-0.169);
        } else if (kte > 2) {
            omega = 1.3802 * Math.pow(kte,-0.2529);
        } else if (kte  > 1) {
            omega = 1.5728 * Math.pow(kte, -0.4319);
        } else {
            omega = 1.6026 * Math.pow(kte,-0.4774);
        }

        return omega;
    }

    public static double individualViscosity(double MW, double omega, double temp) {
        double indViscosity = 0.00002663 * Math.sqrt(MW * 5.0/9.0 * (temp + 459.67)) /
                (Math.pow(3.617,2) * omega);
        return indViscosity;
    }

    public static double phi_ab(double MWa, double MWb, double indViscA, double indViscB) {
        double phi;
        phi = 1.0 / Math.sqrt(8) / Math.sqrt(1 + MWa/MWb) *
                Math.pow(1 + Math.pow(indViscA/indViscB, 0.5)*Math.pow(MWb/MWa, 0.25),2);
        return phi;
    }

    public static double mixtureViscosity(double ammoniaConc, double temp) {

        double ammPercent, airPercent;
        double ammVisc, airVisc;
        double ammViscPartial, airViscPartial;
        double imf_aa, imf_an, imf_nn, imf_na;
        double ammIMF, airIMF;
        double mixViscosity;

        // mole %
        ammPercent = ammMolePercent(ammoniaConc);
        airPercent = 1.0 - ammPercent;

        //individual viscosity
        ammVisc = individualViscosity(ammMW, omega(temp), temp);
        airVisc = individualViscosity(airMW, omega(temp), temp);

        //imf
        // air-air
        imf_aa = phi_ab(airMW,airMW,airVisc,airVisc);
        //air-nh3
        imf_an = phi_ab(airMW, ammMW, airVisc, ammVisc);
        //nh3-nh3
        imf_nn = phi_ab(ammMW,ammMW, ammVisc, ammVisc);
        //nh3-air
        imf_na = phi_ab(ammMW, airMW, ammVisc, airVisc);

        // ammonia partial viscosity
        ammViscPartial = ammVisc * ammPercent;
        // ammonia sum of imf
        ammIMF = airPercent * imf_na + ammPercent * imf_nn;
        // air partial viscosity
        airViscPartial = airVisc * airPercent;
        // air sum of imf
        airIMF = airPercent * imf_aa + ammPercent * imf_an;

        mixViscosity = (ammViscPartial/ammIMF) + (airViscPartial/airIMF);



        return mixViscosity*100;

    }

    public static double pipeHelper(String pipeSize) {

        double pipeID;

        switch(pipeSize) {
            case "1 inch":
                pipeID = 1.049;
                break;
            case "2 inch":
                pipeID = 2.067;
                break;
            case "2.5 inch":
                pipeID = 2.469;
                break;
            case "3 inch":
                pipeID = 3.068;
                break;
            case "4 inch":
                pipeID = 4.026;
                break;
            case "5 inch":
                pipeID = 5.047;
                break;
            case "6 inch":
                pipeID = 6.065;
                break;
            case "7 inch":
                pipeID = 7.023;
                break;
            case "8 inch":
                pipeID = 7.981;
                break;
            case "9 inch":
                pipeID = 8.941;
                break;
            case "10 inch":
                pipeID = 10.02;
                break;
            case "11 inch":
                pipeID = 11.0;
                break;
            case "12 inch":
                pipeID = 12.0;
                break;
            default:
                pipeID = 3;
                break;
        }

        return pipeID;
    }
}