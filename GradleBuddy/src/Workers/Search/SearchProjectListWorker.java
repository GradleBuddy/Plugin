package Workers.Search;

import Models.GearSpec.DependencySpec;
import Models.GearSpec.DependencySpecAuthor;
import Utilities.JCenter;
import Utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/2/14.
 */
public class SearchProjectListWorker  extends SwingWorker<Void, Void> {

    private String searchString;
    private Project project;
    public ArrayList<DependencySpec> specs = new ArrayList<DependencySpec>();

    public SearchProjectListWorker(String searchString,  Project project) {
        this.searchString = searchString;
        this.project = project;
    }

    @Override
    protected Void doInBackground() throws Exception {
        specs = JCenter.searchJcenter(this.searchString);
        return null;
    }

    private String[] versionsForProject(String project, String pathSeparator){
        File versionsDirectory = new File(Utils.androidGearsDirectory().getAbsolutePath()+pathSeparator+project);
        return versionsDirectory.list();
    }
}
