/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
//      TODO (21) Implement LoaderManager.LoaderCallbacks<Cursor>
    //COMPLETE

    /*
     * In this Activity, you can share the selected day's forecast. No social sharing is complete
     * without using a hashtag. #BeTogetherNotTheSame
     */
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

//  TODO (18) Create a String array containing the names of the desired data columns from our ContentProvider
    //COMPLETE
    private static final String [] WEATHER_COLUMN_NAMES = {
        WeatherContract.WeatherEntry.COLUMN_DATE,
        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
        WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
        WeatherContract.WeatherEntry.COLUMN_PRESSURE,
        WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
        WeatherContract.WeatherEntry.COLUMN_DEGREES};

//  TODO (19) Create constant int values representing each column name's position above
    //COMPLETE
    private static final int INDEX_COLUMN_DATE = 0;
    private static final int INDEX_COLUMN_WEATHER_ID = 1;
    private static final int INDEX_COLUMN_MAX_TEMP = 2;
    private static final int INDEX_COLUMN_MIN_TEMP = 3;
    private static final int INDEX_COLUMN_HUMIDITY = 4;
    private static final int INDEX_COLUMN_PRESSURE = 5;
    private static final int INDEX_COLUMN_WIND = 6;
    private static final int INDEX_COLUMN_DEGREES = 7;

//  TODO (20) Create a constant int to identify our loader used in DetailActivity
    //COMPLETE
    private static final int LOADER_ID = 1069;

    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mForecastSummary;

//  TODO (15) Declare a private Uri field called mUri
    //COMPLETE
    private Uri mUri;

//  TODO (10) Remove the mWeatherDisplay TextView declaration
    //COMPLETE

//  TODO (11) Declare TextViews for the date, description, high, low, humidity, wind, and pressure
    //COMPLETE
    TextView mSelectedDate;
    TextView mSelectedDescription;
    TextView mSelectedHighTemp;
    TextView mSelectedLowTemp;
    TextView mSelectedHumidity;
    TextView mSelectedPressure;
    TextView mSelectedWind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//      TODO (12) Remove mWeatherDisplay TextView
        //COMPLETE
//      TODO (13) Find each of the TextViews by ID
        //COMPLETE
        mSelectedDate = (TextView) findViewById(R.id.tv_selected_date);
        mSelectedDescription = (TextView) findViewById(R.id.tv_selected_description);
        mSelectedHighTemp = (TextView) findViewById(R.id.tv_selected_high_temp);
        mSelectedLowTemp = (TextView) findViewById(R.id.tv_selected_low_temp);
        mSelectedHumidity = (TextView) findViewById(R.id.tv_selected_humidity);
        mSelectedPressure = (TextView) findViewById(R.id.tv_selected_pressure);
        mSelectedWind = (TextView) findViewById(R.id.tv_selected_wind);
//      TODO (14) Remove the code that checks for extra text
        //COMPLETE

//      TODO (16) Use getData to get a reference to the URI passed with this Activity's Intent
        //COMPLETE
        mUri = getIntent().getData();
//      TODO (17) Throw a NullPointerException if that URI is null
        //COMPLETE
        if(mUri == null){
            throw new NullPointerException("URI cannot be NULL");
        }
//      TODO (35) Initialize the loader for DetailActivity
        //COMPLETE
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     *
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

//  TODO (22) Override onCreateLoader
    //COMPLETE
//          TODO (23) If the loader requested is our detail loader, return the appropriate CursorLoader
            //COMPLETE

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == LOADER_ID){
            return new CursorLoader(this, mUri, WEATHER_COLUMN_NAMES,
                    null, null, null);
        } else
            throw new RuntimeException("Loader not Implemented: " + id);
    }

//  TODO (24) Override onLoadFinished
    //COMPLETE
//      TODO (25) Check before doing anything that the Cursor has valid data
        //COMPLETE
//      TODO (26) Display a readable data string
        //COMPLETE
//      TODO (27) Display the weather description (using SunshineWeatherUtils)
        //COMPLETE
//      TODO (28) Display the high temperature
        //COMPLETE
//      TODO (29) Display the low temperature
        //COMPLETE
//      TODO (30) Display the humidity
        //COMPLETE
//      TODO (31) Display the wind speed and direction
        //COMPLETE
//      TODO (32) Display the pressure
        //COMPLETE
//      TODO (33) Store a forecast summary in mForecastSummary
        //COMPLETE

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(!(data != null && data.moveToFirst())){
            return;
        }

        long weatherDate = data.getLong(INDEX_COLUMN_DATE);
        String dateString = SunshineDateUtils.getFriendlyDateString(this, weatherDate,true);
        mSelectedDate.setText(dateString);

        int conditionID = data.getInt(INDEX_COLUMN_WEATHER_ID);
        String weatherDescription = SunshineWeatherUtils.getStringForWeatherCondition(this, conditionID);
        mSelectedDescription.setText(weatherDescription);

        double hiTemp = data.getDouble(INDEX_COLUMN_MAX_TEMP);
        String hiTempText = SunshineWeatherUtils.formatTemperature(this, hiTemp);
        mSelectedHighTemp.setText(hiTempText);

        double loTemp = data.getDouble(INDEX_COLUMN_MIN_TEMP);
        String loTempText = SunshineWeatherUtils.formatTemperature(this, loTemp);
        mSelectedLowTemp.setText(loTempText);

        double weatherHumidity = data.getDouble(INDEX_COLUMN_HUMIDITY);
        String humidityText = getString(R.string.format_humidity, weatherHumidity);
        mSelectedHumidity.setText(humidityText);

        float windSpeed = data.getFloat(INDEX_COLUMN_WIND);
        float windDirection = data.getFloat(INDEX_COLUMN_DEGREES);
        String windSpeedDirect = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection);
        mSelectedWind.setText(windSpeedDirect);

        float pressure = data.getFloat(INDEX_COLUMN_PRESSURE);
        String pressureText = getString(R.string.format_pressure, pressure);
        mSelectedPressure.setText(pressureText);

        mForecastSummary = String.format("%s - %s - %s/%s", dateString, weatherDescription, hiTempText, loTempText);

    }

//  TODO (34) Override onLoaderReset, but don't do anything in it yet
    //COMPLETE
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}