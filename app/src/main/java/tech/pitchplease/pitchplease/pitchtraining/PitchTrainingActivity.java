package tech.pitchplease.pitchplease.pitchtraining;

import android.animation.ObjectAnimator;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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

    private Pitch correctPitch = new Pitch(0, Pitch.PitchName.C);
    private int score = 0;
    private int rounds = 0;

    private static final int GUESS_TIME = 30; //In seconds

    private boolean playPitch = false;
    private final int sampleRate = 8000;
    private final int numSamples = GUESS_TIME * sampleRate;
    private final double sample[] = new double[numSamples];

    private final byte generatedSnd[] = new byte[2 * numSamples];
    private AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
            sampleRate, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
            AudioTrack.MODE_STATIC);


    Handler handler = new Handler();


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
                playPitch = false;
                audioTrack.stop();
                if (Pitch.getPitchFromTransposition(noteSeekBar.getProgress()) == correctPitch.getPitchName()) {
                    score++;
                }
                startRound();
            }
        });
        renderGameIcons(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (playPitch) {
            playPitch();
        }
    }

    @Override
    public void onStop() {
        if (playPitch) {
            audioTrack.stop();
        }
        countdown.cancel();
        super.onStop();
    }

    void genTone(){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/correctPitch.getFrequency(correctPitch.getOctave())));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    void playSound(){
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);

        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
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
                playPitch = false;
                if (Pitch.getPitchFromTransposition(noteSeekBar.getProgress()) == correctPitch.getPitchName()) {
                    score++;
                }
                startRound();
            }
        };
    }

    private void startRound() {
        barCountdown.setProgress(GUESS_TIME * 10000);
        animateProgressBar(0, (GUESS_TIME - 1) * 1000);
        textScore.setText("Score: " + score + "/" + rounds);
        playRandomPitch();
        countdown.start();
        rounds++;
    }

    private void playRandomPitch() {
        correctPitch = new Pitch(4, Pitch.getPitchFromTransposition((int) (Math.random() * 12)));
        playPitch = true;
        playPitch();
    }

    private void playPitch() {
        // Use a new tread as this can take a while
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                genTone();
                handler.post(new Runnable() {

                    public void run() {
                        playSound();
                    }
                });
            }
        });
        thread.start();
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
