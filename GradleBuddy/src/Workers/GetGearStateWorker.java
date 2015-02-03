package Workers;

import Models.GearSpec.DependencySpec;
import Utilities.Utils;
import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * Created by matthewyork on 4/6/14.
 */
public class GetGearStateWorker extends SwingWorker<Void, Void> {

    Project project;
    DependencySpec selectedSpec;
    public DependencySpec.DependencyState dependencyState;

    public GetGearStateWorker(Project project, DependencySpec spec) {
        this.project = project;
        this.selectedSpec = spec;
    }

    @Override
    protected Void doInBackground() throws Exception {

        //Get register
        dependencyState = Utils.specStateForSpec(selectedSpec, project);

        return null;
    }
}
