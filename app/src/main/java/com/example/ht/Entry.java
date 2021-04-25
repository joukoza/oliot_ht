package com.example.ht;

public class Entry {

    private String date;
    private int energy;

    public Entry(String givenDate, int givenEnergy){
        date = givenDate;
        energy = givenEnergy;
    }

    @Override
    public String toString(){
        /* Automatically arranges the information to a string for the "Stats"-fragment. */
        return "Date: " + date + "  Total energy: " + energy;
    }

    public void addEnergy(int moreEnergy){
        energy = energy + moreEnergy;
    }

    public String getDate(){    return date;    }
    public int getEnergy(){     return energy;  }

}
