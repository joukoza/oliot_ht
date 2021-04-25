package com.example.ht;

import java.util.ArrayList;

public class Day {
    private String name;
    private String date;

    private ArrayList<Food> foodList = new ArrayList<Food>();

    public Day(String givenName, String givenDate){
        name = givenName;
        date = givenDate;
    }

    public void addFood(String foodName, String foodType){
        foodList.add(new Food(foodName,foodType));
    }

    public Food getFoodByType(String foodType){
        Food returnValue = null;
        for (Food food: foodList){
            /*
            if (!food.getType().equals(foodType)) continue;
            returnValue = food;
            break;
            */
             if (food.getType().equals(foodType)){
                 returnValue = food;
                 break;
             }
        }
        return returnValue;
    }

    public String getName(){    return name; }
    public String getDate(){    return date; }
}
