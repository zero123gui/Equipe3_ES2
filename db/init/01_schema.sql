-- ======================================================
-- ZERA TUDO (DESTRUTIVO)
-- ======================================================
DROP SCHEMA eventos_r1s1 CASCADE;
CREATE SCHEMA eventos_r1s1;
SET search_path = eventos_r1s1, public;

-- ======================================================
-- APOIO GEOGRÁFICO / ENDEREÇAMENTO
-- ======================================================

CREATE TABLE unidade_federacao (
  iduf     INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  siglauf  VARCHAR(5)  NOT NULL,
  nomeuf   VARCHAR(40) NOT NULL,
  CONSTRAINT uq_uf_sigla UNIQUE (siglauf),
  CONSTRAINT ck_uf_sigla_len CHECK (char_length(siglauf) BETWEEN 2 AND 5)
);

CREATE TABLE cidade (
  idcidade   INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nomecidade VARCHAR(40) NOT NULL,
  iduf       INT NOT NULL REFERENCES unidade_federacao (iduf)
               ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX idx_cidade_uf ON cidade (iduf);

CREATE TABLE bairro (
  idbairro   INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nomebairro VARCHAR(40) NOT NULL
  -- no MER, bairro não referencia cidade (mantido)
);

CREATE TABLE tipo_logradouro (
  idtipologradouro INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  siglalogradouro  VARCHAR(10) NOT NULL,
  CONSTRAINT uq_tipo_logradouro_sig UNIQUE (siglalogradouro)
);

CREATE TABLE logradouro (
  idlogradouro      INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nomelogradouro    VARCHAR(40) NOT NULL,
  idtipologradouro  INT NOT NULL REFERENCES tipo_logradouro (idtipologradouro)
                      ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX idx_logradouro_tipo ON logradouro (idtipologradouro);

CREATE TABLE ddd (
  idddd   INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nroddd  INT NOT NULL,
  CONSTRAINT uq_ddd_nro UNIQUE (nroddd),
  CONSTRAINT ck_ddd_nro_range CHECK (nroddd BETWEEN 10 AND 99)
);

CREATE TABLE endereco (
  idendereco   INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  cep          VARCHAR(10) NOT NULL,
  idlogradouro INT NOT NULL REFERENCES logradouro (idlogradouro)
                  ON UPDATE RESTRICT ON DELETE RESTRICT,
  idbairro     INT NOT NULL REFERENCES bairro (idbairro)
                  ON UPDATE RESTRICT ON DELETE RESTRICT,
  idcidade     INT NOT NULL REFERENCES cidade (idcidade)
                  ON UPDATE RESTRICT ON DELETE RESTRICT
  -- atenção: possível inconsistência "bairro x cidade" (modelo do MER)
);
CREATE INDEX idx_endereco_logradouro ON endereco (idlogradouro);
CREATE INDEX idx_endereco_bairro     ON endereco (idbairro);
CREATE INDEX idx_endereco_cidade     ON endereco (idcidade);

-- ======================================================
-- DOMÍNIOS DE NEGÓCIO
-- ======================================================

CREATE TABLE tipo_participante (
  idtipoparticipante INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  tipo               VARCHAR(20) NOT NULL,
  CONSTRAINT uq_tipo_participante UNIQUE (tipo)
);

CREATE TABLE tipo_inscricao (
  idtipoinscricao INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  tipo            VARCHAR(20) NOT NULL,
  CONSTRAINT uq_tipo_inscricao UNIQUE (tipo)
);

-- ======================================================
-- PARTICIPANTE / CONTATOS / LOGIN
-- ======================================================

CREATE TABLE participante (
  idparticipante      INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nomeparticipante    VARCHAR(80),
  idendereco          INT NOT NULL REFERENCES endereco (idendereco)
                         ON UPDATE RESTRICT ON DELETE RESTRICT,
  complementoendereco VARCHAR(50),
  nroendereco         VARCHAR(10),
  idtipoparticipante  INT NOT NULL REFERENCES tipo_participante (idtipoparticipante)
                         ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX idx_participante_end  ON participante (idendereco);
CREATE INDEX idx_participante_tipo ON participante (idtipoparticipante);

CREATE TABLE telefone_participante (
  idtelefone     INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nrotelefone    VARCHAR(15) NOT NULL,
  idparticipante INT NOT NULL REFERENCES participante (idparticipante)
                    ON UPDATE RESTRICT ON DELETE CASCADE,
  idddd          INT NOT NULL REFERENCES ddd (idddd)
                    ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX idx_tel_part_part ON telefone_participante (idparticipante);
CREATE INDEX idx_tel_part_ddd  ON telefone_participante (idddd);

CREATE TABLE login (
  idlogin           INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  emailparticipante VARCHAR(30)  NOT NULL,
  senhausuario      VARCHAR(255) NOT NULL,
  idparticipante    INT NOT NULL REFERENCES participante (idparticipante)
                      ON UPDATE RESTRICT ON DELETE CASCADE,
  CONSTRAINT uq_login_participante UNIQUE (idparticipante),
  CONSTRAINT uq_login_email        UNIQUE (emailparticipante)
);

-- ======================================================
-- EVENTOS E PALESTRAS
-- ======================================================

CREATE TABLE evento (
  idevento            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nomeevento          VARCHAR(40)  NOT NULL,
  dtinicio            DATE         NOT NULL,
  dttermino           DATE         NOT NULL,
  descricao           VARCHAR(100),
  urlsite             VARCHAR(50),
  idendereco          INT NOT NULL REFERENCES endereco (idendereco)
                        ON UPDATE RESTRICT ON DELETE RESTRICT,
  nroendereco         VARCHAR(10),
  complementoendereco VARCHAR(50),
  CONSTRAINT ck_evento_datas CHECK (dttermino >= dtinicio)
);
CREATE INDEX idx_evento_endereco ON evento (idendereco);

CREATE TABLE palestra (
  idpalestra   INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nomepalestra VARCHAR(40)  NOT NULL,
  local        VARCHAR(100),
  descricao    VARCHAR(100),
  qntdvagas    INT          NOT NULL DEFAULT 0,
  dtinicio     DATE         NOT NULL,
  dttermino    DATE         NOT NULL,
  horainicio   TIME         NOT NULL,
  horatermino  TIME         NOT NULL,
  idevento     INT NOT NULL REFERENCES evento (idevento)
                 ON UPDATE RESTRICT ON DELETE CASCADE,
  CONSTRAINT ck_palestra_datas CHECK (dttermino >= dtinicio),
  CONSTRAINT ck_palestra_horas CHECK (horatermino > horainicio),
  CONSTRAINT ck_palestra_vagas CHECK (qntdvagas >= 0)
);
CREATE INDEX idx_palestra_evento ON palestra (idevento);

-- ======================================================
-- INSCRIÇÕES E VÍNCULOS
-- ======================================================

CREATE TABLE participante_evento (
  idparticipanteevento INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  idevento             INT NOT NULL REFERENCES evento (idevento)
                          ON UPDATE RESTRICT ON DELETE CASCADE,
  idtipoinscricao      INT NOT NULL REFERENCES tipo_inscricao (idtipoinscricao)
                          ON UPDATE RESTRICT ON DELETE RESTRICT,
  idparticipante       INT NOT NULL REFERENCES participante (idparticipante)
                          ON UPDATE RESTRICT ON DELETE CASCADE,
  CONSTRAINT uq_participante_evento UNIQUE (idevento, idparticipante)
);
CREATE INDEX idx_part_evento_evt  ON participante_evento (idevento);
CREATE INDEX idx_part_evento_part ON participante_evento (idparticipante);
CREATE INDEX idx_part_evento_tipo ON participante_evento (idtipoinscricao);

CREATE TABLE participante_palestra (
  idparticipantepalestra INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  idpalestra             INT NOT NULL REFERENCES palestra (idpalestra)
                            ON UPDATE RESTRICT ON DELETE CASCADE,
  idparticipanteevento   INT NOT NULL REFERENCES participante_evento (idparticipanteevento)
                            ON UPDATE RESTRICT ON DELETE CASCADE,
  CONSTRAINT uq_participante_palestra UNIQUE (idpalestra, idparticipanteevento)
);
CREATE INDEX idx_part_palestra_pal ON participante_palestra (idpalestra);
CREATE INDEX idx_part_palestra_pev ON participante_palestra (idparticipanteevento);
