package br.com.eventos.api.service;

import br.com.eventos.api.domain.*;
import br.com.eventos.api.dto.FullRegisterDto;
import br.com.eventos.api.dto.FullRegisterResponse;
import br.com.eventos.api.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class FullRegistrationService {

    private final UnidadeFederacaoRepository ufRepo;
    private final CidadeRepository cidadeRepo;
    private final BairroRepository bairroRepo;
    private final TipoLogradouroRepository tipoLogRepo;
    private final LogradouroRepository logradouroRepo;
    private final EnderecoRepository enderecoRepo;
    private final ParticipanteRepository participanteRepo;
    private final TipoParticipanteRepository tipoPartRepo;
    private final LoginRepository loginRepo;
    private final DddRepository dddRepo;
    private final TelefoneParticipanteRepository telRepo;
    private final PasswordEncoder encoder;

    public FullRegistrationService(UnidadeFederacaoRepository ufRepo,
                                   CidadeRepository cidadeRepo,
                                   BairroRepository bairroRepo,
                                   TipoLogradouroRepository tipoLogRepo,
                                   LogradouroRepository logradouroRepo,
                                   EnderecoRepository enderecoRepo,
                                   ParticipanteRepository participanteRepo,
                                   TipoParticipanteRepository tipoPartRepo,
                                   LoginRepository loginRepo,
                                   DddRepository dddRepo,
                                   TelefoneParticipanteRepository telRepo,
                                   PasswordEncoder encoder) {
        this.ufRepo = ufRepo;
        this.cidadeRepo = cidadeRepo;
        this.bairroRepo = bairroRepo;
        this.tipoLogRepo = tipoLogRepo;
        this.logradouroRepo = logradouroRepo;
        this.enderecoRepo = enderecoRepo;
        this.participanteRepo = participanteRepo;
        this.tipoPartRepo = tipoPartRepo;
        this.loginRepo = loginRepo;
        this.dddRepo = dddRepo;
        this.telRepo = telRepo;
        this.encoder = encoder;
    }

    // mapa de prefixos de logradouro -> sigla da sua tabela
    private static final Map<String, String> MAP_TIPO = Map.of(
            "RUA", "R", "R", "R",
            "AVENIDA", "Av", "AV", "Av",
            "RODOVIA", "Rod", "ROD", "Rod",
            "TRAVESSA", "Trav", "TRAV", "Trav"
    );

    private static final class ParsedLogradouro {
        private final String sigla; // "R", "Av", ...
        private final String nome;  // "Brasil", "Safira" ...
        ParsedLogradouro(String sigla, String nome) { this.sigla = sigla; this.nome = nome; }
        String getSigla() { return sigla; }
        String getNome()  { return nome;  }
    }

    private ParsedLogradouro parseLogradouro(String raw) {
        if (raw == null || raw.isBlank()) return new ParsedLogradouro("R", "Desconhecido");
        String s = raw.trim();
        String[] parts = s.split("\\s+", 2);
        String head = parts[0].replace(".", "").toUpperCase();
        String sigla = MAP_TIPO.getOrDefault(head, "R");
        String nome  = (MAP_TIPO.containsKey(head) && parts.length > 1) ? parts[1] : s;
        return new ParsedLogradouro(sigla, nome);
    }

    private int onlyDigitsToInt(String s, int fallback) {
        if (s == null) return fallback;
        String d = s.replaceAll("\\D+", "");
        if (d.length() < 2) return fallback;
        try { return Integer.parseInt(d.substring(0, 2)); } catch (Exception e) { return fallback; }
    }

    @Transactional
    public FullRegisterResponse register(FullRegisterDto dto) {
        // 0) tipo participante deve existir
        var tipo = tipoPartRepo.findById(dto.idTipoParticipante())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo participante invalido"));

        // 1) UF
        String ufSigla = dto.endereco().uf();
        var uf = ufRepo.findBySiglaUf(ufSigla)
                .orElseGet(() -> {
                    var u = new UnidadeFederacao();
                    u.setSiglaUf(ufSigla);
                    u.setNomeUf(ufSigla); // se quiser: mapear "PR" -> "Parana"
                    return ufRepo.save(u);
                });

        // 2) Cidade
        String cidadeNome = dto.endereco().localidade();
        var cidade = cidadeRepo.findByNomeCidadeAndIdUf(cidadeNome, uf.getId())
                .orElseGet(() -> {
                    var c = new Cidade();
                    c.setNomeCidade(cidadeNome);
                    c.setIdUf(uf.getId());
                    return cidadeRepo.save(c);
                });

        // 3) Bairro
        String bairroNome = dto.endereco().bairro();
        var bairro = bairroRepo.findByNomeBairro(bairroNome)
                .orElseGet(() -> {
                    var b = new Bairro();
                    b.setNomeBairro(bairroNome);
                    return bairroRepo.save(b);
                });

        // 4) Tipo + Logradouro
        var parsed = parseLogradouro(dto.endereco().logradouro());
        var tipoLog = tipoLogRepo.findBySiglaLogradouro(parsed.getSigla())
                .orElseGet(() -> {
                    var t = new TipoLogradouro();
                    t.setSiglaLogradouro(parsed.getSigla());
                    return tipoLogRepo.save(t);
                });

        var logradouro = logradouroRepo
                .findByNomeLogradouroAndIdTipoLogradouro(parsed.getNome(), tipoLog.getId())
                .orElseGet(() -> {
                    var l = new Logradouro();
                    l.setNomeLogradouro(parsed.getNome());
                    l.setIdTipoLogradouro(tipoLog.getId());
                    return logradouroRepo.save(l);
                });

        // 5) Endereco (idempotente no conjunto de chaves naturais)
        var endereco = enderecoRepo
                .findByCepAndIdLogradouroAndIdBairroAndIdCidade(
                        dto.endereco().cep(),
                        logradouro.getId(),
                        bairro.getId(),
                        cidade.getId()
                )
                .orElseGet(() -> {
                    var e = new Endereco();
                    e.setCep(dto.endereco().cep());
                    e.setIdLogradouro(logradouro.getId());
                    e.setIdBairro(bairro.getId());
                    e.setIdCidade(cidade.getId());
                    return enderecoRepo.save(e);
                });

        // 6) Participante
        var p = new Participante();
        p.setNome(dto.nome());
        p.setIdTipoParticipante(tipo.getId());
        p.setIdEndereco(endereco.getId());
        p.setNroEndereco(dto.endereco().numero());
        p.setComplementoEndereco(dto.endereco().complemento());
        p = participanteRepo.save(p);

        // 7) Login (email unico)
        if (loginRepo.existsByEmail(dto.email()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ja cadastrado");

        var log = new Login();
        log.setEmail(dto.email());
        log.setSenhaHash(encoder.encode(dto.senha()));
        log.setParticipanteId(p.getId());
        loginRepo.save(log);

        // 8) Telefone (opcional)
        if (dto.telefone() != null && !dto.telefone().isBlank()) {
            int dddNum = onlyDigitsToInt(dto.telefone(), 0);
            if (dddNum >= 10 && dddNum <= 99) {
                var ddd = dddRepo.findByNroDdd(dddNum).orElseGet(() -> {
                    var d = new Ddd();
                    d.setNroDdd(dddNum);
                    return dddRepo.save(d);
                });
                var tel = new TelefoneParticipante();
                tel.setNroTelefone(dto.telefone().replaceAll("\\D+", "")); // só dígitos
                tel.setIdParticipante(p.getId());
                tel.setIdDdd(ddd.getId());
                telRepo.save(tel);
            }
        }

        return new FullRegisterResponse(p.getId(), endereco.getId(), dto.email());
    }
}