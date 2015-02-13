package Workers;

import Models.GearSpec.DependencySpec;
import Utilities.OSValidator;
import Utilities.Utils;

import javax.swing.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by matthewyork on 4/2/14.
 */
public class GetProjectVersionsWorker extends SwingWorker<Void, Void> {
    private DependencySpec spec;
    public ArrayList<String> versions = new ArrayList<String>();

    public GetProjectVersionsWorker(DependencySpec spec) {
        this.spec = spec;
    }

    @Override
    protected Void doInBackground() throws Exception {
        versions = versionsForSpec(this.spec);
        return null;
    }

    private ArrayList<String> versionsForSpec(DependencySpec spec){


        //Get path separator
        String pathSeparator = (OSValidator.isWindows()) ? "\\":"/";

        //Get versions for spec
        return versionsForProject(spec.getName(), pathSeparator);
    }

    private ArrayList<String> versionsForProject(String project, String pathSeparator){
        File versionsDirectory = new File(Utils.gradleBuddyDirectory().getAbsolutePath()+pathSeparator+project);

        ArrayList<String> versions = new ArrayList<String>(Arrays.asList(versionsDirectory.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return (s.equals(".DS_Store")) ? false: true;
            }
        })));
        Collections.reverse(versions);
        return versions;
    }
}
