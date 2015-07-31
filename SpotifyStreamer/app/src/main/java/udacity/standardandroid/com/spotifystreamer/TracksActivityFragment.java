package udacity.standardandroid.com.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
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
import retrofit.RetrofitError;

/**
 * A fragment holding the selected artist's top 10 tracks.
 */
public class TracksActivityFragment extends Fragment
{
    final static String TAG = TracksActivityFragment.class.getSimpleName();
    private static final String KEY_ITEMS_LIST = "keyitemslist";
    public static final String KEY_TRACK_ROW_ITEM = "com.standardandroid.track_row_item";
    public static final String KEY_ARTIST_BITMAP_FILE_NAME = "com.standardandroid.artist_bitmap_file_name";
    private static final String mArtistBitmapFilename = "ArtistFilename";
    private LinearLayout mParentLayout;
    private TracksAdapter mTrackAdapter;
    private Target mLoadTarget;
    private ArrayList<TrackRowItem> mTrackRowItemList = new ArrayList<>();

    public TracksActivityFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
        {
            //We've got data saved. Reconstitute it
            mTrackRowItemList = savedInstanceState.getParcelableArrayList(KEY_ITEMS_LIST);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //Save off the data we need to recreate everything
        outState.putParcelableArrayList(KEY_ITEMS_LIST, mTrackRowItemList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mTrackAdapter = new TracksAdapter(inflater.getContext(), R.layout.list_item_track, new ArrayList<TrackRowItem>());

        //You have to have a view to see and that gets done here in onCreateView
        View v = inflater.inflate(R.layout.fragment_tracks, container, false);

        //Retrieve the ListView you defined in XML (probably in the fragment XML file
        final ListView listView = (ListView)v.findViewById(R.id.listview_tracks);

        //Add the adapter to the listView
        listView.setAdapter(mTrackAdapter);

        //Needed to load the background bitmap
        mParentLayout = (LinearLayout)v.findViewById(R.id.layout_id);

        //Only do this if we don't have data saved
        if(savedInstanceState == null)
        {
            //Note the params are NOT defined in the construction
            FetchTrackTask task = new FetchTrackTask();

            //Grab the correct ID to query Spotify with
            String spotifyId = getActivity().getIntent().getExtras().getString(Intent.EXTRA_TEXT);

            //The params ARE sent in the execute though
            task.execute(spotifyId);
        }
        else
        {
            //Populate the list using the data we just pulled back in
            mTrackAdapter.addAll(mTrackRowItemList);

            int count = mTrackAdapter.getCount();

            for(int index = 0; index < count; index++)
            {
                //Find a big image IF it exists
                TrackRowItem item = mTrackAdapter.getItem(index);

                if(item.hasBigImage())
                {
                    //push it to the background
                    loadBitmap(item.getBigImageUrl());

                    //Just need one.
                    break;
                }
            }
        }

        //Give each of the items a listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                TrackRowItem item = (TrackRowItem)listView.getItemAtPosition(i);

                //Kick off Player Activity
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra(KEY_TRACK_ROW_ITEM, item);

                intent.putExtra(KEY_ARTIST_BITMAP_FILE_NAME, mArtistBitmapFilename);


                startActivity(intent);
            }
        });

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
            mTrackAdapter.clear();

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

                TracksActivity activity = (TracksActivity)getActivity();

                if (album.images.size() == 0)
                {
                    //No image for this one


                    item = new TrackRowItem(null, null, album.name, track.name, track.preview_url, activity.getArtistName());
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

                    item = new TrackRowItem(urlAsString, bigImage, album.name, track.name, track.preview_url,activity.getArtistName() );
                }

                mTrackAdapter.add(item);
                mTrackRowItemList.add(item);
            }

            int count = mTrackAdapter.getCount();

            for(int index = 0; index < count; index++)
            {
               //Find a big image IF it exists
                TrackRowItem item = mTrackAdapter.getItem(index);

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

            Tracks tracks = null;

            try
            {
                tracks = spotify.getArtistTopTrack(spotifyId, options);
            }
            catch(RetrofitError err)
            {
                Log.e(TAG, "Spotify getArtistsTopTrack choked: " + err);
            }

            //Refresh the list to hold new entries
            mTrackRowItemList = new ArrayList<>();

            return tracks;
        }
    }

    public void loadBitmap(String url)
    {
        if (mLoadTarget == null) mLoadTarget = new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                //Cache it off for the track player and show it on the background
                CacheAlbumBitmapTask task = new CacheAlbumBitmapTask(bitmap);
                task.execute();

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

        Picasso.with(getActivity()).load(url).into(mLoadTarget);
    }

    public void setLoadedBitmap(Bitmap bitmap)
    {
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);

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

    private class CacheAlbumBitmapTask extends AsyncTask<Void, Void, Void>
    {
        private Bitmap mImageToStore;

        public CacheAlbumBitmapTask(Bitmap bmp)
        {
            mImageToStore = bmp;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
            //Write file to phone so we don't have to reload it
            FileOutputStream stream = getActivity().openFileOutput(mArtistBitmapFilename, Context.MODE_PRIVATE);
            mImageToStore.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Cleanup
            stream.close();

            //We still need it for the background and Picasso will gripe if we clean it up
//            artistBitmap.recycle();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.w(TAG, "Could not save artist bitmap to disk.");
            }

            return null;
        }
    }

}
