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
        return false;
    }

    ///////////////////////
    // Uninstall
    ///////////////////////

    public static Boolean uninstallGear(DependencySpec spec, Project project, Module module){
        return false;
    }
}
