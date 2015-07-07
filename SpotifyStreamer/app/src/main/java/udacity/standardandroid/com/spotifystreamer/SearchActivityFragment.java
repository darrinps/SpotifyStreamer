package udacity.standardandroid.com.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment
{
    final static String TAG = SearchActivityFragment.class.getSimpleName();
    final public static String ARTIST_NAME = "com.standardandroid.spotifystreamer.artistname";

    private SearchAdapter searchAdapter;

    public SearchActivityFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //You have to have a view to see and that gets done here in onCreateView
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        searchAdapter = new SearchAdapter(inflater.getContext(), R.layout.list_item_search, new ArrayList<SearchRowItem>());

        //Retrieve the ListView you defined in XML (probably in the fragment XML file
        final ListView listView = (ListView)v.findViewById(R.id.listview_search);

        //Add the adapter to the listView
        listView.setAdapter(searchAdapter);

        //Give each of the items a listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                SearchRowItem item = (SearchRowItem)listView.getItemAtPosition(i);

                //Note getActivity() available in fragments to get the context
                //toast.makeText(getActivity(), textString, Toast.LENGTH_LONG).show();

                //Kick off detail Activity
                Intent intent = new Intent(getActivity(), TracksActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, item.getSpotifyId());
                intent.putExtra(ARTIST_NAME, item.getArtistName());

                startActivity(intent);
            }
        });

        final EditText editText = (EditText)v.findViewById(R.id.search_edittext);

        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                if(s.length() > 1)
                {
                    //Note the params are NOT defined in the construction
                    FetchArtistsTask task = new FetchArtistsTask();

                    //The params ARE sent in the execute though
                    task.execute(s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });


        return v;
    }

    /**
     * The first tells the input type. The second an optional method's input type. The third the return type
     */
    public class FetchArtistsTask extends AsyncTask<String, Void, ArtistsPager>
    {

        @Override
        protected void onPostExecute(ArtistsPager artistsPager)
        {
            searchAdapter.clear();

            if (artistsPager == null || artistsPager.artists.items.size() < 1)
            {

                Context context = getActivity().getApplicationContext();
                Toast.makeText(context, "No artists/bands found", Toast.LENGTH_LONG).show();
                return;
            }

            List<Artist> artistsList = artistsPager.artists.items;
            Iterator<Artist> artistIterator = artistsList.iterator();

            while(artistIterator.hasNext())
            {
                Artist artist = artistIterator.next();

                SearchRowItem item = null;

                if(artist.images.size() <= 0 )
                {
                    //No image for this one
                    item = new SearchRowItem(null, artist.name, artist.id);
                }
                else
                {
                    //We have an image
                    String urlAsString = artist.images.get(artist.images.size() - 1).url;

                    item = new SearchRowItem(urlAsString, artist.name, artist.id);

                }

                searchAdapter.add(item);
            }
        }

        @Override
        protected ArtistsPager doInBackground(String... params)
        {

            String artistName = "";
            String[] array = null;

            if(params.length > 0)
            {
                artistName = params[0];
            }
            else
            {
                return null;
            }

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArtistsPager results = spotify.searchArtists(artistName);

            return results;
        }
    }
}
