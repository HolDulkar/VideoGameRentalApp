package app;

import javax.swing.SwingUtilities;
import view.MainFrame;

public class Main {

    public static void main(String[] args) {

        // Inicia la interfície gràfica de manera segura
        SwingUtilities.invokeLater(() -> {
            MainFrame window = new MainFrame();
            window.setVisible(true);
        });
    }
}