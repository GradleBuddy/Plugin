package Workers.Search;

import Models.GearSpec.DependencySpec;
import Models.GearSpec.DependencySpecAuthor;
import Models.GearSpecRegister.GearSpecRegister;
import Utilities.Utils;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/5/14.
 */
public class SearchInstalledProjectsWorker extends SwingWorker<Void, Void>{

    private Project project;
    String searchString;
    public ArrayList<DependencySpec> specs = new ArrayList<DependencySpec>();

    public SearchInstalledProjectsWorker(Project project, String searchString) {
        this.project = project;
        this.searchString = searchString;
    }

    @Override
    protected Void doInBackground() throws Exception {


        return null;
    }
}
