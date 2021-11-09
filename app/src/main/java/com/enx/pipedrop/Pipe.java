package com.enx.pipedrop;

import java.io.Serializable;

public class Pipe implements Serializable {

    // attributes
    private String plant;
    private String valve;
    private double temperature;
    private double pressure;
    private double ammoniaConc;
    private double pipeID;
    private double orificeID;

    public Pipe(String plant, String valve, double temperature, double pressure, double ammoniaConc, double pipeID, double orificeID) {
        this.plant = plant;
        this.valve = valve;
        this.temperature = temperature;
        this.pressure = pressure;
        this.ammoniaConc = ammoniaConc;
        this.pipeID = pipeID;
        this.orificeID = orificeID;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getValve() {
        return valve;
    }

    public void setValve(String valve) {
        this.valve = valve;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getAmmoniaConc() {
        return ammoniaConc;
    }

    public void setAmmoniaConc(double ammoniaConc) {
        this.ammoniaConc = ammoniaConc;
    }

    public double getPipeID() {
        return pipeID;
    }

    public void setPipeID(double pipeID) {
        this.pipeID = pipeID;
    }

    public double getOrificeID() {
        return orificeID;
    }

    public void setOrificeID(double orificeID) {
        this.orificeID = orificeID;
    }
}
