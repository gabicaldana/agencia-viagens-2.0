package agencia.view;

import agencia.controller.DadosController;
import agencia.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PacotesPanel extends JPanel {
    private final DadosController controller;
    private final JFrame parentFrame;
    private JTable table;
    private DefaultTableModel model;
    private List<PacoteViagem> pacotes;

    public PacotesPanel(JFrame parentFrame, DadosController controller) {
        this.parentFrame = parentFrame;
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tabela
        String[] colunas = {"ID", "Nome", "Destino", "Duração", "Preço", "Tipo"};
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
                abrirDialogo(pacotes.get(table.convertRowIndexToModel(selectedRow)));
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um pacote para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnExcluir.addActionListener(e -> excluirPacote());
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
        JDialog dialog = new JDialog(parentFrame, isEdit ? "Editar Pacote" : "Novo Pacote", true);
        dialog.setSize(500, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(parentFrame);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNome = new JTextField(isEdit ? pacote.getNome() : "");
        JTextField txtDestino = new JTextField(isEdit ? pacote.getDestino() : "");
        JTextField txtDuracao = new JTextField(isEdit ? String.valueOf(pacote.getDuracao()) : "");
        JTextField txtPreco = new JTextField(isEdit ? String.valueOf(pacote.getPreco()) : "");
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Aventura", "Luxo", "Cultural"});
        if(isEdit) cbTipo.setSelectedItem(pacote.getTipo());

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; formPanel.add(txtNome, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Destino:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtDestino, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Duração (dias):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtDuracao, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Preço (R$):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(txtPreco, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(cbTipo, gbc);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
            try {
                String nome = txtNome.getText().trim();
                String destino = txtDestino.getText().trim();
                int duracao = Integer.parseInt(txtDuracao.getText().trim());
                double preco = Double.parseDouble(txtPreco.getText().trim());
                String tipo = (String) cbTipo.getSelectedItem();

                if (nome.isEmpty() || destino.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Nome e Destino são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (isEdit) {
                    pacote.setNome(nome);
                    pacote.setDestino(destino);
                    pacote.setDuracao(duracao);
                    pacote.setPreco(preco);
                    pacote.setTipo(tipo);
                    controller.atualizarPacote(pacote);
                } else {
                    // *** AQUI ESTÁ A CORREÇÃO PRINCIPAL ***
                    // Criamos uma única instância de PacoteViagem, passando o tipo
                    PacoteViagem novoPacote = new PacoteViagem(0, nome, destino, duracao, preco, tipo);
                    controller.salvarPacote(novoPacote);
                }
                atualizarTabela();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Duração e Preço devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(btnSalvar);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}