package Renderers;

import Models.GearSpec.DependencySpec;
import Models.GearSpec.DependencySpecAuthor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by matthewyork on 4/1/14.
 */

public class DependencySpecCellRenderer extends JPanel implements ListCellRenderer {
    private static final Color HIGHLIGHT_COLOR = Color.decode("0x2B2B2B");
    private Color cellBackgroundColor = null;

    JPanel specInfoPanel;
    JLabel nameLabel;
    JLabel authorLabel;
    JLabel imageLabel;
    //JLabel jarLabel;
    //ImageIcon declaredIcon = new ImageIcon(getClass().getResource("DependencyStateDeclared.png"));
    //ImageIcon installedIcon = new ImageIcon(getClass().getResource("DependencyStateInstalled.png"));
    //ImageIcon jarfile = new ImageIcon(getClass().getResource("jarfile.png"));

    public DependencySpecCellRenderer() {
        setOpaque(true);
        cellBackgroundColor = getBackground();
    }

    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
    {
        DependencySpec spec = (DependencySpec)value;

        //Check for first runthrough
        if(nameLabel == null){
            //Initialize name panel
            this.setLayout(new BorderLayout());
            this.setBorder(new EmptyBorder(10,10,10,10));
            specInfoPanel = new JPanel();
            specInfoPanel.setLayout(new BoxLayout(specInfoPanel, BoxLayout.Y_AXIS));
            specInfoPanel.setOpaque(false);

            //Set name label
            nameLabel = new JLabel(spec.getName(), JLabel.LEFT);
            nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.PLAIN, 18));

            //Set author label
            authorLabel = new JLabel("", JLabel.LEFT);
            if (spec.getAuthor() != null) {
                if (spec.getAuthor().getName() != null) {
                    authorLabel.setText(spec.getAuthor().getName());
                }
            }

            //Set image
            imageLabel = new JLabel();
            //Set image
            switch (spec.getDependencyState().ordinal()){
                case 0: imageLabel.setIcon(new ImageIcon());
                    break;
                case 1: imageLabel.setIcon(new ImageIcon()); //imageLabel.setIcon(installedIcon);
                    break;
            }


            //set jar image
            /*
            jarLabel = new JLabel();
            if(spec.getType().equals("jar")){
                jarLabel.setIcon(jarfile);
            }else{
                jarLabel.setIcon(new ImageIcon());
            }*/


            //Add components
            this.add(specInfoPanel, BorderLayout.WEST);
            specInfoPanel.add(nameLabel);
            specInfoPanel.add(authorLabel);
            this.add(imageLabel, BorderLayout.EAST);
            //this.add(jarLabel,BorderLayout.CENTER);
        }
        else {
            //Set name label
            nameLabel.setText(spec.getName());

            //Set Author Label
            if (spec.getAuthor() != null) {
                if (spec.getAuthor().getName() != null) {
                    authorLabel.setText(spec.getAuthor().getName());
                }
            }

            //Set image
            /*
            switch (spec.getDependencyState().ordinal()){
                case 0: imageLabel.setIcon(new ImageIcon());
                    break;
                case 1: imageLabel.setIcon(new ImageIcon()); //imageLabel.setIcon(installedIcon);
                    break;
            }*/
            //set if jar
            /*
            if(spec.getType().equals("jar")){
                jarLabel.setIcon(jarfile);
            }else{
                jarLabel.setIcon(new ImageIcon());
            }*/
        }

        if(isSelected) {
            setBackground(cellBackgroundColor.darker());
            setOpaque(true);
            //setForeground(Color.white);
        } else {
            setOpaque(false);
            //setForeground(Color.black);
        }
        return this;
    }
}
