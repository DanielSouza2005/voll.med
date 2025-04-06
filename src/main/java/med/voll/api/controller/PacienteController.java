package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.paciente.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteRepository repository;

    @GetMapping
    public ResponseEntity<Page<DadosListagemPaciente>> listar(@PageableDefault(size = 10, sort = {"nome", "cpf"}) Pageable paginacao) {
        var pacientes = repository.findAllByAtivoTrue(paginacao).map(DadosListagemPaciente::new);
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> listarPacientePorId(@PathVariable Long id) {
        Optional<Paciente> pacienteOpt = repository.findById(id);

        if (pacienteOpt.isPresent()) {
            var paciente = pacienteOpt.get();
            return ResponseEntity.ok(new DadosDetalhamentoPaciente(paciente));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoPaciente> cadastrar(@RequestBody @Valid DadosCadastroPaciente dados,
                                                               UriComponentsBuilder uriBuilder) {
        var paciente = repository.save(new Paciente(dados));
        var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(paciente.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoPaciente(paciente));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> atualizar(@RequestBody @Valid DadosAtualizacaoPaciente dados) {
        Optional<Paciente> pacienteOpt = repository.findById(dados.id());

        if (pacienteOpt.isPresent()) {
            var paciente = pacienteOpt.get();
            paciente.atualizarInformacoes(dados);
            return ResponseEntity.ok(new DadosDetalhamentoPaciente(paciente));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        Optional<Paciente> pacienteOpt = repository.findById(id);

        if (pacienteOpt.isPresent()) {
            var paciente = pacienteOpt.get();
            paciente.excluir();
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}
