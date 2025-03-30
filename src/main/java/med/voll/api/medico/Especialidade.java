package med.voll.api.medico;

public enum Especialidade {
    ORTOPEDIA(0),
    CARDIOLOGIA(1),
    GINECOLOGIA(2),
    DERMATOLOGIA(3);

    private final int codigo;

    Especialidade(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo(){
        return this.codigo;
    }

    public String getDescricao() {
        return this.name();
    }
}
