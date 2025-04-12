package med.voll.api.domain.consulta;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.validacoes.ValidadorAgendamentoConsultas;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendamentoConsultas {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private List<ValidadorAgendamentoConsultas> validadores;

    public Consulta agendar(DadosAgendamentoConsulta dados) {
        if ((dados.idMedico() != null) && !medicoRepository.existsById(dados.idMedico())) {
            throw new ValidacaoException("ID do Médico informado não existe");
        }

        if (!pacienteRepository.existsById(dados.idPaciente())) {
            throw new ValidacaoException("ID do Paciente informado não existe");
        }

        validadores.forEach(v -> v.validar(dados));

        var medico = escolherMedico(dados);
        var paciente = pacienteRepository.getReferenceById(dados.idPaciente());

        if (medico == null) {
            throw new ValidacaoException("Não existe nenhum Médico disponível :(");
        }

        var consulta = new Consulta(null, medico, paciente, dados.data(), null);

        return consultaRepository.save(consulta);
    }

    public Consulta cancelar(DadosCancelamentoConsulta dados) {
        if (!consultaRepository.existsById(dados.id())) {
            throw new ValidacaoException("ID da consulta informado não existe");
        }

        var consulta = consultaRepository.getReferenceById(dados.id());
        consulta.cancelar(dados.motivoCancelamento());
        return consulta;
    }

    private Medico escolherMedico(DadosAgendamentoConsulta dados) {
        if (dados.idMedico() != null) {
            return medicoRepository.getReferenceById(dados.idMedico());
        }

        if (dados.especialidade() == null) {
            throw new ValidacaoException("Especialidade é obrigatória quando não informar o Médico!");
        }

        return medicoRepository.escolherMedicoDisponivelNaData(dados.especialidade(), dados.data());
    }

}
