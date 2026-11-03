import java.util.function.Function;

public class Ordenacao {
    public static <T, K extends Comparable<K>> void ordenar(T[] dados, int quantidade, Function<T, K> extratorDeChave) {
        if (quantidade < 0 || quantidade > dados.length) throw new IllegalArgumentException("Quantidade inválida");
        Object[] auxiliar = new Object[quantidade];
        mergeSort(dados, auxiliar, 0, quantidade, extratorDeChave);
    }

    private static <T, K extends Comparable<K>> void mergeSort(T[] dados, Object[] aux, int inicio, int fim, Function<T, K> extrator) {
        if (fim - inicio <= 1) return;
        int meio = inicio + (fim - inicio) / 2;
        mergeSort(dados, aux, inicio, meio, extrator);
        mergeSort(dados, aux, meio, fim, extrator);
        int i = inicio, j = meio, k = inicio;
        while (i < meio && j < fim) {
            if (extrator.apply(dados[i]).compareTo(extrator.apply(dados[j])) <= 0) aux[k++] = dados[i++];
            else aux[k++] = dados[j++];
        }
        while (i < meio) aux[k++] = dados[i++];
        while (j < fim) aux[k++] = dados[j++];
        for (k = inicio; k < fim; k++) {
            @SuppressWarnings("unchecked") T elemento = (T) aux[k];
            dados[k] = elemento;
        }
    }
}
