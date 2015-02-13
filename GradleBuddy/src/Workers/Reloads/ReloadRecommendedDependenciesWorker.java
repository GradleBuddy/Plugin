package Workers.Reloads;

import Models.GearSpec.DependencySpec;
import Utilities.GradleBuddy;
import Utilities.JCenter;
import Utilities.Utils;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by matthewyork on 2/13/15.
 */

public class ReloadRecommendedDependenciesWorker extends SwingWorker<Void, Void> {

    public ReloadRecommendedDependenciesWorker() {

    }

    @Override
    protected Void doInBackground() throws Exception {
        GradleBuddy.reloadLocalRecommendations();
        return null;
    }
}