package an3enterprises.tapfest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    static int quantity = 0;
    static int numOfClicks;
    public static TextView num;
    private AdView mAdView;
    static int randNumForBonus;
    Random rand = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        startBonusCycle();
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(),
                "ca-app-pub-3940256099942544~3347511713");
        SharedPreferences adsGone = getSharedPreferences("adsGone", Context.MODE_PRIVATE);
        final String isAdsGone = adsGone.getString("adsGone", "false");
        if (isAdsGone.matches("false")){
            mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("D89FEDA779180F27568ECBDF5EEF043F").build();
            mAdView.loadAd(adRequest);
            num = (TextView) findViewById((R.id.num));
        }if (isAdsGone.matches("true")){
            Log.v(MainActivity.class.getName(), "No ads");
        }
        SharedPreferences tapRateSaved = getSharedPreferences("tapRate", Context.MODE_PRIVATE);
        final int tapRateSavedInt = tapRateSaved.getInt("tapRate", 1);
        UpgradesActivity.tapRate = tapRateSavedInt;
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        final int quantitySavedInt = quantitySaved.getInt("quantity", 0);
        quantity = quantitySavedInt;
        displayQuantity();
        TextView num = (TextView) findViewById(R.id.num);

    }

    @Override
    protected void onResume() {
        displayQuantity();
        super.onResume();
    }

    public void increment(View view) {
        numOfClicks += 1;
        quantity = quantity + UpgradesActivity.tapRate;
        if (numOfClicks == randNumForBonus){
            startBonusCycle();
            final MediaPlayer kachingSound = MediaPlayer.create(this, R.raw.kaching_sound_effect);
            kachingSound.start();
            Random bonus = new Random();
            int bonusInt = bonus.nextInt(50000) + 10000;
            quantity += bonusInt;
            Toast toast = Toast.makeText(MainActivity.this,"Bonus: " + bonusInt + "Festcoins", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }
        displayQuantity();

    }

    public static void displayQuantity() {

        num.setText(quantity + " Festcoins");

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

    public void startBonusCycle() {
        numOfClicks = 0;
        randNumForBonus = rand.nextInt(399) + 50;
    }

    @Override
    protected void onPause() {
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor quantitySavedEditor = quantitySaved.edit();
        quantitySavedEditor.putInt("quantity", quantity);
        quantitySavedEditor.commit();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor quantitySavedEditor = quantitySaved.edit();
        quantitySavedEditor.putInt("quantity", quantity);
        quantitySavedEditor.commit();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor quantitySavedEditor = quantitySaved.edit();
        quantitySavedEditor.putInt("quantity", quantity);
        quantitySavedEditor.commit();
        super.onBackPressed();
    }
}