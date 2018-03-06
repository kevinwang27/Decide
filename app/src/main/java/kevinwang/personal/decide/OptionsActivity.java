package kevinwang.personal.decide;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class OptionsActivity extends AppCompatActivity {
    public static final String DISTANCE = "com.personal.kevinwang.DISTANCE";
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            window.setEnterTransition(new Fade());
        }
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

        if (distanceText.getText().toString().equals("")) {
            buildErrorAlert();
            return;
        } else {
            int distance = (int) Double.parseDouble(distanceText.getText().toString());
            if (distance <= 0) {
                buildErrorAlert();
                return;
            } else {
                intent.putExtra(DISTANCE, convertToMeters(distance));
            }
        }

        startActivity(intent);
    }

    /*
     * Builds an alert for when the distance is negative or doesn't exist
     */
    private void buildErrorAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage("Please enter a distance greater than 0")
                .setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    /*
     * Helper method for converting miles to meters (needed for API call)
     */
    private int convertToMeters(int miles) {
        return miles * 1609;
    }
}
