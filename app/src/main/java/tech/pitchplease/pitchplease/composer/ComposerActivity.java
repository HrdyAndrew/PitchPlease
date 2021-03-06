package tech.pitchplease.pitchplease.composer;

import android.animation.ObjectAnimator;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
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

    private final int PLAY_TIME = 30; //play time in seconds
    private Random rand;
    private boolean started = false;

    private String composer;
    private String trackName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!started)
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composer);

        /* //ads
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_ad_unit_id));
        final AdView mAdView = (AdView) findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder().build();
        */

        rand = new Random();

        btnSelections = new Button[4];
        btnStart = (Button) findViewById(R.id.composerBtnStart);
        btnSelections[0] = (Button) findViewById(R.id.composerBtnOne);
        btnSelections[1] = (Button) findViewById(R.id.composerBtnTwo);
        btnSelections[2] = (Button) findViewById(R.id.composerBtnThree);
        btnSelections[3] = (Button) findViewById(R.id.composerBtnFour);
        barCountdown = (ProgressBar) findViewById(R.id.composerBarCountdown);
        textScore = (TextView) findViewById(R.id.composerTextScore);

        SpotifyApi api = new SpotifyApi();
        if (started && mPlayer != null && mPlayer.isPlaying())
            mPlayer.stop();
        mPlayer = new MediaPlayer();
        mPlayer.setScreenOnWhilePlaying(true);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayer.start();
            }
        });

        spotify = api.getService();

        countDown = createCountdownTimer(PLAY_TIME * 1000, 1000);
        composers = resolveComposerMap();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started = true;
                renderGameIcons(true);
                //mAdView.loadAd(adRequest);
                startRound();
            }
        });

        for (final Button btn : btnSelections) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renderGameIcons(false);
                    if (getCorrectArtist().equals(btn.getText()))
                        startAnswer(true);
                    else
                        startAnswer(false);
                }
            });
        }

        if (started) {
            renderGameIcons(true);
            startRound();
        }
    }

    /**
     * Creates and displays the answer screen.
     *
     * @param correct Determines if the answer screen should display correct or incorrect.
     */
    private void startAnswer(boolean correct) {
        countDown.cancel();
        if (correct)
            score++;
        textScore.setText("Score: " + score + "/" + rounds);
        setContentView(R.layout.activity_composer_answer);
        TextView answerTitle = (TextView) findViewById(R.id.composerAnswerTitle);
        TextView answerMessage = (TextView) findViewById(R.id.composerAnswerMessage);
        Button nextButton = (Button) findViewById(R.id.composerAnswerNextButton);
        TextView scoreText = (TextView) findViewById(R.id.composerAnswerScoreText);
        scoreText.setText("Score: " + score + "/" + rounds);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreate(new Bundle());
            }
        });
        if (correct)
            answerTitle.setText("Correct!");
        else
            answerTitle.setText("Incorrect!");
        answerMessage.setText(Html.fromHtml("The piece is <b>" + trackName + "</b> by " +
                "<a href=\"http://en.wikipedia.org/w/index.php?title=Special%3ASearch&search=" + (composer.replaceAll("_", " ").equals("John Adams") ? composer.replaceAll("_", " ") + " (composer)" : composer.replaceAll("_", " ")) + "\"><b>" + composer.replaceAll("_", " ") + "</b></a>"));
        answerMessage.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Starts a round of the Composer guessing game.
     */
    private void startRound() {
        barCountdown.setProgress(PLAY_TIME * 10000);
        textScore.setText("Score: " + score + "/" + rounds);
        LinkedList<String> keys = new LinkedList<>();
        keys.addAll(composers.keySet());
        int random1 = newRandomInt(keys.size());
        int random2 = newRandomInt(keys.size(), random1);
        int random3 = newRandomInt(keys.size(), random1, random2);
        composer = keys.get(random1);
        String alt1 = keys.get(random2);
        String alt2 = keys.get(random3);
        String alt3 = keys.get(newRandomInt(keys.size(), random1, random2, random3));

        random1 = newRandomInt(btnSelections.length);
        random2 = newRandomInt(btnSelections.length, random1);
        random3 = newRandomInt(btnSelections.length, random1, random2);

        Button btnComposer = btnSelections[random1];
        Button btnAlt1 = btnSelections[random2];
        Button btnAlt2 = btnSelections[random3];
        Button btnAlt3 = btnSelections[newRandomInt(btnSelections.length, random1, random2, random3)];

        btnComposer.setText(composer.replaceAll("_", " "));
        btnAlt1.setText(alt1.replaceAll("_", " "));
        btnAlt2.setText(alt2.replaceAll("_", " "));
        btnAlt3.setText(alt3.replaceAll("_", " "));

        correctArtist = composer.replaceAll("_", " ");
        playRandomTrackFromArtist(composers.get(composer));
        animateProgressBar(0, (PLAY_TIME - 1) * 1000);
        countDown.start();

        rounds++;
    }


    /**
     * @param count    The number of possible results
     * @param previous List of values that have already been generated and cannot be generated
     * @return An integer between between 0 (inclusive) and 'count' (exclusive) that is not contained in 'previous'
     * @throws IllegalArgumentException - if the size of 'previous' is greater than or equal to 'count'
     */
    private int newRandomInt(int count, int... previous) {
        if (previous.length >= count)
            throw new IllegalArgumentException("The size of 'previous' cannot be greater than or equal to 'count'");
        int num;
        boolean pass;
        dowhile:
        do {
            num = rand.nextInt(count);
            pass = false;
            for (int i : previous)
                if (num == i)
                    continue dowhile;
            pass = true;
        } while (!pass);
        return num;
    }

    /**
     * Renders icons for base playing screen
     *
     * @param bool If true sets icons for game playing state, else sets to start state.
     */
    private void renderGameIcons(boolean bool) {
        btnStart.setVisibility(bool ? View.GONE : View.VISIBLE);
        for (Button btn : btnSelections) {
            btn.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
        }
        barCountdown.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
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
                startAnswer(false);
            }
        };
    }

    @Override
    public void onStop() {
        try {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        countDown.cancel();
        super.onStop();
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
     * Creates a {@link Map} of Composer to Spotify Artist ID based on the composers string (found in strings.xml).
     *
     * @return A {@link Map} of Composer to ID.
     */
    private Map<String, String> resolveComposerMap() {
        Map<String, String> composers = new HashMap<>();

        Scanner scanner = new Scanner(getResources().getString(R.string.composers));
        while (scanner.hasNext()) {
            composers.put(scanner.next(), scanner.next());
        }

        return composers;
    }

    /**
     * Plays a random track from any of the artist's spotify albums.
     *
     * @param artistID The SPOTIFY artist ID to play a random track for.
     */
    private void playRandomTrackFromArtist(final String artistID) {
        spotify.getArtistAlbums(artistID, new Callback<Pager<Album>>() {
            @Override
            public void success(Pager<Album> albumPager, Response response) {
                Album album = albumPager.items.get(rand.nextInt(albumPager.items.size()));
                spotify.getAlbumTracks(album.id, new Callback<Pager<Track>>() {
                    @Override
                    public void success(Pager<Track> trackPager, Response response) {
                        Track track = trackPager.items.get(rand.nextInt(trackPager.items.size()));
                        trackName = track.name;
                        if (track.preview_url == null) {
                            playRandomTrackFromArtist(artistID);
                            return;
                        }
                        try {
                            mPlayer.reset();
                            mPlayer.setDataSource(track.preview_url);
                            mPlayer.prepareAsync();
                        } catch (IOException e) {
                            Log.e("PitchPlease_MediaPlayer", "Failure playing the track.");
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("PitchPlease_TrackPlayer", "Failure resolving a track to play.");
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("PitchPlease_AlbumPlayer", "Failure resolving a album to play.");
            }
        });
    }

    /**
     * Returns the correct artist for the current round.
     *
     * @return A string of the correct artist's name.
     */
    private String getCorrectArtist() {
        return correctArtist;
    }
}