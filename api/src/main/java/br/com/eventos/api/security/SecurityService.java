package br.com.eventos.api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SecurityService {
    private final Set<String> admins;

    public SecurityService(@Value("${app.security.admin-emails:}") String adminList) {
        this.admins = Stream.of(adminList.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
        System.out.println("ADMINS="+this.admins);
    }

    public boolean isAdmin(String email) { return admins.contains(email); }
}
