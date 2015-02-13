package Utilities;

import Models.GearSpec.DependencySpec;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Request;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by matthewyork on 2/13/15.
 */
public class GradleBuddy {
    public static ArrayList<DependencySpec> searchGradleBuddyListings(String searchString) {
        //Get json string
        String jsonListingsString = listingsJson();

        //Parse Json
        Type type = new TypeToken<ArrayList<DependencySpec>>(){}.getType();
        ArrayList<DependencySpec> masterSpecsList = new Gson().fromJson(jsonListingsString, type);

        //Return matches
        return (searchString == "") ? masterSpecsList : matchingSpecsForSearchString(searchString, masterSpecsList);
    }

    private static String listingsJson() {
        //Get settings file
        File listingsFile = new File(Utils.getDefaultDirectory().getAbsolutePath()+Utils.pathSeparator()+"Dependencies.json");

        try {
            if (listingsFile.exists()) {
                return FileUtils.readFileToString(listingsFile);
            }
            else {
                //Make request and get json
                Request request = Request.Get("https://raw.githubusercontent.com/GradleBuddy/RecommendedDependencies/master/Dependencies.json");
                String jsonString = request.execute().returnContent().asString();

                //Save file so we don't have to fetch it again
                if (jsonString.length() > 0) {
                    FileUtils.write(listingsFile, jsonString);
                }

                return jsonString;
            }
        }
        catch (Exception e) {

        }

        return "[]";
    }

    private static ArrayList<DependencySpec> matchingSpecsForSearchString(String searchString, ArrayList<DependencySpec> masterSpecs) {
        ArrayList<DependencySpec> matchingSpecs = new ArrayList<DependencySpec>();

        //Break up search string, if necessary
        String[] searchParams = searchString.toLowerCase().split(" ");

        //Iterate over all specs looking for matches
        for (DependencySpec spec : masterSpecs) {
            String specMatchString = spec.getName()+" "+spec.getAuthor()+" "+spec.getSerializedTags();
            specMatchString = specMatchString.toLowerCase();

            //Iterate over all search parameters
            for (String searchParam : searchParams) {
                if (specMatchString.contains(searchParam)) {
                    matchingSpecs.add(spec);
                    break;
                }
            }
        }

        return matchingSpecs;
    }

    public static void reloadLocalRecommendations() {
        //Get settings file
        File listingsFile = new File(Utils.getDefaultDirectory().getAbsolutePath()+Utils.pathSeparator()+"Dependencies.json");

        try {
            //Make request and get json
            Request request = Request.Get("https://raw.githubusercontent.com/GradleBuddy/RecommendedDependencies/master/Dependencies.json");
            String jsonString = request.execute().returnContent().asString();

            //Save file so we don't have to fetch it again
            if (jsonString.length() > 0) {
                FileUtils.write(listingsFile, jsonString);
            }
        }
        catch (Exception e) {

        }
    }
}
