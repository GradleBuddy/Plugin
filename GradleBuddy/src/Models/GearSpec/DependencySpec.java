package Models.GearSpec;

import Models.GearSpecRegister.GearSpecRegister;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * Created by matthewyork on 3/31/14.
 */
public class DependencySpec {
    public enum DependencyState {
        DependencyStateUninstalled,
        DependencyStateInstalled
    };

    private DependencyState dependencyState = DependencyState.DependencyStateUninstalled;
    private String name;
    private String summary;
    private String release_notes;
    private String version;
    private String copyright;
    private String homepage;
    private String rating;
    private String votes;
    private String license;
    private DependencySpecAuthor author;
    private int minimum_api;
    private DependencySpecSource source;
    private ArrayList<String> tags;

    public DependencyState getDependencyState() {
        return dependencyState;
    }

    public void setDependencyState(DependencyState dependencyState) {
        this.dependencyState = dependencyState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getRelease_notes() {
        return release_notes;
    }

    public void setRelease_notes(String release_notes) {
        this.release_notes = release_notes;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public DependencySpecAuthor getAuthor() {
        return author;
    }

    public void setAuthor(DependencySpecAuthor author) {
        this.author = author;
    }

    public int getMinimum_api() {
        return minimum_api;
    }

    public void setMinimum_api(int minimum_api) {
        this.minimum_api = minimum_api;
    }

    public DependencySpecSource getSource() {
        return source;
    }

    public void setSource(DependencySpecSource source) {
        this.source = source;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getSerializedTags() {
        if (this.tags != null) {
            return StringUtils.join(this.tags, " ");
        }

        return "";
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    ///////////////////////
    // Helper Methods
    ///////////////////////

    public Boolean isRegistered(Project project){

        return false;
    }
}
