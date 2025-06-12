package agencia.model;

public class Cliente {

    private int id;
    private String nome;
    private String telefone;
    private String email;
    private String tipo; // "NACIONAL" ou "ESTRANGEIRO"
    private String documento; // Armazena CPF ou Passaporte

    // Construtor principal
    public Cliente(int id, String nome, String telefone, String email, String tipo, String documento) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.tipo = tipo;
        this.documento = documento;
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getTipo() { return tipo; }
    public String getDocumento() { return documento; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public void setEmail(String email) { this.email = email; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setDocumento(String documento) { this.documento = documento; }
    
    @Override
    public String toString() {
        return nome + " (" + documento + ")";
    }
}