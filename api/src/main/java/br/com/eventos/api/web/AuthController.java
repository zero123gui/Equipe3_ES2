package br.com.eventos.api.web;

import br.com.eventos.api.domain.Login;
import br.com.eventos.api.dto.*;
import br.com.eventos.api.security.JwtService;
import br.com.eventos.api.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final JwtService jwt;

    public AuthController(AuthService service, JwtService jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    // RF910 - Cadastro de usuário
    @PostMapping("/register")
    public ResponseEntity<Login> register(@RequestBody RegisterDto dto) {
        return ResponseEntity.ok(service.register(dto.participanteId(), dto.email(), dto.senha()));
    }

    // RF920 - Login (email/senha)
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto dto) {
        String token = service.login(dto.email(), dto.senha());
        return ResponseEntity.ok(new TokenDto(token));
    }

    // RF930 - Alterar senha (precisa enviar Bearer token)
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestHeader("Authorization") String authHeader,
                                               @RequestBody ChangePasswordDto dto) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(401).build();
        String token = authHeader.substring(7);
        if (!jwt.validate(token)) return ResponseEntity.status(401).build();
        String email = jwt.getSubject(token);
        service.changePassword(email, dto.senhaAtual(), dto.novaSenha());
        return ResponseEntity.noContent().build();
    }
}
