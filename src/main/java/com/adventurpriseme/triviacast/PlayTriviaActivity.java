package com.adventurpriseme.triviacast;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;


public class PlayTriviaActivity extends Activity {

    // Data members
    boolean m_bWillHost = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_trivia);
        _InitPlayer ();
    }

    // Initialize player settings
    private void _InitPlayer() {
        // Set the checkbox state
        ((CheckBox) findViewById(R.id.checkbox_willHost)).setChecked(m_bWillHost);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_trivia, menu);
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

    // onClick handler for the will host checkbox
    // This determines if the user is willing to host the trivia round
    public void onCheckBoxClicked (View view) {
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
}
