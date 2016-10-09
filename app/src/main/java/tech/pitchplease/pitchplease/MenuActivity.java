package tech.pitchplease.pitchplease;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import tech.pitchplease.pitchplease.composer.ComposerActivity;


public class MenuActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabPreferences);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //createViewActivitySwitch(R.id.btnPitchPlayer, ClassName.class, "MyTag", new String[]{});
        //createViewActivitySwitch(R.id.btnPitchGuesser, ClassName.class, "MyTag", new String[]{});
        //createViewActivitySwitch(R.id.btnIntervalGuesser, ClassName.class, "MyTag", new String[]{});
        createViewActivitySwitch(findViewById(R.id.btnCompGuesser), ComposerActivity.class, "composerTag", new String[]{});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
