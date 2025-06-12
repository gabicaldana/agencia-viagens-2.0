package agencia.view;

import agencia.controller.DadosController;
import agencia.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientesPanel extends JPanel {
    private final DadosController controller;
    private final JFrame parentFrame;
    private JTable table;
    private DefaultTableModel model;
    private List<Cliente> clientes;

    public ClientesPanel(JFrame parentFrame, DadosController controller) {
        this.parentFrame = parentFrame;
        this.controller = controller;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] colunas = {"ID", "Nome", "Telefone", "Email", "Documento", "Tipo"};
        model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        configurarTabela();
        add(new JScrollPane(table), BorderLayout.CENTER);

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

        btnAdicionar.addActionListener(e -> abrirDialogo(null));
        btnEditar.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                abrirDialogo(clientes.get(table.convertRowIndexToModel(selectedRow)));
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um cliente para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnExcluir.addActionListener(e -> excluirCliente());
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
        this.clientes = controller.carregarClientes();
        model.setRowCount(0);
        for (Cliente c : this.clientes) {
        	model.addRow(new Object[]{ c.getId(), c.getNome(), c.getTelefone(), c.getEmail(), c.getDocumento(), c.getTipo() });        }
    }

    private void excluirCliente() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Cliente cliente = clientes.get(table.convertRowIndexToModel(selectedRow));
            int confirm = JOptionPane.showConfirmDialog(this, "Excluir '" + cliente.getNome() + "'?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.excluirCliente(cliente.getId());
                atualizarTabela();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void abrirDialogo(Cliente cliente) {
        boolean isEdit = cliente != null;
        JDialog dialog = new JDialog(parentFrame, isEdit ? "Editar Cliente" : "Novo Cliente", true);
        dialog.setSize(500, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(parentFrame);

        // 1. CRIA O PAINEL DO FORMULÁRIO
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Componentes do Formulário
        JTextField txtNome = new JTextField(isEdit ? cliente.getNome() : "");
        JTextField txtTelefone = new JTextField(isEdit ? cliente.getTelefone() : "");
        JTextField txtEmail = new JTextField(isEdit ? cliente.getEmail() : "");
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"NACIONAL", "ESTRANGEIRO"});
        JTextField txtDocumento = new JTextField(isEdit ? cliente.getDocumento() : "");
        if (isEdit) {
            cbTipo.setSelectedItem(cliente.getTipo());
            cbTipo.setEnabled(false);
        }
        
        // Adiciona componentes ao formulário
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; formPanel.add(txtNome, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtTelefone, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(cbTipo, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Documento:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(txtDocumento, gbc);

        // 2. CRIA O BOTÃO E SUA AÇÃO
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String telefone = txtTelefone.getText().trim();
            String email = txtEmail.getText().trim();
            String documento = txtDocumento.getText().trim();
            if (nome.isEmpty() || documento.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nome e Documento são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isEdit) {
                cliente.setNome(nome);
                cliente.setTelefone(telefone);
                cliente.setEmail(email);
                cliente.setDocumento(documento);
                controller.atualizarCliente(cliente);
            } else {
                String tipo = (String) cbTipo.getSelectedItem();
                Cliente novoCliente = new Cliente(0, nome, telefone, email, tipo, documento);
                controller.salvarCliente(novoCliente);
            }
            
            atualizarTabela();
            dialog.dispose();
        });

        // 3. CRIA O PAINEL DE BOTÕES E ADICIONA O BOTÃO A ELE
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(btnSalvar); 
        
        // 4. ADICIONA OS PAINÉIS AO DIÁLOGO
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}