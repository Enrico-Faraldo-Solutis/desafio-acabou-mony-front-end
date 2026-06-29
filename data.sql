-- Criação do Banco de Dados Único
CREATE DATABASE IF NOT EXISTS db_acabou_mony;
USE db_acabou_mony;

-- 1. TABELA: USUARIOS
CREATE TABLE usuarios (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nome VARCHAR(100) NOT NULL,
                          email VARCHAR(100) NOT NULL UNIQUE,
                          cpf VARCHAR(14) NOT NULL UNIQUE,
                          senha_hash VARCHAR(255) NOT NULL,
                          data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. TABELA: CODIGOS_2FA
CREATE TABLE codigos_2fa (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             usuario_id BIGINT NOT NULL,
                             codigo VARCHAR(6) NOT NULL,
                             data_expiracao DATETIME NOT NULL,
                             usado BOOLEAN DEFAULT FALSE,
                             data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_usuario_2fa FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 3. TABELA: CONTAS
CREATE TABLE contas (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        usuario_id BIGINT NOT NULL,
                        saldo DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
                        status ENUM('ATIVA', 'BLOQUEADA', 'ENCERRADA') DEFAULT 'ATIVA',
                        data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_usuario_conta FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 4. TABELA: TRANSACOES
CREATE TABLE transacoes (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            conta_origem_id BIGINT NOT NULL,
                            conta_destino_id BIGINT NOT NULL,
                            valor DECIMAL(15, 2) NOT NULL,
                            status ENUM('PENDENTE', 'CONCLUIDA', 'FALHA') DEFAULT 'PENDENTE',
                            tipo ENUM('DEBITO', 'CREDITO', 'TRANSFERENCIA') NOT NULL,
                            data_transacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_conta_origem FOREIGN KEY (conta_origem_id) REFERENCES contas(id),
                            CONSTRAINT fk_conta_destino FOREIGN KEY (conta_destino_id) REFERENCES contas(id)
);

-- [NOVO] 5. TABELA: CARTOES
-- Gerencia os cartões associados às contas
CREATE TABLE cartoes (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         conta_id BIGINT NOT NULL,
                         numero_criptografado VARCHAR(255) NOT NULL,
                         cvc_criptografado VARCHAR(255) NOT NULL,
                         data_validade DATE NOT NULL,
                         status ENUM('ATIVO', 'BLOQUEADO', 'CANCELADO') DEFAULT 'ATIVO',
                         tipo ENUM('FISICO', 'VIRTUAL') NOT NULL,
                         data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_conta_cartao FOREIGN KEY (conta_id) REFERENCES contas(id) ON DELETE CASCADE
);

-- [NOVO] 6. TABELA: AUDIT_LOGS
-- Registra todas as ações críticas para auditoria síncrona
CREATE TABLE audit_logs (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            usuario_id BIGINT,
                            acao VARCHAR(100) NOT NULL,
                            entidade_nome VARCHAR(100) NOT NULL,
                            entidade_id BIGINT NOT NULL,
                            detalhes JSON,
                            ip_origem VARCHAR(45),
                            timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_usuario_audit FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- [Anteriormente Identificado] 7. TABELA: NOTIFICACOES
CREATE TABLE notificacoes (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              usuario_id BIGINT NOT NULL,
                              mensagem TEXT NOT NULL,
                              lida BOOLEAN DEFAULT FALSE,
                              data_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT fk_usuario_notificacao FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);