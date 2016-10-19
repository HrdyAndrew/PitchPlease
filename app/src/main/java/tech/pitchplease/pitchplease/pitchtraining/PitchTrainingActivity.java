package tech.pitchplease.pitchplease.pitchtraining;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import tech.pitchplease.pitchplease.Pitch;
import tech.pitchplease.pitchplease.R;

public class PitchTrainingActivity extends AppCompatActivity {
    private SeekBar noteSeekBar;
    private TextView noteText;
    private TextView textScore;
    private ProgressBar barCountdown;
    private Button btnStart;
    private Button btnSubmit;
    private CountDownTimer countdown;

    private Pitch correctPitch;
    private int score = 0;
    private int rounds = 0;

    private static final int GUESS_TIME = 30; //In seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_training);
        noteSeekBar = (SeekBar) findViewById(R.id.pitchTrainingSeek);
        noteText = (TextView) findViewById(R.id.noteText);
        textScore = (TextView) findViewById(R.id.pitchtrainingTextScore);
        barCountdown = (ProgressBar) findViewById(R.id.pitchtrainingBarCountdown);
        btnStart = (Button) findViewById(R.id.pitchtrainingBtnStart);
        btnSubmit = (Button) findViewById(R.id.pitchTrainingBtnSubmit);
        countdown = createCountdownTimer(GUESS_TIME * 1000, 1000);

        noteSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                noteText.setText(Pitch.getPitchFromTransposition(progress).name());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renderGameIcons(true);
                startRound();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Pitch.getPitchFromTransposition(noteSeekBar.getProgress()) == correctPitch.getPitchName()) {
                    score++;
                }
                startRound();
            }
        });
    }

    /**
     * Creates a new cooldown timer based on the passed params. Timer will end the round if time runs out.
     *
     * @param milliseconds Length in milliseconds for each iteration of the timer.
     * @param tickInterval Calls the onTick method this often (in milliseconds).
     * @return A new instance of {@link CountDownTimer}.
     */
    private CountDownTimer createCountdownTimer(long milliseconds, long tickInterval) {
        return new CountDownTimer(milliseconds, tickInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                startRound();
            }
        };
    }

    private void startRound() {
        barCountdown.setProgress(GUESS_TIME * 10000);
        animateProgressBar(0, (GUESS_TIME - 1) * 10000);
        textScore.setText("Score: " + score + "/" + rounds);
        correctPitch = playRandomPitch();
        rounds++;
    }

    private Pitch playRandomPitch() {
        //TODO Actually play something..
        return new Pitch(0, Pitch.PitchName.C);
    }

    /**
     * Smoothly animates the progress bar to a designated progress over a designated period of time.
     *
     * @param progress Progress to animate to.
     * @param duration Duration (in milliseconds) of the animation.
     */
    private void animateProgressBar(int progress, int duration) {
        ObjectAnimator animation = ObjectAnimator.ofInt(barCountdown, "progress", progress);
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());
        animation.start();
    }

    /**
     * Renders icons for base playing screen
     *
     * @param bool If true sets icons for game playing state, else sets to start state.
     */
    private void renderGameIcons(boolean bool) {
        btnStart.setVisibility(bool ? View.GONE : View.VISIBLE);

        btnSubmit.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
        noteSeekBar.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
        noteText.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
        barCountdown.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
    }
}
