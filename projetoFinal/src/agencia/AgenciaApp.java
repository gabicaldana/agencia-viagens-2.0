package agencia;

import agencia.controller.DadosController;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

public class AgenciaApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
            }
            
            DadosController controller = new DadosController();
            
            AgenciaGUI mainView = new AgenciaGUI(controller);
            
            mainView.setVisible(true);
        });
    }
}