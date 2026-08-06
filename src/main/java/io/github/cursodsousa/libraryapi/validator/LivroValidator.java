package io.github.cursodsousa.libraryapi.validator;

import io.github.cursodsousa.libraryapi.exception.CampoInvalidoException;
import io.github.cursodsousa.libraryapi.exception.RegistroDuplicadoException;
import io.github.cursodsousa.libraryapi.model.Livro;
import io.github.cursodsousa.libraryapi.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LivroValidator {

    private final LivroRepository repository;

    private static final int ANO_EXISTENCIA_PRECO = 2020;

    public void validar(Livro livro){
        if(existeLivroComIsbn(livro)) {
            throw new RegistroDuplicadoException("ISBN ja cadastrado");
        }

        if(isPrecoObrigatorioNulo(livro)){
            throw new CampoInvalidoException("preco", "livros a partir de 2020 deve ter preco obrigatoriamente");
        }

    }

    private boolean isPrecoObrigatorioNulo(Livro livro) {
        return livro.getPreco() == null && livro.getDataPublicacao().getYear() >= ANO_EXISTENCIA_PRECO;
    }

    public boolean existeLivroComIsbn(Livro livro){
        Optional<Livro> livroEncotrado = repository.findByIsbn(livro.getIsbn());

        if(livro.getId() == null){
            return livroEncotrado.isPresent();
        }

        return livroEncotrado
                .map(Livro::getId)
                .stream()
                .anyMatch(id -> !id.equals(livro.getId()));

    }

}
