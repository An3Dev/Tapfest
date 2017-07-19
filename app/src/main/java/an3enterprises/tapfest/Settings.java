package an3enterprises.tapfest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import util.IabHelper;
import util.IabResult;
import util.Inventory;
import util.Purchase;

public class Settings extends AppCompatActivity {

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
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0ssKfxSFh9e3AQQkOIahsCAyonPoexSO9d/oEpfXOHVZKGGr+41nXjhQry9ke5rQtelm+knOFmnM4MPzjTysWgTYyu168HJHA6nfwiD50ZVrAAhQwJJubF2ur4MqMllA/RZPLuGMqnYORJ/u7xHmf4u73jHH29xtIyE7YQHB80EgGe5pnYXNRlOHklYLQh6AhU+5KSsYZWqTAUSAda2cK+ZRR2I/LyFkt9Vw0sSBQJGd3gk99lkBvbKF3mulKobUMhBfvn8/KuFOnzqBtMqjTG55o5sj1Sm4WUg2OV3sWHAvTYLorkIL5Zbmpyt5JTez/Jh6AFFbO3biBzKOWLICGwIDAQAB";


        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

        setContentView(R.layout.activity_settings);
        lv = (ListView) findViewById(R.id.settings_listview);
        g.add("\nRemove ads\n");
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
            }
        });
    }

    public void removeAds() throws IabHelper.IabAsyncInProgressException {
        ITEM_SKU = "donation99";
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "no_ads_purchase");
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
                    } else {
                        // handle error
                    }
                }
            };
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
}
