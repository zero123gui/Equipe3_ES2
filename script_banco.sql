-- ======================================================
-- SCHEMA
-- ======================================================
create schema if not exists eventos_r1s1;
set search_path = eventos_r1s1, public;

-- ======================================================
-- APOIO GEOGRÁFICO / ENDEREÇAMENTO
-- ======================================================

create table if not exists unidade_federacao (
  iduf     int generated always as identity primary key,
  siglauf  varchar(5)  not null,
  nomeuf   varchar(40) not null,
  constraint uq_uf_sigla unique (siglauf),
  constraint ck_uf_sigla_len check (char_length(siglauf) between 2 and 5)
);

create table if not exists cidade (
  idcidade   int generated always as identity primary key,
  nomecidade varchar(40) not null,
  iduf       int not null references unidade_federacao (iduf)
               on update restrict on delete restrict
);
create index if not exists idx_cidade_uf on cidade (iduf);

create table if not exists bairro (
  idbairro   int generated always as identity primary key,
  nomebairro varchar(40) not null
  -- no MER, bairro não referencia cidade (mantido)
);

create table if not exists tipo_logradouro (
  idtipologradouro int generated always as identity primary key,
  siglalogradouro  varchar(10) not null,
  constraint uq_tipo_logradouro_sig unique (siglalogradouro)
);

create table if not exists logradouro (
  idlogradouro      int generated always as identity primary key,
  nomelogradouro    varchar(40) not null,
  idtipologradouro  int not null references tipo_logradouro (idtipologradouro)
                      on update restrict on delete restrict
);
create index if not exists idx_logradouro_tipo on logradouro (idtipologradouro);

create table if not exists ddd (
  idddd   int generated always as identity primary key,
  nroddd  int not null,
  constraint uq_ddd_nro unique (nroddd),
  constraint ck_ddd_nro_range check (nroddd between 10 and 99)
);

create table if not exists endereco (
  idendereco   int generated always as identity primary key,
  cep          varchar(10) not null,
  idlogradouro int not null references logradouro (idlogradouro)
                  on update restrict on delete restrict,
  idbairro     int not null references bairro (idbairro)
                  on update restrict on delete restrict,
  idcidade     int not null references cidade (idcidade)
                  on update restrict on delete restrict
  -- atenção: possível inconsistência "bairro x cidade" (modelo do MER)
);
create index if not exists idx_endereco_logradouro on endereco (idlogradouro);
create index if not exists idx_endereco_bairro     on endereco (idbairro);
create index if not exists idx_endereco_cidade     on endereco (idcidade);

-- ======================================================
-- DOMÍNIOS DE NEGÓCIO
-- ======================================================

create table if not exists tipo_participante (
  idtipoparticipante int generated always as identity primary key,
  tipo               varchar(20) not null,
  constraint uq_tipo_participante unique (tipo)
);

create table if not exists tipo_inscricao (
  idtipoinscricao int generated always as identity primary key,
  tipo            varchar(20) not null,
  constraint uq_tipo_inscricao unique (tipo)
);

-- ======================================================
-- PARTICIPANTE / CONTATOS / LOGIN
-- ======================================================

create table if not exists participante (
  idparticipante      int generated always as identity primary key,
  nomeparticipante    varchar(80),
  idendereco          int not null references endereco (idendereco)
                         on update restrict on delete restrict,
  complementoendereco varchar(50),
  nroendereco         varchar(10),
  idtipoparticipante  int not null references tipo_participante (idtipoparticipante)
                         on update restrict on delete restrict
);
create index if not exists idx_participante_end  on participante (idendereco);
create index if not exists idx_participante_tipo on participante (idtipoparticipante);

create table if not exists telefone_participante (
  idtelefone     int generated always as identity primary key,
  nrotelefone    varchar(15) not null,
  idparticipante int not null references participante (idparticipante)
                    on update restrict on delete cascade,
  idddd          int not null references ddd (idddd)
                    on update restrict on delete restrict
);
create index if not exists idx_tel_part_part on telefone_participante (idparticipante);
create index if not exists idx_tel_part_ddd  on telefone_participante (idddd);

create table if not exists login (
  idlogin           int generated always as identity primary key,
  emailparticipante varchar(30)  not null,
  senhausuario      varchar(255) not null, -- MER-2 aumentou o limite
  idparticipante    int not null references participante (idparticipante)
                      on update restrict on delete cascade,
  constraint uq_login_participante unique (idparticipante),
  constraint uq_login_email        unique (emailparticipante)
);

-- ======================================================
-- EVENTOS E PALESTRAS
-- ======================================================

create table if not exists evento (
  idevento           int generated always as identity primary key,
  nomeevento         varchar(40)  not null,
  dtinicio           date         not null,
  dttermino          date         not null,
  descricao          varchar(100),
  urlsite            varchar(50),
  idendereco         int not null references endereco (idendereco)
                        on update restrict on delete restrict,
  nroendereco        varchar(10),
  complementoendereco varchar(50),
  constraint ck_evento_datas check (dttermino >= dtinicio)
);

create index if not exists idx_evento_endereco on evento (idendereco);

create table if not exists palestra (
  idpalestra   int generated always as identity primary key,
  nomepalestra varchar(40)  not null,
  local        varchar(100),             -- MER-2: 100
  descricao    varchar(100),
  qntdvagas    int          not null default 0,
  dtinicio     date         not null,
  dttermino    date         not null,
  horainicio   time         not null,
  horatermino  time         not null,
  idevento     int not null references evento (idevento)
                 on update restrict on delete cascade,
  constraint ck_palestra_datas check (dttermino >= dtinicio),
  constraint ck_palestra_horas check (horatermino > horainicio),
  constraint ck_palestra_vagas check (qntdvagas >= 0)
);
create index if not exists idx_palestra_evento on palestra (idevento);

-- ======================================================
-- INSCRIÇÕES E VÍNCULOS
-- ======================================================

create table if not exists participante_evento (
  idparticipanteevento int generated always as identity primary key,
  idevento             int not null references evento (idevento)
                          on update restrict on delete cascade,
  idtipoinscricao      int not null references tipo_inscricao (idtipoinscricao)
                          on update restrict on delete restrict,
  idparticipante       int not null references participante (idparticipante)
                          on update restrict on delete cascade,
  constraint uq_participante_evento unique (idevento, idparticipante)
);
create index if not exists idx_part_evento_evt  on participante_evento (idevento);
create index if not exists idx_part_evento_part on participante_evento (idparticipante);
create index if not exists idx_part_evento_tipo on participante_evento (idtipoinscricao);

create table if not exists participante_palestra (
  idparticipantepalestra int generated always as identity primary key,
  idpalestra             int not null references palestra (idpalestra)
                            on update restrict on delete cascade,
  idparticipanteevento   int not null references participante_evento (idparticipanteevento)
                            on update restrict on delete cascade,
  constraint uq_participante_palestra unique (idpalestra, idparticipanteevento)
);
create index if not exists idx_part_palestra_pal on participante_palestra (idpalestra);
create index if not exists idx_part_palestra_pev on participante_palestra (idparticipanteevento);
