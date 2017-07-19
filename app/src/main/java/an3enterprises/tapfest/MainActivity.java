package an3enterprises.tapfest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    static int quantity = 0;

    public static TextView num;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(),
                "ca-app-pub-3940256099942544~3347511713");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("D89FEDA779180F27568ECBDF5EEF043F").build();
        mAdView.loadAd(adRequest);
        num = (TextView) findViewById((R.id.num));
        SharedPreferences tapRateSaved = getSharedPreferences("tapRate", Context.MODE_PRIVATE);
        final int tapRateSavedInt = tapRateSaved.getInt("tapRate", 1);
        UpgradesActivity.tapRate = tapRateSavedInt;
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        final int quantitySavedInt = quantitySaved.getInt("quantity", 0);
        quantity = quantitySavedInt;
        displayQuantity(quantity);

    }

    @Override
    protected void onResume() {
        displayQuantity(quantity);
        super.onResume();
    }

    public void increment(View view) {
        quantity = quantity + UpgradesActivity.tapRate;

        displayQuantity(quantity);

    }

    public void displayQuantity(int quantity) {
        TextView num = (TextView) findViewById(R.id.num);
        num.setText("$" + quantity);
    }


    public void upgradesScreen(View view) {
        Intent upgrades = new Intent(this, UpgradesActivity.class);
        startActivity(upgrades);
    }

    @Override
    public void onDestroy(){
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor quantitySavedEditor = quantitySaved.edit();
        quantitySavedEditor.putInt("quantity", quantity);
        quantitySavedEditor.commit();
        super.onDestroy();
    }
    @Override
    public void onStop(){
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor quantitySavedEditor = quantitySaved.edit();
        quantitySavedEditor.putInt("quantity", quantity);
        quantitySavedEditor.commit();
        super.onStop();
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
    }


}