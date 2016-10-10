package tech.pitchplease.pitchplease.composer;

import android.animation.ObjectAnimator;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import tech.pitchplease.pitchplease.R;

public class ComposerActivity extends AppCompatActivity {
    private Button btnStart;
    private Button[] btnSelections;
    private ProgressBar barCountdown;
    private TextView textScore;

    private CountDownTimer countDown;

    private Map<String, String> composers;
    private SpotifyService spotify;
    private MediaPlayer mPlayer;

    private String correctArtist = "";
    private int score = 0;
    private int rounds = 0;
    private int sessionID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composer);

        btnSelections = new Button[4];
        btnStart = (Button) findViewById(R.id.btnStart);
        btnSelections[0] = (Button) findViewById(R.id.btnOne);
        btnSelections[1] = (Button) findViewById(R.id.btnTwo);
        btnSelections[2] = (Button) findViewById(R.id.btnThree);
        btnSelections[3] = (Button) findViewById(R.id.btnFour);
        barCountdown = (ProgressBar) findViewById(R.id.barCountdown);
        textScore = (TextView) findViewById(R.id.textScore);

        SpotifyApi api = new SpotifyApi();
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mp == mPlayer) {
                    mPlayer.start();
                }
            }
        });

        spotify = api.getService();

        countDown = createCountdownTimer(30000, 1000);
        composers = resolveComposerMap();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renderGameIcons(true);
                startRound();
            }
        });

        for (final Button btn : btnSelections) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getCorrectArtist().equals(btn.getText())) {
                        score++;
                    }
                    startRound();
                }
            });
        }
    }

    private void startRound() {
        animateProgressBar(30);
        textScore.setText("Score: " + score + "/" + rounds);
        mPlayer.stop();
        Object[] keys = composers.keySet().toArray();
        String composer = (String) keys[(int) (Math.random() * keys.length)];
        String alt1 = (String) keys[(int) (Math.random() * keys.length)];
        String alt2 = (String) keys[(int) (Math.random() * keys.length)];
        String alt3 = (String) keys[(int) (Math.random() * keys.length)];

        while (composer.equals(alt1)) {
            alt1 = (String) keys[(int) (Math.random() * keys.length)];
        }
        while (composer.equals(alt2)) {
            alt2 = (String) keys[(int) (Math.random() * keys.length)];
        }
        while (composer.equals(alt3)) {
            alt3 = (String) keys[(int) (Math.random() * keys.length)];
        }

        Button btnComposer = btnSelections[(int) (Math.random() * btnSelections.length)];
        Button btnAlt1 = btnSelections[(int) (Math.random() * btnSelections.length)];
        Button btnAlt2 = btnSelections[(int) (Math.random() * btnSelections.length)];
        Button btnAlt3 = btnSelections[(int) (Math.random() * btnSelections.length)];

        while (btnComposer == btnAlt1) {
            btnAlt1 = btnSelections[(int) (Math.random() * btnSelections.length)];
        }
        while (btnComposer == btnAlt2) {
            btnAlt2 = btnSelections[(int) (Math.random() * btnSelections.length)];
        }
        while (btnComposer == btnAlt3) {
            btnAlt3 = btnSelections[(int) (Math.random() * btnSelections.length)];
        }

        btnComposer.setText(composer.replaceAll("_", " "));
        btnAlt1.setText(alt1.replaceAll("_", " "));
        btnAlt2.setText(alt2.replaceAll("_", " "));
        btnAlt3.setText(alt3.replaceAll("_", " "));

        correctArtist = composer.replaceAll("_", " ");
        playRandomTrackFromArtist(composers.get(composer));
        countDown.start();

        rounds++;
    }

    private void renderGameIcons(boolean bool) {
        btnStart.setVisibility(bool ? View.INVISIBLE : View.VISIBLE);
        for (Button btn : btnSelections) {
            btn.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
        }
        barCountdown.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
    }

    private CountDownTimer createCountdownTimer(long milliseconds, long tickInterval) {
        return new CountDownTimer(milliseconds, tickInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int)millisUntilFinished/1000;
                animateProgressBar(progress <=0 ? 30 : progress);
            }

            @Override
            public void onFinish() {
                startRound();
            }
        };
    }

    private void animateProgressBar(int progress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(barCountdown, "progress", progress);
        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private Map<String, String> resolveComposerMap() {
        Map<String, String> composers = new HashMap<>();

        Scanner scanner = new Scanner(getResources().getString(R.string.composers));
        while (scanner.hasNext()) {
            composers.put(scanner.next(), scanner.next());
        }

        return composers;
    }

    private void playRandomTrackFromArtist(final String artistID) {
        spotify.getArtistAlbums(artistID, new Callback<Pager<Album>>() {
            @Override
            public void success(Pager<Album> albumPager, Response response) {
                Album album = albumPager.items.get((int) (Math.random() * albumPager.items.size()));
                spotify.getAlbumTracks(album.id, new Callback<Pager<Track>>() {
                    @Override
                    public void success(Pager<Track> trackPager, Response response) {
                        Track track = trackPager.items.get((int) (Math.random() * trackPager.items.size()));
                        if (track.preview_url == null) {
                            playRandomTrackFromArtist(artistID);
                            return;
                        }
                        try {
                            mPlayer.setAudioSessionId(++sessionID);
                            mPlayer.setDataSource(getApplicationContext(), Uri.parse(track.preview_url));
                            mPlayer.prepare();
                            mPlayer.start();
                        } catch (IOException e) {
                            System.out.println("IOEX");
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //TODO
                        System.out.println("FAILURE");
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                //TODO
                System.out.println("FAILURE");
            }
        });
    }

    private String getCorrectArtist() {
        return correctArtist;
    }
}
