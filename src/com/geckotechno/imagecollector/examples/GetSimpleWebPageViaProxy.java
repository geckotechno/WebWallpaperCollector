package com.geckotechno.imagecollector.examples;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class GetSimpleWebPageViaProxy {

    public static void main(String[] args)throws Exception {
        HttpHost proxy = new HttpHost("web-proxy.cup.hp.com", 8080, "http");

        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

            HttpHost target = new HttpHost("www.google.com", 80, "http");
            HttpGet req = new HttpGet("/");

            System.out.println("executing request to " + target + " via " + proxy);
            HttpResponse rsp = httpclient.execute(target, req);
            HttpEntity entity = rsp.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(rsp.getStatusLine());
            Header[] headers = rsp.getAllHeaders();
            for (int i = 0; i<headers.length; i++) {
                System.out.println(headers[i]);
            }
            System.out.println("----------------------------------------");

            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
            }

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
}
