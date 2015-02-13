package Utilities;

import Models.GearSpec.DependencySpec;
import Models.GearSpec.DependencySpecSource;
import Singletons.SettingsManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by matthewyork on 4/1/14.
 */
public class Utils {

    public static String wrappedStringForString(String inputString, int wrapWidth){
        inputString = inputString.replace("\n", "<br/>");
        return String.format("<html><div style=\"width:%dpx;\">%s</div><html>", wrapWidth, inputString);
    }

    public static File gradleBuddyDirectory(){

        return new File(SettingsManager.getInstance().getSpecsPath()+Utils.pathSeparator()+"repos");
    }

    public static File getDefaultDirectory(){
        File defaultDirectory;
        //Setup file
        if (OSValidator.isWindows()) {
            defaultDirectory = new File(System.getProperty("user.home")+"/GradleBuddy"); //C drive
        } else if (OSValidator.isMac()) {
            defaultDirectory = new File(System.getProperty("user.home")+"/.gradlebuddy"); //Home folder
        } else if (OSValidator.isUnix()) {
            defaultDirectory = new File("~/.gradlebuddy"); //Home folder
        } else if (OSValidator.isSolaris()) {
            defaultDirectory = new File("~/GradleBuddy");//Home folder
        } else {
            defaultDirectory = new File("~/GradleBuddy");//Home folder
        }

        return defaultDirectory;
    }

    public static String pathSeparator(){
        return (OSValidator.isWindows()) ? "\\":"/";
    }

    public static String newLine(){
        return (OSValidator.isWindows()) ? "\r\n":"\n";
    }

    public static boolean ping(String url, int timeout) {
        //url = url.replaceFirst("https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }

    public static DependencySpec specForFile(File specFile){
        DependencySpec spec = null;

        if (specFile != null){
            if(specFile.exists()) {
                //Get string data
                String specString = null;
                try {
                    specString = FileUtils.readFileToString(specFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                //Get spec
                try{
                    spec = new Gson().fromJson(specString, DependencySpec.class);
                }
                catch (JsonSyntaxException e){
                    e.printStackTrace();
                }
            }
        }

        return spec;
    }

    public static DependencySpec specForInfo(String name, String version){
        //Create spec file
        File specFile = null;
        if (version != null){
            specFile = new File(Utils.gradleBuddyDirectory()+Utils.pathSeparator()+name+Utils.pathSeparator()+version+Utils.pathSeparator()+name+".gearspec");
        }
        else {
            String[] versions = versionsForProject(name);
            if (versions.length > 0){
                specFile = new File(Utils.gradleBuddyDirectory()+Utils.pathSeparator()+name+Utils.pathSeparator()+versions[versions.length-1]+Utils.pathSeparator()+name+".gearspec");
            }
        }

        return specForFile(specFile);
    }

    public static String[] versionsForProject(String project){
        File versionsDirectory = new File(Utils.gradleBuddyDirectory().getAbsolutePath()+Utils.pathSeparator()+project);
        return versionsDirectory.list();
    }

    public static DependencySpec.DependencyState specStateForSpec(DependencySpec spec, Project project){
//        if (spec.isRegistered(project)){
//            //Make local separator for speed
//            String pathSeparator = Utils.pathSeparator();
//
//            if (spec.getType().equals(GearSpec.SPEC_TYPE_JAR)){
//                //TODO: Only checks for name, not version...
//                if (new File(project.getBasePath()+pathSeparator+"Gears"+pathSeparator+"Jars"+pathSeparator+spec.getName()+pathSeparator+spec.getVersion()+pathSeparator+Utils.jarFileNameForSpecSource(spec.getSource())).exists()){
//                    return GearSpec.DependencyState.DependencyStateInstalled;
//                }
//                else {
//                    return GearSpec.DependencyState.GearStateDeclared;
//                }
//            }
//            else if(spec.getType().equals(GearSpec.SPEC_TYPE_MODULE)){
//                //TODO: Only checks for name, not version...
//                if(new File(project.getBasePath()+Utils.pathSeparator()+"Gears"+pathSeparator+"Modules"+pathSeparator+spec.getName()+pathSeparator+spec.getVersion()).exists()){
//                    return GearSpec.DependencyState.DependencyStateInstalled;
//                }
//                else{
//                    return GearSpec.DependencyState.GearStateDeclared;
//                }
//            }
//            else {
//                return GearSpec.DependencyState.GearStateDeclared;
//            }
//        }

        return DependencySpec.DependencyState.DependencyStateUninstalled;
    }
}
