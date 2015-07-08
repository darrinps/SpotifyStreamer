package udacity.standardandroid.com.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * A fragment holding the selected artist's top 10 tracks.
 */
public class TracksActivityFragment extends Fragment
{
    final static String TAG = TracksActivityFragment.class.getSimpleName();
    private LinearLayout mParentLayout;
    private TracksAdapter trackAdapter;
    private Target loadTarget;

    public TracksActivityFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        trackAdapter = new TracksAdapter(inflater.getContext(), R.layout.list_item_track, new ArrayList<TrackRowItem>());

        //You have to have a view to see and that gets done here in onCreateView
        View v = inflater.inflate(R.layout.fragment_tracks, container, false);

        //Retrieve the ListView you defined in XML (probably in the fragment XML file
        final ListView listView = (ListView)v.findViewById(R.id.listview_tracks);

        //Add the adapter to the listView
        listView.setAdapter(trackAdapter);

        //Note the params are NOT defined in the construction
        FetchTrackTask task = new FetchTrackTask();

        //Grab the correct ID to query Spotify with
        String spotifyId = getActivity().getIntent().getExtras().getString(Intent.EXTRA_TEXT);

        //The params ARE sent in the execute though
        task.execute(spotifyId);

        mParentLayout = (LinearLayout)v.findViewById(R.id.layout_id);

        return v;
    }

    /**
     * The first tells the input type. The second an optional method's input type. The third the return type
     */
    public class FetchTrackTask extends AsyncTask<String, Void, Tracks>
    {
        @Override
        protected void onPostExecute(Tracks tracks)
        {
            trackAdapter.clear();

            if (tracks == null || tracks.tracks.size() < 1)
            {
                Context context = getActivity().getApplicationContext();

                Toast.makeText(context, "No artists/bands found", Toast.LENGTH_LONG).show();
                return;
            }

            List<Track> trackList = tracks.tracks;

            for (Track track : trackList)
            {
                TrackRowItem item;

                AlbumSimple album = track.album;

                if (album.images.size() == 0)
                {
                    //No image for this one
                    item = new TrackRowItem(null, null, album.name, track.name, track.preview_url);
                }
                else
                {
                    //We have an image. Get the latest one
                    String urlAsString = track.album.images.get(track.album.images.size() - 1).url;

                    //See if there is a large one
                    Iterator<Image> iterator = track.album.images.iterator();

                    String bigImage = null;

                    while (iterator.hasNext())
                    {
                        Image image = iterator.next();

                        if (image.height > 600)
                        {
                            //We have a big one
                            bigImage = image.url;
                        }
                    }

                    item = new TrackRowItem(urlAsString, bigImage, album.name, track.name, track.preview_url);
                }

                trackAdapter.add(item);
            }

            int count = trackAdapter.getCount();

            for(int index = 0; index < count; index++)
            {
               //Find a big image IF it exists
                TrackRowItem item = trackAdapter.getItem(index);

                if(item.hasBigImage())
                {
                    //push it to the background
                    loadBitmap(item.getBigImageUrl());

                    //Just need one.
                    break;
                }
            }
        }

        @Override
        protected Tracks doInBackground(String... params)
        {
            String spotifyId;

            if(params.length > 0)
            {
                spotifyId = params[0];
            }
            else
            {
                return null;
            }

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

//            Log.d(TAG, "Retrieving tracks for ID: " + spotifyId);

            Map<String, Object> options = new HashMap<>();
            options.put("country", "US");

            return spotify.getArtistTopTrack(spotifyId, options);
        }
    }

    public void loadBitmap(String url)
    {
        if (loadTarget == null) loadTarget = new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                // do something with the Bitmap
                setLoadedBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable)
            {
                //NOOP
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {
                //NOOP
            }
        };

        Picasso.with(getActivity()).load(url).into(loadTarget);
    }

    public void setLoadedBitmap(Bitmap b)
    {
        Drawable drawable = new BitmapDrawable(getResources(), b);

        //Make it very translucent so we can see the text on top
        drawable.setAlpha(20);

        int sdk = android.os.Build.VERSION.SDK_INT;

        //Version 16 on has a new way to set the background
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN)
        {
            mParentLayout.setBackgroundDrawable(drawable);
        }
        else
        {
            mParentLayout.setBackground(drawable);
        }
    }
}
