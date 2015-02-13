package Forms;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import Models.GearSpec.DependencySpec;
import Models.GearSpec.DependencySpecUpdate;
import Panels.SpecDetailsPanel;
import Renderers.DependencySpecCellRenderer;
import Renderers.ModuleCellRenderer;
import Renderers.ProjectCellRenderer;
import Singletons.SettingsManager;
import Utilities.GradleBuddy;
import Utilities.Utils;

import java.net.URI;
import java.util.*;

import Workers.*;
import Workers.InstallUninstall.*;
import Workers.Reloads.ReloadRecommendedDependenciesWorker;
import Workers.Search.SearchInstalledProjectsWorker;
import Workers.Search.SearchLocalProjectsListWorker;
import Workers.Search.SearchOnlineListWorker;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * Created by matthewyork on 4/1/14.
 */
public class ManageDependenciesForm {
    public static final int DETAILS_INNER_WIDTH = 230;
    private static final int AGREE_TO_UNINSTALL_GEAR = 1;
    private static final int AGREE_TO_UNINSTALL_GEAR_AND_DEPENDENTS = 2;
    private Boolean dirty = false; //If set true, then we need to rebuild project

    private DependencySpec selectedSpec;
    private DependencySpecUpdate selectedUpdateSpec;
    private ArrayList<DependencySpec> recommendedDependencies;
    private ArrayList<DependencySpec> availableDependencies;
    private ArrayList<DependencySpec> installedProjects;
    private ArrayList<String> projectVersions;
    Project[] targetProjects;
    Module[] targetModules;

    private JTextField SearchTextField;
    private JTabbedPane SearchTabbedPane;
    private JButton doneButton;
    public JPanel MasterPanel;
    private JPanel SearchPanel;
    private JPanel DetailsPanel;
    private JList RecommendedDependenciesList;
    private JList OnlineDependenciesList;
    private JList InstalledList;
    private JScrollPane DetailsScrollPane;
    private JLabel StatusLabel;
    private JButton InstallUninstallButton;
    private JButton OpenInBrowserButton;
    private JLabel LoadingSpinnerLabel;
    private JComboBox TargetProjectComboBox;
    private JLabel HeaderLogo;
    private JComboBox TargetModuleComboBox;
    private JButton UpdateDependencyButton;

    private void createUIComponents() {

    }

    public ManageDependenciesForm() {

        //Setup UI
        setupComboBoxes();
        setupMiscUI();
        setupTables();
        setupSearchTextField();
        setupButtons();
    }

    private void setupTables() {

        //Reload recommended list by fetching new copy of recommended dependencies
        refreshRecommendedDependenciesList("");
        ReloadRecommendedDependenciesWorker worker = new ReloadRecommendedDependenciesWorker() {
            @Override
            protected void done() {
                super.done();
                refreshRecommendedDependenciesList("");
            }
        };
        worker.execute();

        //Get installed dependencies
        refreshInstalledList("");

        //Setup click listener
        RecommendedDependenciesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                didSelectRecommendedSpecAtIndex(RecommendedDependenciesList.getSelectedIndex());
            }
        });
        //AllDependenciesList.setFocusable(false);

        //Setup click listener
        OnlineDependenciesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                didSelectSearchSpecAtIndex(OnlineDependenciesList.getSelectedIndex());
            }
        });
        //AllDependenciesList.setFocusable(false);

        InstalledList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                didSelectInstalledSpecAtIndex(InstalledList.getSelectedIndex());
            }
        });
        //InstalledList.setFocusable(false);
    }

    private void setupSearchTextField() {
        SearchTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                //Get pressed character
                char c = keyEvent.getKeyChar();

                //Build searchString
                String searchString = SearchTextField.getText();
                if (c == 8 && searchString.length() > 0) {
                    searchString = SearchTextField.getText().substring(0, searchString.length() - 1);
                } else if (isValidCharacter(c)) {
                    searchString = SearchTextField.getText() + keyEvent.getKeyChar();
                }


                //Switch to desired tab
                refreshSelectedTabList(searchString);
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });
    }

    private void setupButtons() {
        //Register done button events
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(MasterPanel);
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });
        doneButton.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //If return key is pressed
                if (e.getKeyChar() == 10) {
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(MasterPanel);
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                }
            }
        });

        //Hide Update button
        UpdateDependencyButton.setVisible(false);

        //Register update events
        UpdateDependencyButton.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //If return key is pressed
                if (e.getKeyChar() == 10) {
                    updateGear(selectedUpdateSpec);
                }
            }
        });
        UpdateDependencyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateGear(selectedUpdateSpec);
            }
        });


        //Hide Install/Uninstall button
        InstallUninstallButton.setVisible(false);

        //Register install/uninstall events
        InstallUninstallButton.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //If return key is pressed
                if (e.getKeyChar() == 10) {
                    if (SearchTabbedPane.getSelectedIndex() == 3) {
                        toggleDependency(selectedUpdateSpec);
                    } else {
                        toggleDependency(selectedSpec);
                    }
                }
            }
        });
        InstallUninstallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (SearchTabbedPane.getSelectedIndex() == 3) {
                    toggleDependency(selectedUpdateSpec);
                } else {
                    toggleDependency(selectedSpec);
                }
            }
        });


        //Show homepage button
        OpenInBrowserButton.setVisible(false);

        //Register show in browser events
        OpenInBrowserButton.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //If return key is pressed
                if (e.getKeyChar() == 10) {
                    openSpecHomePageInBrowser();
                }
            }
        });
        OpenInBrowserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                openSpecHomePageInBrowser();
            }
        });
    }

    private void setupComboBoxes() {
        //Get all projects
        ProjectManager pm = ProjectManager.getInstance();
        targetProjects = pm.getOpenProjects();
        Project p = targetProjects[0];
        SettingsManager.getInstance().loadProjectSettings(p);

        //Get all modules
        ModuleManager mm = ModuleManager.getInstance(p);
        targetModules = mm.getModules();

        //Setup Project Combo Box
        TargetProjectComboBox.setModel(new ListComboBoxModel<Project>(Arrays.asList(targetProjects)));
        TargetProjectComboBox.setSelectedIndex(0);
        TargetProjectComboBox.setRenderer(new ProjectCellRenderer());
        TargetProjectComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
        TargetProjectComboBox.setFocusable(false);

        //Setup Module Combo Box
        TargetModuleComboBox.setModel(new ListComboBoxModel<Module>(Arrays.asList(targetModules)));
        if (targetModules.length > 0) {
            //Look for the default name "app". This should cover most cases of selecting the right module by default
            Boolean defaultNameFound = false;
            for (int ii = 0; ii < targetModules.length; ii++){
                Module module = targetModules[ii];
                if (module.getName() == "app"){
                    TargetModuleComboBox.setSelectedIndex(ii);
                    defaultNameFound = true;
                }
            }

            //If "app" is not found, select the first module in the list
            if (!defaultNameFound){
                TargetModuleComboBox.setSelectedIndex(0);
            }
        }
        TargetModuleComboBox.setRenderer(new ModuleCellRenderer());
        TargetModuleComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Module module = (Module) TargetModuleComboBox.getSelectedItem();
                SettingsManager.getInstance().setMainModule(module.getName(), targetProjects[TargetProjectComboBox.getSelectedIndex()]);
            }
        });
        TargetModuleComboBox.setFocusable(false);

        //Get/Set selected module
        String mainModule = SettingsManager.getInstance().getMainModule();
        if (mainModule.equals("")) { //Save default
            SettingsManager.getInstance().setMainModule(targetModules[TargetModuleComboBox.getSelectedIndex()].getName(), p);
        } else { //Pull back selected module
            for (int ii = 0; ii < targetModules.length; ii++) {
                if (targetModules[ii].getName().equals(mainModule)) {
                    TargetModuleComboBox.setSelectedIndex(ii);
                }
            }
        }


    }

    private void setupMiscUI() {
        StatusLabel.setText("");
        LoadingSpinnerLabel.setVisible(false);

        //Focus search bar
        SearchTextField.setVisible(true);
        SearchTextField.requestFocusInWindow();

        //Set header logo background clear
        HeaderLogo.setOpaque(false);

        //Set up listener for change in tab state
        SearchTabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                //Switch to desired tab
                SearchTextField.setText("");
                refreshSelectedTabList(SearchTextField.getText());
                clearDetailsUI();
            }
        });
        SearchTabbedPane.setFocusable(true);

        //Set up the "on-close then rebuild event"
        MasterPanel.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent ancestorEvent) {
                //Add window close listener to resync project after gear work is done
                JFrame masterFrame = (JFrame) SwingUtilities.getRoot(MasterPanel);
                masterFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we) {
                        //If any changes were made, reload and sync project
                        if (dirty) {
                            //Reload project to trigger autosync
                            ProjectManager pm = ProjectManager.getInstance();
                            pm.reloadProject(targetProjects[TargetProjectComboBox.getSelectedIndex()]);
                        }

                    }
                });
            }

            @Override
            public void ancestorRemoved(AncestorEvent ancestorEvent) {

            }

            @Override
            public void ancestorMoved(AncestorEvent ancestorEvent) {

            }
        });

    }


    ///////////////////////
    // Table refresh/reload
    ////////////////////////

    private void refreshSelectedTabList(String searchString) {
        switch (SearchTabbedPane.getSelectedIndex()) {
            case 0:
                refreshRecommendedDependenciesList(searchString);
                break;
            case 1:
                refreshOnlineDependenciesList(searchString);
                break;
            case 2:
                refreshInstalledList(searchString);
                break;
        }
    }

    private void reloadRecommendedList() {
        RecommendedDependenciesList.setListData(recommendedDependencies.toArray());
        RecommendedDependenciesList.setCellRenderer(new DependencySpecCellRenderer());
        RecommendedDependenciesList.setVisibleRowCount(recommendedDependencies.size());
    }

    private void reloadSearchList() {
        OnlineDependenciesList.setListData(availableDependencies.toArray());
        OnlineDependenciesList.setCellRenderer(new DependencySpecCellRenderer());
        OnlineDependenciesList.setVisibleRowCount(availableDependencies.size());
    }

    private void reloadInstalledList() {
        InstalledList.setListData(installedProjects.toArray());
        InstalledList.setCellRenderer(new DependencySpecCellRenderer());
        InstalledList.setVisibleRowCount(installedProjects.size());
    }

    private void refreshRecommendedDependenciesList(String searchString) {
        //Get availableDependencies and reload
        SearchLocalProjectsListWorker worker = new SearchLocalProjectsListWorker(searchString, targetProjects[TargetProjectComboBox.getSelectedIndex()]) {
            @Override
            protected void done() {
                super.done();
                recommendedDependencies = this.specs;
                reloadRecommendedList();
            }
        };
        worker.execute();
    }

    private void refreshOnlineDependenciesList(String searchString) {
        //Get availableDependencies and reload
        SearchOnlineListWorker worker = new SearchOnlineListWorker(searchString, targetProjects[TargetProjectComboBox.getSelectedIndex()]) {
            @Override
            protected void done() {
                super.done();
                availableDependencies = this.specs;
                reloadSearchList();
            }
        };
        worker.execute();
    }

    private void refreshInstalledList(final String searchString) {
        SearchInstalledProjectsWorker installedProjectsWorker = new SearchInstalledProjectsWorker(targetProjects[TargetProjectComboBox.getSelectedIndex()], searchString) {

            @Override
            protected void done() {
                super.done();

                installedProjects = this.specs;
                reloadInstalledList();
            }
        };
        installedProjectsWorker.execute();
    }

    private Boolean isValidCharacter(char c) {
        //Number
        if (c >= 32 && c <= 126) {
            return true;
        }

        return false;
    }


    ///////////////////////
    // JList Selection
    ////////////////////////
    private void didSelectRecommendedSpecAtIndex(int index) {
        if (index >= 0 && index < recommendedDependencies.size()) {
            selectedSpec = recommendedDependencies.get(index);
            setDetailsForSpec(selectedSpec);
            getVersionDetailsForSpec();
        }
    }

    private void didSelectSearchSpecAtIndex(int index) {
        if (index >= 0 && index < availableDependencies.size()) {
            selectedSpec = availableDependencies.get(index);
            setDetailsForSpec(selectedSpec);
            getVersionDetailsForSpec();
        }
    }

    private void didSelectInstalledSpecAtIndex(int index) {
        if (index >= 0 && index < installedProjects.size()) {
            selectedSpec = installedProjects.get(index);
            setDetailsForSpec(selectedSpec); //MAY NEED TO CHANGE
            getVersionDetailsForSpec();
        }

    }

    private void didSelectSpecVersion(int index) {
        if (index >= 0 && index < projectVersions.size()) {
            selectedSpec = Utils.specForInfo(selectedSpec.getName(), projectVersions.get(index));
            setDetailsForSpec(selectedSpec);
        }
    }

    ////////////////////////
    // Details Management
    ///////////////////////

    private void setDetailsForSpec(DependencySpec spec) {
        //If it is the same as you have selected, don't do anything, else, get the specified version

        SpecDetailsPanel specDetailsPanel = new SpecDetailsPanel(spec);

        //Set panel in scrollpane
        DetailsScrollPane.setViewportView(specDetailsPanel);
        DetailsScrollPane.revalidate();
        DetailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        DetailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //Set install/uninstall button
        InstallUninstallButton.setVisible(true);

        //Enable show homepage button again
        OpenInBrowserButton.setVisible(true);

        //Set declaration button based on install state
        setInstallationStatusForSpec(spec);

        //Set update button
        if (spec instanceof DependencySpecUpdate) {
            UpdateDependencyButton.setText("Update Gear to " + ((DependencySpecUpdate) spec).getUpdateVersionNumber());
            UpdateDependencyButton.setVisible(true);
        } else {
            UpdateDependencyButton.setVisible(false);
        }
    }

    private void clearDetailsUI() {
        //Clear information
        SpecDetailsPanel specDetailsPanel = new SpecDetailsPanel(new DependencySpec());

        //Set panel in scrollpane
        DetailsScrollPane.setViewportView(specDetailsPanel);
        DetailsScrollPane.revalidate();
        DetailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        DetailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //Clear version list entry
        projectVersions = new ArrayList<String>();

        //Hide buttons
        InstallUninstallButton.setVisible(false);
        UpdateDependencyButton.setVisible(false);
        OpenInBrowserButton.setVisible(false);
    }

    private void setInstallationStatusForSpec(final DependencySpec spec) {
        GetGearStateWorker worker = new GetGearStateWorker(targetProjects[TargetProjectComboBox.getSelectedIndex()], spec) {
            @Override
            protected void done() {
                super.done();

                if (this.dependencyState == DependencySpec.DependencyState.DependencyStateUninstalled) {
                    InstallUninstallButton.setText("Install Dependency");
                } else if (this.dependencyState == DependencySpec.DependencyState.DependencyStateInstalled) {
                    InstallUninstallButton.setText("Uninstall Dependency");
                }
            }
        };
        worker.execute();
    }

    private void getVersionDetailsForSpec() {
        //Set versions
        GetProjectVersionsWorker worker = new GetProjectVersionsWorker(selectedSpec) {
            @Override
            protected void done() {
                super.done();

                projectVersions = this.versions;
            }
        };
        worker.execute();
    }

    ///////////////////////
    // Website loading
    ///////////////////////
    private void openSpecHomePageInBrowser() {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(URI.create(selectedSpec.getSource().webUrl));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ///////////////////////
    // Install / Uninstall
    ///////////////////////

    private void toggleDependency(final DependencySpec spec) {
        final Project targetProject = targetProjects[TargetProjectComboBox.getSelectedIndex()];

        if (spec.getDependencyState() == DependencySpec.DependencyState.DependencyStateInstalled) {
            ArrayList<DependencySpec> dependenciesToUninstall = new ArrayList<DependencySpec>();

            //Uninstall dependencies, if necessary
            if (dependenciesToUninstall.size() > 0) {
                uninstallDependencies(dependenciesToUninstall, targetProjects[TargetProjectComboBox.getSelectedIndex()], targetModules[TargetModuleComboBox.getSelectedIndex()]);
            }
        } else {
            //Set UI in download state
            StatusLabel.setText("Installing dependency: " + spec.getName());
            LoadingSpinnerLabel.setVisible(true);
            InstallUninstallButton.setEnabled(false);

            InstallDependencyForSpecWorker worker = new InstallDependencyForSpecWorker(spec, targetProjects[TargetProjectComboBox.getSelectedIndex()], targetModules[TargetModuleComboBox.getSelectedIndex()]) {

                @Override
                protected void done() {
                    super.done();

                    //Mark project reload and sync as necessary
                    dirty = true;

                    //Hide loading spinner and renable buttons
                    LoadingSpinnerLabel.setVisible(false);
                    InstallUninstallButton.setEnabled(true);
                    setInstallationStatusForSpec(spec);

                    //Flip button text
                    if (this.successful) {
                        InstallUninstallButton.setText("Uninstall Dependency");
                        StatusLabel.setText("Successfully installed: " + spec.getName());

                        //Reload tables
                        refreshSelectedTabList(SearchTextField.getText());
                    } else {
                        StatusLabel.setText("Installation failed for: " + spec.getName());
                    }
                }
            };
            worker.execute();
        }
    }




    private void uninstallDependencies(ArrayList<DependencySpec> specs, final Project project, Module module) {
        //Set UI in uninstall state
        StatusLabel.setText("Uninstalling dependencies");
        LoadingSpinnerLabel.setVisible(true);
        InstallUninstallButton.setEnabled(false);
        UpdateDependencyButton.setEnabled(false);

        UninstallDependencyForSpecWorker worker = new UninstallDependencyForSpecWorker(specs, project, module) {

            @Override
            protected void done() {
                super.done();
                //Mark a refresh to be triggered
                dirty = true;

                //Hide loading spinner and re-enable buttons
                LoadingSpinnerLabel.setVisible(false);
                InstallUninstallButton.setEnabled(true);
                UpdateDependencyButton.setEnabled(true);

                //Flip button text
                if (this.successful) {
                    InstallUninstallButton.setText("Install Gear");
                    StatusLabel.setText("Successfully uninstalled gear.");
                    UpdateDependencyButton.setVisible(false);
                    refreshSelectedTabList(SearchTextField.getText());

                    //If on the updates page, hide everything
                    if (SearchTabbedPane.getSelectedIndex() >= 1) {
                        clearDetailsUI();
                    } else {
                        setInstallationStatusForSpec(ManageDependenciesForm.this.selectedSpec);
                    }
                } else {
                    StatusLabel.setText("There was a problem uninstalling the gear. Please try again.");
                }
            }
        };
        worker.execute();
    }

    private void updateGear(DependencySpecUpdate spec) {
        //Set UI in uninstall state
        StatusLabel.setText("Updating " + spec.getName() + " to version " + spec.getUpdateVersionNumber());
        LoadingSpinnerLabel.setVisible(true);
        InstallUninstallButton.setEnabled(false);
        UpdateDependencyButton.setEnabled(false);
    }
}


