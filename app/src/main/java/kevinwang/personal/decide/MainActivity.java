package kevinwang.personal.decide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String TYPE_OF_ACTIVITY = "com.personal.kevinwang.TYPE_OF_ACTIVITY";
    public static final int EAT = 0;
    public static final int SHOP = 1;
    public static final int PLAY = 2;
    public static final int RELAX = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launchOptionsActivity(View view, int choice) {
        Intent intent = new Intent(this, OptionsActivity.class);
        switch (choice) {
            case EAT:
                intent.putExtra(TYPE_OF_ACTIVITY, EAT);
                break;
            case SHOP:
                intent.putExtra(TYPE_OF_ACTIVITY, SHOP);
                break;
            case PLAY:
                intent.putExtra(TYPE_OF_ACTIVITY, PLAY);
                break;
            case RELAX:
                intent.putExtra(TYPE_OF_ACTIVITY, RELAX);
                break;
        }
        startActivity(intent);
    }

    public void launchEatOptionsActivity(View view) {
        launchOptionsActivity(view, EAT);
    }

    public void launchShopOptionsActivity(View view) {
        launchOptionsActivity(view, SHOP);
    }

    public void launchPlayOptionsActivity(View view) {
        launchOptionsActivity(view, PLAY);
    }

    public void launchRelaxOptionsActivity(View view) {
        launchOptionsActivity(view, RELAX);
    }
}
