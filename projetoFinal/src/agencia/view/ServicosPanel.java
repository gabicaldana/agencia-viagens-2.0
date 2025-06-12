package agencia.view;

import agencia.controller.DadosController;
import agencia.model.ServicoAdicional;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ServicosPanel extends JPanel {
    private final DadosController controller;
    private final JFrame parentFrame;
    private JTable table;
    private DefaultTableModel model;
    private List<ServicoAdicional> servicos;

    public ServicosPanel(JFrame parentFrame, DadosController controller) {
        this.parentFrame = parentFrame;
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tabela
        String[] colunas = {"ID", "Descrição", "Preço"};
        model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        configurarTabela();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Botões
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");
        botoesPanel.add(btnAdicionar);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnAtualizar);
        add(botoesPanel, BorderLayout.SOUTH);

        // Ações
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

        atualizarTabela();
    }

    private void configurarTabela() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        JDialog dialog = new JDialog(parentFrame, isEdit ? "Editar Serviço" : "Novo Serviço", true);
        dialog.setSize(500, 200);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(parentFrame);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtDescricao = new JTextField(isEdit ? servico.getDescricao() : "");
        JTextField txtPreco = new JTextField(isEdit ? String.valueOf(servico.getPreco()) : "");

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; formPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; formPanel.add(txtDescricao, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Preço (R$):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtPreco, gbc);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
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
        buttonPanel.add(btnSalvar);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}