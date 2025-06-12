package agencia.controller;

import agencia.model.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DadosController {

    private Connection obterConexao() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/agencia_viagens", "root", "ceub123456");
    }

    // --- MÉTODOS DE CLIENTE ---
    public List<Cliente> carregarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nome";
        try (Connection conn = obterConexao(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String tipo = rs.getString("tipo");
                String nome = rs.getString("nome");
                String telefone = rs.getString("telefone");
                String email = rs.getString("email");
                String documento = "NACIONAL".equals(tipo) ? rs.getString("cpf") : rs.getString("passaporte");
                
                clientes.add(new Cliente(id, nome, telefone, email, tipo, documento));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar clientes: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        }
        return clientes;
    }

    public void salvarCliente(Cliente cliente) {
        // Agora usamos uma única query SQL
        String sql = "INSERT INTO clientes (nome, telefone, email, tipo, cpf, passaporte) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = obterConexao(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, cliente.getNome());
            pstmt.setString(2, cliente.getTelefone());
            pstmt.setString(3, cliente.getEmail());
            pstmt.setString(4, cliente.getTipo());
            
            if ("NACIONAL".equals(cliente.getTipo())) {
                pstmt.setString(5, cliente.getDocumento());
                pstmt.setNull(6, Types.VARCHAR);
            } else {
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setString(6, cliente.getDocumento());
            }
            
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar cliente: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void atualizarCliente(Cliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, telefone = ?, email = ?, cpf = ?, passaporte = ? WHERE id = ?";
        try (Connection conn = obterConexao(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getNome());
            pstmt.setString(2, cliente.getTelefone());
            pstmt.setString(3, cliente.getEmail());

            if ("NACIONAL".equals(cliente.getTipo())) {
                pstmt.setString(4, cliente.getDocumento());
                pstmt.setNull(5, Types.VARCHAR);
            } else {
                pstmt.setNull(4, Types.VARCHAR);
                pstmt.setString(5, cliente.getDocumento());
            }
            
            pstmt.setInt(6, cliente.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar cliente: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void excluirCliente(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = obterConexao(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir cliente: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- MÉTODOS DE PACOTE ---
    public List<PacoteViagem> carregarPacotes() {
        List<PacoteViagem> pacotes = new ArrayList<>();
        String sql = "SELECT * FROM pacotes ORDER BY nome";
        try (Connection conn = obterConexao(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Agora sempre criamos um PacoteViagem, passando o tipo lido do banco
                pacotes.add(new PacoteViagem(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("destino"),
                    rs.getInt("duracao"),
                    rs.getDouble("preco"),
                    rs.getString("tipo")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar pacotes: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        }
        return pacotes;
    }
    
    public void salvarPacote(PacoteViagem pacote) {
        String sql = "INSERT INTO pacotes (nome, destino, duracao, preco, tipo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = obterConexao(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, pacote.getNome());
            pstmt.setString(2, pacote.getDestino());
            pstmt.setInt(3, pacote.getDuracao());
            pstmt.setDouble(4, pacote.getPreco());
            pstmt.setString(5, pacote.getTipo().toUpperCase()); // O tipo agora é um campo normal
            
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pacote.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar pacote: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void atualizarPacote(PacoteViagem pacote) {
        String sql = "UPDATE pacotes SET nome = ?, destino = ?, duracao = ?, preco = ?, tipo = ? WHERE id = ?";
        try (Connection conn = obterConexao(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pacote.getNome());
            pstmt.setString(2, pacote.getDestino());
            pstmt.setInt(3, pacote.getDuracao());
            pstmt.setDouble(4, pacote.getPreco());
            pstmt.setString(5, pacote.getTipo().toUpperCase());
            pstmt.setInt(6, pacote.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar pacote: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void excluirPacote(int id) {
        String sql = "DELETE FROM pacotes WHERE id = ?";
        try (Connection conn = obterConexao(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir pacote: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        }
    }


    // --- MÉTODOS DE SERVIÇO ---
    public List<ServicoAdicional> carregarServicos() {
        List<ServicoAdicional> servicos = new ArrayList<>();
        String sql = "SELECT * FROM servicos ORDER BY descricao";
        try (Connection conn = obterConexao(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                servicos.add(new ServicoAdicional(rs.getInt("id"), rs.getString("descricao"), rs.getDouble("preco")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar serviços: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        }
        return servicos;
    }

    public void salvarServico(ServicoAdicional servico) {
        String sql = "INSERT INTO servicos (descricao, preco) VALUES (?, ?)";
        try (Connection conn = obterConexao(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, servico.getDescricao());
            pstmt.setDouble(2, servico.getPreco());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    servico.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar serviço: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void atualizarServico(ServicoAdicional servico) {
        String sql = "UPDATE servicos SET descricao = ?, preco = ? WHERE id = ?";
        try (Connection conn = obterConexao(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, servico.getDescricao());
            pstmt.setDouble(2, servico.getPreco());
            pstmt.setInt(3, servico.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar serviço: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void excluirServico(int id) {
        String sql = "DELETE FROM servicos WHERE id = ?";
        try (Connection conn = obterConexao(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir serviço: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        }
    }


    // --- MÉTODOS DE PEDIDO ---
    public List<Pedido> carregarPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        List<Cliente> todosClientes = carregarClientes();
        List<PacoteViagem> todosPacotes = carregarPacotes();
        List<ServicoAdicional> todosServicos = carregarServicos();

        String sqlPedidos = "SELECT * FROM pedidos ORDER BY data_pedido DESC";
        try (Connection conn = obterConexao(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlPedidos)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int clienteId = rs.getInt("cliente_id");
                Date data = rs.getTimestamp("data_pedido");
                
                Cliente cliente = todosClientes.stream().filter(c -> c.getId() == clienteId).findFirst().orElse(null);
                
                if (cliente != null) {
                    Pedido pedido = new Pedido(id, cliente, data);
                    
                    String sqlPacotes = "SELECT pacote_id FROM pedido_pacotes WHERE pedido_id = ?";
                    try (PreparedStatement pstmtPacotes = conn.prepareStatement(sqlPacotes)) {
                        pstmtPacotes.setInt(1, id);
                        ResultSet rsPacotes = pstmtPacotes.executeQuery();
                        while (rsPacotes.next()) {
                            final int pacoteId = rsPacotes.getInt("pacote_id");
                            todosPacotes.stream().filter(p -> p.getId() == pacoteId).findFirst().ifPresent(pedido::adicionarPacote);
                        }
                    }

                    String sqlServicos = "SELECT servico_id FROM pedido_servicos WHERE pedido_id = ?";
                    try (PreparedStatement pstmtServicos = conn.prepareStatement(sqlServicos)) {
                        pstmtServicos.setInt(1, id);
                        ResultSet rsServicos = pstmtServicos.executeQuery();
                        while (rsServicos.next()) {
                            final int servicoId = rsServicos.getInt("servico_id");
                            todosServicos.stream().filter(s -> s.getId() == servicoId).findFirst().ifPresent(pedido::adicionarServico);
                        }
                    }
                    pedidos.add(pedido);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar pedidos: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        }
        return pedidos;
    }

    public void salvarPedido(Pedido pedido) {
        String sqlPedido = "INSERT INTO pedidos (cliente_id, data_pedido) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = obterConexao();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, pedido.getCliente().getId());
                pstmt.setTimestamp(2, new java.sql.Timestamp(pedido.getData().getTime()));
                pstmt.executeUpdate();
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) pedido.setId(generatedKeys.getInt(1));
                    else throw new SQLException("Falha ao obter o ID do pedido.");
                }
                
                String sqlPacotes = "INSERT INTO pedido_pacotes (pedido_id, pacote_id) VALUES (?, ?)";
                try (PreparedStatement pstmtPacotes = conn.prepareStatement(sqlPacotes)) {
                    for (PacoteViagem pacote : pedido.getPacotes()) {
                        pstmtPacotes.setInt(1, pedido.getId());
                        pstmtPacotes.setInt(2, pacote.getId());
                        pstmtPacotes.addBatch();
                    }
                    pstmtPacotes.executeBatch();
                }

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
                conn.commit();
            } catch(SQLException e) {
                if (conn != null) conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar pedido: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public void excluirPedido(int id) {
        String sqlPedidoPacotes = "DELETE FROM pedido_pacotes WHERE pedido_id = ?";
        String sqlPedidoServicos = "DELETE FROM pedido_servicos WHERE pedido_id = ?";
        String sqlPedido = "DELETE FROM pedidos WHERE id = ?";
        Connection conn = null;
        try {
            conn = obterConexao();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPedidoPacotes)) { pstmt.setInt(1, id); pstmt.executeUpdate(); }
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPedidoServicos)) { pstmt.setInt(1, id); pstmt.executeUpdate(); }
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPedido)) { pstmt.setInt(1, id); pstmt.executeUpdate(); }
            conn.commit();
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(null, "Erro ao excluir pedido: " + e.getMessage(), "Erro DB", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}