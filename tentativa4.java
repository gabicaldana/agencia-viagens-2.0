package Agencia;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class AgenciaViagensGUI extends JFrame {

    private List<Cliente> clientes;
    private List<PacoteViagem> pacotes;
    private List<ServicoAdicional> servicos;
    private List<Pedido> pedidos;

    private JTable tableClientes;
    private JTable tablePacotes;
    private JTable tableServicos;
    private JTable tablePedidos;
    
    private DefaultTableModel modelClientes;
    private DefaultTableModel modelPacotes;
    private DefaultTableModel modelServicos;
    private DefaultTableModel modelPedidos;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    AgenciaViagensGUI frame = new AgenciaViagensGUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public AgenciaViagensGUI() {
        setTitle("Sistema de Agência de Viagens");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Carregar dados
        carregarDados();
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Tahoma", Font.BOLD, 12));
        
        // Aba Clientes
        JPanel panelClientes = new JPanel();
        panelClientes.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Clientes", null, panelClientes, null);
        
        JPanel panelClienteTop = new JPanel();
        panelClienteTop.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelClientes.add(panelClienteTop, BorderLayout.NORTH);
        panelClienteTop.setLayout(new GridLayout(1, 0, 10, 0));
        
        JButton btnCadastrarCliente = new JButton("Cadastrar Cliente");
        btnCadastrarCliente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarCliente();
            }
        });
        btnCadastrarCliente.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelClienteTop.add(btnCadastrarCliente);
        
        JButton btnBuscarCliente = new JButton("Buscar Cliente");
        btnBuscarCliente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarCliente();
            }
        });
        btnBuscarCliente.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelClienteTop.add(btnBuscarCliente);
        
        JButton btnPacotesCliente = new JButton("Pacotes por Cliente");
        btnPacotesCliente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarPacotesPorCliente();
            }
        });
        btnPacotesCliente.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelClienteTop.add(btnPacotesCliente);
        
        JButton btnExcluirCliente = new JButton("Excluir Cliente");
        btnExcluirCliente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                excluirCliente();
            }
        });
        btnExcluirCliente.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelClienteTop.add(btnExcluirCliente);
        
        JPanel panelClienteCenter = new JPanel();
        panelClienteCenter.setBorder(new EmptyBorder(0, 10, 10, 10));
        panelClientes.add(panelClienteCenter, BorderLayout.CENTER);
        panelClienteCenter.setLayout(new BorderLayout(0, 0));
        
        modelClientes = new DefaultTableModel();
        modelClientes.addColumn("ID");
        modelClientes.addColumn("Nome");
        modelClientes.addColumn("Telefone");
        modelClientes.addColumn("Email");
        modelClientes.addColumn("Documento");
        modelClientes.addColumn("Tipo");
        
        tableClientes = new JTable(modelClientes);
        tableClientes.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tableClientes.setRowHeight(25);
        tableClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        atualizarTabelaClientes();
        
        JScrollPane scrollPaneClientes = new JScrollPane(tableClientes);
        panelClienteCenter.add(scrollPaneClientes, BorderLayout.CENTER);
        
        // Aba Pacotes
        JPanel panelPacotes = new JPanel();
        panelPacotes.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Pacotes", null, panelPacotes, null);
        
        JPanel panelPacotesTop = new JPanel();
        panelPacotesTop.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelPacotes.add(panelPacotesTop, BorderLayout.NORTH);
        panelPacotesTop.setLayout(new GridLayout(1, 0, 10, 0));
        
        JButton btnCadastrarPacote = new JButton("Cadastrar Pacote");
        btnCadastrarPacote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarPacote();
            }
        });
        btnCadastrarPacote.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelPacotesTop.add(btnCadastrarPacote);
        
        JButton btnBuscarPacote = new JButton("Buscar Pacote");
        btnBuscarPacote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarPacote();
            }
        });
        btnBuscarPacote.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelPacotesTop.add(btnBuscarPacote);
        
        JButton btnClientesPacote = new JButton("Clientes por Pacote");
        btnClientesPacote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarClientesPorPacote();
            }
        });
        btnClientesPacote.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelPacotesTop.add(btnClientesPacote);
        
        JButton btnExcluirPacote = new JButton("Excluir Pacote");
        btnExcluirPacote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                excluirPacote();
            }
        });
        btnExcluirPacote.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelPacotesTop.add(btnExcluirPacote);
        
        JPanel panelPacotesCenter = new JPanel();
        panelPacotesCenter.setBorder(new EmptyBorder(0, 10, 10, 10));
        panelPacotes.add(panelPacotesCenter, BorderLayout.CENTER);
        panelPacotesCenter.setLayout(new BorderLayout(0, 0));
        
        modelPacotes = new DefaultTableModel();
        modelPacotes.addColumn("ID");
        modelPacotes.addColumn("Nome");
        modelPacotes.addColumn("Destino");
        modelPacotes.addColumn("Duração");
        modelPacotes.addColumn("Preço");
        modelPacotes.addColumn("Tipo");
        
        tablePacotes = new JTable(modelPacotes);
        tablePacotes.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tablePacotes.setRowHeight(25);
        tablePacotes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        atualizarTabelaPacotes();
        
        JScrollPane scrollPanePacotes = new JScrollPane(tablePacotes);
        panelPacotesCenter.add(scrollPanePacotes, BorderLayout.CENTER);
        
        // Aba Serviços
        JPanel panelServicos = new JPanel();
        panelServicos.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Serviços", null, panelServicos, null);
        
        JPanel panelServicosTop = new JPanel();
        panelServicosTop.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelServicos.add(panelServicosTop, BorderLayout.NORTH);
        panelServicosTop.setLayout(new GridLayout(1, 0, 10, 0));
        
        JButton btnCadastrarServico = new JButton("Cadastrar Serviço");
        btnCadastrarServico.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarServico();
            }
        });
        btnCadastrarServico.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelServicosTop.add(btnCadastrarServico);
        
        JButton btnBuscarServico = new JButton("Buscar Serviço");
        btnBuscarServico.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarServico();
            }
        });
        btnBuscarServico.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelServicosTop.add(btnBuscarServico);
        
        JButton btnExcluirServico = new JButton("Excluir Serviço");
        btnExcluirServico.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                excluirServico();
            }
        });
        btnExcluirServico.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelServicosTop.add(btnExcluirServico);
        
        JPanel panelServicosCenter = new JPanel();
        panelServicosCenter.setBorder(new EmptyBorder(0, 10, 10, 10));
        panelServicos.add(panelServicosCenter, BorderLayout.CENTER);
        panelServicosCenter.setLayout(new BorderLayout(0, 0));
        
        modelServicos = new DefaultTableModel();
        modelServicos.addColumn("ID");
        modelServicos.addColumn("Descrição");
        modelServicos.addColumn("Preço");
        
        tableServicos = new JTable(modelServicos);
        tableServicos.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tableServicos.setRowHeight(25);
        tableServicos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        atualizarTabelaServicos();
        
        JScrollPane scrollPaneServicos = new JScrollPane(tableServicos);
        panelServicosCenter.add(scrollPaneServicos, BorderLayout.CENTER);
        
        // Aba Pedidos
        JPanel panelPedidos = new JPanel();
        panelPedidos.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Pedidos", null, panelPedidos, null);
        
        JPanel panelPedidosTop = new JPanel();
        panelPedidosTop.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelPedidos.add(panelPedidosTop, BorderLayout.NORTH);
        panelPedidosTop.setLayout(new GridLayout(1, 0, 10, 0));
        
        JButton btnCriarPedido = new JButton("Criar Pedido");
        btnCriarPedido.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                criarPedido();
            }
        });
        btnCriarPedido.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelPedidosTop.add(btnCriarPedido);
        
        JButton btnDetalharPedido = new JButton("Detalhar Pedido");
        btnDetalharPedido.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                detalharPedido();
            }
        });
        btnDetalharPedido.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelPedidosTop.add(btnDetalharPedido);
        
        JButton btnExcluirPedido = new JButton("Excluir Pedido");
        btnExcluirPedido.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                excluirPedido();
            }
        });
        btnExcluirPedido.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelPedidosTop.add(btnExcluirPedido);
        
        JPanel panelPedidosCenter = new JPanel();
        panelPedidosCenter.setBorder(new EmptyBorder(0, 10, 10, 10));
        panelPedidos.add(panelPedidosCenter, BorderLayout.CENTER);
        panelPedidosCenter.setLayout(new BorderLayout(0, 0));
        
        modelPedidos = new DefaultTableModel();
        modelPedidos.addColumn("ID");
        modelPedidos.addColumn("Cliente");
        modelPedidos.addColumn("Data");
        modelPedidos.addColumn("Total");
        
        tablePedidos = new JTable(modelPedidos);
        tablePedidos.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tablePedidos.setRowHeight(25);
        tablePedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        atualizarTabelaPedidos();
        
        JScrollPane scrollPanePedidos = new JScrollPane(tablePedidos);
        panelPedidosCenter.add(scrollPanePedidos, BorderLayout.CENTER);
        
        // Aba Resumo
        JPanel panelResumo = new JPanel();
        panelResumo.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Resumo Geral", null, panelResumo, null);
        
        JPanel panelResumoTop = new JPanel();
        panelResumoTop.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelResumo.add(panelResumoTop, BorderLayout.NORTH);
        
        JButton btnAtualizarResumo = new JButton("Atualizar Resumo");
        btnAtualizarResumo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                atualizarResumo();
            }
        });
        btnAtualizarResumo.setFont(new Font("Tahoma", Font.BOLD, 14));
        panelResumoTop.add(btnAtualizarResumo);
        
        JPanel panelResumoCenter = new JPanel();
        panelResumoCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelResumo.add(panelResumoCenter, BorderLayout.CENTER);
        panelResumoCenter.setLayout(new BorderLayout(0, 0));
        
        JTextArea textAreaResumo = new JTextArea();
        textAreaResumo.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textAreaResumo.setEditable(false);
        textAreaResumo.setText(gerarResumoGeral());
        
        JScrollPane scrollPaneResumo = new JScrollPane(textAreaResumo);
        panelResumoCenter.add(scrollPaneResumo, BorderLayout.CENTER);
        
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void carregarDados() {
        clientes = AgenciaViagens.carregarClientes();
        pacotes = AgenciaViagens.carregarPacotes();
        servicos = AgenciaViagens.carregarServicos();
        pedidos = AgenciaViagens.carregarPedidos(clientes, pacotes, servicos);
    }
    
    private void atualizarTabelaClientes() {
        modelClientes.setRowCount(0);
        for (Cliente c : clientes) {
            modelClientes.addRow(new Object[] {
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
        modelPacotes.setRowCount(0);
        for (PacoteViagem p : pacotes) {
            modelPacotes.addRow(new Object[] {
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
        modelServicos.setRowCount(0);
        for (ServicoAdicional s : servicos) {
            modelServicos.addRow(new Object[] {
                s.getId(),
                s.getDescricao(),
                s.getPreco()
            });
        }
    }
    
    private void atualizarTabelaPedidos() {
        modelPedidos.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Pedido p : pedidos) {
            modelPedidos.addRow(new Object[] {
                p.getId(),
                p.getCliente().getResumo(),
                sdf.format(p.getData()),
                String.format("R$ %.2f", p.getTotal())
            });
        }
    }
    
    private String gerarResumoGeral() {
        if (pedidos.isEmpty()) {
            return "Nenhum pedido foi realizado ainda.";
        }

        StringBuilder resumo = new StringBuilder("Resumo Geral dos Pedidos:\n\n");
        double totalGeral = 0;
        for (Pedido p : pedidos) {
            resumo.append(p.getResumo()).append("\n");
            totalGeral += p.getTotal();
        }
        resumo.append("\nTOTAL GERAL DE VENDAS: R$").append(String.format("%.2f", totalGeral));
        return resumo.toString();
    }
    
    private void atualizarResumo() {
        carregarDados();
        atualizarTabelaClientes();
        atualizarTabelaPacotes();
        atualizarTabelaServicos();
        atualizarTabelaPedidos();
        JOptionPane.showMessageDialog(this, "Dados atualizados com sucesso!");
    }
    
    private void cadastrarCliente() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lblTitulo = new JLabel("Cadastro de Cliente");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Nome:"), gbc);
        JTextField txtNome = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtNome, gbc);
        
        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Telefone:"), gbc);
        JTextField txtTelefone = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtTelefone, gbc);
        
        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);
        JTextField txtEmail = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);
        
        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Tipo:"), gbc);
        JPanel panelTipo = new JPanel();
        ButtonGroup grupoTipo = new ButtonGroup();
        JRadioButton rbNacional = new JRadioButton("Nacional");
        JRadioButton rbEstrangeiro = new JRadioButton("Estrangeiro");
        grupoTipo.add(rbNacional);
        grupoTipo.add(rbEstrangeiro);
        rbNacional.setSelected(true);
        panelTipo.add(rbNacional);
        panelTipo.add(rbEstrangeiro);
        gbc.gridx = 1;
        panel.add(panelTipo, gbc);
        
        gbc.gridy++; gbc.gridx = 0;
        JLabel lblDocumento = new JLabel("CPF:");
        panel.add(lblDocumento, gbc);
        JTextField txtDocumento = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtDocumento, gbc);
        
        rbNacional.addActionListener(e -> lblDocumento.setText("CPF:"));
        rbEstrangeiro.addActionListener(e -> lblDocumento.setText("Passaporte:"));
        
        int resultado = JOptionPane.showConfirmDialog(this, panel, "Cadastro de Cliente", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (resultado == JOptionPane.OK_OPTION) {
            String nome = txtNome.getText().trim();
            String telefone = txtTelefone.getText().trim();
            String email = txtEmail.getText().trim();
            String documento = txtDocumento.getText().trim();
            
            if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || documento.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Cliente novo;
            if (rbNacional.isSelected()) {
                novo = new ClienteNacional(0, nome, telefone, email, documento);
            } else {
                novo = new ClienteEstrangeiro(0, nome, telefone, email, documento);
            }
            
            AgenciaViagens.salvarCliente(novo);
            clientes.add(novo);
            atualizarTabelaClientes();
            JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
        }
    }
    
    private void buscarCliente() {
        String termo = JOptionPane.showInputDialog(this, "Digite o nome, documento, email ou telefone:");
        if (termo == null || termo.isBlank()) return;
        
        List<Cliente> resultados = new ArrayList<>();
        for (Cliente c : clientes) {
            if (c.getNome().toLowerCase().contains(termo.toLowerCase()) ||
                c.getTelefone().contains(termo) ||
                c.getEmail().toLowerCase().contains(termo.toLowerCase()) ||
                c.getIdentificacao().contains(termo)) {
                resultados.add(c);
            }
        }
        
        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum cliente encontrado com o termo: " + termo);
            return;
        }
        
        // Mostrar resultados em uma nova janela
        JFrame frameResultados = new JFrame("Resultados da Busca");
        frameResultados.setSize(600, 400);
        frameResultados.setLocationRelativeTo(this);
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nome");
        model.addColumn("Telefone");
        model.addColumn("Email");
        model.addColumn("Documento");
        model.addColumn("Tipo");
        
        for (Cliente c : resultados) {
            model.addRow(new Object[] {
                c.getId(),
                c.getNome(),
                c.getTelefone(),
                c.getEmail(),
                c.getIdentificacao(),
                c.getTipoCliente()
            });
        }
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frameResultados.add(scrollPane);
        frameResultados.setVisible(true);
    }
    
    private void excluirCliente() {
        int linha = tableClientes.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela!");
            return;
        }
        
        int id = (int) modelClientes.getValueAt(linha, 0);
        Cliente cliente = null;
        for (Cliente c : clientes) {
            if (c.getId() == id) {
                cliente = c;
                break;
            }
        }
        
        if (cliente == null) return;
        
        // Verificar se o cliente está em algum pedido
        boolean emPedido = false;
        for (Pedido p : pedidos) {
            if (p.getCliente().equals(cliente)) {
                emPedido = true;
                break;
            }
        }
        
        if (emPedido) {
            JOptionPane.showMessageDialog(this, 
                "Não é possível excluir o cliente pois ele está associado a um pedido!",
                "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja excluir o cliente " + cliente.getNome() + "?",
                "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            AgenciaViagens.excluirCliente(id);
            clientes.remove(cliente);
            atualizarTabelaClientes();
            JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
        }
    }
    
    private void mostrarPacotesPorCliente() {
        int linha = tableClientes.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela!");
            return;
        }
        
        int id = (int) modelClientes.getValueAt(linha, 0);
        Cliente cliente = null;
        for (Cliente c : clientes) {
            if (c.getId() == id) {
                cliente = c;
                break;
            }
        }
        
        if (cliente == null) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("Pacotes contratados por ").append(cliente.getResumo()).append(":\n\n");
        boolean encontrou = false;
        for (Pedido p : pedidos) {
            if (p.getCliente().equals(cliente)) {
                encontrou = true;
                for (PacoteViagem pacote : p.getPacotes()) {
                    sb.append(" - ").append(pacote.getResumo()).append("\n");
                }
            }
        }
        
        if (!encontrou) {
            sb.append("Nenhum pacote contratado por este cliente.");
        }
        
        JOptionPane.showMessageDialog(this, sb.toString());
    }
    
    private void cadastrarPacote() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lblTitulo = new JLabel("Cadastro de Pacote");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Nome:"), gbc);
        JTextField txtNome = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtNome, gbc);
        
        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Destino:"), gbc);
        JTextField txtDestino = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtDestino, gbc);
        
        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Duração (dias):"), gbc);
        JTextField txtDuracao = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtDuracao, gbc);
        
        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Preço (R$):"), gbc);
        JTextField txtPreco = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtPreco, gbc);
        
        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Tipo:"), gbc);
        JComboBox<String> cmbTipo = new JComboBox<>();
        cmbTipo.addItem("Aventura");
        cmbTipo.addItem("Luxo");
        cmbTipo.addItem("Cultural");
        gbc.gridx = 1;
        panel.add(cmbTipo, gbc);
        
        int resultado = JOptionPane.showConfirmDialog(this, panel, "Cadastro de Pacote", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (resultado == JOptionPane.OK_OPTION) {
            String nome = txtNome.getText().trim();
            String destino = txtDestino.getText().trim();
            String strDuracao = txtDuracao.getText().trim();
            String strPreco = txtPreco.getText().trim();
            String tipo = (String) cmbTipo.getSelectedItem();
            
            if (nome.isEmpty() || destino.isEmpty() || strDuracao.isEmpty() || strPreco.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                int duracao = Integer.parseInt(strDuracao);
                double preco = Double.parseDouble(strPreco);
                
                PacoteViagem novoPacote;
                switch (tipo) {
                    case "Aventura":
                        novoPacote = new PacoteAventura(0, nome, destino, duracao, preco);
                        break;
                    case "Luxo":
                        novoPacote = new PacoteLuxo(0, nome, destino, duracao, preco);
                        break;
                    case "Cultural":
                        novoPacote = new PacoteCultural(0, nome, destino, duracao, preco);
                        break;
                    default:
                        return;
                }
                
                AgenciaViagens.salvarPacote(novoPacote);
                pacotes.add(novoPacote);
                atualizarTabelaPacotes();
                JOptionPane.showMessageDialog(this, "Pacote cadastrado com sucesso!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Duração e Preço devem ser números válidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarPacote() {
        String termo = JOptionPane.showInputDialog(this, "Digite o nome, destino ou tipo:");
        if (termo == null || termo.isBlank()) return;
        
        List<PacoteViagem> resultados = new ArrayList<>();
        for (PacoteViagem p : pacotes) {
            if (p.getNome().toLowerCase().contains(termo.toLowerCase()) ||
                p.getDestino().toLowerCase().contains(termo.toLowerCase()) ||
                p.getTipo().toLowerCase().contains(termo.toLowerCase())) {
                resultados.add(p);
            }
        }
        
        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum pacote encontrado com o termo: " + termo);
            return;
        }
        
        // Mostrar resultados em uma nova janela
        JFrame frameResultados = new JFrame("Resultados da Busca");
        frameResultados.setSize(600, 400);
        frameResultados.setLocationRelativeTo(this);
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nome");
        model.addColumn("Destino");
        model.addColumn("Duração");
        model.addColumn("Preço");
        model.addColumn("Tipo");
        
        for (PacoteViagem p : resultados) {
            model.addRow(new Object[] {
                p.getId(),
                p.getNome(),
                p.getDestino(),
                p.getDuracao(),
                p.getPreco(),
                p.getTipo()
            });
        }
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frameResultados.add(scrollPane);
        frameResultados.setVisible(true);
    }
    
    private void excluirPacote() {
        int linha = tablePacotes.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote na tabela!");
            return;
        }
        
        int id = (int) modelPacotes.getValueAt(linha, 0);
        PacoteViagem pacote = null;
        for (PacoteViagem p : pacotes) {
            if (p.getId() == id) {
                pacote = p;
                break;
            }
        }
        
        if (pacote == null) return;
        
        // Verificar se o pacote está em algum pedido
        boolean emPedido = false;
        for (Pedido pedido : pedidos) {
            for (PacoteViagem p : pedido.getPacotes()) {
                if (p.equals(pacote)) {
                    emPedido = true;
                    break;
                }
            }
            if (emPedido) break;
        }
        
        if (emPedido) {
            JOptionPane.showMessageDialog(this, 
                "Não é possível excluir o pacote pois ele está associado a um pedido!",
                "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja excluir o pacote " + pacote.getNome() + "?",
                "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            AgenciaViagens.excluirPacote(id);
            pacotes.remove(pacote);
            atualizarTabelaPacotes();
            JOptionPane.showMessageDialog(this, "Pacote excluído com sucesso!");
        }
    }
    
    private void mostrarClientesPorPacote() {
        int linha = tablePacotes.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote na tabela!");
            return;
        }
        
        int id = (int) modelPacotes.getValueAt(linha, 0);
        PacoteViagem pacote = null;
        for (PacoteViagem p : pacotes) {
            if (p.getId() == id) {
                pacote = p;
                break;
            }
        }
        
        if (pacote == null) return;
        
        Set<Cliente> clientesEncontrados = new HashSet<>();
        for (Pedido p : pedidos) {
            if (p.getPacotes().contains(pacote)) {
                clientesEncontrados.add(p.getCliente());
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Clientes que contrataram ").append(pacote.getResumo()).append(":\n\n");
        
        if (clientesEncontrados.isEmpty()) {
            sb.append("Nenhum cliente contratou este pacote.");
        } else {
            for (Cliente c : clientesEncontrados) {
                sb.append(" - ").append(c.getResumo()).append("\n");
            }
        }
        
        JOptionPane.showMessageDialog(this, sb.toString());
    }
    
    private void cadastrarServico() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lblTitulo = new JLabel("Cadastro de Serviço");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Descrição:"), gbc);
        JTextField txtDescricao = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtDescricao, gbc);
        
        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Preço (R$):"), gbc);
        JTextField txtPreco = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtPreco, gbc);
        
        int resultado = JOptionPane.showConfirmDialog(this, panel, "Cadastro de Serviço", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (resultado == JOptionPane.OK_OPTION) {
            String descricao = txtDescricao.getText().trim();
            String strPreco = txtPreco.getText().trim();
            
            if (descricao.isEmpty() || strPreco.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                double preco = Double.parseDouble(strPreco);
                ServicoAdicional novoServico = new ServicoAdicional(0, descricao, preco);
                AgenciaViagens.salvarServico(novoServico);
                servicos.add(novoServico);
                atualizarTabelaServicos();
                JOptionPane.showMessageDialog(this, "Serviço cadastrado com sucesso!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Preço deve ser um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarServico() {
        String termo = JOptionPane.showInputDialog(this, "Digite a descrição do serviço:");
        if (termo == null || termo.isBlank()) return;
        
        List<ServicoAdicional> resultados = new ArrayList<>();
        for (ServicoAdicional s : servicos) {
            if (s.getDescricao().toLowerCase().contains(termo.toLowerCase())) {
                resultados.add(s);
            }
        }
        
        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum serviço encontrado com o termo: " + termo);
            return;
        }
        
        // Mostrar resultados em uma nova janela
        JFrame frameResultados = new JFrame("Resultados da Busca");
        frameResultados.setSize(400, 300);
        frameResultados.setLocationRelativeTo(this);
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Descrição");
        model.addColumn("Preço");
        
        for (ServicoAdicional s : resultados) {
            model.addRow(new Object[] {
                s.getId(),
                s.getDescricao(),
                s.getPreco()
            });
        }
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frameResultados.add(scrollPane);
        frameResultados.setVisible(true);
    }
    
    private void excluirServico() {
        int linha = tableServicos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um serviço na tabela!");
            return;
        }
        
        int id = (int) modelServicos.getValueAt(linha, 0);
        ServicoAdicional servico = null;
        for (ServicoAdicional s : servicos) {
            if (s.getId() == id) {
                servico = s;
                break;
            }
        }
        
        if (servico == null) return;
        
        // Verificar se o serviço está em algum pedido
        boolean emPedido = false;
        for (Pedido p : pedidos) {
            for (ServicoAdicional s : p.getServicos()) {
                if (s.equals(servico)) {
                    emPedido = true;
                    break;
                }
            }
            if (emPedido) break;
        }
        
        if (emPedido) {
            JOptionPane.showMessageDialog(this, 
                "Não é possível excluir o serviço pois ele está associado a um pedido!",
                "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja excluir o serviço " + servico.getDescricao() + "?",
                "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            AgenciaViagens.excluirServico(id);
            servicos.remove(servico);
            atualizarTabelaServicos();
            JOptionPane.showMessageDialog(this, "Serviço excluído com sucesso!");
        }
    }
    
    private void criarPedido() {
        if (clientes.isEmpty() || pacotes.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Cadastre pelo menos um cliente e um pacote antes de criar um pedido!",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Seleção de cliente
        JPanel panelCliente = new JPanel(new BorderLayout());
        panelCliente.setBorder(BorderFactory.createTitledBorder("Selecione o Cliente"));
        DefaultComboBoxModel<Cliente> modelClientes = new DefaultComboBoxModel<>();
        for (Cliente c : clientes) {
            modelClientes.addElement(c);
        }
        JComboBox<Cliente> cmbClientes = new JComboBox<>(modelClientes);
        panelCliente.add(cmbClientes, BorderLayout.CENTER);
        panel.add(panelCliente, BorderLayout.NORTH);
        
        // Seleção de pacotes
        JPanel panelPacotes = new JPanel(new BorderLayout());
        panelPacotes.setBorder(BorderFactory.createTitledBorder("Selecione os Pacotes"));
        
        DefaultListModel<PacoteViagem> listModelPacotes = new DefaultListModel<>();
        for (PacoteViagem p : pacotes) {
            listModelPacotes.addElement(p);
        }
        
        JList<PacoteViagem> listPacotes = new JList<>(listModelPacotes);
        listPacotes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listPacotes.setVisibleRowCount(5);
        JScrollPane scrollPacotes = new JScrollPane(listPacotes);
        panelPacotes.add(scrollPacotes, BorderLayout.CENTER);
        
        // Seleção de serviços
        JPanel panelServicos = new JPanel(new BorderLayout());
        panelServicos.setBorder(BorderFactory.createTitledBorder("Selecione os Serviços Adicionais (Opcional)"));
        
        DefaultListModel<ServicoAdicional> listModelServicos = new DefaultListModel<>();
        for (ServicoAdicional s : servicos) {
            listModelServicos.addElement(s);
        }
        
        JList<ServicoAdicional> listServicos = new JList<>(listModelServicos);
        listServicos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listServicos.setVisibleRowCount(3);
        JScrollPane scrollServicos = new JScrollPane(listServicos);
        panelServicos.add(scrollServicos, BorderLayout.CENTER);
        
        JPanel panelCentro = new JPanel(new GridLayout(2, 1));
        panelCentro.add(panelPacotes);
        panelCentro.add(panelServicos);
        panel.add(panelCentro, BorderLayout.CENTER);
        
        int resultado = JOptionPane.showConfirmDialog(this, panel, "Criar Pedido", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (resultado == JOptionPane.OK_OPTION) {
            Cliente cliente = (Cliente) cmbClientes.getSelectedItem();
            List<PacoteViagem> pacotesSelecionados = listPacotes.getSelectedValuesList();
            List<ServicoAdicional> servicosSelecionados = listServicos.getSelectedValuesList();
            
            if (pacotesSelecionados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione pelo menos um pacote!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Pedido novoPedido = new Pedido(0, cliente, new Date());
            for (PacoteViagem p : pacotesSelecionados) {
                novoPedido.adicionarPacote(p);
            }
            for (ServicoAdicional s : servicosSelecionados) {
                novoPedido.adicionarServico(s);
            }
            
            AgenciaViagens.salvarPedido(novoPedido);
            pedidos.add(novoPedido);
            atualizarTabelaPedidos();
            
            JOptionPane.showMessageDialog(this, 
                "Pedido criado com sucesso!\n\n" + novoPedido.getResumo(),
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void detalharPedido() {
        int linha = tablePedidos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela!");
            return;
        }
        
        int id = (int) modelPedidos.getValueAt(linha, 0);
        Pedido pedido = null;
        for (Pedido p : pedidos) {
            if (p.getId() == id) {
                pedido = p;
                break;
            }
        }
        
        if (pedido == null) return;
        
        JOptionPane.showMessageDialog(this, pedido.getResumo(), "Detalhes do Pedido", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void excluirPedido() {
        int linha = tablePedidos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela!");
            return;
        }
        
        int id = (int) modelPedidos.getValueAt(linha, 0);
        Pedido pedido = null;
        for (Pedido p : pedidos) {
            if (p.getId() == id) {
                pedido = p;
                break;
            }
        }
        
        if (pedido == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja excluir o pedido #" + pedido.getId() + "?",
                "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            AgenciaViagens.excluirPedido(id);
            pedidos.remove(pedido);
            atualizarTabelaPedidos();
            JOptionPane.showMessageDialog(this, "Pedido excluído com sucesso!");
        }
    }
}
