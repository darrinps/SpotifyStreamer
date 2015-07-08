package udacity.standardandroid.com.spotifystreamer;

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

public class SearchAdapter extends ArrayAdapter<SearchRowItem>
{
    private static final String TAG = SearchAdapter.class.getSimpleName();
    private Context context;

    public SearchAdapter(Context context,
                         int resourceId,
                         List<SearchRowItem> items)
    {
        super(context, resourceId, items);
        this.context = context;
    }

    // Holds the row elements
    private class ViewHolder
    {
        ImageView imageView;
        TextView  artistName;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        SearchRowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.list_item_search, null);
            holder = new ViewHolder();
            holder.artistName = (TextView) convertView.findViewById(R.id.list_item_search_textview);
            holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_search_imageview);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.artistName.setText(rowItem.getArtistName());

       String urlAsString = rowItem.getImageId();

        if(urlAsString == null)
        {
            Log.d(TAG, "Image URL was null for artist: " + rowItem.getArtistName());
        }
        else
        {
//            Log.d(TAG, "Image URL artist: " + rowItem.getArtistName() + " = " + rowItem.getImageId().toString());
            Picasso.with(context).load(urlAsString).into(holder.imageView);
        }

        return convertView;
    }
}