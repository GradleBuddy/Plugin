package Panels;

import Forms.ManageDependenciesForm;
import Models.GearSpec.DependencySpec;
import Models.GearSpec.DependencySpecAuthor;
import Utilities.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by matthewyork on 4/2/14.
 */
public class SpecDetailsPanel extends JPanel{
    private DependencySpec selectedSpec;

    public SpecDetailsPanel(DependencySpec selectedSpec) {
        this.selectedSpec = selectedSpec;

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setMaximumSize(new Dimension(ManageDependenciesForm.DETAILS_INNER_WIDTH, -1));


        //Add repo name
        if (selectedSpec.getName() != null){
            JLabel nameLabel = new JLabel(selectedSpec.getName(), JLabel.LEFT);
            nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.BOLD, 14));
            this.add(nameLabel);
        }


        //Add version and type
        if (selectedSpec.getVersion() != null){
            JLabel versionLabel = new JLabel(selectedSpec.getVersion() + " - " + selectedSpec.getSource().name, JLabel.LEFT);
            versionLabel.setFont(new Font(versionLabel.getFont().getName(), Font.BOLD, 12));
            this.add(versionLabel);
        }

        //Add summary
        if (selectedSpec.getSummary() != null) {
            JLabel summaryLabel = new JLabel(Utils.wrappedStringForString(selectedSpec.getSummary(), ManageDependenciesForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            summaryLabel.setFont(new Font(summaryLabel.getFont().getName(), Font.PLAIN, 12));
            this.add(summaryLabel);
        }

        //Add authors
        if (selectedSpec.getRelease_notes() != null) {
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>"+selectedSpec.getVersion()+" - Release Notes", ManageDependenciesForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            this.add(header);

            //Add release notes
            JLabel releaseNotesLabel = new JLabel(Utils.wrappedStringForString(selectedSpec.getRelease_notes(), ManageDependenciesForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            releaseNotesLabel.setFont(new Font(releaseNotesLabel.getFont().getName(), Font.PLAIN, 12));
            this.add(releaseNotesLabel);
        }

        //Add authors
        if (selectedSpec.getAuthor() != null) {
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>Authors", ManageDependenciesForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            this.add(header);

            //Add authors
            JLabel authorLabel = new JLabel(Utils.wrappedStringForString(selectedSpec.getAuthor().getName() + " - " + selectedSpec.getAuthor().getEmail(), ManageDependenciesForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            authorLabel.setFont(new Font(authorLabel.getFont().getName(), Font.PLAIN, 12));
            this.add(authorLabel);
        }

        //Add License
        if (selectedSpec.getLicense() != null) {
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>License", ManageDependenciesForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            this.add(header);

            //Add license
            JLabel licenseLabel = new JLabel(selectedSpec.getLicense(), JLabel.LEFT);
            licenseLabel.setFont(new Font(licenseLabel.getFont().getName(), Font.PLAIN, 12));
            this.add(licenseLabel);
        }

        //Add Copyright
        if (selectedSpec.getCopyright() != null) {
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>Copyright", ManageDependenciesForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            this.add(header);

            //Add copyright
            JLabel copyrightLabel = new JLabel(selectedSpec.getCopyright(), JLabel.LEFT);
            copyrightLabel.setFont(new Font(copyrightLabel.getFont().getName(), Font.PLAIN, 12));
            this.add(copyrightLabel);
        }

        //Add homepage
        /*
        if (selectedSpec.getHomepage() != null) {
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>Homepage", ManageAndroidGearsForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            this.add(header);

            //Add homepage
            JLabel homepageLabel = new JLabel(Utils.wrappedStringForString(selectedSpec.getHomepage(), ManageAndroidGearsForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            homepageLabel.setMaximumSize(new Dimension(ManageAndroidGearsForm.DETAILS_INNER_WIDTH, 30));
            homepageLabel.setFont(new Font(homepageLabel.getFont().getName(), Font.PLAIN, 12));
            this.add(homepageLabel);


        }*/

        //Add Tags
        if (selectedSpec.getTags() != null) {
            //Add header
            JLabel header = new JLabel(Utils.wrappedStringForString("<br/>Tags", ManageDependenciesForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
            this.add(header);

            //Gather tags
            String tagsString = "";
            for (String tag : selectedSpec.getTags()) {
                tagsString = tagsString+tag+", ";
            }
            if (tagsString.length() > 2){
                tagsString = tagsString.substring(0, tagsString.length()-2);
            }

            //Create tags label
            JLabel tagsLabel = new JLabel(Utils.wrappedStringForString(tagsString, ManageDependenciesForm.DETAILS_INNER_WIDTH), JLabel.LEFT);
            tagsLabel.setFont(new Font(tagsLabel.getFont().getName(), Font.PLAIN, 12));
            this.add(tagsLabel);
        }
    }
}
