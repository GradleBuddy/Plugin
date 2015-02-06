package Utilities;

import Models.GearSpec.DependencySpec;
import Models.GearSpec.DependencySpecSource;
import org.apache.commons.httpclient.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by matthewyork on 2/6/15.
 */
public class JCenter {
    public static ArrayList<DependencySpec> searchJcenter(String queryString) {
        ArrayList<DependencySpec> searchResults = new ArrayList<DependencySpec>();

        //Get HTML for Query
        String html = HTMLForQueryString(queryString);

        //If valid HTML was returned
        if (html != "") {
            return dependencySpecsForHTML(html);
        }

        return new ArrayList<DependencySpec>();
    }

    private static String HTMLForQueryString(String queryString) {
        try {
            Request request = Request.Post("https://bintray.com/bintray/jcenter");
            request.addHeader("Host", "bintray.com");
            request.bodyString("filterByPkgName="+queryString+"&repoPath=%2Fbintray%2Fjcenter&sortBy=sortPriority", ContentType.TEXT_PLAIN);

            return request.execute().returnContent().asString();
        }
        catch (Exception e){

        }

        return "";
    }

    private static ArrayList<DependencySpec> dependencySpecsForHTML(String html) {
        ArrayList<DependencySpec> dependencySpecs = new ArrayList<DependencySpec>();

        List<String> htmlComponents = Arrays
                .asList(html
                        .split("\\s*<div class=\"box\">\\s*"));

        // Scan through components and build posts
        for (int xx = 1; xx < htmlComponents.size(); xx++) {
            //Log.i("asdf", xx + "");
            DependencySpec newSpec = dependencySpecForEntry(htmlComponents.get(xx));
            dependencySpecs.add(newSpec);
        }


        return dependencySpecs;
    }

    private static DependencySpec dependencySpecForEntry(String htmlString) {
        DependencySpec spec = new DependencySpec();

        OMScanner scanner = new OMScanner(htmlString);
        int contentIndex = -1;

        //Get user avatar
        contentIndex = htmlString.indexOf("<div class=\"useravatar\">");
        if (contentIndex != -1) {
            scanner.skipToString("background-image: url(");
            //spec.sp
        }

        // Scan Url
        contentIndex = htmlString.indexOf("<div class=\"title\">");
        if (contentIndex != -1) {
            //Get URL
            scanner.setScanIndex(contentIndex + "<a href=\"".length());
            spec.setSource(new DependencySpecSource());
            String urlString = scanner.scanToString("\">");
            spec.getSource().setUrl("https://bintray.com" + urlString);

            //Get Title
            scanner.skipToString(">");
            spec.setName(scanner.scanToString("</a>"));
        }

        //Get rating
        scanner.skipToString("<div itemprop=\"average\" content=\"");
        spec.setRating(scanner.scanToString("\">"));

        //Get votes
        contentIndex = htmlString.indexOf("itemprop=\"votes\">");
        if (contentIndex != -1) {
            spec.setVotes(scanner.scanToString("<"));
        }

        //Get description (summary)
        contentIndex = htmlString.indexOf("<div class=\"description\" title=\"");
        if (contentIndex != -1) {
            spec.setSummary(scanner.scanToString("<"));
        }

        //Get image url
        contentIndex = htmlString.indexOf("<div class=\"description\" title=\"");
        if (contentIndex != -1) {
            spec.setSummary(scanner.scanToString("<"));
            spec.getAuthor().setImageUrl(scanner.scanToString("\""));
        }

        return spec;
    }
}
