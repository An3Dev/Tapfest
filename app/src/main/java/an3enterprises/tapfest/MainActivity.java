package an3enterprises.tapfest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Random;

import static an3enterprises.tapfest.UpgradesActivity.tapRate;

public class MainActivity extends Activity {

    static long quantity = 0;
    static int numOfClicks;
    static int numOfClicksSaved;
    public static TextView num;
    private AdView mAdView;
    static int randNumForBonus;
    static int festDiamonds;
    static TextView festDiamondText;
    Random rand = new Random();
    static String quantityString;
    static int maxBonus;
    static int leastBonus;
    static ProgressBar tapsTillShowPB;
    static double tapsTillShow;
    static String letter;
    static String TAG;
    static boolean isReturning;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);



        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(),
                "ca-app-pub-3940256099942544~3347511713");
        SharedPreferences adsGone = getSharedPreferences("adsGone", Context.MODE_PRIVATE);
        final String isAdsGone = adsGone.getString("adsGone", "false");
        SharedPreferences getFestDiamonds = getSharedPreferences("festDiamonds", Context.MODE_PRIVATE);
        final int getFestDiamondsInt = getFestDiamonds.getInt("festDiamonds", 0);
        festDiamonds = getFestDiamondsInt;

        if (isAdsGone.matches("false")){
            mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            num = (TextView) findViewById((R.id.num));

        }if (isAdsGone.matches("true")){
            Log.v(MainActivity.class.getName(), "No ads");
            Toast.makeText(this, true + "", Toast.LENGTH_SHORT).show();
        }
        SharedPreferences tapRateSaved = getSharedPreferences("tapRate", Context.MODE_PRIVATE);
        final int tapRateSavedInt = tapRateSaved.getInt("tapRate", 1);
        tapRate = tapRateSavedInt;
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        final long quantitySavedLong = quantitySaved.getLong("quantity", 0);
        quantity = quantitySavedLong;
        TextView num = (TextView) findViewById(R.id.num);
        festDiamondText = (TextView) findViewById(R.id.festDiamondText);
        tapsTillShowPB = (ProgressBar) findViewById(R.id.tillShowPB);
        TAG = MainActivity.this + "";
        startBonusCycle();
        displayFestDiamonds();
        displayQuantity();


    }

    @Override
    protected void onResume() {
        displayQuantity();
        displayFestDiamonds();
        super.onResume();
    }

    public void increment(View view) {
        numOfClicks += 1;
        numOfClicksSaved += 1;
        quantity = quantity + tapRate;
        if (quantity == 50) {
            Snackbar.make(view, "Go to Upgrades to earn more FestCoins for every tap", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        if (numOfClicks == randNumForBonus){
            startBonusCycle();
            final MediaPlayer kachingSound = MediaPlayer.create(this, R.raw.kaching);
            kachingSound.start();
            Random bonus = new Random();
            if (quantity + "".length() > 9 && quantity + "".length() < 12) {
               maxBonus = 200000;
                leastBonus = 50000;
            }
            if (quantity + "".length() > 12 && quantity + "".length() < 15) {
                maxBonus = 500000;
                leastBonus = 100000;
            }
            if (quantity + "".length() > 15) {
                maxBonus = 1000000;
                leastBonus = 250000;
            }
            int bonusInt = bonus.nextInt(maxBonus) + leastBonus;
            quantity += bonusInt;
            festDiamonds += 1;
            Snackbar.make(view, "Bonus: $" + bonusInt + " + 1 FestDiamond", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            SharedPreferences festDiamondsSP = getSharedPreferences("festDiamonds", Context.MODE_PRIVATE);
            SharedPreferences.Editor festDiamondsEditor = festDiamondsSP.edit();
            festDiamondsEditor.putInt("festDiamonds", festDiamonds);
            festDiamondsEditor.commit();
            SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
            SharedPreferences.Editor quantitySavedEditor = quantitySaved.edit();
            quantitySavedEditor.putLong("quantity", quantity);
            quantitySavedEditor.commit();
        }if (numOfClicksSaved % 1000 == 0) {
            festDiamonds += 1;
            displayFestDiamonds();
            SharedPreferences festDiamondsSP = getSharedPreferences("festDiamonds", Context.MODE_PRIVATE);
            SharedPreferences.Editor festDiamondsEditor = festDiamondsSP.edit();
            festDiamondsEditor.putInt("festDiamonds", festDiamonds);
            festDiamondsEditor.commit();
        }
        displayQuantity();
        displayFestDiamonds();
    }

    public static void displayQuantity() {
        // more than a million, less than ten million
        if (quantity > 1000000 && quantity < 10000000){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "." + quantityString.charAt(1) + quantityString.charAt(2);
            letter = "M";
            String quantityPlusTapRate = quantity + tapRate + "";
            //if (Long.parseLong(quantityPlusTapRate.substring(0, 3)) == Long.parseLong(quantityString.replace(".", ""))) {
//        tapsTillShow +=  Double.parseDouble(quantityPlusTapRate.substring(0,3)) / Double.parseDouble(quantityString.replace(".", "")) / 100;
            tapsTillShow += 1;
            Log.i("quantity plus taprate", "" + quantityPlusTapRate);
            Log.i("tapsTillShow", "" + tapsTillShow);
            tapsTillShowPB.setProgress((int) tapsTillShow);
            int sfd = tapsTillShowPB.getProgress();
            Log.i("progress", sfd +  "");
            //quantity = 1000000000;
            //tapRate = 1000000;
            //tapsTillShowPB.setProgress(25);
            //}
        }
        // more than ten million, less than one hundred million
        else if (quantity > 10000000 && quantity < 100000000){
            quantityString = "" + quantity;
            quantityString = quantityString.charAt(0) + "" + quantityString.charAt(1) + "." + quantityString.charAt(2) + quantityString.charAt(3);
            letter = "M";
            String quantityPlusTapRate = quantity + tapRate + "";
            tapsTillShow += 1;
            Log.i("quantity plus taprate", "" + quantityPlusTapRate);
            Log.i("tapsTillShow", "" + tapsTillShow);
            String qPlusTDecimal = quantityPlusTapRate.substring(0,2) + "." + quantityPlusTapRate.substring(3,5);
            if (qPlusTDecimal.matches(quantityString)) {
                Log.i("move progress", "true");

                //long length = quantity + "".length();
//                if(quantity + "".substring(1, 5) == ) {
//
//                }

                double tapsTillChange = quantity ;
                Log.i("tapsTillChange", "" + tapsTillChange);

            }else{
                Log.i("dont move progress", "false");
            }
            Log.i("quantity", quantity + "");
            Log.i("qPlusTDecimal", "" + quantityPlusTapRate.substring(0,2) + "." + quantityPlusTapRate.substring(3,5));
            Log.i("quantityString", quantityString);
            tapsTillShowPB.setProgress((int) tapsTillShow);
            int sfd = tapsTillShowPB.getProgress();
            Log.i("progress", sfd +  "");
        }
        // more than one hundred million, less than a billion
        else if (quantity > 100000000 && quantity < 1000000000){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "" + quantityString.charAt(1) + "" + quantityString.charAt(2) + "." + quantityString.charAt(3) + quantityString.charAt(4);
            letter = "M";
        }
        // more than a billion, less than ten billion
        else if (quantity > 1000000000 && quantity < 10000000000L){
            quantityString = "" + quantity;
            quantityString = quantityString.charAt(0) + "." + quantityString.charAt(1) + quantityString.charAt(2);
            letter = "B";
        }
        // more than ten billion, less than one hundred billion
        else if (quantity > 10000000000L && quantity < 100000000000L){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "" + quantityString.charAt(1) + "." + quantityString.charAt(2) + "" + quantityString.charAt(3);
            letter = "B";
        }
        // more than one hundred billion, less than one trillion
        else if (quantity > 100000000000L && quantity < 1000000000000L){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "" + quantityString.charAt(1) + "" + quantityString.charAt(3) + "." + quantityString.charAt(4) + quantityString.charAt(5);
            letter = "B";
        }
        // more than one trillion, less than ten trillion
        else if (quantity > 1000000000000L && quantity < 10000000000000L){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "." + quantityString.charAt(1) + quantityString.charAt(2);
            letter = "B";
        }
        // more than ten trillion, less than one hundred trillion
        else if (quantity > 10000000000000L && quantity < 100000000000000L){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "" + quantityString.charAt(1) + "." + quantityString.charAt(2) + quantityString.charAt(3);
            letter = "B";
        }
        // more than one hundred trillion, less than one quadrillion
        else if (quantity > 100000000000000L && quantity < 1000000000000000L){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "" + quantityString.charAt(1) + "" + quantityString.charAt(2) + "." + quantityString.charAt(3) + quantityString.charAt(4);
            letter = "B";
        }
        // less than a million. No decimals.
        else if (quantity < 1000000) {
            quantityString = "" + quantity;
            letter = "";
            isReturning = true;
        }
        num.setText("$" + quantityString + letter);
        if (isReturning) {
//            return;
        }
        if (tapsTillShow == 100) {
            tapsTillShow = 0;
        }
        //Log.i("MainActivity", quantityString.replace(".", quantity + tapRate + "".length() + ""));
    }

    public static void displayFestDiamonds() {
        festDiamondText.setText(festDiamonds + " FestDiamonds");
    }


    public void upgradesScreen(View view) {
        Intent upgrades = new Intent(this, UpgradesActivity.class);
        startActivity(upgrades);
    }

    @Override
    public void onDestroy(){
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor quantitySavedEditor = quantitySaved.edit();
        quantitySavedEditor.putLong("quantity", quantity);
        quantitySavedEditor.commit();
        SharedPreferences festDiamondsSP = getSharedPreferences("festDiamonds", Context.MODE_PRIVATE);
        SharedPreferences.Editor festDiamondsEditor = festDiamondsSP.edit();
        festDiamondsEditor.putInt("festDiamonds", festDiamonds);
        festDiamondsEditor.commit();
        super.onDestroy();
    }
    @Override
    public void onStop(){
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor quantitySavedEditor = quantitySaved.edit();
        quantitySavedEditor.putLong("quantity", quantity);
        quantitySavedEditor.commit();
        SharedPreferences festDiamondsSP = getSharedPreferences("festDiamonds", Context.MODE_PRIVATE);
        SharedPreferences.Editor festDiamondsEditor = festDiamondsSP.edit();
        festDiamondsEditor.putInt("festDiamonds", festDiamonds);
        festDiamondsEditor.commit();
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
        quantitySavedEditor.putLong("quantity", quantity);
        quantitySavedEditor.commit();
        SharedPreferences festDiamondsSP = getSharedPreferences("festDiamonds", Context.MODE_PRIVATE);
        SharedPreferences.Editor festDiamondsEditor = festDiamondsSP.edit();
        festDiamondsEditor.putInt("festDiamonds", festDiamonds);
        festDiamondsEditor.commit();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor quantitySavedEditor = quantitySaved.edit();
        quantitySavedEditor.putLong("quantity", quantity);
        SharedPreferences festDiamondsSP = getSharedPreferences("festDiamonds", Context.MODE_PRIVATE);
        SharedPreferences.Editor festDiamondsEditor = festDiamondsSP.edit();
        festDiamondsEditor.putInt("festDiamonds", festDiamonds);
        festDiamondsEditor.commit();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor quantitySavedEditor = quantitySaved.edit();
        quantitySavedEditor.putLong("quantity", quantity);
        quantitySavedEditor.commit();
        SharedPreferences festDiamondsSP = getSharedPreferences("festDiamonds", Context.MODE_PRIVATE);
        SharedPreferences.Editor festDiamondsEditor = festDiamondsSP.edit();
        festDiamondsEditor.putInt("festDiamonds", festDiamonds);
        festDiamondsEditor.commit();
//        AlertDialog.Builder builder;
//        builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("Exit Tapfest?");
//        builder.setMessage("Are you sure you want to exit Tapfest?");
//        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                MainActivity.this.finish();
//
//            }
//        });
//        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.show();
    }


}