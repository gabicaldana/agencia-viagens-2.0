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
    // ... (código anterior mantido igual)

    // Métodos para operações de Pacotes
    private void adicionarPacote() {
        JPanel panel = new JPanel(new GridLayout(5, 2));
        
        JTextField nomeField = new JTextField();
        JTextField destinoField = new JTextField();
        JTextField duracaoField = new JTextField();
        JTextField precoField = new JTextField();
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"Aventura", "Luxo", "Cultural"});
        
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Destino:"));
        panel.add(destinoField);
        panel.add(new JLabel("Duração (dias):"));
        panel.add(duracaoField);
        panel.add(new JLabel("Preço (R$):"));
        panel.add(precoField);
        panel.add(new JLabel("Tipo:"));
        panel.add(tipoCombo);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Novo Pacote", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String nome = nomeField.getText().trim();
                String destino = destinoField.getText().trim();
                int duracao = Integer.parseInt(duracaoField.getText().trim());
                double preco = Double.parseDouble(precoField.getText().trim());
                String tipo = (String) tipoCombo.getSelectedItem();
                
                if (nome.isEmpty() || destino.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Pacote adicionado com sucesso!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Duração e preço devem ser números válidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
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
        
        JPanel panel = new JPanel(new GridLayout(5, 2));
        
        JTextField nomeField = new JTextField(pacote.getNome());
        JTextField destinoField = new JTextField(pacote.getDestino());
        JTextField duracaoField = new JTextField(String.valueOf(pacote.getDuracao()));
        JTextField precoField = new JTextField(String.valueOf(pacote.getPreco()));
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"Aventura", "Luxo", "Cultural"});
        tipoCombo.setSelectedItem(pacote.getTipo());
        
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Destino:"));
        panel.add(destinoField);
        panel.add(new JLabel("Duração (dias):"));
        panel.add(duracaoField);
        panel.add(new JLabel("Preço (R$):"));
        panel.add(precoField);
        panel.add(new JLabel("Tipo:"));
        panel.add(tipoCombo);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Pacote", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String nome = nomeField.getText().trim();
                String destino = destinoField.getText().trim();
                int duracao = Integer.parseInt(duracaoField.getText().trim());
                double preco = Double.parseDouble(precoField.getText().trim());
                String tipo = (String) tipoCombo.getSelectedItem();
                
                if (nome.isEmpty() || destino.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Atualizar pacote
                pacote.nome = nome;
                pacote.destino = destino;
                pacote.duracao = duracao;
                pacote.preco = preco;
                pacote.tipo = tipo;
                
                // Atualizar no banco
                excluirPacote(pacote.getId());
                salvarPacote(pacote);
                
                atualizarTabelaPacotes();
                JOptionPane.showMessageDialog(this, "Pacote atualizado com sucesso!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Duração e preço devem ser números válidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void excluirPacote() {
        int selectedRow = pacotesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote para excluir", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) pacotesModel.getValueAt(selectedRow, 0);
        
        // Verificar se o pacote está em algum pedido
        boolean emUso = pedidos.stream()
            .anyMatch(p -> p.getPacotes().stream().anyMatch(pac -> pac.getId() == id));
        
        if (emUso) {
            JOptionPane.showMessageDialog(this, "Este pacote está em uso e não pode ser excluído!", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este pacote?", 
                "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            excluirPacote(id);
            pacotes.removeIf(p -> p.getId() == id);
            atualizarTabelaPacotes();
            JOptionPane.showMessageDialog(this, "Pacote excluído com sucesso!");
        }
    }

    // Métodos para operações de Serviços
    private void adicionarServico() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        
        JTextField descricaoField = new JTextField();
        JTextField precoField = new JTextField();
        
        panel.add(new JLabel("Descrição:"));
        panel.add(descricaoField);
        panel.add(new JLabel("Preço (R$):"));
        panel.add(precoField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Novo Serviço", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String descricao = descricaoField.getText().trim();
                double preco = Double.parseDouble(precoField.getText().trim());
                
                if (descricao.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                ServicoAdicional novo = new ServicoAdicional(0, descricao, preco);
                salvarServico(novo);
                servicos.add(novo);
                atualizarTabelaServicos();
                JOptionPane.showMessageDialog(this, "Serviço adicionado com sucesso!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Preço deve ser um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
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
        
        JPanel panel = new JPanel(new GridLayout(2, 2));
        
        JTextField descricaoField = new JTextField(servico.getDescricao());
        JTextField precoField = new JTextField(String.valueOf(servico.getPreco()));
        
        panel.add(new JLabel("Descrição:"));
        panel.add(descricaoField);
        panel.add(new JLabel("Preço (R$):"));
        panel.add(precoField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Serviço", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String descricao = descricaoField.getText().trim();
                double preco = Double.parseDouble(precoField.getText().trim());
                
                if (descricao.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Atualizar serviço
                servico.descricao = descricao;
                servico.preco = preco;
                
                // Atualizar no banco
                excluirServico(servico.getId());
                salvarServico(servico);
                
                atualizarTabelaServicos();
                JOptionPane.showMessageDialog(this, "Serviço atualizado com sucesso!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Preço deve ser um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void excluirServico() {
        int selectedRow = servicosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço para excluir", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) servicosModel.getValueAt(selectedRow, 0);
        
        // Verificar se o serviço está em algum pedido
        boolean emUso = pedidos.stream()
            .anyMatch(p -> p.getServicos().stream().anyMatch(serv -> serv.getId() == id));
        
        if (emUso) {
            JOptionPane.showMessageDialog(this, "Este serviço está em uso e não pode ser excluído!", 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este serviço?", 
                "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            excluirServico(id);
            servicos.removeIf(s -> s.getId() == id);
            atualizarTabelaServicos();
            JOptionPane.showMessageDialog(this, "Serviço excluído com sucesso!");
        }
    }

    // Métodos de banco de dados (implementação completa)
    private static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/agencia_viagens", "root", "sua senha");
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
            JOptionPane.showMessageDialog(null, "Erro ao carregar clientes: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Erro ao carregar pacotes: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Erro ao carregar serviços: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Erro ao carregar pedidos: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Erro ao salvar cliente: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Erro ao atualizar cliente: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void excluirCliente(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir cliente: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Erro ao salvar pacote: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void excluirPacote(int id) {
        String sql = "DELETE FROM pacotes WHERE id = ?";
        
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir pacote: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Erro ao salvar serviço: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void excluirServico(int id) {
        String sql = "DELETE FROM servicos WHERE id = ?";
        
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir serviço: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Erro ao salvar pedido: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Erro ao excluir pedido: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Classes internas (mantidas da versão anterior)
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

        public String getResumo() {
            return nome + " (" + getIdentificacao() + ")";
        }
        
        public String getNome() {
            return nome;
        }
        
        public String getTelefone() {
            return telefone;
        }
        
        public String getEmail() {
            return email;
        }
        
        public int getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return getResumo();
        }
    }
    
    static class ClienteNacional extends Cliente {
        private String cpf;

        public ClienteNacional(int id, String nome, String telefone, String email, String cpf) {
            super(id, nome, telefone, email);
            this.cpf = cpf;
        }

        public String getIdentificacao() {
            return cpf;
        }
        
        public String getTipoCliente() {
            return "NACIONAL";
        }
    }
    
    static class ClienteEstrangeiro extends Cliente {
        private String passaporte;

        public ClienteEstrangeiro(int id, String nome, String telefone, String email, String passaporte) {
            super(id, nome, telefone, email);
            this.passaporte = passaporte;
        }

        public String getIdentificacao() {
            return passaporte;
        }
        
        public String getTipoCliente() {
            return "ESTRANGEIRO";
        }
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

        public double getPreco() {
            return preco;
        }

        public String getResumo() {
            return nome + " - " + tipo + " (" + destino + ", " + duracao + " dias, R$" + preco + ")";
        }
        
        public String getNome() {
            return nome;
        }
        
        public String getDestino() {
            return destino;
        }
        
        public int getDuracao() {
            return duracao;
        }
        
        public String getTipo() {
            return tipo;
        }
        
        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return getResumo();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            PacoteViagem that = (PacoteViagem) obj;
            return duracao == that.duracao &&
                    Double.compare(that.preco, preco) == 0 &&
                    nome.equals(that.nome) &&
                    destino.equals(that.destino) &&
                    tipo.equals(that.tipo);
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
        int id;
        private String descricao;
        private double preco;

        public ServicoAdicional(int id, String descricao, double preco) {
            this.id = id;
            this.descricao = descricao;
            this.preco = preco;
        }

        public double getPreco() {
            return preco;
        }

        public String getResumo() {
            return descricao + " (R$" + preco + ")";
        }
        
        public String getDescricao() {
            return descricao;
        }
        
        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return getResumo();
        }
    }
    
    static class Pedido {
        int id;
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
            for (PacoteViagem
