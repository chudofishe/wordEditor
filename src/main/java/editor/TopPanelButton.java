package editor;

import javax.swing.*;
import java.awt.*;

public class TopPanelButton extends JButton {

    public TopPanelButton(String imagePath, String name, Dimension size) {
        super(new ImageIcon(imagePath));
        super.setBorderPainted(false);
        super.setContentAreaFilled(false);
        super.setFocusPainted(false);
        super.setOpaque(false);
        super.setMargin(new Insets(0, 0, 0, 0));
        super.setName(name);
        super.setPreferredSize(size);
    }
}
