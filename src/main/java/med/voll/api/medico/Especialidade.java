package med.voll.api.medico;

public enum Especialidade {
    ORTOPEDIA(1),
    CARDIOLOGIA(2),
    GINECOLOGIA(3),
    DERMATOLOGIA(4);

    private final int codigo;

    Especialidade(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}
