package agencia.view;

import agencia.controller.DadosController;
import agencia.model.*;
import javax.swing.*;
import javax.swing.border.*;
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
        
        // Painel principal com gradiente e borda
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new CompoundBorder(
            new EmptyBorder(15, 15, 15, 15),
            new MatteBorder(1, 1, 1, 1, new Color(0x3f51b5)) // Linha de fora
        ));
        mainPanel.setBackground(new Color(0xf5f5f5));

        // Título da janela
        JLabel titleLabel = new JLabel("Gerenciamento de Clientes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0x3f51b5));
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Telefone", "Email", "Documento", "Tipo"};
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
                abrirDialogo(clientes.get(table.convertRowIndexToModel(selectedRow)));
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um cliente para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnExcluir.addActionListener(e -> excluirCliente());
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
                controller.excluirCliente(cliente.getId());
                atualizarTabela();
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
        
        // Painel de título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0x3f51b5));
        titlePanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel dialogTitle = new JLabel(isEdit ? "Editar Cliente" : "Novo Cliente");
        dialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dialogTitle.setForeground(Color.WHITE);
        titlePanel.add(dialogTitle);
        dialog.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

     // Nome - Label
        GridBagConstraints gbcLbl = new GridBagConstraints();
        gbcLbl.gridx = 0;
        gbcLbl.gridy = 0;
        gbcLbl.weightx = 0;
        gbcLbl.anchor = GridBagConstraints.WEST;
        gbcLbl.insets = new Insets(5, 5, 5, 5); // margem 
        JLabel lblNome = new JLabel("Nome*:");
        lblNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblNome, gbcLbl);

        // Nome - Campo de texto
        GridBagConstraints gbcTxt = new GridBagConstraints();
        gbcTxt.gridx = 1;
        gbcTxt.gridy = 0;
        gbcTxt.weightx = 1;
        gbcTxt.fill = GridBagConstraints.HORIZONTAL;
        gbcTxt.insets = new Insets(5, 5, 5, 5); // margem 
        JTextField txtNome = new JTextField(isEdit ? cliente.getNome() : "", 20);
        txtNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNome.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(0xe0e0e0)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(txtNome, gbcTxt);

     // Telefone - Label
        GridBagConstraints gbcLblTelefone = new GridBagConstraints();
        gbcLblTelefone.gridx = 0;
        gbcLblTelefone.gridy = 1;
        gbcLblTelefone.weightx = 0;
        gbcLblTelefone.anchor = GridBagConstraints.WEST;
        gbcLblTelefone.insets = new Insets(5, 5, 5, 5); // margem 
        JLabel lblTelefone = new JLabel("Telefone*:");
        lblTelefone.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblTelefone, gbcLblTelefone);

        // Telefone - Campo de texto
        GridBagConstraints gbcTxtTelefone = new GridBagConstraints();
        gbcTxtTelefone.gridx = 1;
        gbcTxtTelefone.gridy = 1;
        gbcTxtTelefone.weightx = 1;
        gbcTxtTelefone.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtTelefone.insets = new Insets(5, 5, 5, 5); // margem 
        JTextField txtTelefone = new JTextField(isEdit ? cliente.getTelefone() : "", 20);
        txtTelefone.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTelefone.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(0xe0e0e0)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(txtTelefone, gbcTxtTelefone);

     // Email - Label
        GridBagConstraints gbcLblEmail = new GridBagConstraints();
        gbcLblEmail.gridx = 0;
        gbcLblEmail.gridy = 2;
        gbcLblEmail.weightx = 0;
        gbcLblEmail.anchor = GridBagConstraints.WEST;
        gbcLblEmail.insets = new Insets(5, 5, 5, 5);
        JLabel lblEmail = new JLabel("Email*:");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblEmail, gbcLblEmail);

        // Email - Campo de texto
        GridBagConstraints gbcTxtEmail = new GridBagConstraints();
        gbcTxtEmail.gridx = 1;
        gbcTxtEmail.gridy = 2;
        gbcTxtEmail.weightx = 1;
        gbcTxtEmail.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtEmail.insets = new Insets(5, 5, 5, 5);
        JTextField txtEmail = new JTextField(isEdit ? cliente.getEmail() : "", 20);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEmail.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(0xe0e0e0)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(txtEmail, gbcTxtEmail);

     // Tipo - Label
        GridBagConstraints gbcLblTipo = new GridBagConstraints();
        gbcLblTipo.gridx = 0;
        gbcLblTipo.gridy = 3;
        gbcLblTipo.weightx = 0;
        gbcLblTipo.anchor = GridBagConstraints.WEST;
        gbcLblTipo.insets = new Insets(5, 5, 5, 5);
        JLabel lblTipo = new JLabel("Tipo*:");
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblTipo, gbcLblTipo);

        // Tipo - ComboBox
        GridBagConstraints gbcCbTipo = new GridBagConstraints();
        gbcCbTipo.gridx = 1;
        gbcCbTipo.gridy = 3;
        gbcCbTipo.weightx = 1;
        gbcCbTipo.fill = GridBagConstraints.HORIZONTAL;
        gbcCbTipo.insets = new Insets(5, 5, 5, 5);
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"NACIONAL", "ESTRANGEIRO"});
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbTipo.setBackground(Color.WHITE);
        cbTipo.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(0xe0e0e0)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        if (isEdit) {
            cbTipo.setSelectedItem(cliente.getTipo());
            cbTipo.setEnabled(false);
        }
        formPanel.add(cbTipo, gbcCbTipo);

     // Documento - Label
        GridBagConstraints gbcLblDocumento = new GridBagConstraints();
        gbcLblDocumento.gridx = 0;
        gbcLblDocumento.gridy = 4;
        gbcLblDocumento.weightx = 0;
        gbcLblDocumento.anchor = GridBagConstraints.WEST;
        gbcLblDocumento.insets = new Insets(5, 5, 5, 5);
        JLabel lblDocumento = new JLabel("Documento*:");
        lblDocumento.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblDocumento, gbcLblDocumento);

        // Documento - Campo de texto
        GridBagConstraints gbcTxtDocumento = new GridBagConstraints();
        gbcTxtDocumento.gridx = 1;
        gbcTxtDocumento.gridy = 4;
        gbcTxtDocumento.weightx = 1;
        gbcTxtDocumento.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtDocumento.insets = new Insets(5, 5, 5, 5);
        JTextField txtDocumento = new JTextField(isEdit ? cliente.getDocumento() : "", 20);
        txtDocumento.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDocumento.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(0xe0e0e0)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(txtDocumento, gbcTxtDocumento);

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
            	  String nome = txtNome.getText().trim();
                  String telefone = txtTelefone.getText().trim();
                  String email = txtEmail.getText().trim();
                  String documento = txtDocumento.getText().trim();
                  StringBuilder erros = new StringBuilder();
                  
                  if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || documento.isEmpty()) {
                      JOptionPane.showMessageDialog(dialog, "Todos os campos são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                      return;
                  }
                  
                  //validação nome
                  if (!nome.matches("[a-zA-Z ]+")) {
                      erros.append("- Use apenas letras no nome\n");
                  }
                  
                  //validação telefone
                  if (!telefone.matches("^[\\d\\s()\\-]+$")) {
                      JOptionPane.showMessageDialog(dialog, 
                          "Telefone deve conter apenas números, espaços, parênteses ou hífens.", 
                          "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                      return;
                  }
                  
                //validação email
                  if (!email.isEmpty()) {
                      String[] partes = email.split("@");
                      if (partes.length != 2 || 
                          partes[0].isEmpty() || 
                          !partes[1].contains(".") ||
                          partes[1].indexOf(".") == 0 ||
                          partes[1].endsWith(".")) {
                          erros.append("- Email inválido. Use o formato: nome@dominio.com\n");
                      JOptionPane.showMessageDialog(dialog, "Email inválido.", "Use o formato: nome@dominio.com", JOptionPane.ERROR_MESSAGE);
                      return;
                      }
                  }
                  
                  //validação documentos
                  String tipo = (String) cbTipo.getSelectedItem();
                  if ("NACIONAL".equals(tipo) && !documento.matches("\\d{11}")) {
                      JOptionPane.showMessageDialog(dialog, "CPF deve conter 11 dígitos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                      return;
                  }
                  
                  //salvar apos validação
                  if (isEdit) {
                      cliente.setNome(nome);
                      cliente.setTelefone(telefone);
                      cliente.setEmail(email);
                      cliente.setDocumento(documento);
                      controller.atualizarCliente(cliente);
                  } else {
                      Cliente novoCliente = new Cliente(0, nome, telefone, email, tipo, documento);
                      controller.salvarCliente(novoCliente);
                  }
                  
                  atualizarTabela();
                  dialog.dispose();
                  
            });
            
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSalvarDialogo);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
