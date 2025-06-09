//Altere a senha do banco de dados na linha 270!
package Agencia;

import java.util.*;
import javax.swing.*;
import java.sql.*;
import java.util.Date;

abstract class Cliente {
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

class ClienteNacional extends Cliente {
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

class ClienteEstrangeiro extends Cliente {
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

abstract class PacoteViagem {
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

class PacoteAventura extends PacoteViagem {
    public PacoteAventura(int id, String nome, String destino, int duracao, double preco) {
        super(id, nome, destino, duracao, preco, "Aventura");
    }
}

class PacoteLuxo extends PacoteViagem {
    public PacoteLuxo(int id, String nome, String destino, int duracao, double preco) {
        super(id, nome, destino, duracao, preco, "Luxo");
    }
}

class PacoteCultural extends PacoteViagem {
    public PacoteCultural(int id, String nome, String destino, int duracao, double preco) {
        super(id, nome, destino, duracao, preco, "Cultural");
    }
}

class ServicoAdicional {
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

class Pedido {
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
        for (PacoteViagem p : pacotes) total += p.getPreco();
        for (ServicoAdicional s : servicos) total += s.getPreco();
        return total;
    }

    public List<PacoteViagem> getPacotes() {
        return pacotes;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public List<ServicoAdicional> getServicos() {
        return servicos;
    }
    
    public int getId() {
        return id;
    }
    
    public Date getData() {
        return data;
    }
    
    @Override
    public String toString() {
        return cliente.getNome() + " - " + 
               pacotes.size() + " pacote(s), " + 
               servicos.size() + " serviço(s) - Total: R$" + 
               String.format("%.2f", getTotal());
    }
}

public class AgenciaViagens {
    private static final String URL = "jdbc:mysql://localhost:3306/agencia_viagens";
    private static final String USUARIO = "root";
    private static final String SENHA = "sua senha";
    
    private static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
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
    
    private static void salvarPedido(Pedido pedido) {
        String sqlPedido = "INSERT INTO pedidos (cliente_id) VALUES (?)";
        
        try (Connection conn = obterConexao();
             PreparedStatement pstmt = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, pedido.getCliente().getId());
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

    public static void main(String[] args) {
        // Carregar dados do banco
        List<Cliente> clientes = carregarClientes();
        List<PacoteViagem> pacotes = carregarPacotes();
        List<ServicoAdicional> servicos = carregarServicos();
        List<Pedido> pedidos = carregarPedidos(clientes, pacotes, servicos);

        while (true) {
            String[] opcoes = {"Clientes", "Pacotes", "Serviços", "Pedidos", "Resumo Geral", "Sair"};
            int opcao = JOptionPane.showOptionDialog(null, "Escolha uma opção:", "Agência de Viagens",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

            if (opcao == 0) menuClientes(clientes, pedidos);
            else if (opcao == 1) menuPacotes(pacotes, pedidos);
            else if (opcao == 2) menuServicos(servicos);
            else if (opcao == 3) menuPedidos(clientes, pacotes, servicos, pedidos);
            else if (opcao == 4) mostrarResumoGeral(pedidos);
            else break;
        }
    }

    private static void menuClientes(List<Cliente> clientes, List<Pedido> pedidos) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Buscar", "Excluir", "Pacotes por Cliente", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Clientes:", "Clientes",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) cadastrarCliente(clientes);
        else if (escolha == 1) listar(clientes.stream().map(Cliente::getResumo).toList(), "Clientes");
        else if (escolha == 2) buscarCliente(clientes);
        else if (escolha == 3) excluirItem(clientes, "cliente", null);
        else if (escolha == 4) mostrarPacotesPorCliente(clientes, pedidos);
    }
    
    private static void menuPacotes(List<PacoteViagem> pacotes, List<Pedido> pedidos) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Buscar", "Excluir", "Clientes por Pacote", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Pacotes:", "Pacotes",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) cadastrarPacote(pacotes);
        else if (escolha == 1) listar(pacotes.stream().map(PacoteViagem::getResumo).toList(), "Pacotes");
        else if (escolha == 2) buscarPacote(pacotes);
        else if (escolha == 3) excluirItem(pacotes, "pacote", pedidos);
        else if (escolha == 4) mostrarClientesPorPacote(pedidos);
    }

    private static void menuServicos(List<ServicoAdicional> servicos) {
        String[] opcoes = {"Cadastrar", "Visualizar", "Buscar", "Excluir", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Serviços:", "Serviços",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) cadastrarServico(servicos);
        else if (escolha == 1) listar(servicos.stream().map(ServicoAdicional::getResumo).toList(), "Serviços");
        else if (escolha == 2) buscarServico(servicos);
        else if (escolha == 3) excluirItem(servicos, "serviço", null);
    }

    private static void menuPedidos(List<Cliente> clientes, List<PacoteViagem> pacotes, 
            List<ServicoAdicional> servicos, List<Pedido> pedidos) {
        String[] opcoes = {"Criar Pedido", "Visualizar Pedidos", "Excluir Pedido", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null, "Menu de Pedidos:", "Pedidos",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) criarPedido(clientes, pacotes, servicos, pedidos);
        else if (escolha == 1) listarPedidos(pedidos);
        else if (escolha == 2) excluirItem(pedidos, "pedido", pedidos);
    }

    private static void cadastrarCliente(List<Cliente> clientes) {
        String nome;
        while (true) {
            nome = JOptionPane.showInputDialog("Nome:");
            if (nome == null) return; 
            if (nome.isBlank()) {
                JOptionPane.showMessageDialog(null, "Nome obrigatório.", "Cadastro", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        String telefone;
        while (true) {
            telefone = JOptionPane.showInputDialog("Telefone:");
            if (telefone == null) return; 
            if (!telefone.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "O número de telefone deve conter apenas números.", "Cadastro", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        String email;
        while (true) {
            email = JOptionPane.showInputDialog("Email:");
            if (email == null) return; 
            if (email.isBlank()) {
                JOptionPane.showMessageDialog(null, "Email obrigatório.", "Cadastro", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        String[] tipos = {"Nacional", "Estrangeiro"};
        int tipo = JOptionPane.showOptionDialog(null, "Tipo de Cliente:", "Tipo",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, tipos, tipos[0]);
        if (tipo == -1) return;

        String id;
        while (true) {
            id = JOptionPane.showInputDialog(tipo == 0 ? "CPF:" : "Passaporte:");
            if (id == null) return; 
            if (!id.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "Documento deve conter apenas números.", "Cadastro", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        Cliente novo = tipo == 0 ? new ClienteNacional(0, nome, telefone, email, id)
                                 : new ClienteEstrangeiro(0, nome, telefone, email, id);
        salvarCliente(novo);
        clientes.add(novo);
        JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!");
    }

    private static void cadastrarPacote(List<PacoteViagem> pacotes) {
        String nome;
        while (true) {
            nome = JOptionPane.showInputDialog("Nome do pacote:");
            if (nome == null) return;
            if (nome.isBlank()) {
                JOptionPane.showMessageDialog(null, "O pacote deve ter um nome.", "Pacote", JOptionPane.ERROR_MESSAGE);
            } else {
                 break;
            }
        }
        
        String destino;
        while (true) {
            destino = JOptionPane.showInputDialog("Destino:");
            if (destino == null) return;
            if (destino.isBlank()) {
                JOptionPane.showMessageDialog(null, "O pacote deve conter um destino.", "Pacote", JOptionPane.ERROR_MESSAGE);
            } else {
                 break;
            }
        }
        
        String entradaDuracao;
        int duracao;
        while (true) {
            entradaDuracao = JOptionPane.showInputDialog("Duração (dias):");
            if (entradaDuracao == null) return;
            if (entradaDuracao.isBlank()) {
                JOptionPane.showMessageDialog(null, "O pacote deve conter uma duração.", "Pacote", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    duracao = Integer.parseInt(entradaDuracao);
                    break;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Digite um número válido para a duração.", "Pacote", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        String entradaPreco;
        double preco;
        while (true) {
            entradaPreco = JOptionPane.showInputDialog("Preço (R$):");
            if (entradaPreco == null) return;
            if (entradaPreco.isBlank()) {
                JOptionPane.showMessageDialog(null, "O pacote deve conter um preço.", "Pacote", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    preco = Double.parseDouble(entradaPreco);
                    break;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Digite um número válido para o preço.", "Pacote", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        int tipo = JOptionPane.showOptionDialog(null, "Tipo de Pacote:", "Tipo",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                new String[]{"Aventura", "Luxo", "Cultural"}, "Aventura");

        if (tipo == -1) return;

        PacoteViagem novoPacote;
        switch (tipo) {
            case 0 -> novoPacote = new PacoteAventura(0, nome, destino, duracao, preco);
            case 1 -> novoPacote = new PacoteLuxo(0, nome, destino, duracao, preco);
            case 2 -> novoPacote = new PacoteCultural(0, nome, destino, duracao, preco);
            default -> { return; }
        }

        salvarPacote(novoPacote);
        pacotes.add(novoPacote);
        JOptionPane.showMessageDialog(null, "Pacote cadastrado com sucesso!");
    }

    private static void cadastrarServico(List<ServicoAdicional> servicos) {
        String desc;
        while (true) {
            desc = JOptionPane.showInputDialog("Descrição:");
            if (desc == null) return;
            if (desc.isBlank()) {
                JOptionPane.showMessageDialog(null, "O serviço deve conter uma descrição.", "Serviços", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        String entradaPreco;
        double preco;
        while (true) {
            entradaPreco = JOptionPane.showInputDialog("Preço (R$):");
            if (entradaPreco == null) return;
            if (entradaPreco.isBlank()) {
                JOptionPane.showMessageDialog(null, "O serviço deve conter um preço.", "Serviços", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    preco = Double.parseDouble(entradaPreco);
                    break;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Digite um número válido para o preço.", "Serviços", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        ServicoAdicional novoServico = new ServicoAdicional(0, desc, preco);
        salvarServico(novoServico);
        servicos.add(novoServico);
        JOptionPane.showMessageDialog(null, "Serviço cadastrado com sucesso!");
    }

    private static void criarPedido(List<Cliente> clientes, List<PacoteViagem> pacotes, 
            List<ServicoAdicional> servicos, List<Pedido> pedidos) {
    	if (clientes.isEmpty() || pacotes.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Cadastre pelo menos um cliente e um pacote antes.");
    		return;
    	}

    	String[] nomesClientes = clientes.stream().map(Cliente::getResumo).toArray(String[]::new);
    	int idxCliente = JOptionPane.showOptionDialog(null, "Escolha o cliente:", "Cliente",
    			JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, nomesClientes, nomesClientes[0]);
    	if (idxCliente == -1) return;

    	Pedido pedido = new Pedido(0, clientes.get(idxCliente), new Date());

    	while (true) {
    		String[] nomesPacotes = pacotes.stream().map(PacoteViagem::getResumo).toArray(String[]::new);
    		int idxPacote = JOptionPane.showOptionDialog(null, "Escolha um pacote:", "Pacote",
    				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, nomesPacotes, nomesPacotes[0]);
    		if (idxPacote == -1) break;
    		pedido.adicionarPacote(pacotes.get(idxPacote));

    		int mais = JOptionPane.showConfirmDialog(null, "Deseja adicionar outro pacote?");
    		if (mais != JOptionPane.YES_OPTION) break;
    	}

    	if (!servicos.isEmpty()) {
    		int adicionarServ = JOptionPane.showConfirmDialog(null, "Deseja adicionar serviços adicionais?");
    		if (adicionarServ == JOptionPane.YES_OPTION) {
    			while (true) {
    				String[] nomesServicos = servicos.stream().map(ServicoAdicional::getResumo).toArray(String[]::new);
    				int idxServ = JOptionPane.showOptionDialog(null, "Escolha um serviço:", "Serviço",
    						JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, nomesServicos, nomesServicos[0]);
    				if (idxServ == -1) break;
    				pedido.adicionarServico(servicos.get(idxServ));
    				
    				int mais = JOptionPane.showConfirmDialog(null, "Deseja adicionar outro serviço?");
    				if (mais != JOptionPane.YES_OPTION) break;
    			}
    		}
    	}

    	salvarPedido(pedido);
    	pedidos.add(pedido);
    	JOptionPane.showMessageDialog(null, "Pedido criado com sucesso!\n\n" + pedido.getResumo());
    }

    private static void mostrarResumoGeral(List<Pedido> pedidos) {
    	if (pedidos.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Nenhum pedido foi realizado ainda.");
    		return;
    	}

    	StringBuilder resumo = new StringBuilder("Resumo Geral dos Pedidos:\n\n");
    	double totalGeral = 0;
    	for (Pedido p : pedidos) {
    		resumo.append(p.getResumo()).append("\n");
    		totalGeral += p.getTotal();
    	}
    	resumo.append("TOTAL GERAL DE VENDAS: R$").append(String.format("%.2f", totalGeral));
    	JOptionPane.showMessageDialog(null, resumo.toString());
    }

    private static void listarPedidos(List<Pedido> pedidos) {
    	if (pedidos.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Nenhum pedido foi realizado ainda.");
    		return;
    	}

    	StringBuilder sb = new StringBuilder();
    	for (Pedido p : pedidos) {
    		sb.append(p.getResumo()).append("\n");
    	}
    	JOptionPane.showMessageDialog(null, sb.toString(), "Pedidos", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void listar(List<String> itens, String titulo) {
    	if (itens.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Nada cadastrado.", titulo, JOptionPane.WARNING_MESSAGE);
    		return;
    	}
    	StringBuilder sb = new StringBuilder();
    	for (String item : itens) sb.append(item).append("\n");
    	JOptionPane.showMessageDialog(null, sb.toString(), titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private static void excluirItem(List<?> lista, String tipo, List<Pedido> pedidos) {
    	if (lista.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Nenhum " + tipo + " para excluir.", null, JOptionPane.WARNING_MESSAGE);
    		return;
    	}

    	String[] nomes = lista.stream().map(Object::toString).toArray(String[]::new);
    	int idx = JOptionPane.showOptionDialog(null, "Excluir qual " + tipo + "?", "Excluir",
    			JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);

    	if (idx >= 0) {
    		if (tipo.equals("pacote") && pedidos != null) {
    			PacoteViagem pacote = (PacoteViagem) lista.get(idx);
    			boolean pacoteEmUso = false;

    			for (Pedido pedido : pedidos) {
    				for (PacoteViagem p : pedido.getPacotes()) {
    					if (p.equals(pacote)) {
    						pacoteEmUso = true;
    						break;
    					}
    				}
    				if (pacoteEmUso) break;
    			}

    			if (pacoteEmUso) {
    				JOptionPane.showMessageDialog(null, 
    						"Não é possível excluir este pacote pois ele está associado a um ou mais pedidos.", 
    						"Erro", JOptionPane.ERROR_MESSAGE);
    				return;
    			}
    		}

    		if (tipo.equals("cliente")) {
    			excluirCliente(((Cliente)lista.get(idx)).getId());
    		} else if (tipo.equals("pacote")) {
    			excluirPacote(((PacoteViagem)lista.get(idx)).getId());
    		} else if (tipo.equals("serviço")) {
    			excluirServico(((ServicoAdicional)lista.get(idx)).getId());
    		} else if (tipo.equals("pedido")) {
    			excluirPedido(((Pedido)lista.get(idx)).getId());
    		}

    		lista.remove(idx);
    		JOptionPane.showMessageDialog(null, tipo + " removido.");
    	}
    	}

    private static void buscarCliente(List<Cliente> clientes) {
    	if (clientes.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado.");
    		return;
    	}

    	String termo = JOptionPane.showInputDialog("Digite o nome, telefone, email ou documento do cliente:");
    	if (termo == null || termo.isBlank()) return;

    	List<Cliente> resultados = new ArrayList<>();
    	for (Cliente c : clientes) {
    		if (c.getNome().toLowerCase().contains(termo.toLowerCase())) {
    			resultados.add(c);
    			continue;
    		}
    		if (c.getTelefone().contains(termo)) {
    			resultados.add(c);
    			continue;
    		}
    		if (c.getEmail().toLowerCase().contains(termo.toLowerCase())) {
    			resultados.add(c);
    			continue;
    		}
    		if (c.getIdentificacao().contains(termo)) {
    			resultados.add(c);
    		}
    	}

    	if (resultados.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Nenhum cliente encontrado com o termo: " + termo);
    	} else {
    		listar(resultados.stream().map(Cliente::getResumo).toList(), "Resultados da Busca");
    	}
    }

    private static void buscarPacote(List<PacoteViagem> pacotes) {
    	if (pacotes.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Nenhum pacote cadastrado.");
    		return;
    	}

    	String termo = JOptionPane.showInputDialog("Digite o nome, destino ou tipo do pacote:");
    	if (termo == null || termo.isBlank()) return;

    		List<PacoteViagem> resultados = new ArrayList<>();
    		for (PacoteViagem p : pacotes) {
    			if (p.getNome().toLowerCase().contains(termo.toLowerCase())) {
    				resultados.add(p);
    				continue;
    			}
    			if (p.getDestino().toLowerCase().contains(termo.toLowerCase())) {
    				resultados.add(p);
    				continue;
    			}
    			if (p.getTipo().toLowerCase().contains(termo.toLowerCase())) {
    				resultados.add(p);
    			}
    		}

    		if (resultados.isEmpty()) {
    			JOptionPane.showMessageDialog(null, "Nenhum pacote encontrado com o termo: " + termo);
    		} else {
    			listar(resultados.stream().map(PacoteViagem::getResumo).toList(), "Resultados da Busca");
    		}
    }

    private static void buscarServico(List<ServicoAdicional> servicos) {
    	if (servicos.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Nenhum serviço cadastrado.");
    		return;
    	}

    	String termo = JOptionPane.showInputDialog("Digite a descrição do serviço:");
    	if (termo == null || termo.isBlank()) return;

    	List<ServicoAdicional> resultados = new ArrayList<>();
    	for (ServicoAdicional s : servicos) {
    		if (s.getDescricao().toLowerCase().contains(termo.toLowerCase())) {
    			resultados.add(s);
    		}
    	}

    	if (resultados.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Nenhum serviço encontrado com o termo: " + termo);
    	} else {
    		listar(resultados.stream().map(ServicoAdicional::getResumo).toList(), "Resultados da Busca");
    	}
    }

    private static void mostrarPacotesPorCliente(List<Cliente> clientes, List<Pedido> pedidos) {
    	if (clientes.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Nenhum cliente cadastrado.");
    		return;
    	}

    	if (pedidos.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Nenhum pedido foi realizado ainda.");
    		return;
    	}

    	String[] nomesClientes = clientes.stream().map(Cliente::getResumo).toArray(String[]::new);
    	int idxCliente = JOptionPane.showOptionDialog(null, "Escolha o cliente:", "Pacotes por Cliente",
    			JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, nomesClientes, nomesClientes[0]);
    	if (idxCliente == -1) return;

    	Cliente clienteSelecionado = clientes.get(idxCliente);
    	StringBuilder sb = new StringBuilder("Pacotes contratados por " + clienteSelecionado.getResumo() + ":\n\n");

    	boolean encontrou = false;
    	for (Pedido p : pedidos) {
    		if (p.getCliente().equals(clienteSelecionado)) {
    			encontrou = true;
    			for (PacoteViagem pacote : p.getPacotes()) {
    				sb.append(" - ").append(pacote.getResumo()).append("\n");
    			}
    		}
    	}

    	if (!encontrou) {
    		sb.append("Nenhum pacote contratado por este cliente.");
    	}

    	JOptionPane.showMessageDialog(null, sb.toString());
    }

    private static void mostrarClientesPorPacote(List<Pedido> pedidos) {
    	if (pedidos.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Nenhum pedido foi realizado ainda.");
    		return;
    	}

    	Set<PacoteViagem> todosPacotes = new HashSet<>();
    	for (Pedido p : pedidos) {
    		todosPacotes.addAll(p.getPacotes());
    	}

    	if (todosPacotes.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Nenhum pacote foi contratado ainda.");
    		return;
    	}

    	String[] nomesPacotes = todosPacotes.stream().map(PacoteViagem::getResumo).toArray(String[]::new);
    	int idxPacote = JOptionPane.showOptionDialog(null, "Escolha o pacote:", "Clientes por Pacote",
    			JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, nomesPacotes, nomesPacotes[0]);
    	if (idxPacote == -1) return;

    	PacoteViagem pacoteSelecionado = todosPacotes.toArray(new PacoteViagem[0])[idxPacote];
    	StringBuilder sb = new StringBuilder("Clientes que contrataram " + pacoteSelecionado.getResumo() + ":\n\n");

    	Set<Cliente> clientesEncontrados = new HashSet<>();
    	for (Pedido p : pedidos) {
    		if (p.getPacotes().contains(pacoteSelecionado)) {
    			clientesEncontrados.add(p.getCliente());
    		}
    	}

    	if (clientesEncontrados.isEmpty()) {
    		sb.append("Nenhum cliente contratou este pacote.");
    	} else {
    		for (Cliente c : clientesEncontrados) {
    			sb.append(" - ").append(c.getResumo()).append("\n");
    		}
    	}

    	JOptionPane.showMessageDialog(null, sb.toString());
    }
}
