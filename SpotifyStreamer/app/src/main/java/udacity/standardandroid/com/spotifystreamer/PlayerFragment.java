package udacity.standardandroid.com.spotifystreamer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener
{
    private static final String     TAG = PlayerFragment.class.getSimpleName();
    private static final String     KEY_SONG_POSITION = "com.standandroid.last_position";
    private static final String     KEY_ARRAY_LIST = "com.standardandroid.array_list";
    private static final String     KEY_ITEM_INDEX = "com.standardandroid.item_index";
    private static final String     KEY_FILE_NAME = "com.standardandroid.file_name";
    private MediaPlayer             mPlayer;
    private TextView                mArtistText;
    private TextView                mAlbumText;
    private TextView                mTrackText;
    private ImageView               mImageView;
    private ImageButton             mPlayOrPauseButton;
    private ImageButton             mPreviousButton;
    private ImageButton             mNextButton;
    private SeekBar                 mSeekBar;
    private TextView                mStartTextSeekerBar;
    private TextView                mEndTextSeekerBar;
    private Thread                  mOnPreparedListenerThread;
    private MyMediaObserver         mMediaObserver;
    private ArrayList<TrackRowItem> mTrackRowItemsList;
    private int                     mTrackRowIndex;
    private Target                  mLoadTarget;


    public PlayerFragment()
    {
    }

    public static PlayerFragment newInstance(ArrayList arrayList, int index, String bmpFileName)
    {
        PlayerFragment f = new PlayerFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();

        args.putParcelableArrayList(KEY_ARRAY_LIST, arrayList);
        args.putInt(KEY_ITEM_INDEX, index);
        args.putString(KEY_FILE_NAME, bmpFileName);

        f.setArguments(args);

        return f;
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
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
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

        Intent intent = getActivity().getIntent();

        mTrackRowItemsList = intent.getParcelableArrayListExtra(TracksActivityFragment.KEY_TRACK_ROW_LIST);

        String filename;
        TrackRowItem item;

        if(mTrackRowItemsList == null)
        {
            mTrackRowItemsList = (ArrayList<TrackRowItem>) getArguments().get(KEY_ARRAY_LIST);
            filename = (String) getArguments().get(KEY_FILE_NAME);
            mTrackRowIndex = (int)getArguments().get(KEY_ITEM_INDEX);

            item = mTrackRowItemsList.get(mTrackRowIndex);
        }
        else
        {
            filename = intent.getStringExtra(TracksActivityFragment.KEY_ARTIST_BITMAP_FILE_NAME);
            mTrackRowIndex = intent.getIntExtra(TracksActivityFragment.KEY_TRACK_ROW_LIST_POSITION, 0);

            item = mTrackRowItemsList.get(mTrackRowIndex);
        }

        if (item == null)
        {
            Log.w(TAG, "TrackRowItem was null.");
            return view;
        }

        item.getBigImageUrl();

        mArtistText         = (TextView) view.findViewById(R.id.artist_name_id);
        mAlbumText          = (TextView) view.findViewById(R.id.album_name_id);
        mTrackText          = (TextView) view.findViewById(R.id.track_name_id);
        mImageView          = (ImageView) view.findViewById(R.id.image_id);
        mSeekBar            = (SeekBar) view.findViewById(R.id.seek_bar_id);
        mStartTextSeekerBar = (TextView) view.findViewById(R.id.seeker_bar_start_text_id);
        mEndTextSeekerBar   = (TextView) view.findViewById(R.id.seeker_bar_end_text_id);
        mPlayOrPauseButton  = (ImageButton)view.findViewById(R.id.play_pause_button_id);
        mPreviousButton     = (ImageButton)view.findViewById(R.id.previous_button_id);
        mNextButton         = (ImageButton)view.findViewById(R.id.next_button_id);

        mSeekBar.setOnSeekBarChangeListener(this);

        mPreviousButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Pop up a spinner (stop it in the task that loads the song)
                //Pull in the next song.
                if (mTrackRowIndex == 0)
                {
                    mTrackRowIndex = mTrackRowItemsList.size() - 1;
                }
                else
                {
                    mTrackRowIndex--;
                }

                TrackRowItem item = mTrackRowItemsList.get(mTrackRowIndex);

                handleItem(item, true);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Pop up a spinner (stop it in the task that loads the song)
                //Pull in the next song.
                if (mTrackRowIndex == mTrackRowItemsList.size() - 1)
                {
                    mTrackRowIndex = 0;
                }
                else
                {
                    mTrackRowIndex++;
                }

                TrackRowItem item = mTrackRowItemsList.get(mTrackRowIndex);

                handleItem(item, true);
            }
        });

        mPlayOrPauseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mPlayer.isPlaying())
                {
                    //Pause the player
                    mPlayer.pause();

                    //Reset the icon
                    mPlayOrPauseButton.setImageResource(android.R.drawable.ic_media_play);
                }
                else
                {
                    //Start the player
                    mPlayer.start();

                    //Reset the icon
                    mPlayOrPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

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

        //Now finish up using the item but don't load the bitmap from it since that's already done
        handleItem(item,false);

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        int duration = mPlayer.getDuration();
        int position = mPlayer.getCurrentPosition();

        float updatePercentage = ((float)position/duration);

        int updateLocation = (int)(duration * updatePercentage / 1000);
        mStartTextSeekerBar.setText(Utility.getTimeFormat(updateLocation));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        if(mPlayer.isPlaying())
        {
            mPlayer.pause();
        }

        mPlayer.setOnPreparedListener(new MyOnPreparedListener());
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        int position = mSeekBar.getProgress();

        //Now the position is like a percentage so calculate where we need to seek to.
        int duration = mPlayer.getDuration();
        int seekTo = (int)(duration * (position / 100f));

        mSeekBar.setProgress(position);

        if(mPlayer.isPlaying())
        {
            mPlayer.pause();
            mPlayer.seekTo(seekTo);
            mPlayer.start();
        }
        else
        {
            mPlayer.seekTo(seekTo);
            mPlayer.start();
        }

        //Reset the icon
        mPlayOrPauseButton.setImageResource(android.R.drawable.ic_media_pause);
    }

    //First is the type you are passing doInBackground via the execute().
    // Last is the type it returns and onPostExecute receives
    class LoadAlbumBitmapTask extends AsyncTask<String, Void, Bitmap>
    {
        private final WeakReference<ImageView> imageViewWeakReference;

        public LoadAlbumBitmapTask(ImageView imageView) throws IOException
        {
            imageViewWeakReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            String filename = params[0];

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
                int seekLocation = getProgessForSeekBar();

                mSeekBar.setProgress(seekLocation);

                String textLocation = Integer.toString(seekLocation / 1000);

                try
                {
                    //Update it five times a second
                    Thread.sleep(200);
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
            //Stop (politely) the current running thread if it's still alive
            if(mOnPreparedListenerThread != null && mOnPreparedListenerThread.isAlive())
            {
                if(mMediaObserver != null)
                {
                    mMediaObserver.bStop = new AtomicBoolean(true);
                }
            }

            //Get the length of the sample (just in case they can vary
            int duration = mPlayer.getDuration();

            //Now the duration is in milliseconds. Round this off to seconds
            duration /= 1000;

            //Set the initial position to 0
            mStartTextSeekerBar.setText(Utility.getTimeFormat(0));

            //set the duration of the end text for the seek bar
            mEndTextSeekerBar.setText(Utility.getTimeFormat(duration));

            mMediaObserver = new MyMediaObserver();

            //Set up a listener for it to call when it completes
            mPlayer.setOnCompletionListener(new MyOnCompletionListener(mMediaObserver));

            mSeekBar.setProgress(getProgessForSeekBar());

            //Comment out to have them hit start before it starts to play
//            mPlayer.start();

            //Show as ready
            mPlayOrPauseButton.setImageResource(android.R.drawable.ic_media_play);
            mPreviousButton.setImageResource(android.R.drawable.ic_media_previous);
            mNextButton.setImageResource(android.R.drawable.ic_media_next);

            //Save it off so we can stop it later
            mOnPreparedListenerThread = new Thread(mMediaObserver);

            mOnPreparedListenerThread.start();
        }
    }

    private int getProgessForSeekBar()
    {
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        float seekPosition = ((float)position / duration) * 100f;

        return (int)seekPosition;
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
            mSeekBar.setProgress(0);
        }
    }

    private void handleItem(TrackRowItem item, boolean bLoadBitmap)
    {
        if(mPlayer != null)
        {
            if(mPlayer.isPlaying())
            {
                mPlayer.pause();
                mPlayer.stop();
                mPlayer = null;
            }
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

        mPlayer.prepareAsync(); // prepare async to not block main thread

        //Set up a place to listen for it when it's ready to play to set up other things we need
        mPlayer.setOnPreparedListener(new MyOnPreparedListener());

        if(bLoadBitmap && item.hasBigImage())
        {
            //push it to the background
            loadBitmap(item.getBigImageUrl());
        }
    }

    public void loadBitmap(String url)
    {
        if (mLoadTarget == null) mLoadTarget = new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                mImageView.setImageDrawable(drawable);
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
}
