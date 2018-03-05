package kevinwang.personal.decide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class OptionsActivity extends AppCompatActivity {
    public static final String DISTANCE = "com.personal.kevinwang.DISTANCE";
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Intent intent = getIntent();
        type = intent.getIntExtra(MainActivity.TYPE_OF_ACTIVITY, 0);
    }

    /*
     * Pass proper data to the next activity
     */
    public void launchMapsActivity(View view) {
        EditText distanceText = (EditText) findViewById(R.id.distance_text);

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(MainActivity.TYPE_OF_ACTIVITY, type);

        int distance = (int) Double.parseDouble(distanceText.getText().toString());
        intent.putExtra(DISTANCE, convertToMeters(distance));

        startActivity(intent);
    }

    /*
     * Helper method for converting miles to meters (needed for API call)
     */
    private int convertToMeters(int miles) {
        return miles * 1609;
    }
}
