package udacity.standardandroid.com.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


//Akin to the DetailActivity in Sunshine

public class TracksActivity extends AppCompatActivity
{
    private String mArtistName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        //TRYING THIS NEW CODE
        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().add(R.id.tracks_list_container_like_weather_detail, new TracksActivityFragment()).commit();
        }

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        mArtistName = getIntent().getExtras().getString(SearchActivityFragment.ARTIST_NAME);

        actionBar.setSubtitle(mArtistName);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tracks, menu);
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

    public String getArtistName()
    {
        return mArtistName;
    }

}
