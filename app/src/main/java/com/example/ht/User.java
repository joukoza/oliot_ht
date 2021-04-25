package com.example.ht;

import java.util.ArrayList;

public class User {

    private String name;
    private String email;
    private int rank;
    private byte[] passwordHash;
    private byte[] passwordSalt;

    private ArrayList<Entry> entryList = new ArrayList<Entry>();

    public User(String givenName, String givenEmail, int givenRank,
                byte[] givenPasswordHash, byte[] givenPasswordSalt){
        name = givenName;
        email = givenEmail;
        rank = givenRank;
        passwordHash = givenPasswordHash;
        passwordSalt = givenPasswordSalt;
    }

    public void addEntry(String date, int energy){
        int found = 0;
        for (Entry e: entryList){
            /* If an entry exists for the current date, only add the energy. */
            if (e.getDate().equals(date)){
                found = 1;
                e.addEnergy(energy);
                break;
            }
        }
        /* The date wasn't found in any entry. */
        if (found == 0){
            entryList.add(new Entry(date, energy));
        }
    }

    public String getName()                 {   return name;           }
    public String getEmail()                {   return email;          }
    public int getRank()                    {   return rank;           }
    public byte[] getPasswordHash()         {   return passwordHash;   }
    public byte[] getPasswordSalt()         {   return passwordSalt;   }
    public ArrayList<Entry> getEntryList()  {   return entryList;      }

}
