package tech.pitchplease.pitchplease;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.SettingsApi;

import tech.pitchplease.pitchplease.composer.ComposerActivity;
import tech.pitchplease.pitchplease.intervaltraining.IntervalTrainingActivity;
import tech.pitchplease.pitchplease.pitchplayer.PitchPlayerActivity;
import tech.pitchplease.pitchplease.pitchtraining.PitchTrainingActivity;
import tech.pitchplease.pitchplease.settings.SettingsActivity;


public class MenuActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //ads
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_ad_unit_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabPreferences);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        createViewActivitySwitch(findViewById(R.id.btnPitchPlayer), PitchPlayerActivity.class, "pitchPlayerTag");
        createViewActivitySwitch(findViewById(R.id.btnPitchGuesser), PitchTrainingActivity.class, "pitchGuesserTag");
        createViewActivitySwitch(findViewById(R.id.btnIntervalGuesser), IntervalTrainingActivity.class, "intervalGuesserTag");
        //createViewActivitySwitch(R.id.btnIntervalGuesser, ClassName.class, "MyTag", new String[]{});
        createViewActivitySwitch(findViewById(R.id.btnCompGuesser), ComposerActivity.class, "composerTag");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_menu, menu);
        menu.add(0,0,0,"OMG HOW LOLOLOL");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == 0) {
            Intent intent = new Intent(context, SettingsActivity.class);
            context.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates an on click event tied to the {@link View} to change the current activity (window).
     *
     * @param view     The view to attach the onclick listener to.
     * @param activity The class that contains the targeted activity.
     * @param tagName  The base name for the state variables.
     * @param states   State variables to pass to the new activity.
     *                 <p>
     *                 State variables will be passed with the following tag: tagName + [index of state in states]
     *                 Example: tagName = "tag", states = {"Hi", "There", "Friend"}
     *                 Three tags will exist: tag0 -> "Hi", tag1 -> "There", tag2 -> "Friend"
     */
    private void createViewActivitySwitch(View view, final Class activity, final String tagName, final String... states) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, activity);
                for (int i = 0; i < states.length; i++) {
                    intent.putExtra(tagName + i, states[i]);
                }
                context.startActivity(intent);
            }
        });
    }
}
