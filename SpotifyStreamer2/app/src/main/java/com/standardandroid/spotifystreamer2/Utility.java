package com.standardandroid.spotifystreamer2;

/**
 * Created by darri_000 on 8/5/2015.
 */
public class Utility
{
    private static String mCountryCode;

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

    public static String getCountryCode()
    {
        return mCountryCode;
    }

    public static void setCountryCode(String code)
    {
        mCountryCode = code.toUpperCase();
    }
}
