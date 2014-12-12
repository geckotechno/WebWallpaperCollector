package com.geckotechno.imagecollector;

import java.io.File;
import java.net.URL;
import java.util.Set;

/**
 * Created by hale on 12/12/2014.
 */
public class FlickerCollector extends ImageCollectorFromHtml {


    public static void main (String args[]) throws Exception {

        extractFromFlicker();
    }



    private static void extractFromFlicker() throws Exception {
        File dir = new File("C:\\Users\\hale\\Wallpaper-web\\flicker-incoming");
        // Set<String> excludeSet = getPreviousExtractNames(dir);
        //String[] categories = new String[]{ "underwater","science-space","landscapes","nature-weather"};
        //for (int i = 0; i < categories.length; i++ ) {
        URL url = new URL("http://www.flickr.com/search/?q=waterfalls");
        downloadnFlicker(url, dir);
        //    excludeSet.addAll(downloadnNatGeoPhotoOfTheDay(url, dir, excludeSet));
        //}
        // writeExtractFileSet(dir,excludeSet);
    }

    private static void downloadnFlicker(URL url, File dir) throws Exception {
        Set<String> links = downloadLinksFromURL(url);
    }
}
