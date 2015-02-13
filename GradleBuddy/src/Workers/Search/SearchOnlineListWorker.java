package Workers.Search;

import Models.GearSpec.DependencySpec;
import Utilities.JCenter;
import Utilities.Utils;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/2/14.
 */
public class SearchOnlineListWorker extends SwingWorker<Void, Void> {

    private String searchString;
    private Project project;
    public ArrayList<DependencySpec> specs = new ArrayList<DependencySpec>();

    public SearchOnlineListWorker(String searchString, Project project) {
        this.searchString = searchString;
        this.project = project;
    }

    @Override
    protected Void doInBackground() throws Exception {
        specs = JCenter.searchJcenter(this.searchString);
        return null;
    }

    private String[] versionsForProject(String project, String pathSeparator){
        File versionsDirectory = new File(Utils.gradleBuddyDirectory().getAbsolutePath()+pathSeparator+project);
        return versionsDirectory.list();
    }
}
