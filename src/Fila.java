import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Fila<E> {
    private Celula<E> inicio, fim;
    private int tamanho;

    public Fila() {
        Celula<E> sentinela = new Celula<>();
        inicio = sentinela;
        fim = sentinela;
    }

    public boolean filaVazia() { return inicio == fim; }
    public int tamanho() { return tamanho; }

    public void enfilar(E item) {
        Celula<E> nova = new Celula<>(item);
        fim.setProximo(nova);
        fim = nova;
        tamanho++;
    }

    public E desenfilar() {
        if (filaVazia()) throw new NoSuchElementException("Fila vazia");
        Celula<E> removida = inicio.getProximo();
        E item = removida.getItem();
        inicio.setProximo(removida.getProximo());
        if (fim == removida) fim = inicio;
        tamanho--;
        return item;
    }

    /** Calcula a média dos primeiros N elementos sem modificar a fila. */
    public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {
        if (extrator == null) throw new IllegalArgumentException("Extrator obrigatório");
        if (quantidade < 1 || quantidade > tamanho) throw new IllegalArgumentException("Quantidade inválida: " + quantidade);
        double soma = 0;
        Celula<E> atual = inicio.getProximo();
        for (int i = 0; i < quantidade; i++) {
            soma += extrator.apply(atual.getItem());
            atual = atual.getProximo();
        }
        return soma / quantidade;
    }

    /** Filtra somente os N primeiros elementos, preservando a ordem e a fila original. */
    public Fila<E> filtrar(Predicate<E> condicional, int quantidade) {
        if (condicional == null) throw new IllegalArgumentException("Condição obrigatória");
        if (quantidade < 0 || quantidade > tamanho) throw new IllegalArgumentException("Quantidade inválida: " + quantidade);
        Fila<E> resultado = new Fila<>();
        Celula<E> atual = inicio.getProximo();
        for (int i = 0; i < quantidade; i++) {
            E elemento = atual.getItem();
            if (condicional.test(elemento)) resultado.enfilar(elemento);
            atual = atual.getProximo();
        }
        return resultado;
    }
}
