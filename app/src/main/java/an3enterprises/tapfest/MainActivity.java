package an3enterprises.tapfest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Random;

import static an3enterprises.tapfest.UpgradesActivity.tapRate;

public class MainActivity extends Activity {

    static long quantity = 0;
    static int numOfClicks;
    static int numOfClicksSaved;
    public TextView num;
    private AdView mAdView;
    static int randNumForBonus;
    static int festDiamonds;
    TextView festDiamondText;
    Random rand = new Random();
    static String quantityString;
    static int maxBonus;
    static int leastBonus;
    //static ProgressBar tapsTillShowPB;
    //static double tapsTillShow;
    static String letter;
    static String TAG;
    static boolean isReturning;
    static int tapsTillChange;
    static String changingNum;
    static double addedTapsDecimals;
    static double addedTaps;
    static Resources letterGet;
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
        final String isAdsGone = adsGone.getString("adsGone", getResources().getString(R.string.falseString));
        SharedPreferences getFestDiamonds = getSharedPreferences("festDiamonds", Context.MODE_PRIVATE);
        festDiamonds = getFestDiamonds.getInt("festDiamonds", 0);

        if (isAdsGone.matches(getResources().getString(R.string.falseString))){
            mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            num = (TextView) findViewById((R.id.num));

        }if (isAdsGone.matches(getResources().getString(R.string.falseString))){
            Log.v(MainActivity.class.getName(), "No ads");
            //Toast.makeText(this, true + "", Toast.LENGTH_SHORT).show();
        }
        SharedPreferences tapRateSaved = getSharedPreferences("tapRate", Context.MODE_PRIVATE);
        final int tapRateSavedInt = tapRateSaved.getInt("tapRate", 1);
        tapRate = tapRateSavedInt;
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        final long quantitySavedLong = quantitySaved.getLong("quantity", 0);
        quantity = quantitySavedLong;
        festDiamondText = (TextView) findViewById(R.id.festDiamondText);
        //tapsTillShowPB = (ProgressBar) findViewById(R.id.tillShowPB);
        TAG = MainActivity.this + "";

        letterGet = MainActivity.this.getResources();

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
            Snackbar.make(view, getResources().getString(R.string.goToUpgrades), Snackbar.LENGTH_LONG).show();
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
            Snackbar.make(view, getResources().getString(R.string.bonus) + bonusInt + getResources().getString(R.string.plusOneFestDiamond), Snackbar.LENGTH_LONG).show();
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
        //displayProgress();
        displayFestDiamonds();
//        if (quantity == 2) {
//            quantity = 1000000;
//            tapRate = 5000000;
//        }
//        if (quantity >= 1000000) {
//            String quantityPlusTapRate = quantity + tapRate + "";
//            String qPlusTDecimal = quantityPlusTapRate.charAt(0) + "." + quantityPlusTapRate.charAt(1) + quantityPlusTapRate.charAt(2);
//            //Snackbar.make(findViewById(R.id.relative_layout_main),"qPlusTDecimal: " + qPlusTDecimal + " quantityString: " + quantityString, Snackbar.LENGTH_INDEFINITE).show();
//            quantityString = "" + quantity;
//            quantityString =  quantityString.charAt(0) + "." + quantityString.charAt(1) + quantityString.charAt(2);
//            if (qPlusTDecimal.matches(quantityString)) {
//                displayProgress();
//            } else {
//                tapsTillShowPB.setVisibility(View.INVISIBLE);
//            }
//        }
    }

    public void displayQuantity() {
        //quantity = 70000000;
        // more than a million, less than ten million
        if (quantity > 1000000 && quantity < 10000000){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "." + quantityString.charAt(1) + quantityString.charAt(2);
            letter = letterGet.getString(R.string.abbreviatedMillion);
            //changingNum = "tenThousand";
//            String quantityPlusTapRate = quantity + tapRate + "";
//            //tapsTillShow += 1;
//            Log.i("quantity plus taprate", "" + quantityPlusTapRate);
//            //Log.i("tapsTillShow", "" + tapsTillShow);
//            String qPlusTDecimal = quantityPlusTapRate.charAt(0) + "." + quantityPlusTapRate.charAt(1) + quantityPlusTapRate.charAt(2);
//            if (qPlusTDecimal.matches(quantityString)) {
//                Log.i("move progress", "true");
//
//
//                String lastFive = quantity + "";
//                lastFive = lastFive.substring(2,7);
//                Log.i("lastFive", lastFive);
//
//
//                //Calculate how many taps it takes to get to the next change.
//                // I do this by dividing the next number that will visibly show a change on the screen by the tapRate. It'll return the number of taps it'll take,
//                tapsTillChange = 10000 / tapRate;
//                Log.i("tapsTillChange", "" + tapsTillChange);
//
//            }else{
//                Log.i("dont move progress", "false");
//            }
//            Log.i("taprate", tapRate + "");
//            Log.i("quantity", quantity + "");
//            Log.i("qPlusTDecimal", qPlusTDecimal);
//            Log.i("quantityString", quantityString);
//            if (tapsTillChange != 0) {
//                percentPerTap = 100 / tapsTillChange;
//                Log.i("percentPerTap", "" + percentPerTap);
//
//                tapsTillShowPB.setProgress((int)percentPerTap);
//            }else {
//                Log.i("percentPerTap", "Nope");
//            }

//            if (percentPerTap % 1 == 0 || 100 / tapsTillChange != 9) {
//                tapsTillShowPB.setProgress((int) 100 / tapsTillChange);
//                ///Log.i("blah", 100/ tapsTillChange + "");
//                int sfd = tapsTillShowPB.getProgress();
//                Log.i("progress", sfd +  "");
//            }
//            Log.i(percentPerTap + "");
        }
        // more than ten million, less than one hundred million
        else if (quantity > 10000000 && quantity < 100000000){
            quantityString = "" + quantity;
            quantityString = quantityString.charAt(0) + "" + quantityString.charAt(1) + "." + quantityString.charAt(2) + quantityString.charAt(3);
            letter = letterGet.getString(R.string.abbreviatedMillion);
            //changingNum = "tenThousand";
//            String quantityPlusTapRate = quantity + tapRate + "";
//            //tapsTillShow += 1;
//            Log.i("quantity plus taprate", "" + quantityPlusTapRate);
//            //Log.i("tapsTillShow", "" + tapsTillShow);
//            String qPlusTDecimal = quantityPlusTapRate.charAt(0) + "." + quantityPlusTapRate.charAt(1) + quantityPlusTapRate.charAt(2);
//            if (qPlusTDecimal.matches(quantityString)) {
//                Log.i("move progress", "true");
//
//
//                String lastFive = quantity + "";
//                lastFive = lastFive.substring(3,8);
//                Log.i("lastFive", lastFive);
//
//                //Calculate how many taps it takes to get to the next change.
//                // I do this by dividing the next number that will visibly show a change on the screen by the tapRate. It'll return the number of taps it'll take,
//                tapsTillChange = 10000 / tapRate;
//                Log.i("tapsTillChange", "" + tapsTillChange);
//
//            }else{
//                Log.i("dont move progress", "false");
//            }
//            Log.i("taprate", tapRate + "");
//            Log.i("quantity", quantity + "");
//            Log.i("qPlusTDecimal", qPlusTDecimal);
//            Log.i("quantityString", quantityString);
//            if (tapsTillChange != 0) {
//                double percentPerTap = 100 / tapsTillChange;
//                Log.i("percentPerTap", "" + percentPerTap);
//            }else {
//                Log.i("percentPerTap", "Nope");
//            }

//            if (percentPerTap % 1 == 0 || 100 / tapsTillChange != 9) {
//                tapsTillShowPB.setProgress((int) 100 / tapsTillChange);
//                ///Log.i("blah", 100/ tapsTillChange + "");
//                int sfd = tapsTillShowPB.getProgress();
//                Log.i("progress", sfd +  "");
//            }
//            Log.i(percentPerTap + "");


        }
        // more than one hundred million, less than a billion
        else if (quantity > 100000000 && quantity < 1000000000){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "" + quantityString.charAt(1) + "" + quantityString.charAt(2) + "." + quantityString.charAt(3) + quantityString.charAt(4);
            letter = letterGet.getString(R.string.abbreviatedMillion);
            //changingNum = "tenThousand";
        }
        // more than a billion, less than ten billion
        else if (quantity > 1000000000 && quantity < 10000000000L){
            quantityString = "" + quantity;
            quantityString = quantityString.charAt(0) + "." + quantityString.charAt(1) + quantityString.charAt(2);
            letter = letterGet.getString(R.string.abbreviatedBillion);

            //changingNum = "tenMillion";
        }
        // more than ten billion, less than one hundred billion
        else if (quantity > 10000000000L && quantity < 100000000000L){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "" + quantityString.charAt(1) + "." + quantityString.charAt(2) + "" + quantityString.charAt(3);
            letter = letterGet.getString(R.string.abbreviatedBillion);

//            changingNum = "tenMillion";
        }
        // more than one hundred billion, less than one trillion
        else if (quantity > 100000000000L && quantity < 1000000000000L){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "" + quantityString.charAt(1) + "" + quantityString.charAt(3) + "." + quantityString.charAt(4) + quantityString.charAt(5);
            letter = letterGet.getString(R.string.abbreviatedMillion);
            //changingNum = "tenMillion";
        }
        // more than one trillion, less than ten trillion
        else if (quantity > 1000000000000L && quantity < 10000000000000L){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "." + quantityString.charAt(1) + quantityString.charAt(2);
            letter = letterGet.getString(R.string.abbreviatedTrillion);

            //changingNum = "tenBillion";
        }
        // more than ten trillion, less than one hundred trillion
        else if (quantity > 10000000000000L && quantity < 100000000000000L){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "" + quantityString.charAt(1) + "." + quantityString.charAt(2) + quantityString.charAt(3);
            letter = letterGet.getString(R.string.abbreviatedTrillion);

            //changingNum = "tenBillion";
        }
        // more than one hundred trillion, less than one quadrillion
        else if (quantity > 100000000000000L && quantity < 1000000000000000L){
            quantityString = "" + quantity;
            quantityString =  quantityString.charAt(0) + "" + quantityString.charAt(1) + "" + quantityString.charAt(2) + "." + quantityString.charAt(3) + quantityString.charAt(4);
            letter = letterGet.getString(R.string.abbreviatedTrillion);

            //changingNum = "tenBillion";
        }
        // less than a million. No decimals.
        else if (quantity < 1000000) {
            quantityString = "" + quantity;
            letter = "";
            isReturning = true;
        }
        num = (TextView) findViewById(R.id.num);
        num.setText("$" + quantityString + letter);
        if (isReturning) {
            return;
        }

        //Log.i("MainActivity", quantityString.replace(".", quantity + tapRate + "".length() + ""));
    }

    public void displayFestDiamonds() {
        festDiamondText.setText(festDiamonds + letterGet.getString(R.string.festDiamondsSpace));
    }


    public void upgradesScreen(View view) {
        Intent upgrades = new Intent(this, UpgradesActivity.class);
        startActivity(upgrades);
    }

//    public void displayProgress() {
//
//        if (changingNum.matches("tenThousand")) {
//            if (tapRate >= 10000) {
//                tapsTillShowPB.setVisibility(View.INVISIBLE);
//            }
//            if (tapRate < 10000) {
//                //Toast.makeText(this, "less than ten thousand", Toast.LENGTH_SHORT).show();
//                int tapsTillShow = 10000 / tapRate;
//                Snackbar.make(findViewById(R.id.relative_layout_main), "tapsTillShow: " + tapsTillShow, Snackbar.LENGTH_SHORT).show();
//
//                if (round(100 / (double) tapsTillShow, 2) < 1) {
//                    double percentPerTap = round(100 / (double) tapsTillShow, 2);
//                    Snackbar.make(findViewById(R.id.relative_layout_main), "percentPerTapDecimal: " + percentPerTap, Snackbar.LENGTH_LONG).show();
//                    addedTapsDecimals += percentPerTap;
//                    Snackbar.make(findViewById(R.id.relative_layout_main), "addedTapsDecimals" + addedTapsDecimals, Snackbar.LENGTH_SHORT).show();
//                    if(round(addedTapsDecimals, 0) > 1) {
//                        tapsTillShowPB.setProgress((int) addedTapsDecimals);
//                    }
//                }
//                if (100 / (double) tapsTillShow > 1) {
//                    double percentPerTap = round(100 / (double) tapsTillShow, 2);
//                    Snackbar.make(findViewById(R.id.relative_layout_main), "percentPerTap: " + percentPerTap, Snackbar.LENGTH_SHORT).show();
//                }
//            }
//        }
//        if (changingNum.matches("tenMillion")) {
//            if (tapRate >= 10000000) {
//                tapsTillShowPB.setVisibility(View.INVISIBLE);
//            }
//            if (tapRate < 10000000) {
//                //Toast.makeText(this, "less than ten thousand", Toast.LENGTH_SHORT).show();
//                int tapsTillShow = 10000000 / tapRate;
//                Snackbar.make(findViewById(R.id.relative_layout_main), "tapsTillShow: " + tapsTillShow, Snackbar.LENGTH_SHORT).show();
//
//                if (round(100 / (double) tapsTillShow, 2) < 1) {
//                    double percentPerTap = round(100 / (double) tapsTillShow, 2);
//                    Log.i("percentPerTapDecimal", "" + percentPerTap);
//                    addedTapsDecimals += percentPerTap;
//                    Log.i("addedTapsDecimals", "" + addedTapsDecimals);
//                    Snackbar.make(findViewById(R.id.relative_layout_main), "addedTapsDecimals" + addedTapsDecimals, Snackbar.LENGTH_SHORT).show();
//                    if(round(addedTapsDecimals, 0) > 1) {
//                        tapsTillShowPB.setProgress((int) addedTapsDecimals);
//                    }
//                }
//                if (100 / (double) tapsTillShow > 1) {
//                    double percentPerTap = round(100 / (double) tapsTillShow, 2);
//                    Snackbar.make(findViewById(R.id.relative_layout_main), "percentPerTap: " + percentPerTap, Snackbar.LENGTH_SHORT).show();
//                }
//            }
//        }
//
//        if (changingNum.matches("tenBillion")) {
//            if (tapRate >= 10000000000L) {
//                tapsTillShowPB.setVisibility(View.INVISIBLE);
//            }
//            if (tapRate < 10000000000L) {
//                //Toast.makeText(this, "less than ten thousand", Toast.LENGTH_SHORT).show();
//                long tapsTillShow = 10000000000L / tapRate;
//                Toast.makeText(this, "tapsTillShow: " + tapsTillShow, Toast.LENGTH_SHORT).show();
//                if (100 / (double) tapsTillShow < 1) {
//                    double percentPerTap = round(100 / (double) tapsTillShow, 2);
//                    Toast.makeText(this, "percentPerTapDecimal: " + percentPerTap, Toast.LENGTH_SHORT).show();
//                }
//                if (100 / (double) tapsTillShow > 1) {
//                    double percentPerTap = round(100 / (double) tapsTillShow, 2);
//                    Toast.makeText(this, "percentPerTap: " + percentPerTap, Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//
//        if (tapsTillShowPB.getProgress() >= 100) {
//            tapsTillShowPB.setProgress(0);
//        }
//
//
////        int tapsTillChange = (int) quantity / (int) tapRate;
////        double percentPerTap = 100 / tapsTillChange;
////        tapsTillShowPB.setProgress((int)percentPerTap);
////        Toast.makeText(this, "percentPerTap: " + percentPerTap, Toast.LENGTH_SHORT).show();
////        Toast.makeText(this, "tapsTillChange: " + tapsTillChange, Toast.LENGTH_SHORT).show();
//    }

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
        displayFestDiamonds();
        displayQuantity();
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
        displayFestDiamonds();
        displayQuantity();
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
        displayFestDiamonds();
        displayQuantity();
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
        displayFestDiamonds();
        displayQuantity();
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
        displayFestDiamonds();
        displayQuantity();
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
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