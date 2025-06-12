package agencia;

import agencia.controller.DadosController;
import agencia.view.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Font;

public class AgenciaGUI extends JFrame {

    public static final Font FONT_PADRAO = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_TABELA_HEADER = new Font("Segoe UI", Font.BOLD, 14);

    public AgenciaGUI(DadosController controller) {
        super("Agência de Viagens (MVC)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_PADRAO);
        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        tabbedPane.addTab("Clientes", new ClientesPanel(this, controller));
        tabbedPane.addTab("Pacotes", new PacotesPanel(this, controller));
        tabbedPane.addTab("Serviços", new ServicosPanel(this, controller));
        tabbedPane.addTab("Pedidos", new PedidosPanel(this, controller));
        tabbedPane.addTab("Relatórios", new RelatoriosPanel(controller));

        add(tabbedPane);
    }
}