package app.com.example.android.sunshine;

import android.app.LauncherActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForeCastFragment extends Fragment {

    public ForeCastFragment() {
    }

    @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                // Add this line in order for this fragment to handle menu events.
                        setHasOptionsMenu(true);
            }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastframent, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
               // Handle action bar item clicks here. The action bar will
               // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
               int id = item.getItemId();
               if (id == R.id.action_refresh) {
                   new FetChWeatherTask().execute();
                       return true;
                  }
                return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> weekForecast = new ArrayList<String>();
        weekForecast.add("Today - Sunny - 88/63");
        weekForecast.add("Tomorrow - Foggy - 70/46");
        weekForecast.add("Weds - Cloudy - 72/63");
        weekForecast.add("Thurs - Rainy - 64/51");
        weekForecast.add("Fri - Sunny - 70/46");
        weekForecast.add("Sat - Sunny - 76/68");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //The current context (this fragment's parent activity)
                getActivity(),
                //ID of list item layout
                R.layout.list_item_forecast,
                //Id of the textView to populate
                R.id.list_item_forecast_textview,
                //Forecast data
                weekForecast);

        //Get a reference to the ListView, and attach this adapter to it
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);

        return rootView;
    }


    public class FetChWeatherTask extends AsyncTask<Void, Void, Void>
    {
        private final String LOG_TAG = FetChWeatherTask.class.getSimpleName();

        public Void doInBackground(Void... params){
            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=83280&mode=json&units=metric&cnt=7";
                String apiKey = "&APPID=cd61a7f8a92fab819c6b57d4d373b052";
                URL url = new URL(baseUrl.concat(apiKey));

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

    }
}
