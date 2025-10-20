// br/com/eventos/api/repo/EnderecoRepository.java
package br.com.eventos.api.repo;

import br.com.eventos.api.domain.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {
    Optional<Endereco> findByCepAndIdLogradouroAndIdBairroAndIdCidade(
            String cep, Integer idLogradouro, Integer idBairro, Integer idCidade);
}
