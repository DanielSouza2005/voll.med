package med.voll.api.domain.medico;

import med.voll.api.domain.consulta.Consulta;
import med.voll.api.domain.endereco.DadosEndereco;
import med.voll.api.domain.paciente.DadosCadastroPaciente;
import med.voll.api.domain.paciente.Paciente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MedicoRepositoryTest {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Deve retornar true quando o médico está ativo")
    void findAtivoById_Ativo() {
        var medico = cadastrarMedico("Ativo", "ativo@voll.med", "00001", Especialidade.CARDIOLOGIA);
        Boolean ativo = medicoRepository.findAtivoById(medico.getId());
        assertThat(ativo).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando o médico está inativo")
    void findAtivoById_Inativo() {
        var medico = cadastrarMedico("Inativo", "inativo@voll.med", "00002", Especialidade.CARDIOLOGIA);
        medico.excluir();
        Boolean ativo = medicoRepository.findAtivoById(medico.getId());
        assertThat(ativo).isFalse();
    }

    @Test
    @DisplayName("Deve retornar null quando o ID não existe")
    void findAtivoById_IdInexistente() {
        Boolean ativo = medicoRepository.findAtivoById(999L);
        assertThat(ativo).isNull();
    }

    @Test
    @DisplayName("Deve retornar apenas médicos ativos")
    void findAllByAtivoTrue_DeveRetornarApenasAtivos() {
        cadastrarMedico("Ativo1", "a1@voll.med", "111", Especialidade.DERMATOLOGIA);
        var inativo = cadastrarMedico("Inativo", "i@voll.med", "222", Especialidade.CARDIOLOGIA);
        inativo.excluir();

        var page = medicoRepository.findAllByAtivoTrue(Pageable.ofSize(10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getNome()).isEqualTo("Ativo1");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há médicos ativos")
    void findAllByAtivoTrue_SemMedicosAtivos() {
        var inativo = cadastrarMedico("Inativo", "i@voll.med", "333", Especialidade.CARDIOLOGIA);
        inativo.excluir();

        var page = medicoRepository.findAllByAtivoTrue(Pageable.ofSize(10));

        assertThat(page).isEmpty();
    }

    @Test
    @DisplayName("Deveria Devolver Null quando único médico cadastrado não está disponível na data")
    void escolherMedicoDisponivelNaDataCenario1() {
        var proximaSegundaAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10, 0);
        var medico = cadastrarMedico("Medico", "medico@voll.med", "123456", Especialidade.CARDIOLOGIA);
        var paciente = cadastrarPaciente("Paciente", "paciente@email.com", "00000000000");
        cadastrarConsulta(medico, paciente, proximaSegundaAs10);

        var medicoLivre = medicoRepository.escolherMedicoDisponivelNaData(Especialidade.CARDIOLOGIA, proximaSegundaAs10);

        assertThat(medicoLivre).isNull();
    }

    @Test
    @DisplayName("Deveria Devolver Médico quando está disponível na data")
    void escolherMedicoDisponivelNaDataCenario2() {
        var proximaSegundaAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10, 0);
        var medico = cadastrarMedico("Medico", "medico@voll.med", "123456", Especialidade.CARDIOLOGIA);

        var medicoLivre = medicoRepository.escolherMedicoDisponivelNaData(Especialidade.CARDIOLOGIA, proximaSegundaAs10);

        assertThat(medicoLivre).isEqualTo(medico);
    }

    private void cadastrarConsulta(Medico medico, Paciente paciente, LocalDateTime data) {
        em.persist(new Consulta(null, medico, paciente, data, ""));
    }

    private Medico cadastrarMedico(String nome, String email, String crm, Especialidade especialidade) {
        var medico = new Medico(dadosMedico(nome, email, crm, especialidade));
        em.persist(medico);
        return medico;
    }

    private Paciente cadastrarPaciente(String nome, String email, String cpf) {
        var paciente = new Paciente(dadosPaciente(nome, email, cpf));
        em.persist(paciente);
        return paciente;
    }

    private DadosCadastroMedico dadosMedico(String nome, String email, String crm, Especialidade especialidade) {
        return new DadosCadastroMedico(
                nome,
                email,
                "61999999999",
                crm,
                especialidade,
                dadosEndereco()
        );
    }

    private DadosCadastroPaciente dadosPaciente(String nome, String email, String cpf) {
        return new DadosCadastroPaciente(
                nome,
                email,
                "61999999999",
                cpf,
                dadosEndereco()
        );
    }

    private DadosEndereco dadosEndereco() {
        return new DadosEndereco(
                "rua xpto",
                "bairro",
                "00000000",
                "Brasilia",
                "DF",
                null,
                null
        );
    }
}