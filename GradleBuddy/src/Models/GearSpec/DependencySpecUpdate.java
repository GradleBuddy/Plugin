package Models.GearSpec;

/**
 * Created by matthewyork on 4/9/14.
 * Updated by adfleshner  on 4/22/14.
 */
public class DependencySpecUpdate extends DependencySpec {

    public DependencySpecUpdate(DependencySpec spec) {
        setName(spec.getName());
        setSummary(spec.getSummary());
        setRelease_notes(spec.getRelease_notes());
        setVersion(spec.getVersion());
        setCopyright(spec.getCopyright());
        setHomepage(spec.getHomepage());
        setLicense(spec.getLicense());
        setAuthor(spec.getAuthor());
        setMinimum_api(spec.getMinimum_api());
        setSource(spec.getSource());
        setTags(spec.getTags());
        setDependencyState(spec.getDependencyState());
    }

    private String updateVersionNumber;

    public String getUpdateVersionNumber() {
        return updateVersionNumber;
    }

    public void setUpdateVersionNumber(String updateVersionNumber) {
        this.updateVersionNumber = updateVersionNumber;
    }
}
