package Utilities;

import Models.GearSpec.DependencySpec;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;

/**
 * Created by matthewyork on 4/9/14.
 */
public class GearSpecManager {

    ///////////////////////
    // Install
    ///////////////////////

    public static Boolean installGear(DependencySpec spec, Project project, Module module){
        return GearSpecManager.installGear(spec, project, module, null);
    }

    public static Boolean installGear(DependencySpec spec, Project project, Module module, DependencySpec parentSpec){
        File buildFile = new File(new File(module.getModuleFilePath()).getParentFile().getAbsolutePath() + Utils.pathSeparator() + "build.gradle");

        if (buildFile.exists()) {
            try {
                //Read back string from build file
                String buildFileString = FileUtils.readFileToString(buildFile);

                //Create new addition
                String newDependencyString = "dependencies{"+spec.getSource().dependency+"}";

                //Check to see if it is already there
                if (!buildFileString.contains(spec.getSource().dependency)) {
                    //Make edit
                    buildFileString = buildFileString.concat("\n"+newDependencyString);

                    //Write changes to settings.gradle
                    FileUtils.forceDelete(buildFile);
                    FileUtils.write(buildFile, buildFileString);

                    return true;
                }
                else {
                    return true;
                }
            }
            catch (Exception e) {

            }
        }

        return false;
    }

    ///////////////////////
    // Uninstall
    ///////////////////////

    public static Boolean uninstallGear(DependencySpec spec, Project project, Module module){
        return false;
    }
}
