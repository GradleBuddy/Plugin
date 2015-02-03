package Workers.InstallUninstall;

import Models.GearSpec.DependencySpec;
import Utilities.GearSpecManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * Created by matthewyork on 4/4/14.
 */
public class InstallDependencyForSpecWorker extends SwingWorker<Void, Void> {

    private DependencySpec selectedSpec;
    private Project project;
    private Module module;
    public boolean successful;

    public InstallDependencyForSpecWorker(DependencySpec spec, Project project, Module module) {
        this.selectedSpec = spec;
        this.project = project;
        this.module = module;
    }

    @Override
    protected Void doInBackground() throws Exception {

        if (selectedSpec != null){
            successful = GearSpecManager.installGear(selectedSpec, project, module);
        }

        return null;
    }
}
