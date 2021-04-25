package com.example.ht;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.util.Base64;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    /* The rank-variable is used to determine what clearance the user has
    * and what fragments get loaded.
    * 0 - User who isn't signed in
    * 1 - Regular user who's signed in
    * 2 - Moderator
    * 3 - Administrator */
    private int currentUserRank = 0;
    /* Keeps track of the username after signing in. */
    private String currentUserName = "";
    /* Keeps track of the current weekday after choosing one in the Home-fragment. */
    private String currentWeekday = "";
    /* Keeps track of the current date after choosing one in the Home-fragment. */
    private String currentDate = "";

    /* Used for decoding user input passwords into byte arrays. */
    Charset defaultCharset = StandardCharsets.UTF_8;

    private DrawerLayout drawer;

    Context context = null;

    /* Defining the fragments here so that they can be called from all of
    * MainActivity's methods.*/
    Fragment fragHome;
    Fragment fragSignNone;
    Fragment fragSignUser;
    Fragment fragReg;
    Fragment fragDayNone;
    Fragment fragDayUser;
    Fragment fragStatsNone;
    Fragment fragStatsUser;

    ArrayList<User> userList = new ArrayList<User>();

    Restaurant rest = new Restaurant("Petran Purilaisparatiisi");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);

        fragHome = new FragmentHome();
        fragSignNone = new FragmentSignInNone();
        fragSignUser = new FragmentSignInUser();
        fragReg = new FragmentRegister();
        fragDayNone = new FragmentDayNone();
        fragDayUser = new FragmentDayUser();
        fragStatsNone = new FragmentStatsNone();
        fragStatsUser = new FragmentStatsUser();

        /* Rebuilds the users and their lists of entries from files.*/
        readUserListFromJSON();
        readEntryListFromJSON();

        /* This is here to provide a template for the restaurant. Normally this could
        * be called by people who add their restaurants to this app or something similar.*/
        initRestaurant();

        /* Load the home fragment during startup. */

        loadHomeFragment();

        NavigationView navView = findViewById(R.id.nav_view);
        /* Handles the various choices in the navigation drawer. */
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                fragHome).commit();
                        break;
                    case R.id.nav_sign_in:
                        /* The user isn't signed in. */
                        if (currentUserRank == 0){
                            loadSignInNoneFragment();
                        }
                        else {
                            loadSignInUserFragment();
                        }
                        break;
                    case R.id.nav_stats:
                        if (currentUserRank == 0){
                            loadStatsNoneFragment();
                        }
                        else {
                            loadStatsUserFragment();
                        }
                }
                return true;
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void buttonSignIn(View v){
        /* The user clicks the "Sign in"-button. Gets the user's input (username and password)
        * from the SignIn-fragment and then checks if the user was found from the
        * userList.*/
        FragmentSignInNone fragSign = (FragmentSignInNone) getSupportFragmentManager().
                findFragmentById(R.id.fragment_container);
        String userName = fragSign.getUserName();
        String password = fragSign.getPassword();

        int status = findUser(userName, password);
        if (status == 0){
            System.out.println("Username wasn't found.");
        }
        else if (status == 1){
            System.out.println("Incorrect password.");
        }
        else {
            /* The user successfully logged in. */
            currentUserName = userName;
            for (User user: userList){
                if (user.getName().equals(userName)){
                    currentUserRank = user.getRank();
                    break;
                }
            }
            /* Changes to the SignIn-fragment that's displayed for signed in users. */
            loadSignInUserFragment();
        }
    }

    public void buttonCreateAccount(View v){
        /* User clicks the "Create Account"-button. Gets the user's input from the Register-fragment,
        * performs various checks to ensure that the user can be created without issues and
        * then adds the new user to the userList. Also updates the JSON-file that keeps
        * track of registered users.*/
        FragmentRegister fragReg = (FragmentRegister) getSupportFragmentManager().
                findFragmentById(R.id.fragment_container);
        String userName = fragReg.getUserName();
        String password = fragReg.getPassword();
        String email = fragReg.getEmail();
        int rank = fragReg.getRank();

        if (userName.equals("")){
            System.out.println("Username can't be empty.");
            return;
        }
        if (password.equals("")){
            System.out.println("Password can't be empty.");
            return;
        }

        int status = findUser(userName, password);
        if (status > 0){
            System.out.println("Username is already taken.");
            return;
        }
        byte[] salt = getRandomSalt();
        byte[] passwordByte = convertInputPasswordToByteArray(password);
        byte[] passwordHashByte = hashPassword(passwordByte, salt);

        System.out.println("User successfully added.");

        userList.add(new User(userName, email, rank, passwordHashByte, salt));
        writeUserListToJSON();
        /* This file keeps track of the items the users have chosen.
        * It is created here for every user so that its existence
        * doesn't have to be verified later. */
        writeJSONToFile("", userName + "_entries.json");
    }

    public void buttonRegister(View v){
        /* User clicks the "Register"-button and the program loads the appropriate fragment.*/
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragReg).commit();
    }

    public void buttonSignOut(View v){
        /* User clicks the "Sign out"-button and two variables are updated to reflect
        * that the user isn't signed in anymore. */
        currentUserName = "";
        currentUserRank = 0;
        loadSignInNoneFragment();
    }

    public void writeUserListToJSON(){
        /* Parses the userList to JSON objects and writes them into a file. */
        JSONArray users = new JSONArray();
        String passwordHashString = "";
        String saltString = "";
        try {
            for (User u: userList){
                JSONObject user = new JSONObject();
                passwordHashString = encodeByteArrayToJSONString(u.getPasswordHash());
                saltString = encodeByteArrayToJSONString(u.getPasswordSalt());
                user.put("username", u.getName());
                user.put("email", u.getEmail());
                user.put("rank", u.getRank());
                user.put("password_hash", passwordHashString);
                user.put("password_salt", saltString);
                users.put(user);
            }
            writeJSONToFile("{\"users\":"+users.toString()+"}", "users.json");
        } catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void readUserListFromJSON(){
        /* Parses the "users.json"-file to objects in userList. */
        try{
            String name, email;
            int rank;
            byte[] passwordHash, passwordSalt;

            JSONObject obj = new JSONObject(readJSONFromFile("users.json"));
            JSONArray userArray = obj.getJSONArray("users");
            for (int i = 0; i < userArray.length(); i++){
                JSONObject user = userArray.getJSONObject(i);
                name = user.getString("username");
                email = user.getString("email");

                rank = user.getInt("rank");

                passwordHash = decodeJSONStringToByteArray(user.getString("password_hash"));
                passwordSalt = decodeJSONStringToByteArray(user.getString("password_salt"));

                userList.add(new User(name, email, rank, passwordHash, passwordSalt));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int findUser(String userName, String password){
        /* Used to check if an user can be found from userList and if the
        * user's password was correct. Returns a different number value
        * based on the result. */
        int status = 0;
        for (User user: userList){
            if (user.getName().equals(userName)){
                status++;
                /* Get the user's actual password hash. */
                byte[] actualPasswordHash = user.getPasswordHash();
                /* Convert the provided password to a byte array.
                *  Get the user's actual password salt and hash the provided password.
                *  Compare to user's actual password hash.*/
                byte[] passwordByte = convertInputPasswordToByteArray(password);
                byte[] actualSalt = user.getPasswordSalt();
                byte[] passwordHash = hashPassword(passwordByte, actualSalt);

                if (Arrays.equals(passwordHash, actualPasswordHash)){
                    status++;
                }
                /* If the username was found, no need to check the rest. */
                break;
            }
        }
        return status;
    }

    public byte[] hashPassword(byte[] password, byte[] salt){
        /* Salts and hashes the user's password with SHA-512. */
        byte[] passwordHash = null;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            passwordHash = md.digest(password);
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();

        }
        return passwordHash;
    }

    public void readEntryListFromJSON(){
        /* Reads each user's entryLists from files. */
        String userName = "";
        String date = "";
        int energy = 0;
        for (User u: userList){
            try{
                userName = u.getName();
                JSONArray ar = new JSONArray(readJSONFromFile(userName+"_entries.json"));
                for (int i = 0; i < ar.length(); i++){
                    JSONObject ob = ar.getJSONObject(i);
                    date = ob.getString("date");
                    energy = ob.getInt("energy");

                    u.addEntry(date, energy);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

        }
    }

    public void writeJSONToFile(String JSONString, String fileName){
        /* Writes JSON-strings to a file. */
        try{
            OutputStreamWriter oS = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            oS.write(JSONString);
            oS.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String readJSONFromFile(String fileName) {
        /* Returns a JSON-string that's read from a file. */
        String json = "";
        try{
            InputStream iS = context.openFileInput(fileName);
            BufferedReader bR = new BufferedReader(new InputStreamReader(iS));
            String currentLine = bR.readLine();
            while(currentLine != null){
                json = json.concat(currentLine);
                currentLine = bR.readLine();
            }
            iS.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return json;
    }

    private void loadStatsNoneFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragStatsNone).commit();
    }

    private void loadStatsUserFragment(){
        /* Loads the data from the user's entryList. */
        Bundle bundle = new Bundle();
        bundle.putString("userName", currentUserName);
        int totalEnergy = 0;

        User temp = null;
        for (User u: userList){
            if (u.getName().equals(currentUserName)){
                temp = u;
                break;
            }
        }
        ArrayList<Entry> entries = temp.getEntryList();
        ArrayList<String> stringEntries = new ArrayList<String>();

        /* Reads the entryList into an ArrayList of strings because it can
        * be put directly into a bundle as an argument. */
        for (Entry e: entries){
            stringEntries.add(e.toString());
            totalEnergy = totalEnergy + e.getEnergy();
        }

        /* Sorts the entries into ascending order. */
        Collections.sort(stringEntries);
        stringEntries.add("Total energy over all time: " + totalEnergy);
        bundle.putStringArrayList("entries", stringEntries);
        fragStatsUser.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragStatsUser).commit();
    }

    private void loadSignInNoneFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragSignNone).commit();
    }

    private void loadSignInUserFragment(){
        /* Username and user's rank are used in the "Sign in"-fragment so they're
        * provided as arguments when loading the fragment. */
        Bundle bundle = new Bundle();
        bundle.putString("userName", currentUserName);
        bundle.putInt("userRank", currentUserRank);
        fragSignUser.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragSignUser).commit();
    }

    private void loadHomeFragment(){
        Bundle bundle = new Bundle();
        bundle.putString("restaurantName", rest.getName());
        fragHome.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragHome).commit();
    }

    /* There's different buttons to load each weekday's menu. */
    public void loadMondayFragment(View v){
        loadDayFragment("Monday");
    }

    public void loadTuesdayFragment(View v){
        loadDayFragment("Tuesday");
    }

    public void loadWednesdayFragment(View v){
        loadDayFragment("Wednesday");
    }

    public void loadThursdayFragment(View v){
        loadDayFragment("Thursday");
    }

    public void loadFridayFragment(View v){
        loadDayFragment("Friday");
    }

    public void loadSaturdayFragment(View v){
        loadDayFragment("Saturday");
    }

    public void loadSundayFragment(View v){
        loadDayFragment("Sunday");
    }

    private void loadDayFragment(String day){
        /* The "Day"-fragment is loaded with same commands so the weekday
        * just needs to be received as a parameter. */
        currentWeekday = day;
        Day d = rest.getDay(currentWeekday);
        currentDate = d.getDate();
        Bundle bundle = new Bundle();
        bundle.putString("dayName", currentWeekday);
        bundle.putString("dayDate", currentDate);
        /* There's two types of foods for every day. */
        Food foodOmnivore = rest.getFood(day,"omnivore");
        String foodInfoOmnivore = foodOmnivore.getName() + ", kCal:" + foodOmnivore.getCalories();

        Food foodVegan = rest.getFood(day, "vegan");
        String foodInfoVegan = foodVegan.getName() + ", kCal: " + foodVegan.getCalories();

        bundle.putString("foodInfoOmnivore", foodInfoOmnivore);
        bundle.putString("foodInfoVegan", foodInfoVegan);

        /* There's two separate "Day"-fragments for signed in and not signed in users.*/
        if (currentUserRank == 0) {
            fragDayNone.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragDayNone).commit();
        } else {
            fragDayUser.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragDayUser).commit();
        }
    }

    /* There's separate buttons for adding each type of food from each weekday.*/
    public void addOmnivoreFoodToUser(View v){
        addFoodToUser("omnivore");
    }

    public void addVeganFoodToUser(View v){
        addFoodToUser("vegan");
    }

    private void addFoodToUser(String foodType){
        /* Adds the desired food to the user's entryList. */
        Food food = rest.getFood(currentWeekday, foodType);
        Day day = rest.getDay(currentWeekday);
        String date = day.getDate();
        int kCal = food.getCalories();

        /* Also writes the entryList to a file so it can be loaded during program launch. */
        ArrayList<Entry> entryList = null;
        for (User u: userList){
            if (u.getName().equals(currentUserName)){
                u.addEntry(date, kCal);
                entryList = u.getEntryList();
                break;
            }
        }
        /* Gson used to simplify the process of converting a JSON-string to the entryList. */
        Gson gson = new Gson();
        String JSONString = gson.toJson(entryList);
        writeJSONToFile(JSONString, currentUserName + "_entries.json");
    }

    private void initRestaurant(){
        /* Creates the menu's for each day so the program can be tested without
        * properly implemented user-privileges. */
        rest.createDayList();
        /* The food items are in Finnish, because these are also used for
        * the Fineli API calls. */
        rest.addMenu("Monday", "Omenahillo","vegan");
        rest.addMenu("Monday", "Makkarakeitto","omnivore");
        rest.addMenu("Tuesday", "Porkkana","vegan");
        rest.addMenu("Tuesday", "Siskonmakkarakeitto","omnivore");
        rest.addMenu("Wednesday", "Perunalastu","vegan");
        rest.addMenu("Wednesday", "Juustonaksu","omnivore");
        rest.addMenu("Thursday", "Kurkku","vegan");
        rest.addMenu("Thursday", "Juustofondue","omnivore");
        rest.addMenu("Friday", "Puolukkahillo","vegan");
        rest.addMenu("Friday", "Marenki","omnivore");
        rest.addMenu("Saturday", "Lanttu","vegan");
        rest.addMenu("Saturday", "Kebabliha","omnivore");
        rest.addMenu("Sunday", "Kantarelli","vegan");
        rest.addMenu("Sunday", "Broilerisuikale","omnivore");
    }

    private byte[] convertInputPasswordToByteArray(String input){
        return defaultCharset.encode(input).array();
    }

    /* Base64 is used to store the password hashes and salts in the users.json-file. */
    private String encodeByteArrayToJSONString(byte[] byteInput){
        String returnString = Base64.encodeToString(byteInput, Base64.NO_WRAP);
        return returnString;
    }

    private byte[] decodeJSONStringToByteArray(String stringInput){
        byte[] returnByteArray = Base64.decode(stringInput, Base64.NO_WRAP);
        return returnByteArray;
    }

    private byte[] getRandomSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}