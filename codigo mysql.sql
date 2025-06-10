CREATE DATABASE IF NOT EXISTS agencia_viagens;
USE agencia_viagens;

-- Tabela de clientes (ajustada para os tipos específicos do Java)
CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo ENUM('NACIONAL', 'ESTRANGEIRO') NOT NULL,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    cpf VARCHAR(14),
    passaporte VARCHAR(20)
);

-- Tabela de pacotes (com tipos específicos do Java)
CREATE TABLE IF NOT EXISTS pacotes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    destino VARCHAR(100) NOT NULL,
    duracao INT NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    tipo ENUM('Aventura', 'Luxo', 'Cultural') NOT NULL
);

-- Tabela de serviços
CREATE TABLE IF NOT EXISTS servicos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(100) NOT NULL,
    preco DECIMAL(10,2) NOT NULL
);

-- Tabela de pedidos
CREATE TABLE IF NOT EXISTS pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);

-- Tabela de relacionamento pedido-pacotes
CREATE TABLE IF NOT EXISTS pedido_pacotes (
    pedido_id INT NOT NULL,
    pacote_id INT NOT NULL,
    PRIMARY KEY (pedido_id, pacote_id),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (pacote_id) REFERENCES pacotes(id) ON DELETE CASCADE
);

-- Tabela de relacionamento pedido-serviços
CREATE TABLE IF NOT EXISTS pedido_servicos (
    pedido_id INT NOT NULL,
    servico_id INT NOT NULL,
    PRIMARY KEY (pedido_id, servico_id),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (servico_id) REFERENCES servicos(id) ON DELETE CASCADE
);

-- Adicionando índices para melhorar performance
CREATE INDEX idx_cliente_tipo ON clientes(tipo);
CREATE INDEX idx_pacote_tipo ON pacotes(tipo);
CREATE INDEX idx_pedido_data ON pedidos(data_pedido);
