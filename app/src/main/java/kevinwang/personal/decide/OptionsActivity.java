package kevinwang.personal.decide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class OptionsActivity extends AppCompatActivity {
    public static final String DISTANCE = "com.personal.kevinwang.DISTANCE";
    public static final String PRICE = "com.personal.kevinwang.PRICE";
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Intent intent = getIntent();
        type = intent.getIntExtra(MainActivity.TYPE_OF_ACTIVITY, 0);
    }

    public void launchMapsActivity(View view) {
        EditText distanceText = (EditText) findViewById(R.id.distance_text);
        EditText priceText = (EditText) findViewById(R.id.price_text);

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(MainActivity.TYPE_OF_ACTIVITY, type);

        int distance = Integer.parseInt(distanceText.getText().toString());
        intent.putExtra(DISTANCE, convertToMeters(distance));

        int price = Integer.parseInt(priceText.getText().toString());
        if (price <= 10) {
            intent.putExtra(PRICE, 0);
        } else if (price <= 20) {
            intent.putExtra(PRICE, 1);
        } else if (price <= 30) {
            intent.putExtra(PRICE, 2);
        } else if (price <= 60) {
            intent.putExtra(PRICE, 3);
        } else {
            intent.putExtra(PRICE, 4);
        }
        startActivity(intent);
    }

    private int convertToMeters(int miles) {
        return miles * 1609;
    }
}
