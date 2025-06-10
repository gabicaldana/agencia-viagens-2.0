package Agencia;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class AgenciaGUI extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable clientesTable, pacotesTable, servicosTable, pedidosTable;
    private DefaultTableModel clientesModel, pacotesModel, servicosModel, pedidosModel;
    private List<Cliente> clientes;
    private List<PacoteViagem> pacotes;
    private List<ServicoAdicional> servicos;
    private List<Pedido> pedidos;

    public AgenciaGUI() {
        super("Agência de Viagens");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        carregarDados();
        
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Clientes", criarClientesPanel());
        tabbedPane.addTab("Pacotes", criarPacotesPanel());
        tabbedPane.addTab("Serviços", criarServicosPanel());
        tabbedPane.addTab("Pedidos", criarPedidosPanel());
        tabbedPane.addTab("Relatórios", criarRelatoriosPanel());
        
        add(tabbedPane);
    }

    private void carregarDados() {
        clientes = carregarClientes();
        pacotes = carregarPacotes();
        servicos = carregarServicos();
        pedidos = carregarPedidos(clientes, pacotes, servicos);
    }

    // ==================== CLIENTES ====================
    private JPanel criarClientesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] colunas = {"ID", "Nome", "Telefone", "Email", "Documento", "Tipo"};
        clientesModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        clientesTable = new JTable(clientesModel);
        
        atualizarTabelaClientes();
        
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");
        
        btnAdicionar.addActionListener(e -> adicionarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());
        btnAtualizar.addActionListener(e -> atualizarTabelaClientes());
        
        JPanel botoesPanel = new JPanel();
        botoesPanel.add(btnAdicionar);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnAtualizar);
        
        panel.add(new JScrollPane(clientesTable), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void adicionarCliente() {
        JDialog dialog = new JDialog(this, "Novo Cliente", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        
        JTextField txtNome = new JTextField();
        JTextField txtTelefone = new JTextField();
        JTextField txtEmail = new JTextField();
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Nacional", "Estrangeiro"});
        JTextField txtDocumento = new JTextField();
        
        dialog.add(new JLabel("Nome:"));
        dialog.add(txtNome);
        dialog.add(new JLabel("Telefone:"));
        dialog.add(txtTelefone);
        dialog.add(new JLabel("Email:"));
        dialog.add(txtEmail);
        dialog.add(new JLabel("Tipo:"));
        dialog.add(cbTipo);
        dialog.add(new JLabel("Documento:"));
        dialog.add(txtDocumento);
        
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnSalvar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String telefone = txtTelefone.getText().trim();
            String email = txtEmail.getText().trim();
            String tipo = (String) cbTipo.getSelectedItem();
            String documento = txtDocumento.getText().trim();
            
            if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || documento.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Cliente novo = tipo.equals("Nacional") ? 
                new ClienteNacional(0, nome, telefone, email, documento) :
                new ClienteEstrangeiro(0, nome, telefone, email, documento);
            
            salvarCliente(novo);
            clientes.add(novo);
            atualizarTabelaClientes();
            dialog.dispose();
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.add(btnSalvar);
        dialog.add(btnCancelar);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editarCliente() {
        int selectedRow = clientesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) clientesModel.getValueAt(selectedRow, 0);
        Cliente cliente = clientes.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
        
        if (cliente == null) return;
        
        JDialog dialog = new JDialog(this, "Editar Cliente", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        
        JTextField txtNome = new JTextField(cliente.getNome());
        JTextField txtTelefone = new JTextField(cliente.getTelefone());
        JTextField txtEmail = new JTextField(cliente.getEmail());
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Nacional", "Estrangeiro"});
        cbTipo.setSelectedItem(cliente instanceof ClienteNacional ? "Nacional" : "Estrangeiro");
        JTextField txtDocumento = new JTextField(cliente.getIdentificacao());
        
        dialog.add(new JLabel("Nome:"));
        dialog.add(txtNome);
        dialog.add(new JLabel("Telefone:"));
        dialog.add(txtTelefone);
        dialog.add(new JLabel("Email:"));
        dialog.add(txtEmail);
        dialog.add(new JLabel("Tipo:"));
        dialog.add(cbTipo);
        dialog.add(new JLabel("Documento:"));
        dialog.add(txtDocumento);
        
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnSalvar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String telefone = txtTelefone.getText().trim();
            String email = txtEmail.getText().trim();
            String tipo = (String) cbTipo.getSelectedItem();
            String documento = txtDocumento.getText().trim();
            
            if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || documento.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            cliente.nome = nome;
            cliente.telefone = telefone;
            cliente.email = email;
            
            if (tipo.equals("Nacional") && cliente instanceof ClienteNacional) {
                ((ClienteNacional) cliente).cpf = documento;
            } else if (tipo.equals("Estrangeiro") && cliente instanceof ClienteEstrangeiro) {
                ((ClienteEstrangeiro) cliente).passaporte = documento;
            }
            
            atualizarCliente(cliente);
            atualizarTabelaClientes();
            dialog.dispose();
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.add(btnSalvar);
        dialog.add(btnCancelar);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void excluirCliente() {
        int selectedRow = clientesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) clientesModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir este cliente?", "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            excluirCliente(id);
            clientes.removeIf(c -> c.getId() == id);
            atualizarTabelaClientes();
        }
    }

    private void atualizarTabelaClientes() {
        clientesModel.setRowCount(0);
        for (Cliente c : clientes) {
            clientesModel.addRow(new Object[]{
                c.getId(), c.getNome(), c.getTelefone(), 
                c.getEmail(), c.getIdentificacao(), c.getTipoCliente()
            });
        }
    }

    // ==================== PACOTES ====================
    private JPanel criarPacotesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] colunas = {"ID", "Nome", "Destino", "Duração", "Preço", "Tipo"};
        pacotesModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pacotesTable = new JTable(pacotesModel);
        
        atualizarTabelaPacotes();
        
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");
        
        btnAdicionar.addActionListener(e -> adicionarPacote());
        btnEditar.addActionListener(e -> editarPacote());
        btnExcluir.addActionListener(e -> excluirPacote());
        btnAtualizar.addActionListener(e -> atualizarTabelaPacotes());
        
        JPanel botoesPanel = new JPanel();
        botoesPanel.add(btnAdicionar);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnAtualizar);
        
        panel.add(new JScrollPane(pacotesTable), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void adicionarPacote() {
        JDialog dialog = new JDialog(this, "Novo Pacote", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        
        JTextField txtNome = new JTextField();
        JTextField txtDestino = new JTextField();
        JTextField txtDuracao = new JTextField();
        JTextField txtPreco = new JTextField();
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Aventura", "Luxo", "Cultural"});
        
        dialog.add(new JLabel("Nome:"));
        dialog.add(txtNome);
        dialog.add(new JLabel("Destino:"));
        dialog.add(txtDestino);
        dialog.add(new JLabel("Duração (dias):"));
        dialog.add(txtDuracao);
        dialog.add(new JLabel("Preço (R$):"));
        dialog.add(txtPreco);
        dialog.add(new JLabel("Tipo:"));
        dialog.add(cbTipo);
        
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnSalvar.addActionListener(e -> {
            try {
                String nome = txtNome.getText().trim();
                String destino = txtDestino.getText().trim();
                int duracao = Integer.parseInt(txtDuracao.getText().trim());
                double preco = Double.parseDouble(txtPreco.getText().trim());
                String tipo = (String) cbTipo.getSelectedItem();
                
                if (nome.isEmpty() || destino.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                PacoteViagem novo;
                switch (tipo) {
                    case "Aventura":
                        novo = new PacoteAventura(0, nome, destino, duracao, preco);
                        break;
                    case "Luxo":
                        novo = new PacoteLuxo(0, nome, destino, duracao, preco);
                        break;
                    case "Cultural":
                        novo = new PacoteCultural(0, nome, destino, duracao, preco);
                        break;
                    default:
                        throw new IllegalArgumentException("Tipo inválido");
                }
                
                salvarPacote(novo);
                pacotes.add(novo);
                atualizarTabelaPacotes();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Duração e preço devem ser números válidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.add(btnSalvar);
        dialog.add(btnCancelar);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editarPacote() {
        int selectedRow = pacotesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) pacotesModel.getValueAt(selectedRow, 0);
        PacoteViagem pacote = pacotes.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        
        if (pacote == null) return;
        
        JDialog dialog = new JDialog(this, "Editar Pacote", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        
        JTextField txtNome = new JTextField(pacote.getNome());
        JTextField txtDestino = new JTextField(pacote.getDestino());
        JTextField txtDuracao = new JTextField(String.valueOf(pacote.getDuracao()));
        JTextField txtPreco = new JTextField(String.valueOf(pacote.getPreco()));
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Aventura", "Luxo", "Cultural"});
        cbTipo.setSelectedItem(pacote.getTipo());
        
        dialog.add(new JLabel("Nome:"));
        dialog.add(txtNome);
        dialog.add(new JLabel("Destino:"));
        dialog.add(txtDestino);
        dialog.add(new JLabel("Duração (dias):"));
        dialog.add(txtDuracao);
        dialog.add(new JLabel("Preço (R$):"));
        dialog.add(txtPreco);
        dialog.add(new JLabel("Tipo:"));
        dialog.add(cbTipo);
        
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnSalvar.addActionListener(e -> {
            try {
                String nome = txtNome.getText().trim();
                String destino = txtDestino.getText().trim();
                int duracao = Integer.parseInt(txtDuracao.getText().trim());
                double preco = Double.parseDouble(txtPreco.getText().trim());
                String tipo = (String) cbTipo.getSelectedItem();
                
                if (nome.isEmpty() || destino.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                pacote.nome = nome;
                pacote.destino = destino;
                pacote.duracao = duracao;
                pacote.preco = preco;
                pacote.tipo = tipo;
                
                excluirPacote(pacote.getId());
                salvarPacote(pacote);
                
                atualizarTabelaPacotes();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Duração e preço devem ser números válidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.add(btnSalvar);
        dialog.add(btnCancelar);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void excluirPacote() {
        int selectedRow = pacotesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote para excluir", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) pacotesModel.getValueAt(selectedRow, 0);
        
        boolean emUso = pedidos.stream()
            .anyMatch(p -> p.getPacotes().stream().anyMatch(pac -> pac.getId() == id));
        
        if (emUso) {
            JOptionPane.showMessageDialog(this, "Este pacote está em uso e não pode ser excluído!", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir este pacote?", "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            excluirPacote(id);
            pacotes.removeIf(p -> p.getId() == id);
            atualizarTabelaPacotes();
        }
    }

    private void atualizarTabelaPacotes() {
        pacotesModel.setRowCount(0);
        for (PacoteViagem p : pacotes) {
            pacotesModel.addRow(new Object[]{
                p.getId(), p.getNome(), p.getDestino(), 
                p.getDuracao(), p.getPreco(), p.getTipo()
            });
        }
    }

    // ==================== SERVIÇOS ====================
    private JPanel criarServicosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] colunas = {"ID", "Descrição", "Preço"};
        servicosModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        servicosTable = new JTable(servicosModel);
        
        atualizarTabelaServicos();
        
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");
        
        btnAdicionar.addActionListener(e -> adicionarServico());
        btnEditar.addActionListener(e -> editarServico());
        btnExcluir.addActionListener(e -> excluirServico());
        btnAtualizar.addActionListener(e -> atualizarTabelaServicos());
        
        JPanel botoesPanel = new JPanel();
        botoesPanel.add(btnAdicionar);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnAtualizar);
        
        panel.add(new JScrollPane(servicosTable), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void adicionarServico() {
        JDialog dialog = new JDialog(this, "Novo Serviço", true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        
        JTextField txtDescricao = new JTextField();
        JTextField txtPreco = new JTextField();
        
        dialog.add(new JLabel("Descrição:"));
        dialog.add(txtDescricao);
        dialog.add(new JLabel("Preço (R$):"));
        dialog.add(txtPreco);
        
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnSalvar.addActionListener(e -> {
            try {
                String descricao = txtDescricao.getText().trim();
                double preco = Double.parseDouble(txtPreco.getText().trim());
                
                if (descricao.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                ServicoAdicional novo = new ServicoAdicional(0, descricao, preco);
                salvarServico(novo);
                servicos.add(novo);
                atualizarTabelaServicos();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Preço deve ser um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.add(btnSalvar);
        dialog.add(btnCancelar);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editarServico() {
        int selectedRow = servicosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) servicosModel.getValueAt(selectedRow, 0);
        ServicoAdicional servico = servicos.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
        
        if (servico == null) return;
        
        JDialog dialog = new JDialog(this, "Editar Serviço", true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        
        JTextField txtDescricao = new JTextField(servico.getDescricao());
        JTextField txtPreco = new JTextField(String.valueOf(servico.getPreco()));
        
        dialog.add(new JLabel("Descrição:"));
        dialog.add(txtDescricao);
        dialog.add(new JLabel("Preço (R$):"));
        dialog.add(txtPreco);
        
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnSalvar.addActionListener(e -> {
            try {
                String descricao = txtDescricao.getText().trim();
                double preco = Double.parseDouble(txtPreco.getText().trim());
                
                if (descricao.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                servico.descricao = descricao;
                servico.preco = preco;
                
                excluirServico(servico.getId());
                salvarServico(servico);
                
                atualizarTabelaServicos();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Preço deve ser um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.add(btnSalvar);
        dialog.add(btnCancelar);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void excluirServico() {
        int selectedRow = servicosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço para excluir", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) servicosModel.getValueAt(selectedRow, 0);
        
        boolean emUso = pedidos.stream()
            .anyMatch(p -> p.getServicos().stream().anyMatch(serv -> serv.getId() == id));
        
        if (emUso) {
            JOptionPane.showMessageDialog(this, "Este serviço está em uso e não pode ser excluído!", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir este serviço?", "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            excluirServico(id);
            servicos.removeIf(s -> s.getId() == id);
            atualizarTabelaServicos();
        }
    }

    private void atualizarTabelaServicos() {
        servicosModel.setRowCount(0);
        for (ServicoAdicional s : servicos) {
            servicosModel.addRow(new Object[]{
                s.getId(), s.getDescricao(), s.getPreco()
            });
        }
    }

    // ==================== PEDIDOS ====================
    private JPanel criarPedidosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] colunas = {"ID", "Cliente", "Data", "Pacotes", "Serviços", "Total"};
        pedidosModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pedidosTable = new JTable(pedidosModel);
        
        atualizarTabelaPedidos();
        
        JButton btnNovo = new JButton("Novo Pedido");
        JButton btnDetalhes = new JButton("Ver Detalhes");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");
        
        btnNovo.addActionListener(e -> criarPedido());
        btnDetalhes.addActionListener(e -> verDetalhesPedido());
        btnExcluir.addActionListener(e -> excluirPedido());
        btnAtualizar.addActionListener(e -> atualizarTabelaPedidos());
        
        JPanel botoesPanel = new JPanel();
        botoesPanel.add(btnNovo);
        botoesPanel.add(btnDetalhes);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnAtualizar);
        
        panel.add(new JScrollPane(pedidosTable), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void criarPedido() {
        if (clientes.isEmpty() || pacotes.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Cadastre clientes e pacotes primeiro!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Novo Pedido", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        
        // Painel de seleção
        JPanel selecaoPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        
        // Selecionar cliente
        JPanel clientePanel = new JPanel(new BorderLayout());
        clientePanel.add(new JLabel("Cliente:"), BorderLayout.NORTH);
        JComboBox<Cliente> clienteCombo = new JComboBox<>();
        for (Cliente c : clientes) clienteCombo.addItem(c);
        clientePanel.add(clienteCombo, BorderLayout.CENTER);
        
        // Selecionar pacotes
        JPanel pacotesPanel = new JPanel(new BorderLayout());
        pacotesPanel.add(new JLabel("Pacotes:"), BorderLayout.NORTH);
        DefaultListModel<PacoteViagem> pacotesListModel = new DefaultListModel<>();
        for (PacoteViagem p : pacotes) pacotesListModel.addElement(p);
        JList<PacoteViagem> pacotesList = new JList<>(pacotesListModel);
        pacotesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        pacotesPanel.add(new JScrollPane(pacotesList), BorderLayout.CENTER);
        
        // Selecionar serviços
        JPanel servicosPanel = new JPanel(new BorderLayout());
        servicosPanel.add(new JLabel("Serviços Adicionais:"), BorderLayout.NORTH);
        DefaultListModel<ServicoAdicional> servicosListModel = new DefaultListModel<>();
        for (ServicoAdicional s : servicos) servicosListModel.addElement(s);
        JList<ServicoAdicional> servicosList = new JList<>(servicosListModel);
        servicosList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        servicosPanel.add(new JScrollPane(servicosList), BorderLayout.CENTER);
        
        selecaoPanel.add(clientePanel);
        selecaoPanel.add(pacotesPanel);
        selecaoPanel.add(servicosPanel);
        
        // Botões
        JPanel botoesPanel = new JPanel();
        JButton btnSalvar = new JButton("Salvar Pedido");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnSalvar.addActionListener(e -> {
            Cliente cliente = (Cliente) clienteCombo.getSelectedItem();
            List<PacoteViagem> pacotesSelecionados = pacotesList.getSelectedValuesList();
            
            if (pacotesSelecionados.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Selecione pelo menos um pacote!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            List<ServicoAdicional> servicosSelecionados = servicosList.getSelectedValuesList();
            
            Pedido novoPedido = new Pedido(0, cliente, new Date());
            for (PacoteViagem p : pacotesSelecionados) novoPedido.adicionarPacote(p);
            for (ServicoAdicional s : servicosSelecionados) novoPedido.adicionarServico(s);
            
            salvarPedido(novoPedido);
            pedidos.add(novoPedido);
            atualizarTabelaPedidos();
            
            JOptionPane.showMessageDialog(dialog, 
                "Pedido criado com sucesso!\nTotal: R$" + String.format("%.2f", novoPedido.getTotal()));
            dialog.dispose();
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        botoesPanel.add(btnSalvar);
        botoesPanel.add(btnCancelar);
        
        dialog.add(selecaoPanel, BorderLayout.CENTER);
        dialog.add(botoesPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void verDetalhesPedido() {
        int selectedRow = pedidosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) pedidosModel.getValueAt(selectedRow, 0);
        Pedido pedido = pedidos.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        
        if (pedido == null) return;
        
        JTextArea textArea = new JTextArea(pedido.getResumo());
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), 
            "Detalhes do Pedido #" + pedido.getId(), JOptionPane.PLAIN_MESSAGE);
    }

    private void excluirPedido() {
        int selectedRow = pedidosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para excluir", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) pedidosModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir este pedido?", "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            excluirPedido(id);
            pedidos.removeIf(p -> p.getId() == id);
            atualizarTabelaPedidos();
        }
    }

    private void atualizarTabelaPedidos() {
        pedidosModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (Pedido p : pedidos) {
            pedidosModel.addRow(new Object[]{
                p.getId(),
                p.getCliente().getNome(),
                sdf.format(p.getData()),
                p.getPacotes().size(),
                p.getServicos().size(),
                String.format("R$ %.2f", p.getTotal())
            });
        }
    }

    // ==================== RELATÓRIOS ====================
    private JPanel criarRelatoriosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        
        JButton btnResumo = new JButton("Resumo Geral");
        JButton btnVendas = new JButton("Total de Vendas");
        JButton btnPacotesPopulares = new JButton("Pacotes Populares");
        
        btnResumo.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("RESUMO GERAL\n\n");
            sb.append("Total de Clientes: ").append(clientes.size()).append("\n");
            sb.append("Total de Pacotes: ").append(pacotes.size()).append("\n");
            sb.append("Total de Serviços: ").append(servicos.size()).append("\n");
            sb.append("Total de Pedidos: ").append(pedidos.size()).append("\n");
            
            double totalVendas = pedidos.stream().mapToDouble(Pedido::getTotal).sum();
            sb.append("Total de Vendas: R$").append(String.format("%.2f", totalVendas)).append("\n");
            
            textArea.setText(sb.toString());
        });
        
        btnVendas.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("RELATÓRIO DE VENDAS\n\n");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            Map<String, Double> vendasPorData = new TreeMap<>();
            for (Pedido p : pedidos) {
                String data = sdf.format(p.getData());
                vendasPorData.put(data, vendasPorData.getOrDefault(data, 0.0) + p.getTotal());
            }
            
            for (Map.Entry<String, Double> entry : vendasPorData.entrySet()) {
                sb.append(entry.getKey()).append(": R$").append(String.format("%.2f", entry.getValue())).append("\n");
            }
            
            textArea.setText(sb.toString());
        });
        
        btnPacotesPopulares.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("PACOTES POPULARES\n\n");
            
            Map<String, Integer> contagemPacotes = new HashMap<>();
            for (Pedido p : pedidos) {
                for (PacoteViagem pacote : p.getPacotes()) {
                    String nomePacote = pacote.getNome();
                    contagemPacotes.put(nomePacote, contagemPacotes.getOrDefault(nomePacote, 0) + 1);
                }
            }
            
            List<Map.Entry<String, Integer>> lista = new ArrayList<>(contagemPacotes.entrySet());
            lista.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            
            for (Map.Entry<String, Integer> entry : lista) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" vendas\n");
            }
            
            textArea.setText(sb.toString());
        });
        
        JPanel botoesPanel = new JPanel();
        botoesPanel.add(btnResumo);
        botoesPanel.add(btnVendas);
        botoesPanel.add(btnPacotesPopulares);
        
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    // ==================== BANCO DE DADOS ====================
    private static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/agencia_viagens", "root", "sua_senha");
    }
    
    private static List<Cliente> carregarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String tipo = rs.getString("tipo");
                String nome = rs.getString("nome");
                String telefone = rs.getString("telefone");
                String email = rs.getString("email");
                
                if (tipo.equals("NACIONAL")) {
                    String cpf = rs.getString("cpf");
                    clientes.add(new ClienteNacional(id, nome, telefone, email, cpf));
                } else {
                    String passaporte = rs.getString("passaporte");
                    clientes.add(new ClienteEstrangeiro(id, nome, telefone, email, passaporte));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
        return clientes;
    }
    
    private static List<PacoteViagem> carregarPacotes() {
        List<PacoteViagem> pacotes = new ArrayList<>();
        String sql = "SELECT * FROM pacotes";
        
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String destino = rs.getString("destino");
                int duracao = rs.getInt("duracao");
                double preco = rs.getDouble("preco");
                String tipo = rs.getString("tipo");
                
                switch (tipo) {
                    case "AVENTURA":
                        pacotes.add(new PacoteAventura(id, nome, destino, duracao, preco));
                        break;
                    case "LUXO":
                        pacotes.add(new PacoteLuxo(id, nome, destino, duracao, preco));
                        break;
                    case "CULTURAL":
                        pacotes.add(new PacoteCultural(id, nome, destino, duracao, preco));
                        break;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar pacotes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
        return pacotes;
    }
    
    private static List<ServicoAdicional> carregarServicos() {
        List<ServicoAdicional> servicos = new ArrayList<>();
        String sql = "SELECT * FROM servicos";
        
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String descricao = rs.getString("descricao");
                double preco = rs.getDouble("preco");
                servicos.add(new ServicoAdicional(id, descricao, preco));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar serviços: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
        return servicos;
    }
    
    private static List<Pedido> carregarPedidos(List<Cliente> clientes, List<PacoteViagem> pacotes, List<ServicoAdicional> servicos) {
        List<Pedido> pedidos = new ArrayList<>();
        String sqlPedidos = "SELECT * FROM pedidos";
        
        try (Connection conn = obterConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlPedidos)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                int clienteId = rs.getInt("cliente_id");
                Date data = rs.getTimestamp("data_pedido");
                
                Cliente cliente = clientes.stream()
                    .filter(c -> c.getId() == clienteId)
                    .findFirst()
                    .orElse(null);
                
                if (cliente != null) {
                    Pedido pedido = new Pedido(id, cliente, data);
                    
                    // Carregar pacotes do pedido
                    String sqlPacotes = "SELECT pacote_id FROM pedido_pacotes WHERE pedido_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sqlPacotes)) {
                        pstmt.setInt(1, id);
                        ResultSet rsPacotes = pstmt.executeQuery();
                        
                        while (rsPacotes.next()) {
                            int pacoteId = rsPacotes.getInt("pacote_id");
                            pacotes.stream()
                                .filter(p -> p.getId() == pacoteId)
                                .findFirst()
                                .ifPresent(pedido::adicionarPacote);
                        }
                    }
                    
                    // Carregar serviços do pedido
                    String sqlServicos = "SELECT servico_id FROM pedido_servicos WHERE pedido_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sqlServicos)) {
                        pstmt.setInt(1, id);
                        ResultSet rsServicos = pstmt.executeQuery();
                        
                        while (rsServicos.next()) {
                            int servicoId = rsServicos.getInt("servico_id");
                            servicos.stream()
                                .filter(s -> s.getId() == servicoId)
                                .findFirst()
                                .ifPresent(pedido::adicionarServico);
                        }
                    }
                    
                    pedidos.add(pedido);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
        return pedidos;
    }
    
    private static void salvarCliente(Cliente cliente) {
        String sqlNacional = "INSERT INTO clientes (tipo, nome, telefone, email, cpf) VALUES (?, ?, ?, ?, ?)";
        String sqlEstrangeiro = "INSERT INTO clientes (tipo, nome, telefone, email, passaporte) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = obterConexao()) {
            PreparedStatement pstmt;
            
            if (cliente instanceof ClienteNacional) {
                pstmt = conn.prepareStatement(sqlNacional, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, "NACIONAL");
                pstmt.setString(2, cliente.getNome());
                pstmt.setString(3, cliente.getTelefone());
                pstmt.setString(4, cliente.getEmail());
                pstmt.setString(5, ((ClienteNacional)cliente).getIdentificacao());
            } else {
                pstmt = conn.prepareStatement(sqlEstrangeiro, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, "ESTRANGEIRO");
                pstmt.setString(2, cliente.getNome());
                pstmt.setString(3, cliente.getTelefone());
                pstmt.setString(4, cliente.getEmail());
                pstmt.setString(5, ((ClienteEstrangeiro)cliente).getIdentificacao());
            }
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.id = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void atualizarCliente(Cliente cliente) {
        String sqlNacional = "UPDATE clientes SET nome = ?, telefone = ?, email = ?, cpf = ? WHERE id = ?";
        String sqlEstrangeiro = "UPDATE clientes SET nome = ?, telefone = ?, email = ?, passaporte = ? WHERE id = ?";
        
        try (Connection conn = obterConexao()) {
            PreparedStatement pstmt;
            
            if (cliente instanceof ClienteNacional) {
                pstmt = conn.prepareStatement(sqlNacional);
                pstmt.setString(1, cliente.getNome());
                pstmt.setString(2, cliente.getTelefone());
                pstmt.setString(3, cliente.getEmail());
                pstmt.setString(4, ((ClienteNacional)cliente).getIdentificacao());
                pstmt.setInt(5, cliente.getId());
            } else {
                pstmt = conn.prepareStatement(sqlEstrangeiro);
                pstmt.setString(1, cliente.getNome());
                pstmt.setString(2, cliente.getTelefone());
                pstmt.setString(3, cliente.getEmail());
                pstmt.setString(4, ((ClienteEstrangeiro)cliente).getIdentificacao());
                pstmt.setInt(5, cliente.getId());
            }
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void excluirCliente(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void salvarPacote(PacoteViagem pacote) {
        String sql = "INSERT INTO pacotes (nome, destino, duracao, preco, tipo) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, pacote.getNome());
            pstmt.setString(2, pacote.getDestino());
            pstmt.setInt(3, pacote.getDuracao());
            pstmt.setDouble(4, pacote.getPreco());
            pstmt.setString(5, pacote.getTipo().toUpperCase());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pacote.id = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar pacote: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void excluirPacote(int id) {
        String sql = "DELETE FROM pacotes WHERE id = ?";
        
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir pacote: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void salvarServico(ServicoAdicional servico) {
        String sql = "INSERT INTO servicos (descricao, preco) VALUES (?, ?)";
        
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, servico.getDescricao());
            pstmt.setDouble(2, servico.getPreco());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    servico.id = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar serviço: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void excluirServico(int id) {
        String sql = "DELETE FROM servicos WHERE id = ?";
        
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir serviço: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void salvarPedido(Pedido pedido) {
        String sqlPedido = "INSERT INTO pedidos (cliente_id, data_pedido) VALUES (?, ?)";
        
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, pedido.getCliente().getId());
            pstmt.setTimestamp(2, new java.sql.Timestamp(pedido.getData().getTime()));
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pedido.id = generatedKeys.getInt(1);
                }
            }
            
            // Salvar pacotes do pedido
            String sqlPacotes = "INSERT INTO pedido_pacotes (pedido_id, pacote_id) VALUES (?, ?)";
            try (PreparedStatement pstmtPacotes = conn.prepareStatement(sqlPacotes)) {
                for (PacoteViagem pacote : pedido.getPacotes()) {
                    pstmtPacotes.setInt(1, pedido.getId());
                    pstmtPacotes.setInt(2, pacote.getId());
                    pstmtPacotes.addBatch();
                }
                pstmtPacotes.executeBatch();
            }
            
            // Salvar serviços do pedido
            if (!pedido.getServicos().isEmpty()) {
                String sqlServicos = "INSERT INTO pedido_servicos (pedido_id, servico_id) VALUES (?, ?)";
                try (PreparedStatement pstmtServicos = conn.prepareStatement(sqlServicos)) {
                    for (ServicoAdicional servico : pedido.getServicos()) {
                        pstmtServicos.setInt(1, pedido.getId());
                        pstmtServicos.setInt(2, servico.getId());
                        pstmtServicos.addBatch();
                    }
                    pstmtServicos.executeBatch();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar pedido: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void excluirPedido(int id) {
        String sqlPedidoPacotes = "DELETE FROM pedido_pacotes WHERE pedido_id = ?";
        String sqlPedidoServicos = "DELETE FROM pedido_servicos WHERE pedido_id = ?";
        String sqlPedido = "DELETE FROM pedidos WHERE id = ?";
        
        try (Connection conn = obterConexao()) {
            // Excluir relacionamentos primeiro
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPedidoPacotes)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPedidoServicos)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // Excluir o pedido
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPedido)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir pedido: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== MAIN ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new AgenciaGUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ==================== CLASSES INTERNAS ====================
    abstract static class Cliente {
        protected int id;
        protected String nome;
        protected String telefone;
        protected String email;

        public Cliente(int id, String nome, String telefone, String email) {
            this.id = id;
            this.nome = nome;
            this.telefone = telefone;
            this.email = email;
        }

        public abstract String getIdentificacao();
        public abstract String getTipoCliente();

        public int getId() { return id; }
        public String getNome() { return nome; }
        public String getTelefone() { return telefone; }
        public String getEmail() { return email; }
        
        @Override
        public String toString() {
            return nome + " (" + getIdentificacao() + ")";
        }
    }

    static class ClienteNacional extends Cliente {
        private String cpf;

        public ClienteNacional(int id, String nome, String telefone, String email, String cpf) {
            super(id, nome, telefone, email);
            this.cpf = cpf;
        }

        @Override
        public String getIdentificacao() { return cpf; }
        @Override
        public String getTipoCliente() { return "NACIONAL"; }
    }

    static class ClienteEstrangeiro extends Cliente {
        private String passaporte;

        public ClienteEstrangeiro(int id, String nome, String telefone, String email, String passaporte) {
            super(id, nome, telefone, email);
            this.passaporte = passaporte;
        }

        @Override
        public String getIdentificacao() { return passaporte; }
        @Override
        public String getTipoCliente() { return "ESTRANGEIRO"; }
    }

    abstract static class PacoteViagem {
        protected int id;
        protected String nome;
        protected String destino;
        protected int duracao;
        protected double preco;
        protected String tipo;

        public PacoteViagem(int id, String nome, String destino, int duracao, double preco, String tipo) {
            this.id = id;
            this.nome = nome;
            this.destino = destino;
            this.duracao = duracao;
            this.preco = preco;
            this.tipo = tipo;
        }

        public double getPreco() { return preco; }
        public String getNome() { return nome; }
        public String getDestino() { return destino; }
        public int getDuracao() { return duracao; }
        public String getTipo() { return tipo; }
        public int getId() { return id; }
        
        public String getResumo() {
            return nome + " - " + tipo + " (" + destino + ", " + duracao + " dias, R$" + preco + ")";
        }
        
        @Override
        public String toString() {
            return getResumo();
        }
    }

    static class PacoteAventura extends PacoteViagem {
        public PacoteAventura(int id, String nome, String destino, int duracao, double preco) {
            super(id, nome, destino, duracao, preco, "Aventura");
        }
    }

    static class PacoteLuxo extends PacoteViagem {
        public PacoteLuxo(int id, String nome, String destino, int duracao, double preco) {
            super(id, nome, destino, duracao, preco, "Luxo");
        }
    }

    static class PacoteCultural extends PacoteViagem {
        public PacoteCultural(int id, String nome, String destino, int duracao, double preco) {
            super(id, nome, destino, duracao, preco, "Cultural");
        }
    }

    static class ServicoAdicional {
        private int id;
        private String descricao;
        private double preco;

        public ServicoAdicional(int id, String descricao, double preco) {
            this.id = id;
            this.descricao = descricao;
            this.preco = preco;
        }

        public double getPreco() { return preco; }
        public String getDescricao() { return descricao; }
        public int getId() { return id; }
        
        public String getResumo() {
            return descricao + " (R$" + preco + ")";
        }
        
        @Override
        public String toString() {
            return getResumo();
        }
    }

    static class Pedido {
        private int id;
        private Cliente cliente;
        private List<PacoteViagem> pacotes = new ArrayList<>();
        private List<ServicoAdicional> servicos = new ArrayList<>();
        private Date data;

        public Pedido(int id, Cliente cliente, Date data) {
            this.id = id;
            this.cliente = cliente;
            this.data = data;
        }

        public void adicionarPacote(PacoteViagem pacote) {
            pacotes.add(pacote);
        }

        public void adicionarServico(ServicoAdicional servico) {
            servicos.add(servico);
        }

        public String getResumo() {
            StringBuilder sb = new StringBuilder("Cliente: " + cliente.getResumo() + "\nPacotes:\n");
            for (PacoteViagem p : pacotes) sb.append(" - ").append(p.getResumo()).append("\n");
            if (!servicos.isEmpty()) {
                sb.append("Serviços adicionais:\n");
                for (ServicoAdicional s : servicos) sb.append(" - ").append(s.getResumo()).append("\n");
            }
            sb.append("Total do pedido: R$").append(String.format("%.2f", getTotal())).append("\n");
            return sb.toString();
        }

        public double getTotal() {
            double total = 0;
            for (PacoteViagem p : pacotes) total += p.getPreco();
            for (ServicoAdicional s : servicos) total += s.getPreco();
            return total;
        }

        public List<PacoteViagem> getPacotes() { return pacotes; }
        public Cliente getCliente() { return cliente; }
        public List<ServicoAdicional> getServicos() { return servicos; }
        public int getId() { return id; }
        public Date getData() { return data; }
        
        @Override
        public String toString() {
            return cliente.getNome() + " - " + 
                   pacotes.size() + " pacote(s), " + 
                   servicos.size() + " serviço(s) - Total: R$" + 
                   String.format("%.2f", getTotal());
        }
    }
}
