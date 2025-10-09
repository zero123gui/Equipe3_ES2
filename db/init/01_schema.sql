-- ======================================================================
-- ES2 - Modelo Físico (PostgreSQL) - Schema: public
-- ======================================================================

-- Garantir que estamos no schema public
SET search_path TO public;

-- ----------------------------------------------------------------------
-- DROP TABLES (para rodadas de desenvolvimento)
-- ----------------------------------------------------------------------
DROP TABLE IF EXISTS public.login CASCADE;
DROP TABLE IF EXISTS public.telefone_participante CASCADE;
DROP TABLE IF EXISTS public.ddd CASCADE;
DROP TABLE IF EXISTS public.participante CASCADE;
DROP TABLE IF EXISTS public.tipo_participante CASCADE;
DROP TABLE IF EXISTS public.endereco CASCADE;
DROP TABLE IF EXISTS public.logradouro CASCADE;
DROP TABLE IF EXISTS public.tipo_logradouro CASCADE;
DROP TABLE IF EXISTS public.bairro CASCADE;
DROP TABLE IF EXISTS public.cidade CASCADE;
DROP TABLE IF EXISTS public.unidade_federacao CASCADE;

-- ======================================================================
-- Tabelas “de apoio” (sem dependências)
-- ======================================================================

-- Unidade Federacao (UF)
CREATE TABLE public.unidade_federacao (
    idUF           INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    siglaUF        VARCHAR(255),
    nomeUF         VARCHAR(255)
);

-- Bairro
CREATE TABLE public.bairro (
    idBairro       INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nomeBairro     VARCHAR(255)
);

-- TipoLogradouro
CREATE TABLE public.tipo_logradouro (
    idTipoLogradouro INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    siglaLogradouro  VARCHAR(255)
);

-- ======================================================================
-- Tabelas que dependem das anteriores
-- ======================================================================

-- Cidade (depende de UF)
CREATE TABLE public.cidade (
    idCidade     INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nomeCidade   VARCHAR(255),
    idUF         INTEGER NOT NULL REFERENCES public.unidade_federacao(idUF)
        ON UPDATE RESTRICT ON DELETE RESTRICT
);

-- Logradouro (depende de TipoLogradouro)
CREATE TABLE public.logradouro (
    idLogradouro     INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nomeLogradouro   VARCHAR(255),
    idTipoLogradouro INTEGER NOT NULL REFERENCES public.tipo_logradouro(idTipoLogradouro)
        ON UPDATE RESTRICT ON DELETE RESTRICT
);

-- Endereco (depende de Logradouro, Bairro, Cidade)
CREATE TABLE public.endereco (
    idEndereco    INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cep           VARCHAR(255),
    idLogradouro  INTEGER NOT NULL REFERENCES public.logradouro(idLogradouro)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    idBairro      INTEGER NOT NULL REFERENCES public.bairro(idBairro)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    idCidade      INTEGER NOT NULL REFERENCES public.cidade(idCidade)
        ON UPDATE RESTRICT ON DELETE RESTRICT
);

-- TipoParticipante
-- (Ajuste: usando idTipoParticipante para casar com a FK em Participante)
CREATE TABLE public.tipo_participante (
    idTipoParticipante INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo               VARCHAR(255)
);

-- ======================================================================
-- Tabelas “core”
-- ======================================================================

-- Participante (depende de TipoParticipante e Endereco)
CREATE TABLE public.participante (
    idParticipante        INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nomeParticipante      VARCHAR(255),
    idTipoParticipante    INTEGER NOT NULL REFERENCES public.tipo_participante(idTipoParticipante)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    idEndereco            INTEGER NOT NULL REFERENCES public.endereco(idEndereco)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    complementoEndereco   VARCHAR(255),
    nroEndereco           VARCHAR(255)
);

-- DDD
CREATE TABLE public.ddd (
    idDDD   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nroDDD  INTEGER
);

-- TelefoneParticipante (depende de Participante e DDD)
CREATE TABLE public.telefone_participante (
    idTelefone     INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nroTelefone    VARCHAR(255),
    idParticipante INTEGER NOT NULL REFERENCES public.participante(idParticipante)
        ON UPDATE RESTRICT ON DELETE CASCADE,
    idDDD          INTEGER NOT NULL REFERENCES public.ddd(idDDD)
        ON UPDATE RESTRICT ON DELETE RESTRICT
);

-- Login (depende de Participante)
CREATE TABLE public.login (
    idLogin           INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    emailParticipante VARCHAR(255),
    senhaUsuario      VARCHAR(255),
    idParticipante    INTEGER NOT NULL REFERENCES public.participante(idParticipante)
        ON UPDATE RESTRICT ON DELETE CASCADE
);

-- ======================================================================
-- Índices úteis em FKs
-- ======================================================================
CREATE INDEX idx_cidade_uf           ON public.cidade(idUF);
CREATE INDEX idx_logradouro_tipo     ON public.logradouro(idTipoLogradouro);
CREATE INDEX idx_endereco_logradouro ON public.endereco(idLogradouro);
CREATE INDEX idx_endereco_bairro     ON public.endereco(idBairro);
CREATE INDEX idx_endereco_cidade     ON public.endereco(idCidade);
CREATE INDEX idx_participante_tipo   ON public.participante(idTipoParticipante);
CREATE INDEX idx_participante_end    ON public.participante(idEndereco);
CREATE INDEX idx_telefone_part_part  ON public.telefone_participante(idParticipante);
CREATE INDEX idx_telefone_part_ddd   ON public.telefone_participante(idDDD);
CREATE INDEX idx_login_part          ON public.login(idParticipante);

-- ======================================================================
-- Regras de unicidade práticas
-- ======================================================================
ALTER TABLE public.unidade_federacao  ADD CONSTRAINT uq_uf_sigla            UNIQUE (siglaUF);
ALTER TABLE public.tipo_participante  ADD CONSTRAINT uq_tipo_participante   UNIQUE (tipo);
ALTER TABLE public.tipo_logradouro    ADD CONSTRAINT uq_tipo_logradouro_sig UNIQUE (siglaLogradouro);
ALTER TABLE public.login              ADD CONSTRAINT uq_login_participante  UNIQUE (idParticipante);
ALTER TABLE public.login              ADD CONSTRAINT uq_login_email         UNIQUE (emailParticipante);