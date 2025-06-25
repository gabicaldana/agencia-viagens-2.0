package agencia.view;

import agencia.controller.DadosController;
import agencia.model.ServicoAdicional;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ServicosFrame extends JFrame {
    private final DadosController controller;
    private JTable table;
    private DefaultTableModel model;
    private List<ServicoAdicional> servicos;

    public ServicosFrame(DadosController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Serviços Adicionais");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new CompoundBorder(
            new EmptyBorder(15, 15, 15, 15),
            new MatteBorder(1, 1, 1, 1, new Color(0x3f51b5))
        ));
        mainPanel.setBackground(new Color(0xf5f5f5));

        // Título da janela
        JLabel titleLabel = new JLabel("Gerenciamento de Serviços");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0x3f51b5));
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] colunas = {"ID", "Descrição", "Preço"};
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
                abrirDialogo(servicos.get(table.convertRowIndexToModel(selectedRow)));
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um serviço para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnExcluir.addActionListener(e -> excluirServico());
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
        this.servicos = controller.carregarServicos();
        model.setRowCount(0);
        for (ServicoAdicional s : this.servicos) {
            model.addRow(new Object[]{s.getId(), s.getDescricao(), String.format("%.2f", s.getPreco())});
        }
    }

    private void excluirServico() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            ServicoAdicional servico = servicos.get(table.convertRowIndexToModel(selectedRow));
            int confirm = JOptionPane.showConfirmDialog(this, "Excluir o serviço '" + servico.getDescricao() + "'?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.excluirServico(servico.getId());
                atualizarTabela();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um serviço para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void abrirDialogo(ServicoAdicional servico) {
        boolean isEdit = servico != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar Serviço" : "Novo Serviço", true);
        dialog.setSize(500, 200);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
        
        // Adicionando um painel de título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0x3f51b5));
        titlePanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel dialogTitle = new JLabel(isEdit ? "Editar Serviço" : "Novo Serviço");
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

        
        // Descrição - Label
        GridBagConstraints gbcLbl = new GridBagConstraints();
        gbcLbl.gridx = 0;
        gbcLbl.gridy = 0;
        gbcLbl.weightx = 0;
        gbcLbl.anchor = GridBagConstraints.WEST;
        gbcLbl.insets = new Insets(5, 5, 5, 5); // margem 
        JLabel lblDescricao = new JLabel("Descrição:");
        lblDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblDescricao, gbcLbl);

        // Descrição - Campo de texto
        GridBagConstraints gbcTxt = new GridBagConstraints();
        gbcTxt.gridx = 1;
        gbcTxt.gridy = 0;
        gbcTxt.weightx = 1;
        gbcTxt.fill = GridBagConstraints.HORIZONTAL;
        gbcTxt.insets = new Insets(5, 5, 5, 5); // margem 
        JTextField txtDescricao = new JTextField(isEdit ? servico.getDescricao() : "", 20);
        txtDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescricao.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(0xe0e0e0)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(txtDescricao, gbcTxt);
        
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
        JTextField txtPreco = new JTextField(isEdit ? String.valueOf(servico.getPreco()) : "", 20);
        txtPreco.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPreco.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(0xe0e0e0)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(txtPreco, gbcTxtPreco);
        
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
        	try {
                String descricao = txtDescricao.getText().trim();
                double preco = Double.parseDouble(txtPreco.getText().trim());

                if (descricao.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "A descrição é obrigatória.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (isEdit) {
                    servico.setDescricao(descricao);
                    servico.setPreco(preco);
                    controller.atualizarServico(servico);
                } else {
                    controller.salvarServico(new ServicoAdicional(0, descricao, preco));
                }
                atualizarTabela();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Preço deve ser um número válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSalvarDialogo);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
