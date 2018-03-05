package kevinwang.personal.decide;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by kevinwang on 3/4/18.
 */

public class HTTPRequestTask extends AsyncTask<MapsActivity.Data, Void, String> {

    /*
     * Listener for when the tsk finishes
     */
    public interface AsyncResponse {
        void processFinished(String response);
    }

    private AsyncResponse asyncResponse = null;

    HTTPRequestTask(AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
    }

    @Override
    protected String doInBackground(MapsActivity.Data... data) {
        try {
            return getNearbySearch(data[0]);
        } catch (Exception e){
            Log.d("loc", e.toString());
            return "";
        }
    }

    /*
     * Makes the API call and reads the resulting InputStream
     */
    private String getNearbySearch(MapsActivity.Data data) throws Exception{
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                data.getLatitude() + "," + data.getLongitude() + "&radius=" + data.getMaxDistance() +
                "&type=" + data.getType() + "&key=" + data.getContext().getString(R.string.google_maps_api_key);
        Log.d("loc", url);
        URL obj = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) obj.openConnection();
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        InputStream response = connection.getInputStream();
        return convertStreamToString(response);
    }

    protected void onPostExecute(String result) {
        asyncResponse.processFinished(result);
    }

    /*
     * Helper method to convert an InputStream to a String
     */
    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
