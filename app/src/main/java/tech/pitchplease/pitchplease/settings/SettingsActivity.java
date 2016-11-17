package tech.pitchplease.pitchplease.settings;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.os.Bundle;

import tech.pitchplease.pitchplease.R;

/**
 * Created by Sanchez on 11/15/2016.
 */

public class SettingsActivity extends AppCompatActivity {

    private Button darkMode;
    private Button btnSettings;
    private boolean isDark = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composer);
    }

    public void changeMode(){
        if(isDark){
            isDark = true;
            android.provider.Settings.System.putInt(this.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, 200);
        }
        else {
            isDark = false;
            android.provider.Settings.System.putInt(this.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, 500);
        }
    }



    /*true = on
    * false= off
    * */
    public boolean getMode(){
        return isDark;
    }
    private void renderGameIcons(boolean bool) {
        darkMode.setVisibility(bool ? View.GONE : View.VISIBLE);

    }

    //protected void onCreate(Bundle savedInstanceState){
        //darkMode = (Button) findViewById(R.id.button); //change this
        /*darkMode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                changeMode();}});*/
    //}




}
