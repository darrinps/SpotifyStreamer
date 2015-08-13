package com.standardandroid.spotifystreamer2;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by darri_000 on 7/1/2015.
 */
public class TracksAdapter extends ArrayAdapter<TrackRowItem>
{
    private static final String TAG = TracksAdapter.class.getSimpleName();

    Context context;

    public TracksAdapter(Context context,
                         int resourceId,
                         List<TrackRowItem> items)
    {
        super(context, resourceId, items);
        this.context = context;
    }

    // Holds the row elements
    private class ViewHolder
    {
        ImageView imageView;
        TextView albumName;
        TextView trackName;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        TrackRowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.list_item_track, null);
            holder = new ViewHolder();
            holder.albumName = (TextView) convertView.findViewById(R.id.list_item_album_textview);
            holder.trackName = (TextView) convertView.findViewById(R.id.list_item_track_textview);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_track_imageview);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.albumName.setText("Album: " + rowItem.getAlbumName());
        holder.trackName.setText("Track: " + rowItem.getTrackName());

        String urlAsString = rowItem.getImageId();

        if(urlAsString == null)
        {
            Log.d(TAG, "Image URL was null for track: " + rowItem.getTrackName());
        }
        else
        {
//            Log.d(TAG, "Image URL track: " + rowItem.getTrackName() + " = " + rowItem.getImageId().toString());
            Picasso.with(context).load(urlAsString).into(holder.imageView);
        }

        Log.d(TAG, "Converted View for track: " + rowItem.getTrackName());

        return convertView;
    }
}