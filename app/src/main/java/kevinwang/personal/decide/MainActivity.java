package kevinwang.personal.decide;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity {
    public static final String TYPE_OF_ACTIVITY = "com.personal.kevinwang.TYPE_OF_ACTIVITY";
    public static final int EAT = 0;
    public static final int SHOP = 1;
    public static final int PLAY = 2;
    public static final int RELAX = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new Fade());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
     * Launches the next screen and sends which button was pressed
     */
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
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
