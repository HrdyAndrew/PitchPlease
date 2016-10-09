package tech.pitchplease.pitchplease.composer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import tech.pitchplease.pitchplease.R;

public class ComposerActivity extends AppCompatActivity {
    private Button btnStart;
    private Button btnOne;
    private Button btnTwo;
    private Button btnThree;
    private Button btnFour;
    private ProgressBar barCountdown;

    private CountDownTimer countDown;

    private Map<String, String> composers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composer);

        btnStart = (Button)findViewById(R.id.btnStart);
        btnOne = (Button)findViewById(R.id.btnOne);
        btnTwo = (Button)findViewById(R.id.btnTwo);
        btnThree = (Button)findViewById(R.id.btnThree);
        btnFour = (Button)findViewById(R.id.btnFour);
        barCountdown = (ProgressBar)findViewById(R.id.barCountdown);

        countDown = createCountdownTimer(30000, 1000);
        composers = resolveComposerMap();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renderGameIcons(true);
                countDown.start();
            }
        });
    }

    private void renderGameIcons(boolean bool) {
        btnStart.setVisibility(bool ? View.INVISIBLE : View.VISIBLE);
        btnOne.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
        btnTwo.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
        btnThree.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
        btnFour.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
        barCountdown.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
    }

    private CountDownTimer createCountdownTimer(long milliseconds, long tickInterval) {
        return new CountDownTimer(milliseconds, tickInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                barCountdown.setProgress((int)millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {

            }
        };
    }

    private Map<String, String> resolveComposerMap() {
        Map<String, String> composers = new HashMap<>();

        InputStream stream = null;
        try {
            stream = getResources().openRawResource(getResources().getIdentifier("ComposerIDs", "raw", getPackageName()));
            Scanner scanner = new Scanner(stream);
            while (scanner.hasNextLine()) {
                composers.put(scanner.nextLine(), scanner.nextLine());
                scanner.nextLine();
            }
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    //TODO Do something.
                }
            }
        }

        return composers;
    }
}
