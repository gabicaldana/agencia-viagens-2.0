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
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Carregar dados
        carregarDados();
        
        // Criar abas
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Clientes", criarClientesPanel());
        tabbedPane.addTab("Pacotes", criarPacotesPanel());
        tabbedPane.addTab("Serviços", criarServicosPanel());
        tabbedPane.addTab("Pedidos", criarPedidosPanel());
        tabbedPane.addTab("Relatórios", criarRelatoriosPanel());
        
        add(tabbedPane);
        setVisible(true);
    }
    
    private void carregarDados() {
        clientes = carregarClientes();
        pacotes = carregarPacotes();
        servicos = carregarServicos();
        pedidos = carregarPedidos(clientes, pacotes, servicos);
    }
    
    private void atualizarTabelaClientes() {
        clientes = carregarClientes();
        clientesModel.setRowCount(0);
        for (Cliente c : clientes) {
            clientesModel.addRow(new Object[]{
                c.getId(),
                c.getNome(),
                c.getTelefone(),
                c.getEmail(),
                c.getIdentificacao(),
                c.getTipoCliente()
            });
        }
    }
    
    private void atualizarTabelaPacotes() {
        pacotes = carregarPacotes();
        pacotesModel.setRowCount(0);
        for (PacoteViagem p : pacotes) {
            pacotesModel.addRow(new Object[]{
                p.getId(),
                p.getNome(),
                p.getDestino(),
                p.getDuracao(),
                p.getPreco(),
                p.getTipo()
            });
        }
    }
    
    private void atualizarTabelaServicos() {
        servicos = carregarServicos();
        servicosModel.setRowCount(0);
        for (ServicoAdicional s : servicos) {
            servicosModel.addRow(new Object[]{
                s.getId(),
                s.getDescricao(),
                s.getPreco()
            });
        }
    }
    
    private void atualizarTabelaPedidos() {
        pedidos = carregarPedidos(clientes, pacotes, servicos);
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
    
    private JPanel criarClientesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Modelo da tabela
        String[] colunas = {"ID", "Nome", "Telefone", "Email", "Documento", "Tipo"};
        clientesModel = new DefaultTableModel(colunas, 0);
        clientesTable = new JTable(clientesModel);
        clientesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Preencher tabela
        for (Cliente c : clientes) {
            clientesModel.addRow(new Object[]{
                c.getId(),
                c.getNome(),
                c.getTelefone(),
                c.getEmail(),
                c.getIdentificacao(),
                c.getTipoCliente()
            });
        }
        
        // Botões
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnPacotesCliente = new JButton("Pacotes por Cliente");
        
        JPanel botoesPanel = new JPanel();
        botoesPanel.add(btnAdicionar);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnAtualizar);
        botoesPanel.add(btnPacotesCliente);
        
        // Listeners
        btnAdicionar.addActionListener(e -> adicionarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());
        btnAtualizar.addActionListener(e -> atualizarTabelaClientes());
        btnPacotesCliente.addActionListener(e -> mostrarPacotesPorCliente());
        
        panel.add(new JScrollPane(clientesTable), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel criarPacotesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Modelo da tabela
        String[] colunas = {"ID", "Nome", "Destino", "Duração", "Preço", "Tipo"};
        pacotesModel = new DefaultTableModel(colunas, 0);
        pacotesTable = new JTable(pacotesModel);
        pacotesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Preencher tabela
        for (PacoteViagem p : pacotes) {
            pacotesModel.addRow(new Object[]{
                p.getId(),
                p.getNome(),
                p.getDestino(),
                p.getDuracao(),
                p.getPreco(),
                p.getTipo()
            });
        }
        
        // Botões
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnClientesPacote = new JButton("Clientes por Pacote");
        
        JPanel botoesPanel = new JPanel();
        botoesPanel.add(btnAdicionar);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnAtualizar);
        botoesPanel.add(btnClientesPacote);
        
        // Listeners
        btnAdicionar.addActionListener(e -> adicionarPacote());
        btnEditar.addActionListener(e -> editarPacote());
        btnExcluir.addActionListener(e -> excluirPacote());
        btnAtualizar.addActionListener(e -> atualizarTabelaPacotes());
        btnClientesPacote.addActionListener(e -> mostrarClientesPorPacote());
        
        panel.add(new JScrollPane(pacotesTable), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel criarServicosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Modelo da tabela
        String[] colunas = {"ID", "Descrição", "Preço"};
        servicosModel = new DefaultTableModel(colunas, 0);
        servicosTable = new JTable(servicosModel);
        servicosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Preencher tabela
        for (ServicoAdicional s : servicos) {
            servicosModel.addRow(new Object[]{
                s.getId(),
                s.getDescricao(),
                s.getPreco()
            });
        }
        
        // Botões
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");
        
        JPanel botoesPanel = new JPanel();
        botoesPanel.add(btnAdicionar);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnAtualizar);
        
        // Listeners
        btnAdicionar.addActionListener(e -> adicionarServico());
        btnEditar.addActionListener(e -> editarServico());
        btnExcluir.addActionListener(e -> excluirServico());
        btnAtualizar.addActionListener(e -> atualizarTabelaServicos());
        
        panel.add(new JScrollPane(servicosTable), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel criarPedidosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Modelo da tabela
        String[] colunas = {"ID", "Cliente", "Data", "Pacotes", "Serviços", "Total"};
        pedidosModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pedidosTable = new JTable(pedidosModel);
        pedidosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Preencher tabela
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
        
        // Botões
        JButton btnCriar = new JButton("Criar Pedido");
        JButton btnDetalhes = new JButton("Ver Detalhes");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");
        
        JPanel botoesPanel = new JPanel();
        botoesPanel.add(btnCriar);
        botoesPanel.add(btnDetalhes);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnAtualizar);
        
        // Listeners
        btnCriar.addActionListener(e -> criarPedido());
        btnDetalhes.addActionListener(e -> verDetalhesPedido());
        btnExcluir.addActionListener(e -> excluirPedido());
        btnAtualizar.addActionListener(e -> atualizarTabelaPedidos());
        
        panel.add(new JScrollPane(pedidosTable), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel criarRelatoriosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        
        // Botões
        JButton btnResumo = new JButton("Resumo Geral");
        JButton btnVendas = new JButton("Total de Vendas");
        JButton btnPacotesPopulares = new JButton("Pacotes Populares");
        
        JPanel botoesPanel = new JPanel();
        botoesPanel.add(btnResumo);
        botoesPanel.add(btnVendas);
        botoesPanel.add(btnPacotesPopulares);
        
        // Listeners
        btnResumo.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("RESUMO GERAL\n\n");
            sb.append("Total de Clientes: ").append(clientes.size()).append("\n");
            sb.append("Total de Pacotes: ").append(pacotes.size()).append("\n");
            sb.append("Total de Serviços: ").append(servicos.size()).append("\n");
            sb.append("Total de Pedidos: ").append(pedidos.size()).append("\n");
            
            double totalVendas = 0;
            for (Pedido p : pedidos) totalVendas += p.getTotal();
            sb.append("Total de Vendas: R$").append(String.format("%.2f", totalVendas)).append("\n");
            
            textArea.setText(sb.toString());
        });
        
        btnVendas.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("RELATÓRIO DE VENDAS\n\n");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            // Agrupar vendas por data
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
            
            // Contar ocorrências de cada pacote
            Map<String, Integer> contagemPacotes = new HashMap<>();
            for (Pedido p : pedidos) {
                for (PacoteViagem pacote : p.getPacotes()) {
                    String nomePacote = pacote.getNome();
                    contagemPacotes.put(nomePacote, contagemPacotes.getOrDefault(nomePacote, 0) + 1);
                }
            }
            
            // Ordenar por popularidade
            List<Map.Entry<String, Integer>> lista = new ArrayList<>(contagemPacotes.entrySet());
            lista.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            
            for (Map.Entry<String, Integer> entry : lista) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" vendas\n");
            }
            
            textArea.setText(sb.toString());
        });
        
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Métodos para operações de Clientes
    private void adicionarCliente() {
        JPanel panel = new JPanel(new GridLayout(5, 2));
        
        JTextField nomeField = new JTextField();
        JTextField telefoneField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"Nacional", "Estrangeiro"});
        JTextField documentoField = new JTextField();
        
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Telefone:"));
        panel.add(telefoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Tipo:"));
        panel.add(tipoCombo);
        panel.add(new JLabel("Documento (CPF/Passaporte):"));
        panel.add(documentoField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Novo Cliente", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String nome = nomeField.getText().trim();
            String telefone = telefoneField.getText().trim();
            String email = emailField.getText().trim();
            String tipo = (String) tipoCombo.getSelectedItem();
            String documento = documentoField.getText().trim();
            
            if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || documento.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Cliente novo = tipo.equals("Nacional") ? 
                new ClienteNacional(0, nome, telefone, email, documento) :
                new ClienteEstrangeiro(0, nome, telefone, email, documento);
            
            salvarCliente(novo);
            clientes.add(novo);
            atualizarTabelaClientes();
            JOptionPane.showMessageDialog(this, "Cliente adicionado com sucesso!");
        }
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
        
        JPanel panel = new JPanel(new GridLayout(5, 2));
        
        JTextField nomeField = new JTextField(cliente.getNome());
        JTextField telefoneField = new JTextField(cliente.getTelefone());
        JTextField emailField = new JTextField(cliente.getEmail());
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"Nacional", "Estrangeiro"});
        tipoCombo.setSelectedItem(cliente instanceof ClienteNacional ? "Nacional" : "Estrangeiro");
        JTextField documentoField = new JTextField(cliente.getIdentificacao());
        
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Telefone:"));
        panel.add(telefoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Tipo:"));
        panel.add(tipoCombo);
        panel.add(new JLabel("Documento (CPF/Passaporte):"));
        panel.add(documentoField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Cliente", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String nome = nomeField.getText().trim();
            String telefone = telefoneField.getText().trim();
            String email = emailField.getText().trim();
            String tipo = (String) tipoCombo.getSelectedItem();
            String documento = documentoField.getText().trim();
            
            if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || documento.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Atualizar cliente
            cliente.nome = nome;
            cliente.telefone = telefone;
            cliente.email = email;
            
            if (tipo.equals("Nacional") && cliente instanceof ClienteNacional) {
                ((ClienteNacional) cliente).cpf = documento;
            } else if (tipo.equals("Estrangeiro") && cliente instanceof ClienteEstrangeiro) {
                ((ClienteEstrangeiro) cliente).passaporte = documento;
            }
            
            // Atualizar no banco (implementar método update)
            atualizarCliente(cliente);
            atualizarTabelaClientes();
            JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
        }
    }
    
    private void excluirCliente() {
        int selectedRow = clientesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) clientesModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este cliente?", 
                "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            excluirCliente(id);
            clientes.removeIf(c -> c.getId() == id);
            atualizarTabelaClientes();
            JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
        }
    }
    
    private void mostrarPacotesPorCliente() {
        int selectedRow = clientesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) clientesModel.getValueAt(selectedRow, 0);
        Cliente cliente = clientes.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
        
        if (cliente == null) return;
        
        StringBuilder sb = new StringBuilder("Pacotes contratados por " + cliente.getNome() + ":\n\n");
        boolean encontrou = false;
        
        for (Pedido p : pedidos) {
            if (p.getCliente().equals(cliente)) {
                for (PacoteViagem pacote : p.getPacotes()) {
                    sb.append(" - ").append(pacote.getNome()).append(" (").append(pacote.getDestino()).append(")\n");
                    encontrou = true;
                }
            }
        }
        
        if (!encontrou) {
            sb.append("Nenhum pacote contratado por este cliente.");
        }
        
        JOptionPane.showMessageDialog(this, sb.toString());
    }
    
    // Métodos para operações de Pacotes (similar aos de Clientes)
    private void adicionarPacote() {
        // Implementação similar a adicionarCliente
    }
    
    private void editarPacote() {
        // Implementação similar a editarCliente
    }
    
    private void excluirPacote() {
        // Implementação similar a excluirCliente
    }
    
    private void mostrarClientesPorPacote() {
        int selectedRow = pacotesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) pacotesModel.getValueAt(selectedRow, 0);
        PacoteViagem pacote = pacotes.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        
        if (pacote == null) return;
        
        StringBuilder sb = new StringBuilder("Clientes que contrataram " + pacote.getNome() + ":\n\n");
        Set<String> clientesEncontrados = new HashSet<>();
        
        for (Pedido p : pedidos) {
            if (p.getPacotes().contains(pacote)) {
                clientesEncontrados.add(p.getCliente().getNome());
            }
        }
        
        if (clientesEncontrados.isEmpty()) {
            sb.append("Nenhum cliente contratou este pacote.");
        } else {
            for (String nome : clientesEncontrados) {
                sb.append(" - ").append(nome).append("\n");
            }
        }
        
        JOptionPane.showMessageDialog(this, sb.toString());
    }
    
    // Métodos para operações de Pedidos
    private void criarPedido() {
        if (clientes.isEmpty() || pacotes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cadastre clientes e pacotes primeiro!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Criar Pedido", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());
        
        // Seleção de cliente
        JPanel clientePanel = new JPanel(new FlowLayout());
        JComboBox<Cliente> clienteCombo = new JComboBox<>();
        for (Cliente c : clientes) clienteCombo.addItem(c);
        clientePanel.add(new JLabel("Cliente:"));
        clientePanel.add(clienteCombo);
        
        // Lista de pacotes
        JPanel pacotesPanel = new JPanel(new BorderLayout());
        pacotesPanel.add(new JLabel("Pacotes:"), BorderLayout.NORTH);
        
        DefaultListModel<PacoteViagem> pacotesModel = new DefaultListModel<>();
        for (PacoteViagem p : pacotes) pacotesModel.addElement(p);
        
        JList<PacoteViagem> pacotesList = new JList<>(pacotesModel);
        pacotesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        pacotesPanel.add(new JScrollPane(pacotesList), BorderLayout.CENTER);
        
        // Lista de serviços
        JPanel servicosPanel = new JPanel(new BorderLayout());
        servicosPanel.add(new JLabel("Serviços Adicionais:"), BorderLayout.NORTH);
        
        DefaultListModel<ServicoAdicional> servicosModel = new DefaultListModel<>();
        for (ServicoAdicional s : servicos) servicosModel.addElement(s);
        
        JList<ServicoAdicional> servicosList = new JList<>(servicosModel);
        servicosList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        servicosPanel.add(new JScrollPane(servicosList), BorderLayout.CENTER);
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridLayout(3, 1));
        mainPanel.add(clientePanel);
        mainPanel.add(pacotesPanel);
        mainPanel.add(servicosPanel);
        
        // Botões
        JButton btnSalvar = new JButton("Salvar Pedido");
        JButton btnCancelar = new JButton("Cancelar");
        
        JPanel botoesPanel = new JPanel();
        botoesPanel.add(btnSalvar);
        botoesPanel.add(btnCancelar);
        
        btnSalvar.addActionListener(e -> {
            Cliente cliente = (Cliente) clienteCombo.getSelectedItem();
            List<PacoteViagem> pacotesSelecionados = pacotesList.getSelectedValuesList();
            List<ServicoAdicional> servicosSelecionados = servicosList.getSelectedValuesList();
            
            if (pacotesSelecionados.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Selecione pelo menos um pacote!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Pedido novoPedido = new Pedido(0, cliente, new Date());
            for (PacoteViagem p : pacotesSelecionados) novoPedido.adicionarPacote(p);
            for (ServicoAdicional s : servicosSelecionados) novoPedido.adicionarServico(s);
            
            salvarPedido(novoPedido);
            pedidos.add(novoPedido);
            atualizarTabelaPedidos();
            
            JOptionPane.showMessageDialog(dialog, "Pedido criado com sucesso!\nTotal: R$" + 
                    String.format("%.2f", novoPedido.getTotal()));
            dialog.dispose();
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(botoesPanel, BorderLayout.SOUTH);
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
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Detalhes do Pedido", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void excluirPedido() {
        int selectedRow = pedidosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para excluir", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) pedidosModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este pedido?", 
                "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            excluirPedido(id);
            pedidos.removeIf(p -> p.getId() == id);
            atualizarTabelaPedidos();
            JOptionPane.showMessageDialog(this, "Pedido excluído com sucesso!");
        }
    }
    
    // Métodos de banco de dados (já existentes na versão anterior)
    private static List<Cliente> carregarClientes() {
        // Implementação existente
    }
    
    private static List<PacoteViagem> carregarPacotes() {
        // Implementação existente
    }
    
    private static List<ServicoAdicional> carregarServicos() {
        // Implementação existente
    }
    
    private static List<Pedido> carregarPedidos(List<Cliente> clientes, List<PacoteViagem> pacotes, List<ServicoAdicional> servicos) {
        // Implementação existente
    }
    
    private static void salvarCliente(Cliente cliente) {
        // Implementação existente
    }
    
    private static void atualizarCliente(Cliente cliente) {
        // Implementar método de atualização
    }
    
    private static void excluirCliente(int id) {
        // Implementação existente
    }
    
    // Métodos similares para Pacotes, Serviços e Pedidos...

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AgenciaGUI();
        });
    }
    
    // Classes internas (mantidas da versão anterior)
    abstract static class Cliente {
        // Implementação existente
    }
    
    static class ClienteNacional extends Cliente {
        // Implementação existente
    }
    
    static class ClienteEstrangeiro extends Cliente {
        // Implementação existente
    }
    
    abstract static class PacoteViagem {
        // Implementação existente
    }
    
    static class PacoteAventura extends PacoteViagem {
        // Implementação existente
    }
    
    static class PacoteLuxo extends PacoteViagem {
        // Implementação existente
    }
    
    static class PacoteCultural extends PacoteViagem {
        // Implementação existente
    }
    
    static class ServicoAdicional {
        // Implementação existente
    }
    
    static class Pedido {
        // Implementação existente
    }
}
