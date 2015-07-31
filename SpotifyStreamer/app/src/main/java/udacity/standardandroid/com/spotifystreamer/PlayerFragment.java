package udacity.standardandroid.com.spotifystreamer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener
{
    private static final String TAG = PlayerFragment.class.getSimpleName();
    private static final String KEY_SONG_POSITION = "com.standandroid.last_position";
    private MediaPlayer mPlayer;
    private TextView  mArtistText;
    private TextView  mAlbumText;
    private TextView  mTrackText;
    private ImageView mImageView;
    private SeekBar   mSeekBar;
    private TextView  mStartTextSeekerBar;
    private TextView  mEndTextSeekerBar;

    public PlayerFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            //We've got data saved. Reconstitute it
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //Save off the data we need to recreate everything..ie the spot the song was playing at
        //TODO: Get location and put it in the line below
        outState.putLong(KEY_SONG_POSITION, 1L);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        Bundle startBundle = getActivity().getIntent().getExtras();

        TrackRowItem item = (TrackRowItem) startBundle.getParcelable(TracksActivityFragment.KEY_TRACK_ROW_ITEM);

        String filename = getActivity().getIntent().getStringExtra(TracksActivityFragment.KEY_ARTIST_BITMAP_FILE_NAME);

        if (item == null)
        {
            Log.w(TAG, "TrackRowItem was null.");
            return view;
        }

        mArtistText = (TextView) view.findViewById(R.id.artist_name_id);
        mAlbumText  = (TextView) view.findViewById(R.id.album_name_id);
        mTrackText  = (TextView) view.findViewById(R.id.track_name_id);
        mImageView  = (ImageView) view.findViewById(R.id.image_id);
        mSeekBar    = (SeekBar) view.findViewById(R.id.seek_bar_id);
        mStartTextSeekerBar = (TextView) view.findViewById(R.id.seeker_bar_start_text_id);
        mEndTextSeekerBar   = (TextView) view.findViewById(R.id.seeker_bar_end_text_id);

        try
        {
            LoadAlbumBitmapTask task = new LoadAlbumBitmapTask(mImageView);
            task.execute(filename);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.w(TAG, "Couldn't load/set the image");
        }

        mArtistText.setText(item.getArtistName());
        mAlbumText.setText(item.getAlbumName());
        mTrackText.setText(item.getTrackName());

        String url = item.getPreviewUrl();
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try
        {
            mPlayer.setDataSource(url);
        }
        catch (IOException e)
        {
            e.printStackTrace();

            Log.e(TAG, "Player died trying to set the data source with URL: " + url);
            Context context = getActivity().getApplicationContext();
            Toast.makeText(context, "Temporarily unable to play that track. Please try another.", Toast.LENGTH_LONG).show();
        }

        mPlayer.setOnPreparedListener(this);
        mPlayer.prepareAsync(); // prepare async to not block main thread

        //Set up a place to listen for it when it's ready to play to set up other things we need
        mPlayer.setOnPreparedListener(new MyOnPreparedListener());

        return view;
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        if (mPlayer == null)
        {
            Log.w(TAG, "Media player was null in onPrepared call");
        }
        else
        {
            mPlayer.start();
        }
    }

    //First is the type you are passing doInBackground via the execute().
    // Last is the type it returns and onPostExecute receives
    class LoadAlbumBitmapTask extends AsyncTask<String, Void, Bitmap>
    {
        private final WeakReference<ImageView> imageViewWeakReference;

        public LoadAlbumBitmapTask(ImageView imageView) throws FileNotFoundException, IOException
        {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            String filename = (String) params[0];

            Bitmap artistBitmap = null;

            try
            {
                FileInputStream is = getActivity().openFileInput(filename);
                artistBitmap = BitmapFactory.decodeStream(is);
                is.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return artistBitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            if (imageViewWeakReference != null && bitmap != null)
            {
                final ImageView imageView = imageViewWeakReference.get();

                if (imageView != null)
                {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    class MyMediaObserver implements Runnable
    {
        private AtomicBoolean bStop = new AtomicBoolean(false);

        public void stop()
        {
            bStop.set(true);
        }

        @Override
        public void run()
        {
            while (!bStop.get())
            {
                mSeekBar.setProgress(mPlayer.getCurrentPosition());

                try
                {
                    //Update it four times a second
                    Thread.sleep(250);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener
    {
        @Override
        public void onPrepared(MediaPlayer mp)
        {
            //Get the length of the sample (just in case they can vary
            int duration = mPlayer.getDuration();

            //Set the initial position to 0
            mStartTextSeekerBar.setText(Integer.toString(0));

            //set the duration of the end text for the seek bar
            mEndTextSeekerBar.setText(Integer.toString(duration));

            MyMediaObserver observer = new MyMediaObserver();

            //Set up a listener for it to call when it completes
            mPlayer.setOnCompletionListener(new MyOnCompletionListener(observer));


            mPlayer.start();

            new Thread(observer).start();
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener
    {
        private MyMediaObserver mObserver;

        private MyOnCompletionListener(MyMediaObserver observer)
        {
            mObserver = observer;
        }

        @Override
        public void onCompletion(MediaPlayer mp)
        {
            mObserver.stop();
            mSeekBar.setProgress(mp.getCurrentPosition());
        }
    }
}
