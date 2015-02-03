package Actions;

import Forms.AboutGradleBuddyForm;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;

/**
 * Created by matthewyork on 12/3/14.
 */
public class AboutGradleBuddyAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        JFrame frame = new JFrame("About Gradle Buddy");
        frame.setContentPane(new AboutGradleBuddyForm().MasterPanel);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
