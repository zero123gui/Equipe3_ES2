package br.com.eventos.api.service;

import br.com.eventos.api.domain.Login;
import br.com.eventos.api.repo.LoginRepository;
import br.com.eventos.api.repo.ParticipanteRepository;
import br.com.eventos.api.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final LoginRepository loginRepo;
    private final ParticipanteRepository participanteRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(LoginRepository loginRepo,
                       ParticipanteRepository participanteRepo,
                       PasswordEncoder encoder,
                       JwtService jwt) {
        this.loginRepo = loginRepo;
        this.participanteRepo = participanteRepo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public Login register(Integer participanteId, String email, String senhaPura) {
        if (!participanteRepo.existsById(participanteId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participante inexistente");

        if (loginRepo.existsByEmail(email))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");

        if (loginRepo.existsByParticipanteId(participanteId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Participante já possui login");

        Login l = new Login();
        l.setParticipanteId(participanteId);
        l.setEmail(email);
        l.setSenhaHash(encoder.encode(senhaPura));
        return loginRepo.save(l);
    }

    public String login(String email, String senhaPura) {
        Login l = loginRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));
        if (!encoder.matches(senhaPura, l.getSenhaHash()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        return jwt.generate(l.getEmail(), l.getParticipanteId());
    }

    public void changePassword(String emailFromToken, String senhaAtual, String novaSenha) {
        Login l = loginRepo.findByEmail(emailFromToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido"));
        if (!encoder.matches(senhaAtual, l.getSenhaHash()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Senha atual incorreta");
        l.setSenhaHash(encoder.encode(novaSenha));
        loginRepo.save(l);
    }
}
