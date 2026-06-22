package model;

public enum Laboratorio {
    BIOLOGIA("Biologia"),
    QUIMICA("Química"),
    FISICA("Física"),
    COMPUTACAO("Computação");

    private final String descricao;

    Laboratorio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
