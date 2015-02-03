package Forms;

import Singletons.SettingsManager;
import Utilities.OSValidator;
import Utilities.Utils;
import Workers.Settings.SetCreateIgnoreEntryWorker;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by matthewyork on 4/6/14.
 */
public class SettingsForm {

    private static int SETTINGS_FRAME_WIDTH = 500;

    public JPanel MasterPanel;
    private JButton resynchronizeAndroidGearsButton;
    private JTextPane allowAndroidGearsToTextPane;
    private JTextPane normallyTheGearsPluginTextPane;
    private JCheckBox createGitignoreEntryCheckBox;
    private JCheckBox autoSyncGearsCheckBox;
    private JTextPane byCheckingYesAndroidTextPane;
    private JLabel LoadingSpinnerLabel;
    private JPanel ResyncProgressPanel;
    private JLabel ResyncStatusLabel;
    private JTextField SpecUrlTextField;
    private JButton FindURLButton;
    private JButton DefaultSpecPathButton;

    public SettingsForm() {
        SettingsManager.getInstance().loadSettings();
        setupCheckBoxes();
        setupMiscUI();
    }

    private void setupCheckBoxes() {
        //Set ignore checkbox. Add check/uncheck listener
        createGitignoreEntryCheckBox.setSelected(SettingsManager.getInstance().getAutoIgnore());
        createGitignoreEntryCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                setCreateIgnoreSelected(createGitignoreEntryCheckBox.isSelected());
            }
        });

        //Set ignore checkbox. Add check/uncheck listener
        autoSyncGearsCheckBox.setSelected(SettingsManager.getInstance().getAutoSync());
        autoSyncGearsCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                setAutoSync(autoSyncGearsCheckBox.isSelected());
            }
        });
    }
/*
    private void setupButtons() {
        /*
        resynchronizeAndroidGearsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showResyncLoadingMessage();
                resyncSpecs();
            }
        });

        FindURLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if(OSValidator.isWindows()){
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setCurrentDirectory(new File(SettingsManager.getInstance().getSpecsPath()));
                    int returnVal = chooser.showOpenDialog(MasterPanel.getTopLevelAncestor());

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File specsDirectory = chooser.getSelectedFile();

                        setSpecsRepoDirectory(specsDirectory);
                    }
                }
                else if (OSValidator.isMac()){
                    //Get top level frame
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(MasterPanel);

                    //Create dialog for choosing gearspec file
                    System.setProperty("apple.awt.fileDialogForDirectories", "true");
                    FileDialog fd = new FileDialog(topFrame, "Choose a directory", FileDialog.LOAD);
                    fd.setDirectory(SettingsManager.getInstance().getSpecsPath());
                    fd.setVisible(true);

                    //Get file
                    String filename = fd.getFile();
                    if (filename == null)
                        System.out.println("You cancelled the choice");
                    else {
                        System.out.println("You chose " + filename);

                        //Get spec file
                        File specsDirectory = new File(fd.getDirectory()+Utils.pathSeparator()+filename);

                        //If it exists, set it as the selected file path
                        setSpecsRepoDirectory(specsDirectory);

                    }
                }
            }
        });


    }
*/
    private void setupMiscUI() {
        ResyncProgressPanel.setVisible(false);

        //Set spec repository path
        SpecUrlTextField.setText(SettingsManager.getInstance().getSpecsPath());
    }

    private void setCreateIgnoreSelected(final Boolean selected){
        SetCreateIgnoreEntryWorker worker = new SetCreateIgnoreEntryWorker(selected){
            @Override
            protected void done() {
                super.done();

                //Give some feedback as to the success or failure of the ignore entry
                if (success){
                    if (selected){
                        showAddedIgnoreDialog(MasterPanel);
                    }
                    else {
                        showRemovedIgnoreDialog(MasterPanel);
                    }
                }
                else {
                    if (selected){
                        showFailedToAddIgnoreDialog(MasterPanel);
                    }
                    else {
                        showFailedToRemoveIgnoreDialog(MasterPanel);
                    }
                }
            }
        };
        worker.execute();
    }

    private void setAutoSync(final boolean selected){
        SettingsManager.getInstance().setAutoSync(selected);
    }

    ///////////////////////
    // Dialogs
    ///////////////////////

    private void showAddedIgnoreDialog(JPanel panel) {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(panel),
                "Android Gears successfully added an entry to your ignore file",
                "Ignore File",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void showFailedToAddIgnoreDialog(JPanel panel) {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(panel),
                "Android Gears failed to add an entry to your ignore file, but will still attempt to do so for new projects. For best results, ignore the \"Gears\" folder in your project directory.",
                "Ignore File",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void showRemovedIgnoreDialog(JPanel panel) {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(panel),
                "Android Gears successfully removed its entry in your ignore file",
                "Ignore File",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void showFailedToRemoveIgnoreDialog(JPanel panel) {
        Object[] options = {"OK"};
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(panel),
                "Android Gears failed to remove its entry in your ignore file. Please find and remove the \"Gears\" folder entry if you would like to stop the ignoring of Android Gears.",
                "Ignore File",
                JOptionPane.OK_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void showResyncSuccessMessage(){
        LoadingSpinnerLabel.setVisible(false);
        ResyncStatusLabel.setText("Successfully synchronized Android Gears");
        resynchronizeAndroidGearsButton.setEnabled(true);
    }

    private void showResyncErrorMessage(){
        LoadingSpinnerLabel.setVisible(false);
        ResyncStatusLabel.setText("Failed to synchronize Android Gears. Please check your network connection.");
        resynchronizeAndroidGearsButton.setEnabled(true);

    }

    private void showResyncLoadingMessage(){
        LoadingSpinnerLabel.setVisible(true);
        ResyncStatusLabel.setText("Resynchronizing Android Gears Repository...");
        resynchronizeAndroidGearsButton.setEnabled(false);
        ResyncProgressPanel.setVisible(true);
    }


}
