package com.geckotechno.imagecollector;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hale on 12/12/2014.
 */
public class NgsPhotoOfTheDayExtractor extends ImageCollectorFromHtml {

    public static void main (String args[]) throws Exception {
            extractFromNGS();
    }

    protected static void extractFromNGS() throws Exception {
        File dir = new File("C:\\Users\\hale\\Wallpaper-web\\ngs-incoming");
        Set<String> excludeSet = getPreviousExtractNames(dir);
        String[] categories = new String[]{ "underwater","science-space","landscapes","nature-weather",
                                                "black-white","adventure-exploration"};
        for (int i = 0; i < categories.length; i++ ) {
            System.out.println("NGS searching: " + categories[i]);
            URL url = new URL("http://photography.nationalgeographic.com/photography/photo-of-the-day/" + categories[i] +"/");
            excludeSet.addAll(downloadnNatGeoPhotoOfTheDay(url, dir, excludeSet));
        }
        writeExtractFileSet(dir,excludeSet);
    }

    private static Set<String> downloadnNatGeoPhotoOfTheDay(URL url, File dir, Set<String> excludeFiles) throws Exception {
        Set<String> newFiles = new HashSet<>();
        Set<String> links = downloadLinksFromURL(url);
//        System.out.println("links:\n" + outputList(links));
        if (links == null) {
          System.out.println("--Error getting files");
          return newFiles;
        }

        Set<String> imgFolderLinks = filterImgFolderLinks(links);

        System.out.print("Searching: ");

           //dumpImageLinks(new File("C:\\Users\\hale\\Wallpaper-web"), urlHeader, linkHandler.getLinks());
//        String childUrl = imgFolderLinks.iterator().next();

//        Set<String> excludeFiles = new HashSet<>(Arrays.asList(dir.list()));

        for (String childUrl: imgFolderLinks ) {
//            System.out.println("\n\ngetfirst url: " + childUrl);
            Set<String> childLinks = downloadLinksFromURL(new URL(childUrl));
            if (childLinks != null) {
                String childImg = findImgLInk(childLinks, getUrlTail(childUrl));
                //        System.out.println("links:\n" + outputList(parseImgLinks(childLinks)));
                //            System.out.println("child img link: " + childImg);
                if (childImg != null && childImg.length() > 20 && fileNameNotMatch(childImg, excludeFiles)) {
                    System.out.print(DOWNLOAD_FILE_CHAR);
                    downloadImagesFromURL(childImg, dir);
                    newFiles.add(getBaseName(childImg));
                } else {
                    System.out.print(SKIP_FILE_CHAR);
                }
            } else {
                System.out.print(ERROR_CHAR);
            }

        }
        System.out.println();

        return newFiles;
    }

    private static void downloadnFlicker(URL url, File dir) throws Exception {
        Set<String> links = downloadLinksFromURL(url);
    }
}
