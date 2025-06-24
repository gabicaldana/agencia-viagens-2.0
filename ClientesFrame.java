package agencia.view;

import agencia.controller.DadosController;
import agencia.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientesFrame extends JFrame {
    private final DadosController controller;
    private JTable table;
    private DefaultTableModel model;
    private List<Cliente> clientes;

    public ClientesFrame(DadosController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Clientes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] colunas = {"ID", "Nome", "Telefone", "Email", "Documento", "Tipo"};
        model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        configurarTabela();
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");
        botoesPanel.add(btnAdicionar);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnAtualizar);
        mainPanel.add(botoesPanel, BorderLayout.SOUTH);

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

        setContentPane(mainPanel);
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
            model.addRow(new Object[]{ c.getId(), c.getNome(), c.getTelefone(), c.getEmail(), c.getDocumento(), c.getTipo() });
        }
    }

    private void excluirCliente() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Cliente cliente = clientes.get(table.convertRowIndexToModel(selectedRow));
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Excluir o cliente '" + cliente.getNome() + "'?", 
                "Confirmação", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.excluirCliente(cliente.getId())) {
                    atualizarTabela();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Selecione um cliente para excluir.", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void abrirDialogo(Cliente cliente) {
        boolean isEdit = cliente != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar Cliente" : "Novo Cliente", true);
        dialog.setSize(500, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Componentes do Formulário
        JTextField txtNome = new JTextField(isEdit ? cliente.getNome() : "", 20);
        JTextField txtTelefone = new JTextField(isEdit ? cliente.getTelefone() : "", 20);
        JTextField txtEmail = new JTextField(isEdit ? cliente.getEmail() : "", 20);
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"NACIONAL", "ESTRANGEIRO"});
        JTextField txtDocumento = new JTextField(isEdit ? cliente.getDocumento() : "", 20);
        
        if (isEdit) {
            cbTipo.setSelectedItem(cliente.getTipo());
            cbTipo.setEnabled(false);
        }

        // Adiciona máscara para telefone
        txtTelefone.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                String text = ((JTextField) input).getText();
                return text.matches("\\d*"); // Aceita apenas números
            }
        });

        // Layout dos campos
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; formPanel.add(new JLabel("Nome*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; formPanel.add(txtNome, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Telefone*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtTelefone, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Email*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Tipo*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(cbTipo, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Documento*:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(txtDocumento, gbc);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
            // Obtém os valores dos campos
            String nome = txtNome.getText().trim();
            String telefone = txtTelefone.getText().trim();
            String email = txtEmail.getText().trim();
            String documento = txtDocumento.getText().trim();
            String tipo = (String) cbTipo.getSelectedItem();

            // Validações
            StringBuilder erros = new StringBuilder();
            
            // Verifica campos obrigatórios
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nome é um campo obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (telefone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Telefone é um campo obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Email é um campo obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (documento.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Documento é um campo obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
           
            // Valida nome (somente letras e espaços)
            if (!nome.isEmpty()) {
                for (char c : nome.toCharArray()) {
                    if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                        erros.append("- Nome deve conter apenas letras e espaços\n");
                        break;
                    }
                }
            }

            // Valida telefone (somente números)
            if (!telefone.isEmpty() && !telefone.matches("\\d+")) {
                erros.append("- Telefone deve conter apenas números\n");
            }

            // Valida email
            if (!email.isEmpty()) {
                String[] partes = email.split("@");
                if (partes.length != 2 || 
                    partes[0].isEmpty() || 
                    !partes[1].contains(".") ||
                    partes[1].indexOf(".") == 0 ||
                    partes[1].
