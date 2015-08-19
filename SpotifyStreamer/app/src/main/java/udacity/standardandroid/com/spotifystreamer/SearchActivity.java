package udacity.standardandroid.com.spotifystreamer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class SearchActivity extends AppCompatActivity implements SearchActivityFragment.Callback
{
    private String countryCode;
    private static boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    public static final String TAG = SearchActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.tracks_list_container_like_weather_detail)== null)
        {
            //Single pane mode
            mTwoPane = false;
        }
        else
        {
            mTwoPane = true;

            if(savedInstanceState == null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.tracks_list_container_like_weather_detail, new TracksActivityFragment(), DETAILFRAGMENT_TAG).commit();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if(id == R.id.menu_country_code)
        {
            getCountryCode();
        }
        else if(id == R.id.menu_notifications)
        {

        }
        else
        {
            Log.w(TAG, "Unknown ID found in menu...ignoring.");
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCountryCode()
    {
        // get prompts.xml view
        Context context = getApplicationContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View selectorView = inflater.inflate(R.layout.country_selector_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(selectorView);

        final EditText userInput = (EditText) selectorView.findViewById(R.id.countryCodeEditText);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Utility.setCountryCode(userInput.getText().toString());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        //Set the correct type of dialog to allow it to be shown
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        // show it
        alertDialog.show();

    }

    @Override
    public void onArtistSelected(String artistName, String spotifyArtistId)
    {
        if(mTwoPane)
        {
            Bundle args = new Bundle();
            args.putString(SearchActivityFragment.ARTIST_NAME, artistName);
            args.putString(SearchActivityFragment.SPOTIFY_ID, spotifyArtistId);

            TracksActivityFragment fragment = new TracksActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.tracks_list_container_like_weather_detail, fragment, DETAILFRAGMENT_TAG).commit();

            //Get rid of that Pesky keyboard once we select
            final EditText editText = (EditText)findViewById(R.id.search_edittext);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
        else
        {
            //Kick off detail Activity
            Intent intent = new Intent(this, TracksActivity.class);
            intent.putExtra(SearchActivityFragment.SPOTIFY_ID, spotifyArtistId);
            intent.putExtra(SearchActivityFragment.ARTIST_NAME, artistName);

            startActivity(intent);
        }
    }

    public static boolean isTwoPane()
    {
        return mTwoPane;
    }
}
