package Utilities;

import Models.GearSpec.DependencySpec;
import Models.GearSpec.DependencySpecAuthor;
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
        String html =  HTMLForQueryString(queryString);

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
            request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            request.addHeader("Accept", "*/*");
            request.addHeader("Origin", "https://bintray.com");
            request.addHeader("Connection", "keep-alive");
            String asdf = "filterByPkgName="+queryString+"&repoPath=%2Fbintray%2Fjcenter&sortBy=sortPriority";
            request.bodyString("filterByPkgName="+queryString+"&repoPath=%2Fbintray%2Fjcenter&sortBy=sortPriority", ContentType.APPLICATION_FORM_URLENCODED);

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

        //TESTING
        //DependencySpec testSpec = dependencySpecForEntry(testHTML());
        //dependencySpecs.add(testSpec);

        // Scan through components and build posts
        for (int xx = 1; xx < htmlComponents.size(); xx++) {
            //Log.i("asdf", xx + "");
            DependencySpec newSpec = dependencySpecForEntry(htmlComponents.get(xx));
            dependencySpecs.add(newSpec);
        }


        return dependencySpecs;
    }

    private static DependencySpec dependencySpecForEntry(String htmlString) {
        //Sanitize HTML string
        htmlString = htmlString.replaceAll("\n", "");

        DependencySpec spec = new DependencySpec();
        spec.setSource(new DependencySpecSource());
        spec.getSource().name = "jCenter (bintray.com)";

        OMScanner scanner = new OMScanner(htmlString);
        int contentIndex = -1;

        //Get user avatar
        contentIndex = htmlString.indexOf("<div class=\"useravatar\">");
        if (contentIndex != -1) {
            scanner.setScanIndex(contentIndex + "id=score_".length());

            //Get user image
            scanner.skipToString("background-image: url(");
            String authorUrlString = scanner.scanToString(")");
            spec.setAuthor(new DependencySpecAuthor());
            spec.getAuthor().setImageUrl(authorUrlString);


            //Get user name
            scanner.skipToString("class=\"username\"");
            scanner.skipToString(">");
            spec.getAuthor().setName(scanner.scanToString("<").trim());
        }

        // Scan Url
        contentIndex = htmlString.indexOf("<div class=\"title\">");
        if (contentIndex != -1) {
            scanner.setScanIndex(contentIndex + "<div class=\"title\">".length());
            //Get URL
            scanner.skipToString("<a href=\"");
            String urlString = scanner.scanToString("\"");
            spec.getSource().webUrl = "https://bintray.com" + urlString;

            //Get Title
            scanner.skipToString(">");
            spec.setName(scanner.scanToString("</a>").trim());
        }

        //Get rating
        scanner.skipToString("<div itemprop=\"average\" content=\"");
        spec.setRating(scanner.scanToString("\">"));
        scanner.setScanIndex(0);

        //Get votes
        contentIndex = htmlString.indexOf("itemprop=\"votes\">");
        if (contentIndex != -1) {
            scanner.setScanIndex(contentIndex + "itemprop=\"votes\">".length());
            spec.setVotes(scanner.scanToString("<"));
        }

        //Get description (summary)
        contentIndex = htmlString.indexOf("<div class=\"description\" title=\"");
        if (contentIndex != -1) {
            scanner.setScanIndex(contentIndex + "<div class=\"description\" title=\"".length());
            spec.setSummary(scanner.scanToString("\""));
        }

        //Get version
        contentIndex = htmlString.indexOf("<div class=\"version\">");
        if (contentIndex != -1) {
            scanner.setScanIndex(contentIndex + "<div class=\"version\">".length());
            spec.setVersion(scanner.scanToString("<").trim());
        }

        //Get date


        return spec;
    }

    public static String testHTML() {
        return "<div class=\"leftboxcol\">\n" +
                "   <div class=\"userAvatarBrowsePackage\">\n" +
                "      <a href=\"/jfrog\" title=\"jfrog\">\n" +
                "         <div class=\"useravatar\">\n" +
                "            <div class='avatar-icon avatar-small avatar-org null' data-tooltip='jfrog' style='height:50px; width:50px; display:block; background-image: url(https://10428.https.cdn.softlayer.net/8010428/dal05.objectstorage.softlayer.net/v1/AUTH_3c173d3a-8847-45dc-9d93-faf1d6e70fe5/avatars/jfrogdev/organization/jfrog);' ></div>\n" +
                "         </div>\n" +
                "      </a>\n" +
                "      <a href=\"/jfrog\" title=\"jfrog\">\n" +
                "         <div class=\"username\" title=\"JFrog\">                                JFrog                            </div>\n" +
                "      </a>\n" +
                "   </div>\n" +
                "   <div class=\"title\">                                                                                   <a href=\"/jfrog/jfrog-jars/gradle-bintray-plugin/view\" onmouseover=\"jQuery(this).html(&#39;gradle-bintray-plugin-jfrog&#39;)\" alt=\"\" onmouseout=\"jQuery(this).html(&#39;gradle-bintray-plugin-jfrog&#39;)\">                        gradle-bintray-plugin-jfrog                    </a>                </div>\n" +
                "   <div class=\"stars\">\n" +
                "      <span itemprop=\"rating\" itemscope itemtype=\"http://data-vocabulary.org/Rating\">\n" +
                "         <div itemprop=\"average\" content=\"4.8666666667\"></div>\n" +
                "      </span>\n" +
                "      <div class=\"ratingDisplay\">\n" +
                "         <div>\n" +
                "            <div class=\"star on\">                            <span style=\"width:100%;\"/>                        </div>\n" +
                "         </div>\n" +
                "         <div>\n" +
                "            <div class=\"star on\">                            <span style=\"width:100%;\"/>                        </div>\n" +
                "         </div>\n" +
                "         <div>\n" +
                "            <div class=\"star on\">                            <span style=\"width:100%;\"/>                        </div>\n" +
                "         </div>\n" +
                "         <div>\n" +
                "            <div class=\"star on\">                            <span style=\"width:100%;\"/>                        </div>\n" +
                "         </div>\n" +
                "         <div>\n" +
                "            <div class=\"star on\">                            <span style=\"width:86.66666666999996%;\"/>                        </div>\n" +
                "         </div>\n" +
                "         <div class=\"tdRating\">\n" +
                "            <div class=\"ratingsPageHeadLabel\">                                            (<span itemprop=\"votes\">15</span>)                                    </div>\n" +
                "         </div>\n" +
                "      </div>\n" +
                "   </div>\n" +
                "   <div class=\"description\" title=\"A Gradle plugin for publishing to Bintray, with Bintray-specific extensions; Supports both the new and the old Gradle publishing models.\">                    A Gradle plugin for publishing to Bintray, with Bintray-specific extensions; Supports both the new and the old Gradle publishing models.                </div>\n" +
                "</div>\n" +
                "<div class=\"rightboxcol\">\n" +
                "   <div class=\"pkglinksymbol\" title=\"This package is linked from another repository\"></div>\n" +
                "   <a href=\"/jfrog/jfrog-jars/gradle-bintray-plugin/view\">\n" +
                "      <div class=\"itemavatar\">\n" +
                "         <div class='avatar-icon avatar-large null ' style='height:90px; width:90px; background-image: url(https://10428.https.cdn.softlayer.net/8010428/dal05.objectstorage.softlayer.net/v1/AUTH_3c173d3a-8847-45dc-9d93-faf1d6e70fe5/avatars/fc7174b5-f033-4917-b02a-644c486b8631);' data-tooltip=\"gradle-bintray-plugin\"></div>\n" +
                "      </div>\n" +
                "   </a>\n" +
                "   <a href=\"/jfrog/jfrog-jars/gradle-bintray-plugin/1.1/view\" class=\"latest\" title=\"Version: 1.1\">\n" +
                "      <div class=\"version\">                                                        1.1                        </div>\n" +
                "   </a>\n" +
                "   <div class=\"date\">                                                    Jan 21, 2015                                            </div>\n" +
                "</div>\n" +
                "</div>                                                                                                                                    \n" +
                "<div class=\"box floatright\">\n" +
                "   <div class=\"leftboxcol\">\n" +
                "      <div class=\"userAvatarBrowsePackage\">\n" +
                "         <a href=\"/groovy\" title=\"groovy\">\n" +
                "            <div class=\"useravatar\">\n" +
                "               <div class='avatar-icon avatar-small avatar-org null' data-tooltip='groovy' style='height:50px; width:50px; display:block; background-image: url(https://10428.https.cdn.softlayer.net/8010428/dal05.objectstorage.softlayer.net/v1/AUTH_3c173d3a-8847-45dc-9d93-faf1d6e70fe5/avatars/glaforge/organization/groovy);' ></div>\n" +
                "            </div>\n" +
                "         </a>\n" +
                "         <a href=\"/groovy\" title=\"groovy\">\n" +
                "            <div class=\"username\" title=\"Groovy programming language\">                                Groovy programming l&hellip;                            </div>\n" +
                "         </a>\n" +
                "      </div>\n" +
                "      <div class=\"title\">                                                                                    <a href=\"/groovy/maven/groovy/view\" onmouseover=\"jQuery(this).html(&#39;groovy&#39;)\" alt=\"jcenter - Maven - groovy\" onmouseout=\"jQuery(this).html(&#39;groovy&#39;)\">                        groovy                    </a>                </div>\n" +
                "      <div class=\"stars\">\n" +
                "         <span itemprop=\"rating\" itemscope itemtype=\"http://data-vocabulary.org/Rating\">\n" +
                "            <div itemprop=\"average\" content=\"5.0\"></div>\n" +
                "         </span>\n" +
                "         <div class=\"ratingDisplay\">\n" +
                "            <div>\n" +
                "               <div class=\"star on\">                            <span style=\"width:100%;\"/>                        </div>\n" +
                "            </div>\n" +
                "            <div>\n" +
                "               <div class=\"star on\">                            <span style=\"width:100%;\"/>                        </div>\n" +
                "            </div>\n" +
                "            <div>\n" +
                "               <div class=\"star on\">                            <span style=\"width:100%;\"/>                        </div>\n" +
                "            </div>\n" +
                "            <div>\n" +
                "               <div class=\"star on\">                            <span style=\"width:100%;\"/>                        </div>\n" +
                "            </div>\n" +
                "            <div>\n" +
                "               <div class=\"star on\">                            <span style=\"width:100%;\"/>                        </div>\n" +
                "            </div>\n" +
                "            <div class=\"tdRating\">\n" +
                "               <div class=\"ratingsPageHeadLabel\">                                            (<span itemprop=\"votes\">7</span>)                                    </div>\n" +
                "            </div>\n" +
                "         </div>\n" +
                "      </div>\n" +
                "      <div class=\"description\" title=\"Groovy...\n" +
                "         is an agile and dynamic language for the Java Virtual Machine\n" +
                "         builds upon the strengths of Java but has additional power features inspired by languages like Python, Ruby and Smalltalk\n" +
                "         makes modern programming features available to Java developers with almost-zero learning curve\n" +
                "         provides the ability to statically type check and statically compile your code for robustness and performance\n" +
                "         supports Domain-Specific Languages and other compact syntax so your code becomes easy to read and maintain\n" +
                "         makes writing shell and build scripts easy with its powerful processing primitives, OO abilities and an Ant DSL\n" +
                "         increases developer productivity by reducing scaffolding code when developing web, GUI, database or console applications\n" +
                "         simplifies testing by supporting unit testing and mocking out-of-the-box\n" +
                "         seamlessly integrates with all existing Java classes and libraries\n" +
                "         compiles straight to Java bytecode so you can use it anywhere you can use Java\">                    Groovy...\n" +
                "         is an agile and dynamic language for the Java Virtual Machine\n" +
                "         builds upon the strengths of Java but has additional power features i&hellip;                \n" +
                "      </div>\n" +
                "   </div>\n" +
                "   <div class=\"rightboxcol\">\n" +
                "      <div class=\"pkglinksymbol\" title=\"This package is linked from another repository\"></div>\n" +
                "      <a href=\"/groovy/maven/groovy/view\">\n" +
                "         <div class=\"itemavatar\">\n" +
                "            <div class='avatar-icon avatar-large null ' style='height:90px; width:90px; background-image: url(https://10428.https.cdn.softlayer.net/8010428/dal05.objectstorage.softlayer.net/v1/AUTH_3c173d3a-8847-45dc-9d93-faf1d6e70fe5/avatars/pkg/groovy/maven/groovy/db5defbc6ad17f33f2dc2914440bbe801372ab3c);' data-tooltip=\"groovy\"></div>\n" +
                "         </div>\n" +
                "      </a>\n" +
                "      <a href=\"/groovy/maven/groovy/2.4.0/view\" class=\"latest\" title=\"Version: 2.4.0\">\n" +
                "         <div class=\"version\">                                                        2.4.0                        </div>\n" +
                "      </a>\n" +
                "      <div class=\"date\">                                                    Feb 04, 2015                                            </div>\n" +
                "   </div>\n" +
                "</div>";
    }
}
