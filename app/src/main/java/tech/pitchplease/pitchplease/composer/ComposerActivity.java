package tech.pitchplease.pitchplease.composer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import tech.pitchplease.pitchplease.R;

public class ComposerActivity extends AppCompatActivity{
    private Button btnStart;
    private Button btnOne;
    private Button btnTwo;
    private Button btnThree;
    private Button btnFour;
    private ProgressBar barCountdown;

    private CountDownTimer countDown;

    private Map<String, String> composers;
    private SpotifyService spotify;
    private MediaPlayer mPlayer;

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

        //countDown = createCountdownTimer(30000, 1000);
        //composers = resolveComposerMap();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renderGameIcons(true);
                countDown.start();
            }
        });

        SpotifyApi api = new SpotifyApi();
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        spotify = api.getService();
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

    private void playRandomTrackFromArtist(String artistID) {
        spotify.getArtistAlbums(artistID, new Callback<Pager<Album>>() {
            @Override
            public void success(Pager<Album> albumPager, Response response) {
                Album album = albumPager.items.get((int)(Math.random() * albumPager.items.size()));
                spotify.getAlbumTracks(album.id, new Callback<Pager<Track>>() {
                    @Override
                    public void success(Pager<Track> trackPager, Response response) {
                        Track track = trackPager.items.get((int) (Math.random() * trackPager.items.size()));
                        try {
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
}
