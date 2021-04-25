package com.example.ht;

import java.util.ArrayList;

public class Restaurant {

    private String name;
    private ArrayList<Day> dayList = new ArrayList<Day>();

    public Restaurant(String givenName){
        name = givenName;
    }

    public void createDayList(){
        /* This would normally receive the date(s) and then create a weekday based on that.
        * A whole week has been implemented manually to display program functionality. */
        dayList.clear();
        dayList.add(new Day("Monday", "2021-04-19"));
        dayList.add(new Day("Tuesday", "2021-04-20"));
        dayList.add(new Day("Wednesday", "2021-04-21"));
        dayList.add(new Day("Thursday", "2021-04-22"));
        dayList.add(new Day("Friday", "2021-04-23"));
        dayList.add(new Day("Saturday", "2021-04-24"));
        dayList.add(new Day("Sunday", "2021-04-25"));
    }

    public void addMenu(String weekday, String newFood, String newType){
        for (Day day: dayList){
            /*if (!day.getName().equals(weekday)) continue;
            day.addFood(newFood, newType);
            break;*/
            if (day.getName().equals(weekday)){
                day.addFood(newFood, newType);
                break;
            }
        }
    }

    public Day getDay(String weekday){
        Day returnValue = null;
        for (Day day: dayList){
            /*if (!day.getName().equals(weekday)) continue;
            returnValue = day;
            break;*/
            if (day.getName().equals(weekday)){
                returnValue = day;
                break;
            }
        }
        return returnValue;
    }

    public Food getFood(String weekday, String foodType){
        Food returnValue = null;
        for (Day day: dayList){
            /*if (!day.getName().equals(weekday)) continue;
            returnValue = day.getFoodByType(foodType);
            break;*/
            if (day.getName().equals(weekday)){
                returnValue = day.getFoodByType(foodType);
                break;
            }
        }
        return returnValue;
    }

    public String getName(){ return name; }
}
