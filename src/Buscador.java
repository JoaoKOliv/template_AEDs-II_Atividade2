import java.util.function.Function;

public class Buscador<T, K extends Comparable<K>> {
    private final Function<T, K> extratorDeChave;
    private long comparacoes;

    public Buscador(Function<T, K> extratorDeChave) { this.extratorDeChave = extratorDeChave; }

    public int buscaSequencial(T[] dados, int quantidade, K chave) {
        zerarComparacoes();
        for (int i = 0; i < quantidade; i++) {
            comparacoes++;
            if (chave.compareTo(extratorDeChave.apply(dados[i])) == 0) return i;
        }
        return -1;
    }

    /** Pré-condição: vetor ordenado pela chave em ordem crescente. */
    public int buscaBinaria(T[] dados, int quantidade, K chave) {
        zerarComparacoes();
        int inicio = 0, fim = quantidade - 1;
        while (inicio <= fim) {
            int meio = inicio + (fim - inicio) / 2;
            comparacoes++;
            int resultado = chave.compareTo(extratorDeChave.apply(dados[meio]));
            if (resultado == 0) return meio;
            if (resultado < 0) fim = meio - 1;
            else inicio = meio + 1;
        }
        return -1;
    }

    public int primeiraPosicaoMaiorOuIgual(T[] dados, int quantidade, K chave) {
        zerarComparacoes();
        int inicio = 0, fim = quantidade;
        while (inicio < fim) {
            int meio = inicio + (fim - inicio) / 2;
            comparacoes++;
            int resultado = chave.compareTo(extratorDeChave.apply(dados[meio]));
            if (resultado <= 0) fim = meio;
            else inicio = meio + 1;
        }
        return inicio;
    }

    public long getComparacoes() { return comparacoes; }
    public void zerarComparacoes() { comparacoes = 0; }
}
