package agencia.view;

import agencia.controller.DadosController;
import agencia.model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PacotesFrame extends JFrame {
    private final DadosController controller;
    private JTable table;
    private DefaultTableModel model;
    private List<PacoteViagem> pacotes;

    public PacotesFrame(DadosController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Pacotes de Viagem");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new CompoundBorder(
            new EmptyBorder(15, 15, 15, 15),
            new MatteBorder(1, 1, 1, 1, new Color(0x3f51b5))
        ));
        mainPanel.setBackground(new Color(0xf5f5f5));

        // Título da janela
        JLabel titleLabel = new JLabel("Gerenciamento de Pacotes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0x3f51b5));
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Destino", "Duração", "Preço", "Tipo"};
        model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        configurarTabela();
        
        // Painel da tabela com borda e sombra
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(0xe0e0e0)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Painel de botões com estilo
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botoesPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        botoesPanel.setBackground(new Color(0xf5f5f5));
        
        JButton btnAdicionar = criarBotaoEstilizado("Adicionar", new Color(0x4caf50));
        JButton btnEditar = criarBotaoEstilizado("Editar", new Color(0x2196f3));
        JButton btnExcluir = criarBotaoEstilizado("Excluir", new Color(0xf44336));
        JButton btnAtualizar = criarBotaoEstilizado("Atualizar", new Color(0x607d8b));
        
        botoesPanel.add(btnAdicionar);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnAtualizar);
        mainPanel.add(botoesPanel, BorderLayout.SOUTH);

        btnAdicionar.addActionListener(e -> abrirDialogo(null));
        btnEditar.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                abrirDialogo(pacotes.get(table.convertRowIndexToModel(selectedRow)));
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um pacote para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnExcluir.addActionListener(e -> excluirPacote());
        btnAtualizar.addActionListener(e -> atualizarTabela());

        setContentPane(mainPanel);
        pack();
        atualizarTabela();
    }

    private JButton criarBotaoEstilizado(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 2, 1, cor.darker()),
            new EmptyBorder(5, 15, 5, 15)
        ));
        return botao;
    }

    private void configurarTabela() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0x3f51b5));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(0xbbdefb));
        table.setGridColor(new Color(0xe0e0e0));
        table.setShowGrid(true);
    }
    
    private void atualizarTabela() {
        this.pacotes = controller.carregarPacotes();
        model.setRowCount(0);
        for (PacoteViagem p : this.pacotes) {
            model.addRow(new Object[]{p.getId(), p.getNome(), p.getDestino(), p.getDuracao(), String.format("%.2f", p.getPreco()), p.getTipo()});
        }
    }

    private void excluirPacote() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            PacoteViagem pacote = pacotes.get(table.convertRowIndexToModel(selectedRow));
            int confirm = JOptionPane.showConfirmDialog(this, "Excluir o pacote '" + pacote.getNome() + "'?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.excluirPacote(pacote.getId());
                atualizarTabela();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um pacote para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void abrirDialogo(PacoteViagem pacote) {
        boolean isEdit = pacote != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar Pacote" : "Novo Pacote", true);
        dialog.setSize(500, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
        
        // Adicionando um painel de título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0x3f51b5));
        titlePanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel dialogTitle = new JLabel(isEdit ? "Editar Pacote" : "Novo Pacote");
        dialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dialogTitle.setForeground(Color.WHITE);
        titlePanel.add(dialogTitle);
        dialog.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;


       // Nome - Label
        GridBagConstraints gbcLbl = new GridBagConstraints();
        gbcLbl.gridx = 0;
        gbcLbl.gridy = 0;
        gbcLbl.weightx = 0;
        gbcLbl.anchor = GridBagConstraints.WEST;
        gbcLbl.insets = new Insets(5, 5, 5, 5); // margem opcional
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblNome, gbcLbl);

        // Nome - Campo de texto
        GridBagConstraints gbcTxt = new GridBagConstraints();
        gbcTxt.gridx = 1;
        gbcTxt.gridy = 0;
        gbcTxt.weightx = 1;
        gbcTxt.fill = GridBagConstraints.HORIZONTAL;
        gbcTxt.insets = new Insets(5, 5, 5, 5); // margem opcional
        JTextField txtNome = new JTextField(isEdit ? pacote.getNome() : "", 20);
        txtNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNome.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(0xe0e0e0)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(txtNome, gbcTxt);

        
        // Destino - Label
        GridBagConstraints gbcLblDestino = new GridBagConstraints();
        gbcLblDestino.gridx = 0;
        gbcLblDestino.gridy = 1;
        gbcLblDestino.weightx = 0;
        gbcLblDestino.anchor = GridBagConstraints.WEST;
        gbcLblDestino.insets = new Insets(5, 5, 5, 5); // margem opcional
        JLabel lblDestino = new JLabel("Destino:");
        lblDestino.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblDestino, gbcLblDestino);

        // Destino - Campo de texto
        GridBagConstraints gbcTxtDestino = new GridBagConstraints();
        gbcTxtDestino.gridx = 1;
        gbcTxtDestino.gridy = 1;
        gbcTxtDestino.weightx = 1;
        gbcTxtDestino.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtDestino.insets = new Insets(5, 5, 5, 5); // margem opcional
        JTextField txtDestino = new JTextField(isEdit ? pacote.getDestino() : "", 20);
        txtDestino.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDestino.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(0xe0e0e0)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(txtDestino, gbcTxtDestino);

        // Duração - Label
        GridBagConstraints gbcLblDuracao = new GridBagConstraints();
        gbcLblDuracao.gridx = 0;
        gbcLblDuracao.gridy = 2;
        gbcLblDuracao.weightx = 0;
        gbcLblDuracao.anchor = GridBagConstraints.WEST;
        gbcLblDuracao.insets = new Insets(5, 5, 5, 5);
        JLabel lblDuracao = new JLabel("Duração:");
        lblDuracao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblDuracao, gbcLblDuracao);

        // Duração - Campo de texto
        GridBagConstraints gbcTxtDuracao = new GridBagConstraints();
        gbcTxtDuracao.gridx = 1;
        gbcTxtDuracao.gridy = 2;
        gbcTxtDuracao.weightx = 1;
        gbcTxtDuracao.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtDuracao.insets = new Insets(5, 5, 5, 5);
        JTextField txtDuracao = new JTextField(isEdit ? String.valueOf(pacote.getDuracao()) : "", 20);
        txtDuracao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDuracao.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(0xe0e0e0)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(txtDuracao, gbcTxtDuracao);

     // Preco - Label
        GridBagConstraints gbcLblPreco = new GridBagConstraints();
        gbcLblPreco.gridx = 0;
        gbcLblPreco.gridy = 3;
        gbcLblPreco.weightx = 0;
        gbcLblPreco.anchor = GridBagConstraints.WEST;
        gbcLblPreco.insets = new Insets(5, 5, 5, 5);
        JLabel lblPreco = new JLabel("Preço:");
        lblPreco.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblPreco, gbcLblPreco);

        // Preco - Campo de texto
        GridBagConstraints gbcTxtPreco = new GridBagConstraints();
        gbcTxtPreco.gridx = 1;  
        gbcTxtPreco.gridy = 3;  
        gbcTxtPreco.weightx = 1;  
        gbcTxtPreco.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtPreco.insets = new Insets(5, 5, 5, 5);
        JTextField txtPreco = new JTextField(isEdit ? String.valueOf(pacote.getPreco()) : "", 20);
        txtPreco.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPreco.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(0xe0e0e0)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(txtPreco, gbcTxtPreco);
        
     // Tipo - Label
        GridBagConstraints gbcLblTipo = new GridBagConstraints();
        gbcLblTipo.gridx = 0;
        gbcLblTipo.gridy = 4; 
        gbcLblTipo.weightx = 0;
        gbcLblTipo.anchor = GridBagConstraints.WEST;
        gbcLblTipo.insets = new Insets(5, 5, 5, 5);
        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblTipo, gbcLblTipo);

        // Tipo - ComboBox
        GridBagConstraints gbcCbTipo = new GridBagConstraints();
        gbcCbTipo.gridx = 1;
        gbcCbTipo.gridy = 4; 
        gbcCbTipo.weightx = 1;
        gbcCbTipo.fill = GridBagConstraints.HORIZONTAL;
        gbcCbTipo.insets = new Insets(5, 5, 5, 5);
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Aventura", "Luxo", "Cultural"});
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbTipo.setBackground(Color.WHITE);
        cbTipo.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(0xe0e0e0)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        if (isEdit) {
            cbTipo.setSelectedItem(pacote.getTipo());
            cbTipo.setEnabled(false);
        }
        formPanel.add(cbTipo, gbcCbTipo);

        // Botão para salvar o cadastro
        JButton btnSalvarDialogo = new JButton("Salvar");
        btnSalvarDialogo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSalvarDialogo.setBackground(new Color(0x4caf50));
        btnSalvarDialogo.setForeground(Color.WHITE);
        btnSalvarDialogo.setFocusPainted(false);
        btnSalvarDialogo.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 2, 1, new Color(0x4caf50).darker()),
            new EmptyBorder(5, 15, 5, 15)
        ));
        
        btnSalvarDialogo.addActionListener(e -> {
            // Lógica de validação e salvamento específica para o diálogo
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSalvarDialogo);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
