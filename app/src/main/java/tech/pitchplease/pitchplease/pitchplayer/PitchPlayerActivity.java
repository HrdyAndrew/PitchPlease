package tech.pitchplease.pitchplease.pitchplayer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import tech.pitchplease.pitchplease.Pitch;
import tech.pitchplease.pitchplease.R;

public class PitchPlayerActivity extends AppCompatActivity {
    private SeekBar noteSeekBar;
    private TextView noteText;
    private Button btnSubmit;

    private Pitch correctPitch = new Pitch(4, Pitch.PitchName.C);

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
        setContentView(R.layout.activity_pitchplayer);
        noteSeekBar = (SeekBar) findViewById(R.id.pitchPlayerSeek);
        noteText = (TextView) findViewById(R.id.noteText);
        btnSubmit = (Button) findViewById(R.id.pitchPlayerBtnSubmit);

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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playPitch) {
                    audioTrack.stop();
                    playPitch = false;
                }
                playChosenPitch();
            }
        });
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

    private void playChosenPitch() {
        correctPitch = new Pitch(4, Pitch.getPitchFromTransposition(noteSeekBar.getProgress()));
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
}
