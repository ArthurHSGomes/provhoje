package model;

public class Equipamento {

    private String codigo;
    private String nome;
    private Laboratorio laboratorio;
    private int quantidadeDisponivel;
    private double valorPatrimonial;

    public Equipamento(String codigo, String nome, Laboratorio laboratorio,
                       int quantidadeDisponivel, double valorPatrimonial) {
        this.codigo = codigo;
        this.nome = nome;
        this.laboratorio = laboratorio;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.valorPatrimonial = valorPatrimonial;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Laboratorio getLaboratorio() { return laboratorio; }
    public void setLaboratorio(Laboratorio laboratorio) { this.laboratorio = laboratorio; }

    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(int quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }

    public double getValorPatrimonial() { return valorPatrimonial; }
    public void setValorPatrimonial(double valorPatrimonial) { this.valorPatrimonial = valorPatrimonial; }

    @Override
    public String toString() {
        return String.format("Código: %s | Nome: %-30s | Lab: %-12s | Qtd: %3d | Valor: R$ %,.2f",
                codigo, nome, laboratorio.getDescricao(), quantidadeDisponivel, valorPatrimonial);
    }
}
