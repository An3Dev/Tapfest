package an3enterprises.tapfest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import an3enterprises.tapfest.util.IabHelper;
import an3enterprises.tapfest.util.IabResult;
import an3enterprises.tapfest.util.Inventory;
import an3enterprises.tapfest.util.Purchase;

import static an3enterprises.tapfest.MainActivity.festDiamonds;
import static an3enterprises.tapfest.MainActivity.quantity;

public class Settings extends Activity {

    ArrayAdapter settings;
    ListView lv;
    List<String> g = new ArrayList<String>();
    IabHelper mHelper;
    private static final String TAG = Settings.class.getName();
    public static String ITEM_SKU = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);


        String base64EncodedPublicKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyGZDHmNf00wEDaSFUd+iIi5Z5hx6a/QSA59r+IMY5Hymc0ZkLmXcwf04bwkd+KzVW8I6wQ27OA6RaQP9pfmHMgYXGTdKHwsgqUT6BY9tWYehNstGAVdMacOc1v/cLJDrPqIIPyqmrliZwmu/3gOiBR7TwKg1cvP29/z1lpgmcmwZO0G8f5pD5fGPqhc2A0pwW0n2y1FH1FEH8v4fDzABf2kUuy3YJhgBrB8RYgyfG/zl2dRM3XhmtsuP3D4sYFzo+vJRDx5XxKbQfB5GTiLCTcrffMtPINI52pgWVAGfD4R2zmfviXYxXwls+08f8agZdZ6VNya4ZUb7yRgmZCDgKQIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (!result.isSuccess()) {
                                               Log.d(TAG, "In-app Billing setup failed: " + result);
                                           } else {
                                               Log.d(TAG, "In-app Billing is set up OK");
                                               mHelper.enableDebugLogging(true, TAG);
                                           }
                                       }
                                   });
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_settings);
        lv = (ListView) findViewById(R.id.settings_listview);
        //Add this line in 30 days.
//        g.add("\nRemove ads\n");
        g.add("\nShare\n");
        g.add("\nOther games & apps\n");
        SharedPreferences cheatCodeUsed = getSharedPreferences("isCheatCodeUsed", Context.MODE_PRIVATE);
        String cheatCodeUsedString = cheatCodeUsed.getString("isCheatCodeUsed", "false");
        if (cheatCodeUsedString.matches("false")) {
            g.add("\nCheat code\n");
        }

        settings = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, g);
        lv.setAdapter(settings);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, final long id) {
                if (g.get(position).matches("\nRemove ads\n")) {
                    try {
                        removeAds();
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
                if (g.get(position).matches("\nShare\n")) {
                    onInviteClicked();
                }
                if (g.get(position).matches("\nOther games & apps\n")) {
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:An3Enterprises")));
                    } catch (android.content.ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=An3Enterprises")));
                    }
                }
                if (g.get(position).matches("\nCheat code\n")) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(Settings.this);
                    builder.setTitle("Cheat Code");
                    builder.setMessage("Enter a cheat code");
                    final EditText input = new EditText(Settings.this);
                    input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    ListView.LayoutParams lp = new ListView.LayoutParams(ListView.LayoutParams.WRAP_CONTENT, ListView.LayoutParams.WRAP_CONTENT);
                    input.setHint("The secret cheat code");
                    input.setLayoutParams(lp);
                    builder.setView(input);
                    builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    final AlertDialog dialog = builder.create();

                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Boolean wantToCloseDialog = false;
                            if (input.getText().toString().isEmpty()) {
                                Toast.makeText(Settings.this, "The cheat code is not blank. Nice try.", Toast.LENGTH_SHORT).show();
                                View view = Settings.this.getCurrentFocus();
                                if (view != null) {
                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            }

                            if (input.getText().toString().length() > 0 && input.getText().toString().matches("La Mosca")) {
                                festDiamonds += 1000000;
                                SharedPreferences quantitySaved = getSharedPreferences("quantity", Context.MODE_PRIVATE);
                                SharedPreferences.Editor quantitySavedEditor = quantitySaved.edit();
                                quantitySavedEditor.putLong("quantity", quantity);
                                quantitySavedEditor.commit();
                                SharedPreferences festDiamondsSP = getSharedPreferences("festDiamonds", Context.MODE_PRIVATE);
                                SharedPreferences.Editor festDiamondsEditor = festDiamondsSP.edit();
                                festDiamondsEditor.putInt("festDiamonds", festDiamonds);
                                festDiamondsEditor.commit();
                                SharedPreferences cheatCodeUsed = getSharedPreferences("isCheatCodeUsed", Context.MODE_PRIVATE);
                                SharedPreferences.Editor cheatCodeUsedEditor = cheatCodeUsed.edit();
                                cheatCodeUsedEditor.putString("isCheatCodeUsed", "true");
                                cheatCodeUsedEditor.commit();
                                Snackbar.make(findViewById(R.id.linearlayout_settings), "You just earned 1,000,000(M) FestDiamonds! Use them wisely.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                wantToCloseDialog = true;


                            } if(!input.getText().toString().isEmpty() && input.getText().toString().length() != 0 && !input.getText().toString().matches("La Mosca")) {
                                Toast.makeText(Settings.this, "That's not the cheat code...Sorry.", Toast.LENGTH_SHORT).show();
                                getWindow().setSoftInputMode(
                                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                );
                            }
                            if (wantToCloseDialog) {
                                dialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
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
                        Toast.makeText(Settings.this, "Success", Toast.LENGTH_SHORT).show();
                        SharedPreferences adsGone = getSharedPreferences("adsGone", Context.MODE_PRIVATE);
                        SharedPreferences.Editor adsGoneEditor = adsGone.edit();
                        adsGoneEditor.putString("adsGone", "true");
                    } else {
                        // handle error
                    }
                }
            };


    public void removeAds() throws IabHelper.IabAsyncInProgressException {

        ITEM_SKU = "no_ads_purchase";
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "no_ads_purchase");

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

    private void onInviteClicked() {
        String url = "https://play.google.com/store/apps/details?id=an3enterprises.guessthenumber";
        String shareBody = getResources().getString(R.string.you_should_install_tapfest);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_name);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody + " " + url);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }


}
