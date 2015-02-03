package Workers.Search;

import Models.GearSpec.DependencySpec;
import Models.GearSpec.DependencySpecUpdate;
import Models.GearSpec.GearSpecAuthor;
import Models.GearSpecRegister.GearSpecRegister;
import Utilities.GearSpecRegistrar;
import Utilities.Utils;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by matthewyork on 4/9/14.
 */
public class SearchUpdatableProjectsWorker extends SwingWorker<Void, Void>{

    private Project project;
    private String searchString;
    public ArrayList<DependencySpecUpdate> specs = new ArrayList<DependencySpecUpdate>();

    public SearchUpdatableProjectsWorker(Project project, String searchString) {
        this.project = project;
        this.searchString = searchString;
    }

    @Override
    protected Void doInBackground() throws Exception {

        //Get specNames register
        GearSpecRegister register = GearSpecRegistrar.getRegister(this.project);

        //Populate a list of installed gears
        if (register != null){
            ArrayList<DependencySpec> installedSpecs = new ArrayList<DependencySpec>();
            for (DependencySpec declaredSpec : register.declaredGears){
                if (Utils.specStateForSpec(declaredSpec, project) == DependencySpec.DependencyState.DependencyStateInstalled){
                    declaredSpec.setDependencyState(Utils.specStateForSpec(declaredSpec, project));
                    installedSpecs.add(declaredSpec);
                }
            }

            //Filter down to only those gears with updates
            filterInstalledGears(installedSpecs);

            return null;
        }

        return null;
    }

    private  void filterInstalledGears(ArrayList<DependencySpec> installedGears) {
        //Iterate over all installed gears
        for (DependencySpec spec : installedGears){

            //Get all versions for spec
            String[] versions = Utils.versionsForProject(spec.getName());

            //If the version does not match the latest version, mark it as updateable and
            if (versions.length > 0){
                //If the version does not equal the last available version, then a
                if (!versions[versions.length -1].equals(spec.getVersion())){
                    DependencySpecUpdate updateSpec = new DependencySpecUpdate(spec);
                    updateSpec.setUpdateVersionNumber(versions[versions.length -1]);

                    String[] searchParameters = searchString.split(" ");
                    for (String searchParamter : searchParameters){
                        String filterString = spec.getName().toLowerCase() + " " + spec.getVersion().toLowerCase();

                        //Gather tags
                        if (spec.getTags() != null){
                            for (String tag : spec.getTags()) {
                                filterString = filterString+tag.toLowerCase()+" ";
                            }
                        }

                        //Gather authors
                        for (GearSpecAuthor author : spec.getAuthors()) {
                            filterString = filterString+author.getName().toLowerCase()+" ";
                        }

                        //Filter with the search string over spec metadata
                        if(filterString.contains(searchParamter.toLowerCase())){
                            //Add sepec
                            this.specs.add(updateSpec);
                        }
                    }
                }
            }
        }
    }
}
