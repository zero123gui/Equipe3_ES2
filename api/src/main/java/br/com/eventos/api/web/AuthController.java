package br.com.eventos.api.web;

import br.com.eventos.api.domain.Login;
import br.com.eventos.api.dto.*;
import br.com.eventos.api.security.JwtService;
import br.com.eventos.api.service.AuthService;
import br.com.eventos.api.service.FullRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final JwtService jwt;
    private final FullRegistrationService fullRegistrationService;

    public AuthController(AuthService service,
                          JwtService jwt,
                          FullRegistrationService fullRegistrationService) {
        this.service = service;
        this.jwt = jwt;
        this.fullRegistrationService = fullRegistrationService;
    }

    // RF910 - Cadastro simples (já existente)
    @PostMapping("/register")
    public ResponseEntity<Login> register(@RequestBody RegisterDto dto) {
        return ResponseEntity.ok(service.register(dto.participanteId(), dto.email(), dto.senha()));
    }

    // NOVO: Cadastro completo (participante + endereço + login + telefone opcional)
    // Body esperado: FullRegisterDto
    // {
    //   "nome": "...",
    //   "email": "...",
    //   "telefone": "...",        // opcional
    //   "senha": "...",
    //   "idTipoParticipante": 1,
    //   "endereco": {
    //     "cep": "...",
    //     "logradouro": "Avenida Safira",  // do ViaCEP (sem número)
    //     "bairro": "Porto Meira",
    //     "localidade": "Foz do Iguacu",
    //     "uf": "PR",
    //     "numero": "123",
    //     "complemento": "Bloco A"
    //   }
    // }
    @PostMapping("/register-full")
    public ResponseEntity<FullRegisterResponse> registerFull(@Valid @RequestBody FullRegisterDto dto) {
        var result = fullRegistrationService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
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
