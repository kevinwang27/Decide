package kevinwang.personal.decide;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by kevinwang on 3/4/18.
 */

public class HTTPRequestTask extends AsyncTask<Context, Void, String> {
    private String getNearbySearch(Context context, int maxDistance, int maxPrice, String type) throws Exception{
        URL obj = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
        HttpsURLConnection connection = (HttpsURLConnection) obj.openConnection();
        connection.addRequestProperty("location", -33.8670522 + "," + 151.1957362);
        connection.addRequestProperty("radius", String.valueOf(maxDistance));
        connection.addRequestProperty("minprice", "0");
        connection.addRequestProperty("maxprice", String.valueOf(maxPrice));
        connection.addRequestProperty("type", type);
        connection.addRequestProperty("key", context.getString(R.string.google_maps_api_key));

        int responseCode = connection.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    @Override
    protected String doInBackground(Context... contexts) {
        try {
            return getNearbySearch(contexts[0], 1610,
                    2, "restaurant");
        } catch (Exception e){
            Log.d("loc", e.toString());
            return "";
        }
    }

    protected void onPostExecute(String result) {
        Log.d("loc", result);
    }

}
