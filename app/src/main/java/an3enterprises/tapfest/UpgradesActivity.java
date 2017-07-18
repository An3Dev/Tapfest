package an3enterprises.tapfest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class UpgradesActivity extends AppCompatActivity {

    SharedPreferences tapRateSaved;
    public static Button upgradeButton;
    static int tapRate = 1;
    private RewardedVideoAd mAd;


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
        upgradeButton.setText("$" + tapRate + " per tap");
        if (tapRate == 1){
            Toast.makeText(UpgradesActivity.this, "Tap above to increase dollars per tap.^", Toast.LENGTH_LONG).show();
        }
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
                Toast.makeText(UpgradesActivity.this, "Reward video", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoStarted() {
                Toast.makeText(UpgradesActivity.this, "Reward video started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdClosed() {
                Button rewardAdButton = (Button) findViewById(R.id.reward_ad_btn);
                rewardAdButton.setEnabled(false);
                loadRewardedVideoAd();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Toast.makeText(UpgradesActivity.this, "You just received $50k for watching the video!", Toast.LENGTH_SHORT).show();
                MainActivity.quantity += 50000;

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                Toast.makeText(UpgradesActivity.this, "Video left application", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Toast.makeText(UpgradesActivity.this, "Video failed to load. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });

        loadRewardedVideoAd();

    }

    public void incrementTapState(View view) {
        Button upgradeButton = (Button) findViewById(R.id.upgrade_button);
        if (MainActivity.quantity < tapRate + 1) {
            upgradeButton.setBackgroundColor(getResources().getColor(R.color.lightGray));
            Toast.makeText(UpgradesActivity.this, "You don't have enough dollars.", Toast.LENGTH_SHORT).show();
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
            MainActivity.quantity = MainActivity.quantity - tapRate;
            tapRate = tapRate + 1;
            upgradeButton.setText("$" + tapRate + " per tap");
            MainActivity.num.setText("$" + MainActivity.quantity);

        }

    }

    public void showRewardAd(View view) {
        mAd.show();
    }

    private void loadRewardedVideoAd() {
        mAd.loadAd("ca-app-pub-7638825445174820/2783317190", new AdRequest.Builder().build());
    }

}
