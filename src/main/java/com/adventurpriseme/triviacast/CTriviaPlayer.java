package com.adventurpriseme.triviacast;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

/**
 * This class defines a player for the trivia game.
 *
 * Created by Timothy on 11/19/2014.
 * Copyright 11/19/2014 adventurpriseme.com
 */
public class CTriviaPlayer implements IPerson {
    // Data members
    private String m_strName = "";
    private boolean m_bWillHost = true;
    private int m_nScore = 0;

    /**
     * Constructor
     */
    public CTriviaPlayer () {}

    /**
     * Constructor
     * @param context (required)  The context in which the alert will be spawned.
     */
    public CTriviaPlayer (final Context context) {
        Initialize(context);
    }

    /**
     * Initializes the trivia player.
     * <p>
     * This will pop up an alert to ask the user for a player name if one isn't already assigned.
     *
     * @param context (required)  The context in which the alert will be spawned.
     */
    public void Initialize (final Context context) {

        // Get the name if it's not already set
        if (m_strName.equals("")) {
            // Inflate the alert prompt's layout
       //     LayoutInflater inflater = LayoutInflater.from(context);
        //    final View layout = inflater.inflate(R.layout.alert_getplayerinfo, null);

            // Create the edittext for user input
            final EditText editText = new EditText(context);

            // Create the alert
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setView(editText);
            alert.setTitle(R.string.alert_whoAreYou);

            // Set the okay button and its listener
            alert.setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int nWhichButton) {
                    // Set the name to the text view
                    // If we want to validate the user name, do it in setName()
                    setName(editText.getEditableText().toString());
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int nWhichButton) {
                    // User hit cancel
                    dialog.cancel();
                }
            });

            // Create and spawn the dialog
            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        }
    }

    /**
     * Get the person's name.
     *
     * @return a string containing the person's name.
     */
    @Override
    public String getName() { return m_strName; }

    /**
     * Set the person's name.
     *
     * @param strName (required)  The new player name
     */
    @Override
    public void setName(String strName) { m_strName = strName; }

    /**
     * Get the player's willingness to host a game.
     *
     * @return True if player is willing to host, false otherwise.
     */
    public boolean getWillHost () { return m_bWillHost; }

    /**
     * Set the player's willingness to host a game.
     *
     * @param bWillHost (required)  True if player is willing to host, false otherwise.
     */
    public void setWillHost (boolean bWillHost) {
        if (m_bWillHost != bWillHost) {
            m_bWillHost = bWillHost;
        }
    }

    /**
     * Gets the player's score.
     *
     * @return The player's score.
     */
    public int getScore () { return m_nScore; }

    /**
     * Sets the player's score.
     *
     * @param nScore (optional) Sets the player's score.
     */
    public void setScore (int nScore) {
        if (m_nScore != nScore) {
            m_nScore = nScore;
        }
    }
}
