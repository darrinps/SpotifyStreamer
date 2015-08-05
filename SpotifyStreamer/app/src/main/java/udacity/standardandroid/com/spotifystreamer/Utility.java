package udacity.standardandroid.com.spotifystreamer;

/**
 * Created by darri_000 on 8/5/2015.
 */
public class Utility
{
    static public String getTimeFormat(int seconds)
    {
        int minutes = seconds / 60;
        int secondsRemaining = seconds % 60;

        StringBuffer buff = new StringBuffer();
        buff.append(minutes);
        buff.append(":");

        if(secondsRemaining < 10)
        {
            buff.append("0");
        }

        buff.append(secondsRemaining);

        return buff.toString();
    }
}
