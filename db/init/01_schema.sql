-- === Schema ===
CREATE SCHEMA IF NOT EXISTS eventos_r1s1;
SET search_path = eventos_r1s1, public;

-- =======================================================
-- Tabelas de apoio geográfico / endereçamento
-- =======================================================

CREATE TABLE unidade_federacao (
  id_uf               INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  sigla_uf            VARCHAR(5)  NOT NULL,
  nome_uf             VARCHAR(40) NOT NULL,
  CONSTRAINT uq_unidade_federacao_sigla UNIQUE (sigla_uf),
  CONSTRAINT ck_unidade_federacao_sigla_len CHECK (char_length(sigla_uf) BETWEEN 2 AND 5)
);

CREATE TABLE cidade (
  id_cidade           INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nome_cidade         VARCHAR(40) NOT NULL,
  id_uf               INT NOT NULL REFERENCES unidade_federacao (id_uf) ON DELETE RESTRICT
);

CREATE INDEX ix_cidade_uf ON cidade (id_uf);

CREATE TABLE bairro (
  id_bairro           INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nome_bairro         VARCHAR(40) NOT NULL
  -- OBS: no MER o Bairro não referencia Cidade; mantido como está (ver observações).
);

CREATE TABLE tipo_logradouro (
  id_tipo_logradouro  INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  sigla_logradouro    VARCHAR(10) NOT NULL
);

CREATE TABLE logradouro (
  id_logradouro       INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nome_logradouro     VARCHAR(40) NOT NULL,
  id_tipo_logradouro  INT NOT NULL REFERENCES tipo_logradouro (id_tipo_logradouro) ON DELETE RESTRICT
);

CREATE INDEX ix_logradouro_tipo ON logradouro (id_tipo_logradouro);

CREATE TABLE ddd (
  id_ddd              INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nro_ddd             INT NOT NULL,
  CONSTRAINT uq_ddd_nro UNIQUE (nro_ddd),
  CONSTRAINT ck_ddd_nro_range CHECK (nro_ddd BETWEEN 10 AND 99)
);

CREATE TABLE endereco (
  id_endereco         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  cep                 VARCHAR(10) NOT NULL,
  id_logradouro       INT NOT NULL REFERENCES logradouro (id_logradouro) ON DELETE RESTRICT,
  id_bairro           INT NOT NULL REFERENCES bairro (id_bairro) ON DELETE RESTRICT,
  id_cidade           INT NOT NULL REFERENCES cidade (id_cidade) ON DELETE RESTRICT
  -- OBS: possível inconsistência Bairro x Cidade; ver observações.
);

CREATE INDEX ix_endereco_logradouro ON endereco (id_logradouro);
CREATE INDEX ix_endereco_bairro     ON endereco (id_bairro);
CREATE INDEX ix_endereco_cidade     ON endereco (id_cidade);

-- =======================================================
-- Cadastros de domínio do negócio
-- =======================================================

CREATE TABLE tipo_participante (
  id_tipo_participante INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  tipo                 VARCHAR(20) NOT NULL,
  CONSTRAINT uq_tipo_participante UNIQUE (tipo)
);

CREATE TABLE tipo_inscricao (
  id_tipo_inscricao   INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  tipo                VARCHAR(20) NOT NULL,
  CONSTRAINT uq_tipo_inscricao UNIQUE (tipo)
);

-- =======================================================
-- Núcleo: Participante, contatos e credenciais
-- =======================================================

CREATE TABLE participante (
  id_participante         INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nome_participante       VARCHAR(80)  NOT NULL,
  complemento_endereco    VARCHAR(50),
  nro_endereco            VARCHAR(10),
  id_endereco             INT NOT NULL REFERENCES endereco (id_endereco) ON DELETE RESTRICT,
  id_tipo_participante    INT NOT NULL REFERENCES tipo_participante (id_tipo_participante) ON DELETE RESTRICT
);

CREATE INDEX ix_participante_endereco       ON participante (id_endereco);
CREATE INDEX ix_participante_tipo           ON participante (id_tipo_participante);

CREATE TABLE telefone_participante (
  id_telefone        INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nro_telefone       VARCHAR(15) NOT NULL,
  id_participante    INT NOT NULL REFERENCES participante (id_participante) ON DELETE CASCADE,
  id_ddd             INT NOT NULL REFERENCES ddd (id_ddd) ON DELETE RESTRICT
);

CREATE INDEX ix_tel_participante_part       ON telefone_participante (id_participante);
CREATE INDEX ix_tel_participante_ddd        ON telefone_participante (id_ddd);

CREATE TABLE login (
  id_login           INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  email_participante VARCHAR(30)  NOT NULL,
  senha_usuario      VARCHAR(15)  NOT NULL,
  id_participante    INT NOT NULL UNIQUE REFERENCES participante (id_participante) ON DELETE CASCADE,
  CONSTRAINT uq_login_email UNIQUE (email_participante)
  -- Em produção: armazenar hash (tamanho maior) e políticas de senha.
);

-- =======================================================
-- Núcleo: Eventos e Palestras
-- =======================================================

CREATE TABLE evento (
  id_evento          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nome_evento        VARCHAR(40)  NOT NULL,
  dt_inicio          DATE         NOT NULL,
  dt_termino         DATE         NOT NULL,
  local              VARCHAR(40),
  descricao          VARCHAR(100),
  url_site           VARCHAR(50),
  CONSTRAINT ck_evento_datas CHECK (dt_termino >= dt_inicio)
);

CREATE TABLE palestra (
  id_palestra        INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nome_palestra      VARCHAR(40)  NOT NULL,
  local              VARCHAR(40),
  descricao          VARCHAR(100),
  qntd_vagas         INT          NOT NULL DEFAULT 0,
  dt_inicio          DATE         NOT NULL,
  dt_termino         DATE         NOT NULL,
  hora_inicio        TIME         NOT NULL,
  hora_termino       TIME         NOT NULL,
  id_evento          INT NOT NULL REFERENCES evento (id_evento) ON DELETE CASCADE,
  CONSTRAINT ck_palestra_datas CHECK (dt_termino >= dt_inicio),
  CONSTRAINT ck_palestra_horas CHECK (hora_termino > hora_inicio),
  CONSTRAINT ck_palestra_vagas CHECK (qntd_vagas >= 0)
);

CREATE INDEX ix_palestra_evento ON palestra (id_evento);

-- =======================================================
-- Inscrições e vínculo com palestras
-- =======================================================

CREATE TABLE participante_evento (
  id_participante_evento INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  id_evento              INT NOT NULL REFERENCES evento (id_evento) ON DELETE CASCADE,
  id_tipo_inscricao      INT NOT NULL REFERENCES tipo_inscricao (id_tipo_inscricao) ON DELETE RESTRICT,
  id_participante        INT NOT NULL REFERENCES participante (id_participante) ON DELETE CASCADE,
  CONSTRAINT uq_participante_evento UNIQUE (id_evento, id_participante)
);

CREATE INDEX ix_part_evento_evento       ON participante_evento (id_evento);
CREATE INDEX ix_part_evento_participante ON participante_evento (id_participante);
CREATE INDEX ix_part_evento_tipo         ON participante_evento (id_tipo_inscricao);

CREATE TABLE participante_palestra (
  id_participante_palestra INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  id_palestra              INT NOT NULL REFERENCES palestra (id_palestra) ON DELETE CASCADE,
  id_participante_evento   INT NOT NULL REFERENCES participante_evento (id_participante_evento) ON DELETE CASCADE,
  CONSTRAINT uq_participante_palestra UNIQUE (id_palestra, id_participante_evento)
);

CREATE INDEX ix_part_palestra_palestra ON participante_palestra (id_palestra);
CREATE INDEX ix_part_palestra_pevento  ON participante_palestra (id_participante_evento);

-- =======================================================
-- (Opcional) Views e futuras regras/validações
-- - Ex.: trigger para controlar lotação de qntd_vagas
-- - Ex.: view com agenda de palestras por evento
-- =======================================================
