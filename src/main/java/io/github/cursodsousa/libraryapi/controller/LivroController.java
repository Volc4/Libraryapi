package io.github.cursodsousa.libraryapi.controller;

import io.github.cursodsousa.libraryapi.controller.dto.CadastroLivroDTO;
import io.github.cursodsousa.libraryapi.controller.dto.ResultadoPesquisaLivroDTO;
import io.github.cursodsousa.libraryapi.controller.mappers.LivroMapper;
import io.github.cursodsousa.libraryapi.model.GeneroLivro;
import io.github.cursodsousa.libraryapi.model.Livro;
import io.github.cursodsousa.libraryapi.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("Livros")
@RequiredArgsConstructor
public class LivroController implements GenericController {

    private final LivroService service;
    private final LivroMapper mapper;

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody @Valid CadastroLivroDTO dto) {
        Livro livro = mapper.toEntity(dto);
        service.salvar(livro);
        var url = gerarHeaderLocation(livro.getId());
        return ResponseEntity.created(url).build();
    }
    @GetMapping("{id}")
    public ResponseEntity<ResultadoPesquisaLivroDTO> obterDetalhes(@PathVariable("id") String id){
        return service.obterPorId(UUID.fromString(id))
                .map(livro ->{
                    var dto =mapper.toDTO(livro);
                    return ResponseEntity.ok(dto);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable("id") String id){

        return service.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    service.deletar(livro);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ResultadoPesquisaLivroDTO>> pesquisa(
           @RequestParam(value = "isbn", required = false)
            String isbn,
           @RequestParam(value = "titulo", required = false)
            String titulo,
           @RequestParam(value = "nome-autor", required = false)
            String nomeAutor,
           @RequestParam(value = "genero", required = false)
            GeneroLivro genero,
           @RequestParam(value = "DataPublicacao", required = false)
            Integer anoPublicacao,
           @RequestParam(value = "pagina", defaultValue = "0")
            Integer pagina,
           @RequestParam(value = "tamanho-pagina", defaultValue = "10")
            Integer tamanhoPagina

    ){
        var paginaResultado = service.pesquisa(isbn, titulo, nomeAutor, genero, anoPublicacao, pagina, tamanhoPagina);

        Page<ResultadoPesquisaLivroDTO> resultado = paginaResultado.map(mapper::toDTO);

//        var lista = paginaResultado
//                .stream()
//                .map(mapper::toDTO)
//                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> atualizar(@PathVariable("id") String id,
                                          @RequestBody @Valid CadastroLivroDTO dto){

        return service.obterPorId(UUID.fromString(id))
                .map(livro -> {
                   Livro entidadeAux = mapper.toEntity(dto);

                   livro.setAutor(entidadeAux.getAutor());
                   livro.setDataPublicacao(entidadeAux.getDataPublicacao());
                   livro.setGenero(entidadeAux.getGenero());
                   livro.setTitulo(entidadeAux.getTitulo());
                   livro.setPreco(entidadeAux.getPreco());
                   livro.setIsbn(entidadeAux.getIsbn());

                   service.atualizar(livro);

                   return ResponseEntity.noContent().build();

                }).orElseGet( () -> ResponseEntity.notFound().build() );

    }

}

