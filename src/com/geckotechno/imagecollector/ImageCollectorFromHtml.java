package com.geckotechno.imagecollector;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.*;
import org.xml.sax.ContentHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class ImageCollectorFromHtml {

    private static final String EXTRACTLIST = "extractlist.txt";
    protected static final String SKIP_FILE_CHAR = ".";
    protected static final String DOWNLOAD_FILE_CHAR = "N";
    protected static final String ERROR_CHAR = "E";


    protected static void writeExtractFileSet(File dir, Set<String> excludeSet) throws IOException {
        File extractListFile = getExtractFile(dir);
        FileWriter f = new FileWriter(extractListFile);
        for (String fname : excludeSet) {
            f.write(fname);
            f.write('\n');
        }
        f.close();
    }


    protected static Set<String> getPreviousExtractNames(File dir) throws IOException {
        Set<String> fileSet = new HashSet<>();
        File extractListFile = getExtractFile(dir);
        if (extractListFile.exists() && extractListFile.isFile()) {
            BufferedReader br = new BufferedReader(new FileReader(extractListFile));
            try {
                String line = br.readLine();

                while (line != null) {
                    fileSet.add(line);
                    line = br.readLine();
                }
            } finally {
                br.close();
            }
        }
        return fileSet;
    }

    private static File getExtractFile(File dir) {
        return new File(dir, EXTRACTLIST);
    }



    protected static boolean fileNameNotMatch(String childImg, Set<String> excludeFiles) {
        return ! excludeFiles.contains(getBaseName(childImg));
    }

    protected static String getBaseName(String childImg) {
        return childImg.substring(childImg.lastIndexOf('/') + 1);
    }

    static String findImgLInk(Set<String> childLinks, String urlTail) {
        Set<String> images = filterImgLinks(childLinks);
        for (String img : images) {
            if (img.contains(urlTail)) {
                return img;
            }
        }
        return null;
    }

    private static Set<String> filterImgLinks(Set<String> childLinks) {
        Set<String> sReturn = new HashSet<>();
        for (String link : childLinks) {
            if (isImageLink(link)){
                sReturn.add(link);
            }
        }
        return sReturn;
    }

    protected static String getUrlTail(String childUrl) {
        String[] parts = childUrl.split("/");
        for (int i = (parts.length - 1); i >= 0; i--) {
            if (parts[i].length() > 0) {
                return parts[i];
            }
        }
        return null;
    }

    protected static Set<String> filterImgFolderLinks(Set<String> links) {
        Set<String> sReturn = new HashSet<>();
        Set<String> imgKey = parseImgKeyLinks(links);
        for (String link : links) {
            if (! isImageLink(link) ) {
                for (String key : imgKey) {
                    if (link.contains(key)) {
                        sReturn.add(link);
                    }
                }
            }
        }
        return sReturn;
    }



    private static Set<String> parseImgKeyLinks(Set<String> links) {
        Set<String> sReturn = new HashSet<>();
        for (String link : links) {
            if (isImageLink(link)) {
                String tail = getBaseName(link);
                if (tail.indexOf('_') >=0 ) {
                    tail = tail.substring(0,tail.indexOf('_'));
                }
                if (tail.indexOf('.') >=0) {
                    tail = tail.substring(0,tail.indexOf('.'));
                }
                sReturn.add(tail);
            }
        }

        return sReturn;
    }

    private static Set<String> parseImgLinks(Set<String> links) {
        Set<String> sReturn = new HashSet<>();
        for (String link : links) {
            if (isImageLink(link)) {
                sReturn.add(link);
            }
        }

        return sReturn;
    }

    private static void dumpImageLinks(File dir, String urlHeader, List<Link> links) throws Exception {
        for (Link link : links) {
            if (isImageLink(link)) {
                downloadImagesFromURL(linkURL(urlHeader,link), dir);
            }
        }
    }

    private static boolean isImageLink(Link link) {
        return isImageLink(link.getUri());
    }

    private static boolean isImageLink(String link) {
        return link.endsWith("jpg");
    }

    private static String outputLinks(String urlHeader, List<Link> links) {
        StringBuilder sb = new StringBuilder();
        for (Link link : links) {
            sb.append(linkURL(urlHeader, link));
            sb.append('\n');
        }
        return sb.toString();
    }

    private static String outputList(List<String> links) {
        StringBuilder sb = new StringBuilder();
        for (String link : links) {
            sb.append(link);
            sb.append('\n');
        }
        return sb.toString();
    }

    private static String outputList(Set<String> links) {
        StringBuilder sb = new StringBuilder();
        for (String link : links) {
            sb.append(link);
            sb.append('\n');
        }
        return sb.toString();
    }

    protected static Set<String>  downloadLinksFromURL(URL url) throws Exception {
        String urlHeader = url.toString().replace(url.getPath(),"");
        try {
            InputStream input = url.openStream();
            LinkContentHandler linkHandler = new LinkContentHandler();
            ContentHandler textHandler = new BodyContentHandler();
            ToHTMLContentHandler toHTMLHandler = new ToHTMLContentHandler();
            TeeContentHandler teeHandler = new TeeContentHandler(linkHandler, textHandler, toHTMLHandler);
            Metadata metadata = new Metadata();
            ParseContext parseContext = new ParseContext();
            HtmlParser parser = new HtmlParser();
            parser.parse(input, teeHandler, metadata, parseContext);
            input.close();

            linkHandler.getLinks();

            return convertLinksToStringSet(urlHeader, linkHandler.getLinks());
        } catch (Exception e) {
            return null;
        }
    }

    private static Set<String> convertLinksToStringSet(String urlHeader, List<Link> links) {
        Set<String> sReturn = new HashSet<>();
        for (Link link : links) {
            sReturn.add(linkURL(urlHeader, link));
        }
        return sReturn;
    }

    protected static void  downloadImagesFromURL(String imgUrl, File dir) throws Exception {
        URL url = new URL(imgUrl);
        String ext = imgUrl.substring(imgUrl.lastIndexOf('.') + 1);
        try {
            BufferedImage img = ImageIO.read(url);
//        ImageIO.write(img,ext,new File(dir,"image_" + dir.listFiles().length + "." + ext));
            ImageIO.write(img,ext,new File(dir,getBaseName(imgUrl)));
        } catch (IOException e) {
            System.out.print(ERROR_CHAR);
            System.out.println(e.getCause().getMessage());
        }
    }


    private static String linkURL(String urlHeader, Link link) {
        String uriPrefix = "";
        if (link.getUri().startsWith("//")) {
            uriPrefix = "http:";
        } else if (link.getUri().startsWith("/")) {
            uriPrefix = urlHeader;
        }
        return uriPrefix +  link.getUri();
    }
}
