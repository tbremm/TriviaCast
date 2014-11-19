package com.adventurpriseme.triviacast;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;


public class PlayTriviaActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Cast.MessageReceivedCallback {

    // Data members
    static boolean m_bWillHost = true;
    MediaRouter mMediaRouter;
    MediaRouteSelector mMediaRouteSelector;
    static MediaRouter.Callback mMediaRouterCallback;
    private static final String TAG = "Trivia Activity";
    private GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_trivia);

        // Show the Up button in the action bar.
        setupActionBar();

        // Create the media router, selector, and callback for the chromecast
        mMediaRouter = MediaRouter.getInstance(getApplicationContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(CastMediaControlIntent.categoryForCast("53EAA363")).build();
        mMediaRouterCallback = new MyMediaRouterCallback();

        _InitPlayer();
    }

    // Initialize player settings
    private void _InitPlayer() {
        // Set the checkbox state
        ((CheckBox) findViewById(R.id.checkbox_willHost)).setChecked(m_bWillHost);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_trivia, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // onClick handler for the will host checkbox
    // This determines if the user is willing to host the trivia round
    public void onCheckBoxClicked(View view) {
        // Is the view (checkbox) checked?
        boolean bChecked = ((CheckBox) view).isChecked();

        // Operate on the specific checkbox
        switch (view.getId()) {
            case R.id.checkbox_willHost:    // Is player willing to host?
                m_bWillHost = bChecked;
                break;
            default:
                // Should never get here
                break;
        }
    }

    private boolean mWaitingForReconnect = false;
    private boolean mApplicationStarted = false;
    private TriviaChannel mTriviaChannel;
    @Override
    public void onConnected(Bundle bundle) {
        if (mWaitingForReconnect) {
            mWaitingForReconnect = false;
            //reconnectChannels();
        } else {
            try {
                Cast.CastApi.launchApplication(mApiClient, "53EAA363", false).setResultCallback(
                        new ResultCallback<Cast.ApplicationConnectionResult>() {
                            @Override
                            public void onResult(Cast.ApplicationConnectionResult result) {
                                Status status = result.getStatus();
                                if (status.isSuccess()) {
                                    ApplicationMetadata applicationMetadata = result.getApplicationMetadata();
                                    String sessionId = result.getSessionId();
                                    String applicationStatus = result.getApplicationStatus();
                                    boolean wasLaunched = result.getWasLaunched();
                                    mApplicationStarted = true;

                                    TextView tmp = (TextView) findViewById(R.id.textRoundResult);
                                    mTriviaChannel = new TriviaChannel(tmp);
                                    try {
                                        Cast.CastApi.setMessageReceivedCallbacks(mApiClient,
                                                mTriviaChannel.getNamespace(),
                                                mTriviaChannel);

                                        //sendMessage("http://gnosm.net/missilecommand/sounds/524.mp3");
                                    } catch (IOException e) {
                                        Log.e(TAG, "Exception while creating channel", e);
                                    }
                                }
                            }
                        }
                );
            } catch (Exception e) {
                Log.e(TAG, "Failed to launch application", e);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO
        mWaitingForReconnect = true;
    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
        // TODO
        Log.d(TAG, "onMessageReceived: " + message);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO
        Log.e(TAG, "onConnectionFailed...");
    }

    private void sendMessage (String message) {
        if (mApiClient != null && mTriviaChannel != null) {
            try {
                Cast.CastApi.sendMessage(mApiClient, mTriviaChannel.getNamespace(), message)
                        .setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status result) {
                                        if (!result.isSuccess()) {
                                            Log.e(TAG, "Sending message failed");
                                        }
                                    }
                                });
            } catch (Exception e) {
                Log.e(TAG, "Exception while sending message", e);
            }

        } else if (mApiClient == null) {
            Log.e (TAG, "mApiClient is null!");
        } else { // mRPSChannel == null
            Log.e(TAG, "mRPSChannel is null!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }
    @Override
    protected void onPause() {
        if (isFinishing()) {
            mMediaRouter.removeCallback(mMediaRouterCallback);
        }
        super.onPause();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }
    @Override
    protected void onStop() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onStop();
    }

    private final class MyMediaRouterCallback extends MediaRouter.Callback {
        private CastDevice mSelectedDevice;
        private Cast.Listener mCastClientListener;
        private final String TAG = "My Media Router Callback";

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            mSelectedDevice = CastDevice.getFromBundle(info.getExtras());
            String routeId = info.getId();

            mCastClientListener = new Cast.Listener() {
                @Override
                public void onApplicationStatusChanged() {
                    if (mApiClient != null) {
                        Log.d(TAG, "onApplicationStatusChanged: "
                                + Cast.CastApi.getApplicationStatus(mApiClient));
                    }
                }

                @Override
                public void onVolumeChanged() {
                    if (mApiClient != null) {
                        Log.d(TAG, "onVolumeChanged: " + Cast.CastApi.getVolume(mApiClient));
                    }
                }

                @Override
                public void onApplicationDisconnected(int errorCode) {
                    Log.d(TAG, "Application Disconnected: " + errorCode);
                    // fixme teardown();
                }
            };

            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                    .builder(mSelectedDevice, mCastClientListener);

            mApiClient = new GoogleApiClient.Builder(PlayTriviaActivity.this)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(PlayTriviaActivity.this)
                    .addOnConnectionFailedListener(PlayTriviaActivity.this)
                    .build();

            mApiClient.connect();
        }
    }
}
