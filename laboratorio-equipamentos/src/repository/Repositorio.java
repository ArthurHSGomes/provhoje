package repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Repositorio<T> {

    private ArrayList<T> lista;

    public Repositorio() {
        this.lista = new ArrayList<>();
    }

    public void adicionar(T elemento) {
        lista.add(elemento);
    }

    public boolean remover(T elemento) {
        return lista.remove(elemento);
    }

    public List<T> listar() {
        return Collections.unmodifiableList(lista);
    }

    public List<T> buscar(Predicate<T> filtro) {
        return lista.stream()
                .filter(filtro)
                .collect(Collectors.toList());
    }
}
