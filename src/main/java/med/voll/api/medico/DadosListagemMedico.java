package med.voll.api.medico;

public record DadosListagemMedico(
        String nome,
        String email,
        String crm,
        String especialidade
) {
    public DadosListagemMedico(Medico medico){
        this(medico.getNome(), medico.getEmail(), medico.getCrm(), medico.getEspecialidade().getDescricao());
    }
}
