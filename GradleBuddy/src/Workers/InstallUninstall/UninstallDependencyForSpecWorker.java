package Workers.InstallUninstall;

import Models.GearSpec.DependencySpec;
import Utilities.GearSpecManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/4/14.
 */
public class UninstallDependencyForSpecWorker extends SwingWorker<Void, Void> {

    private ArrayList<DependencySpec> selectedSpecs;
    private Project project;
    private Module module;
    public boolean successful;

    public UninstallDependencyForSpecWorker(ArrayList<DependencySpec> selectedSpecs, Project project, Module module) {
        this.selectedSpecs = selectedSpecs;
        this.project = project;
        this.module = module;
    }

    @Override
    protected Void doInBackground() throws Exception {

        for (DependencySpec selectedSpec : this.selectedSpecs){
            if(!GearSpecManager.uninstallGear(selectedSpec, project, module)){
                successful = false;
                break;
            }
        }


        successful = true;
        return null;
    }
}
