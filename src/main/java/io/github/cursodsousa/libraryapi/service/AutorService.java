package io.github.cursodsousa.libraryapi.service;

import io.github.cursodsousa.libraryapi.exception.OperacaoNaoPermitidaException;
import io.github.cursodsousa.libraryapi.model.Autor;
import io.github.cursodsousa.libraryapi.repository.AutorRepository;
import io.github.cursodsousa.libraryapi.repository.LivroRepository;
import io.github.cursodsousa.libraryapi.validator.AutorValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AutorService {

    private final AutorRepository repository;
    private final AutorValidator validator;
    private final LivroRepository livroRepository;

    public AutorService(AutorRepository repository, AutorValidator validator, LivroRepository livroRepository){
        this.repository = repository;
        this.validator = validator;
        this.livroRepository = livroRepository;
    }

    public Autor salvar(Autor autor){
        validator.validar(autor);
        return repository.save(autor);
    }

    public void atualizar(Autor autor){
        if (autor.getId() == null){
            throw new IllegalArgumentException("ID de autor invalido");
        }

        validator.validar(autor);

        repository.save(autor);

    }

    public Optional<Autor> obterPorId(UUID id){
        return repository.findById(id);
    }

    public void deletar(Autor autor){

        if(possuiLivro(autor)){
            throw new OperacaoNaoPermitidaException("Nao e permitido excluir autor com livros cadastrados");
        }

        repository.delete(autor);
    }

    public List<Autor> pesquisa(String nome, String nacionalidade){
        if(nome ==null && nacionalidade == null){
            return repository.findAll();
        } else if(nome == null){
            return repository.findByNacionalidade(nacionalidade);
        } else if (nacionalidade == null) {
            return repository.findByNome(nome);
        }else{
            return repository.findByNomeAndNacionalidade(nome, nacionalidade);
        }

    }

    public boolean possuiLivro(Autor autor){
        return livroRepository.existsByAutor(autor);
    }

}
