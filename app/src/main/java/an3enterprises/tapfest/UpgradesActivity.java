package an3enterprises.tapfest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import an3enterprises.tapfest.util.IabHelper;
import an3enterprises.tapfest.util.IabResult;
import an3enterprises.tapfest.util.Inventory;
import an3enterprises.tapfest.util.Purchase;

import static an3enterprises.tapfest.MainActivity.displayFestDiamonds;
import static an3enterprises.tapfest.MainActivity.displayQuantity;
import static an3enterprises.tapfest.MainActivity.festDiamonds;
import static an3enterprises.tapfest.MainActivity.quantity;

public class UpgradesActivity extends Activity {

    SharedPreferences tapRateSaved;
    public static Button upgradeButton;
    static int tapRate = 1;
    private RewardedVideoAd mAd;
    IabHelper mHelper;
    private static final String TAG = Settings.class.getName();
    public static String ITEM_SKU = null;
    static int diamondsNeeded;

    @Override
    protected void onStop() {
        SharedPreferences tapRateSaved = getSharedPreferences("tapRate", Context.MODE_PRIVATE);
        SharedPreferences.Editor tapRateSavedEditor = tapRateSaved.edit();
        tapRateSavedEditor.putInt("tapRate", tapRate);
        tapRateSavedEditor.commit();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_upgrades);
        SharedPreferences tapRateSaved = getSharedPreferences("tapRate", Context.MODE_PRIVATE);
        final int tapRateSavedInt = tapRateSaved.getInt("tapRate", 1);
        tapRate = tapRateSavedInt;
        upgradeButton = (Button) findViewById((R.id.upgrade_button));
        upgradeButton.setText(tapRate + getResources().getString(R.string.festCoinsPerTap));

        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                Button rewardAdButton = (Button) findViewById(R.id.reward_ad_btn);
                rewardAdButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                rewardAdButton.setEnabled(true);
            }

            @Override
            public void onRewardedVideoAdOpened() {
            }

            @Override
            public void onRewardedVideoStarted() {
            }

            @Override
            public void onRewardedVideoAdClosed() {
                Button rewardAdButton = (Button) findViewById(R.id.reward_ad_btn);
                rewardAdButton.setEnabled(false);
                loadRewardedVideoAd();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Snackbar.make(findViewById(R.id.scrollViewUpgrades), getResources().getString(R.string.earnedFestCoinsFromVideo), Snackbar.LENGTH_SHORT);
                quantity += 500000;

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
            }
        });

        loadRewardedVideoAd();

        String base64EncodedPublicKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyGZDHmNf00wEDaSFUd+iIi5Z5hx6a/QSA59r+IMY5Hymc0ZkLmXcwf04bwkd+KzVW8I6wQ27OA6RaQP9pfmHMgYXGTdKHwsgqUT6BY9tWYehNstGAVdMacOc1v/cLJDrPqIIPyqmrliZwmu/3gOiBR7TwKg1cvP29/z1lpgmcmwZO0G8f5pD5fGPqhc2A0pwW0n2y1FH1FEH8v4fDzABf2kUuy3YJhgBrB8RYgyfG/zl2dRM3XhmtsuP3D4sYFzo+vJRDx5XxKbQfB5GTiLCTcrffMtPINI52pgWVAGfD4R2zmfviXYxXwls+08f8agZdZ6VNya4ZUb7yRgmZCDgKQIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result)
                                       {
                                           if (!result.isSuccess()) {
                                               Log.d(TAG, "In-app Billing setup failed: " + result);
                                           } else {
                                               Log.d(TAG, "In-app Billing is set up OK");
                                               mHelper.enableDebugLogging(true, TAG);
                                           }
                                       }
                                   });

        upgradeButton.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                long timesCanBeBought = quantity/tapRate;
                quantity -= timesCanBeBought*tapRate;
                tapRate += timesCanBeBought;
                Snackbar.make(v, getResources().getString(R.string. youSpeedUpgraded), Snackbar.LENGTH_SHORT).show();
                upgradeButton.setText(tapRate + getResources().getString(R.string. festCoinsPerTap));
                displayQuantity();
                upgradeButton.setBackgroundColor(getResources().getColor(R.color.lightGray));
                MainActivity.displayQuantity();
                upgradeButton.setBackgroundColor(getResources().getColor(R.color.lightGray));

                return true;
            }
        });

        if (quantity < tapRate + 1) {
            upgradeButton.setBackgroundColor(getResources().getColor(R.color.lightGray));
        }

    }

    public void consumeItem() throws IabHelper.IabAsyncInProgressException {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) throws IabHelper.IabAsyncInProgressException {


            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
            }
        }
    };IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        Snackbar.make(findViewById(R.id.linear_layout_upgrades), getResources().getString(R.string. noMoreAds), Snackbar.LENGTH_LONG).show();

                    } else {
                        // handle error
                    }
                }
            };

    public void incrementTapState(View view) {
        Button upgradeButton = (Button) findViewById(R.id.upgrade_button);
        if (quantity > tapRate){
            upgradeButton.setEnabled(true);
        }
        if (quantity < tapRate + 1) {
            upgradeButton.setBackgroundColor(getResources().getColor(R.color.lightGray));
            upgradeButton.setEnabled(false);
            Snackbar.make(view, getResources().getString(R.string. dontHaveEnoughFestCoins), Snackbar.LENGTH_LONG).show();
//            final TextView mSwitcher = (TextView) findViewById(R.id.disappearing_textview);
//            mSwitcher.setText("");
//            mSwitcher.setVisibility(View.VISIBLE);
//            mSwitcher.setText("Sorry, You don't have enough dollars. So go to the main screen and start clicking!");
//            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 2.0f);
//            AlphaAnimation fadeOut = new AlphaAnimation(2.0f, 0.0f);
//            mSwitcher.startAnimation(fadeIn);
//            mSwitcher.startAnimation(fadeOut);
//            fadeIn.setDuration(2000);
//            fadeIn.setFillAfter(true);
//            fadeOut.setDuration(2000);
//            fadeOut.setFillAfter(true);
//            fadeOut.setStartOffset(2200 + fadeIn.getStartOffset());

        } else {
            quantity = quantity - tapRate;
            tapRate = tapRate + 1;
            upgradeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            upgradeButton.setText(tapRate + getResources().getString(R.string.festCoinsPerTap));
            if (quantity < tapRate + 1) {
                upgradeButton.setBackgroundColor(getResources().getColor(R.color.lightGray));
                Snackbar.make(view, getResources().getString(R.string. dontHaveEnoughFestCoins), Snackbar.LENGTH_SHORT).show();
            }
            displayQuantity();

        }

    }

    public void showRewardAd(View view) {
        mAd.show();
    }

    private void loadRewardedVideoAd() {
        mAd.loadAd("ca-app-pub-7638825445174820/2783317190", new AdRequest.Builder().build());
    }

    public void buyFestCoins50(final View view) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(UpgradesActivity.this);
        builder.setTitle(getResources().getString(R.string.buyFiftyThousandFestCoins));
        if (festDiamonds >= 80) {
            builder.setMessage(getResources().getString(R.string.costEightyFestDiamonds));
            builder.setPositiveButton(getResources().getString(R.string. buy), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    festDiamonds -= 80;
                    quantity += 50000;
                    Snackbar.make(view, getResources().getString(R.string. justBoughtFiftyThousand), Snackbar.LENGTH_LONG).show();
                }
            });
        }if (festDiamonds < 80) {
            diamondsNeeded = 80 - festDiamonds;
            builder.setMessage(getResources().getString(R.string. sorryYouNeed) + diamondsNeeded + getResources().getString(R.string. moreFestDiamonds));
            builder.setPositiveButton(getResources().getString(R.string. buyFestDiamonds), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    determinePurchaseAmount(diamondsNeeded);
                }
            });
            builder.setNegativeButton(getResources().getString(R.string. OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }builder.show();
        displayFestDiamonds();
        displayQuantity();
    }

    public void buyFestCoins200(final View view) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(UpgradesActivity.this);
        builder.setTitle(getResources().getString(R.string. buyTwoHundredThousandFestCoins));
        if (festDiamonds >= 200) {
            builder.setMessage(getResources().getString(R.string. costTwoHundredDiamonds));
            builder.setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    festDiamonds -= 200;
                    quantity += 200000;
                    Snackbar.make(view, getResources().getString(R.string. justBoughtTwoHundredThousand), Snackbar.LENGTH_LONG)
                            .show();
                }
            });
        }if (festDiamonds < 200) {
            diamondsNeeded = 200 - festDiamonds;
            builder.setMessage(getResources().getString(R.string. sorryYouNeed) + diamondsNeeded + getResources().getString(R.string. moreFestDiamonds));
            builder.setPositiveButton(getResources().getString(R.string. buyFestDiamonds), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    determinePurchaseAmount(diamondsNeeded);
                }
            });
            builder.setNegativeButton(getResources().getString(R.string. OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }builder.show();
        displayFestDiamonds();
        displayQuantity();
    }

    public void buyFestCoins500(final View view) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(UpgradesActivity.this);
        builder.setTitle(getResources().getString(R.string. five_hundred_thousand_festcoins));
        if (festDiamonds >= 500) {
            builder.setMessage(getResources().getString(R.string. costFiveHundredDiamonds));
            builder.setPositiveButton(getResources().getString(R.string. buy), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    festDiamonds -= 500;
                    quantity += 500000;
                    Snackbar.make(view, getResources().getString(R.string. justBoughtFiveHundredThousand), Snackbar.LENGTH_LONG).show();
                }
            });
        }if (festDiamonds < 500) {
            diamondsNeeded = 500 - festDiamonds;
            builder.setMessage(getResources().getString(R.string. sorryYouNeed) + diamondsNeeded + getResources().getString(R.string. moreFestDiamonds));
            builder.setPositiveButton(getResources().getString(R.string. buyFestDiamonds), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    determinePurchaseAmount(diamondsNeeded);
                }
            });
            builder.setNegativeButton(getResources().getString(R.string. OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }builder.show();
        displayFestDiamonds();
        displayQuantity();
    }

    public void buyFestCoins1000(final View view) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(UpgradesActivity.this);
        builder.setTitle(getResources().getString(R.string.buyOneMillionFestCoins));
        if (festDiamonds >= 1000) {
            builder.setMessage(getResources().getString(R.string. costOneThousandFestDiamonds));
            builder.setPositiveButton(getResources().getString(R.string.buy), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    festDiamonds -= 1000;
                    quantity += 1000000;
                    Snackbar.make(view, getResources().getString(R.string.justBoughtOneMillion), Snackbar.LENGTH_LONG).show();
                }
            });
        }if (festDiamonds < 1000) {
            diamondsNeeded = 1000 - festDiamonds;
            builder.setMessage(getResources().getString(R.string.sorryYouNeed) + diamondsNeeded + getResources().getString(R.string. moreFestDiamonds));
            builder.setPositiveButton(getResources().getString(R.string. buyFestDiamonds), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    determinePurchaseAmount(diamondsNeeded);
                }
            });
            builder.setNegativeButton(getResources().getString(R.string. OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }builder.show();
        displayFestDiamonds();
        displayQuantity();
    }

    public void buyFestCoins1000000(final View view) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(UpgradesActivity.this);
        builder.setTitle(getResources().getString(R.string. buyOneBillionFestCoins));
        if (festDiamonds >= 1000000) {
            builder.setMessage(getResources().getString(R.string. costOneMillionDiamonds));
            builder.setPositiveButton(getResources().getString(R.string. buy), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    festDiamonds -= 1000000;
                    quantity += 1000000000;
                    Snackbar.make(view, getResources().getString(R.string. justBoughtOneBillion), Snackbar.LENGTH_LONG).show();
                }
            });
        }if (festDiamonds < 1000000) {
            diamondsNeeded = 1000000 - festDiamonds;
            builder.setMessage(getResources().getString(R.string. sorryYouNeed) + diamondsNeeded + getResources().getString(R.string. moreFestDiamonds));
            builder.setPositiveButton(getResources().getString(R.string. buyFestDiamonds), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    determinePurchaseAmount(diamondsNeeded);
                }
            });
            builder.setNegativeButton(getResources().getString(R.string. OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }builder.show();
        displayFestDiamonds();
        displayQuantity();
    }

    public void buyFestDiamondseighty(View view) throws IabHelper.IabAsyncInProgressException {
        ITEM_SKU = "buy_fest_diamonds_80";
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "buy_fest_diamonds");
    }

    public void buyFestDiamonds5hundred(View view) throws IabHelper.IabAsyncInProgressException {
        ITEM_SKU = "buy_fest_diamonds_500";
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "buy_fest_diamonds_500");
    }

    public void buyFestDiamondsThousand(View view) throws IabHelper.IabAsyncInProgressException {
        ITEM_SKU = "buy_fest_diamonds_1000";
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "buy_fest_diamonds_1000");
    }

    public void buyFestDiamondsMillion(View view) throws IabHelper.IabAsyncInProgressException {
        ITEM_SKU = "buy_fest_diamonds_1000000";
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "buy_fest_diamonds_1000000");
    }

    @Override
    protected void onPause() {
        SharedPreferences tapRateSaved = getSharedPreferences("tapRate", Context.MODE_PRIVATE);
        SharedPreferences.Editor tapRateSavedEditor = tapRateSaved.edit();
        tapRateSavedEditor.putInt("tapRate", tapRate);
        tapRateSavedEditor.commit();
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
        SharedPreferences tapRateSaved = getSharedPreferences("tapRate", Context.MODE_PRIVATE);
        SharedPreferences.Editor tapRateSavedEditor = tapRateSaved.edit();
        tapRateSavedEditor.putInt("tapRate", tapRate);
        tapRateSavedEditor.commit();
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
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences tapRateSaved = getSharedPreferences("tapRate", Context.MODE_PRIVATE);
        SharedPreferences.Editor tapRateSavedEditor = tapRateSaved.edit();
        tapRateSavedEditor.putInt("tapRate", tapRate);
        tapRateSavedEditor.commit();
        SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
        SharedPreferences.Editor quantitySavedEditor = quantitySaved.edit();
        quantitySavedEditor.putLong("quantity", quantity);
        quantitySavedEditor.commit();
        SharedPreferences festDiamondsSP = getSharedPreferences("festDiamonds", Context.MODE_PRIVATE);
        SharedPreferences.Editor festDiamondsEditor = festDiamondsSP.edit();
        festDiamondsEditor.putLong("festDiamonds", festDiamonds);
        festDiamondsEditor.commit();
        displayFestDiamonds();
        displayQuantity();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        SharedPreferences tapRateSaved = getSharedPreferences("tapRate", Context.MODE_PRIVATE);
        SharedPreferences.Editor tapRateSavedEditor = tapRateSaved.edit();
        tapRateSavedEditor.putInt("tapRate", tapRate);
        tapRateSavedEditor.commit();
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
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        try {
            if (!mHelper.handleActivityResult(requestCode,
                    resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) throws IabHelper.IabAsyncInProgressException {
            if (result.isFailure()) {
                // Handle error
                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
            }

        }
    };

    public void determinePurchaseAmount(int amountNeeded) {
        if (amountNeeded <= 80) {
           // Log.i("UpgradesActivity", "buy 80");
            Button buyFestDiamonds80Btn = (Button) findViewById(R.id.buy_festdiamonds_80);
            try {
                buyFestDiamondseighty(buyFestDiamonds80Btn);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
        if (amountNeeded <= 500 && amountNeeded > 80) {
            //Log.i("UpgradesActivity", "buy 500");
            Button buyFestDiamonds500Btn = (Button) findViewById(R.id.buy_festdiamonds_500);
            try {
                buyFestDiamonds5hundred(buyFestDiamonds500Btn);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
        if (amountNeeded <= 1000 && amountNeeded > 500) {
            //Log.i("UpgradesActivity", "buy 1000");
            Button buyFestDiamonds1000Btn = (Button) findViewById(R.id.buy_festdiamonds_1000);
            try {
                buyFestDiamondsThousand(buyFestDiamonds1000Btn);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
        if (amountNeeded <= 1000000 && amountNeeded > 1000000) {
            //Log.i("UpgradesActivity", "buy 10000");
            Button buyFestDiamonds1000000Btn = (Button) findViewById(R.id.buy_festdiamonds_1000000);
            try {
                buyFestDiamondsMillion(buyFestDiamonds1000000Btn);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }else{
            try {
                //Log.i("UpgradesActivity", "Else triggered");
                Button buyFestDiamonds1000000Btn = (Button) findViewById(R.id.buy_festdiamonds_1000000);
                buyFestDiamondsMillion(buyFestDiamonds1000000Btn);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
    }

    public void increaseTapRateAmount(final View view) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(UpgradesActivity.this);
        builder.setTitle(getResources().getString(R.string. increaseTapRateAmount));
        builder.setMessage(getResources().getString(R.string. costFiftyDiamonds));
        if (festDiamonds >= 50) {
            builder.setPositiveButton(getResources().getString(R.string. increaseAmount), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    festDiamonds -= 50;
                    tapRate += 50000;
                    Snackbar.make(view, getResources().getString(R.string. tapRateWasIncreased), Snackbar.LENGTH_LONG);
                    SharedPreferences tapRateSaved = getSharedPreferences("tapRate", Context.MODE_PRIVATE);
                    SharedPreferences.Editor tapRateSavedEditor = tapRateSaved.edit();
                    tapRateSavedEditor.putInt("tapRate", tapRate);
                    tapRateSavedEditor.commit();
                    SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
                    SharedPreferences.Editor quantitySavedEditor = quantitySaved.edit();
                    quantitySavedEditor.putLong("quantity", quantity);
                    quantitySavedEditor.commit();
                    SharedPreferences festDiamondsSP = getSharedPreferences("festDiamonds", Context.MODE_PRIVATE);
                    SharedPreferences.Editor festDiamondsEditor = festDiamondsSP.edit();
                    festDiamondsEditor.putInt("festDiamonds", festDiamonds);
                    festDiamondsEditor.commit();
                    upgradeButton.setText(tapRate + getResources().getString(R.string.festCoinsPerTap));
                    displayFestDiamonds();
                    displayQuantity();
                }
            });
            builder.setNegativeButton(getResources().getString(R.string. cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }if (festDiamonds < 50) {
            builder.setPositiveButton(getResources().getString(R.string. buyFestDiamonds), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int amountNeeded = 50 - festDiamonds;
                    determinePurchaseAmount(amountNeeded);
                }
            });
            builder.setNegativeButton(getResources().getString(R.string. OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        builder.show();
    }


}
