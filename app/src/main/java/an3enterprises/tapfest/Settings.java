package an3enterprises.tapfest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import an3enterprises.tapfest.util.IabHelper;
import an3enterprises.tapfest.util.IabResult;
import an3enterprises.tapfest.util.Inventory;
import an3enterprises.tapfest.util.Purchase;

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
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_settings);
        lv = (ListView) findViewById(R.id.settings_listview);
        g.add("\nRemove ads\n");
        g.add("\nShare\n");
        g.add("\nOther games & apps\n");
        settings = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, g);
        lv.setAdapter(settings);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (g.get(position).matches("\nRemove ads\n")){
                    try {
                        removeAds();
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
                if (g.get(position).matches("\nShare\n")){
                    onInviteClicked();
                }
                if(g.get(position).matches("\nOther games & apps\n")){
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:An3Enterprises")));
                    } catch (android.content.ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=An3Enterprises")));
                    }
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
        Toast.makeText(Settings.this, "Remove ads", Toast.LENGTH_SHORT).show();

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
        String shareBody = "Hey you should play Tapfest by An3Enterprises. Its really fun!";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_name);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody + " " + url);
        startActivity(Intent.createChooser(sharingIntent, "Share Tapfest using..."));
    }


}
