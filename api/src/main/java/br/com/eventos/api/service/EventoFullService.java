// src/main/java/br/com/eventos/api/service/EventoFullService.java
package br.com.eventos.api.service;

import br.com.eventos.api.domain.*;
import br.com.eventos.api.dto.*;
import br.com.eventos.api.repo.*;
import br.com.eventos.api.security.SecurityService; // <-- precisa existir no seu projeto
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

// IMPORTS do Spring Security
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@Service
public class EventoFullService {

    private final UnidadeFederacaoRepository ufRepo;
    private final CidadeRepository cidadeRepo;
    private final BairroRepository bairroRepo;
    private final TipoLogradouroRepository tipoLogRepo;
    private final LogradouroRepository logradouroRepo;
    private final EnderecoRepository enderecoRepo;
    private final EventoRepository eventoRepo;
    private final SecurityService security; // <-- INJETADO

    public EventoFullService(UnidadeFederacaoRepository ufRepo,
                             CidadeRepository cidadeRepo,
                             BairroRepository bairroRepo,
                             TipoLogradouroRepository tipoLogRepo,
                             LogradouroRepository logradouroRepo,
                             EnderecoRepository enderecoRepo,
                             EventoRepository eventoRepo,
                             SecurityService security) {          // <-- adiciona no construtor
        this.ufRepo = ufRepo;
        this.cidadeRepo = cidadeRepo;
        this.bairroRepo = bairroRepo;
        this.tipoLogRepo = tipoLogRepo;
        this.logradouroRepo = logradouroRepo;
        this.enderecoRepo = enderecoRepo;
        this.eventoRepo = eventoRepo;
        this.security = security;
    }

    // --- Admin gate ---
    private void requireAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sem autenticação");
        }
        Object principal = auth.getPrincipal();
        String email = (principal instanceof String) ? (String) principal : null;
        if (email == null || "anonymousUser".equals(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sem autenticação");
        }
        if (!security.isAdmin(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas admin");
        }
    }

    // mapeia prefixos de logradouro para a sigla que existe em tipo_logradouro
    private static final Map<String, String> MAP_TIPO = Map.of(
            "RUA", "R", "R", "R",
            "AVENIDA", "Av", "AV", "Av",
            "RODOVIA", "Rod", "ROD", "Rod",
            "TRAVESSA", "Trav", "TRAV", "Trav"
    );

    private static class ParsedLogradouro {
        final String sigla;  // "R", "Av", ...
        final String nome;   // "Brasil", "Paulista" ...
        ParsedLogradouro(String sigla, String nome) { this.sigla = sigla; this.nome = nome; }
    }

    private ParsedLogradouro parseLogradouro(String raw) {
        if (raw == null || raw.isBlank()) return new ParsedLogradouro("R", "Desconhecido");
        String s = raw.trim();
        String[] parts = s.split("\\s+", 2);
        String head = parts[0].replace(".", "").toUpperCase();
        String sigla = MAP_TIPO.getOrDefault(head, "R");
        String nome = (MAP_TIPO.containsKey(head) && parts.length > 1) ? parts[1] : s;
        return new ParsedLogradouro(sigla, nome);
    }

    @Transactional
    public EventoFullCreateResponse criar(EventoFullCreateDto dto) {
        requireAdmin(); // <-- só admin cria

        if (dto.dtTermino().isBefore(dto.dtInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dtTermino deve ser >= dtInicio");
        }

        // 1) UF
        String ufSigla = dto.endereco().uf();
        var uf = ufRepo.findBySiglaUf(ufSigla).orElseGet(() -> {
            var u = new UnidadeFederacao();
            u.setSiglaUf(ufSigla);
            u.setNomeUf(ufSigla);
            return ufRepo.save(u);
        });

        // 2) Cidade
        String cidadeNome = dto.endereco().localidade();
        var cidade = cidadeRepo.findByNomeCidadeAndIdUf(cidadeNome, uf.getId()).orElseGet(() -> {
            var c = new Cidade();
            c.setNomeCidade(cidadeNome);
            c.setIdUf(uf.getId());
            return cidadeRepo.save(c);
        });

        // 3) Bairro
        String bairroNome = dto.endereco().bairro();
        var bairro = bairroRepo.findByNomeBairro(bairroNome).orElseGet(() -> {
            var b = new Bairro();
            b.setNomeBairro(bairroNome);
            return bairroRepo.save(b);
        });

        // 4) Tipo + Logradouro
        var parsed = parseLogradouro(dto.endereco().logradouro());
        var tipoLog = tipoLogRepo.findBySiglaLogradouro(parsed.sigla).orElseGet(() -> {
            var t = new TipoLogradouro();
            t.setSiglaLogradouro(parsed.sigla);
            return tipoLogRepo.save(t);
        });

        var logradouro = logradouroRepo
                .findByNomeLogradouroAndIdTipoLogradouro(parsed.nome, tipoLog.getId())
                .orElseGet(() -> {
                    var l = new Logradouro();
                    l.setNomeLogradouro(parsed.nome);
                    l.setIdTipoLogradouro(tipoLog.getId());
                    return logradouroRepo.save(l);
                });

        // 5) Endereco
        var endereco = enderecoRepo
                .findByCepAndIdLogradouroAndIdBairroAndIdCidade(
                        dto.endereco().cep(),
                        logradouro.getId(),
                        bairro.getId(),
                        cidade.getId()
                ).orElseGet(() -> {
                    var e = new Endereco();
                    e.setCep(dto.endereco().cep());
                    e.setIdLogradouro(logradouro.getId());
                    e.setIdBairro(bairro.getId());
                    e.setIdCidade(cidade.getId());
                    return enderecoRepo.save(e);
                });

        // 6) Evento
        var ev = new Evento();
        ev.setNomeEvento(dto.nomeEvento());
        ev.setDtInicio(dto.dtInicio());
        ev.setDtTermino(dto.dtTermino());
        ev.setDescricao(dto.descricao());
        ev.setUrlSite(dto.urlSite());
        ev.setIdEndereco(endereco.getId());
        ev.setNroEndereco(dto.endereco().numero());
        ev.setComplementoEndereco(dto.endereco().complemento());

        ev = eventoRepo.save(ev);
        return new EventoFullCreateResponse(ev.getId(), endereco.getId());
    }

    @Transactional
    public EventoFullCreateResponse atualizar(Integer idEvento, EventoFullCreateDto dto) {
        requireAdmin(); // <-- só admin atualiza

        var ev = eventoRepo.findById(idEvento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));

        if (dto.dtTermino().isBefore(dto.dtInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dtTermino deve ser >= dtInicio");
        }

        // (mesma lógica de UF/Cidade/Bairro/Logradouro/Endereco da criação)
        String ufSigla = dto.endereco().uf();
        var uf = ufRepo.findBySiglaUf(ufSigla).orElseGet(() -> {
            var u = new UnidadeFederacao();
            u.setSiglaUf(ufSigla);
            u.setNomeUf(ufSigla);
            return ufRepo.save(u);
        });

        String cidadeNome = dto.endereco().localidade();
        var cidade = cidadeRepo.findByNomeCidadeAndIdUf(cidadeNome, uf.getId()).orElseGet(() -> {
            var c = new Cidade();
            c.setNomeCidade(cidadeNome);
            c.setIdUf(uf.getId());
            return cidadeRepo.save(c);
        });

        String bairroNome = dto.endereco().bairro();
        var bairro = bairroRepo.findByNomeBairro(bairroNome).orElseGet(() -> {
            var b = new Bairro();
            b.setNomeBairro(bairroNome);
            return bairroRepo.save(b);
        });

        var parsed = parseLogradouro(dto.endereco().logradouro());
        var tipoLog = tipoLogRepo.findBySiglaLogradouro(parsed.sigla).orElseGet(() -> {
            var t = new TipoLogradouro();
            t.setSiglaLogradouro(parsed.sigla);
            return tipoLogRepo.save(t);
        });

        var logradouro = logradouroRepo
                .findByNomeLogradouroAndIdTipoLogradouro(parsed.nome, tipoLog.getId())
                .orElseGet(() -> {
                    var l = new Logradouro();
                    l.setNomeLogradouro(parsed.nome);
                    l.setIdTipoLogradouro(tipoLog.getId());
                    return logradouroRepo.save(l);
                });

        var endereco = enderecoRepo
                .findByCepAndIdLogradouroAndIdBairroAndIdCidade(
                        dto.endereco().cep(),
                        logradouro.getId(),
                        bairro.getId(),
                        cidade.getId()
                ).orElseGet(() -> {
                    var e = new Endereco();
                    e.setCep(dto.endereco().cep());
                    e.setIdLogradouro(logradouro.getId());
                    e.setIdBairro(bairro.getId());
                    e.setIdCidade(cidade.getId());
                    return enderecoRepo.save(e);
                });

        ev.setNomeEvento(dto.nomeEvento());
        ev.setDtInicio(dto.dtInicio());
        ev.setDtTermino(dto.dtTermino());
        ev.setDescricao(dto.descricao());
        ev.setUrlSite(dto.urlSite());
        ev.setIdEndereco(endereco.getId());
        ev.setNroEndereco(dto.endereco().numero());
        ev.setComplementoEndereco(dto.endereco().complemento());

        ev = eventoRepo.save(ev);
        return new EventoFullCreateResponse(ev.getId(), endereco.getId());
    }
}
