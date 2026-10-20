import java.util.function.Function;

public class Fila <E> extends Celula<E>{

    private Celula<E> inicio,fim;
    Function<E, Double> extrator;

    public Fila(){
        Celula <E> Sentinela = new Celula<E>();
        inicio = Sentinela;
        fim = Sentinela;
    }

    boolean filaVazia(){
        return inicio == fim;
    }

    void enfilar(E item){
        Celula <E> novaCelula = new Celula<E>(item);

        fim.setProximo(novaCelula);
        fim = novaCelula;
    }

    E desenfilar(){
        if(filaVazia()){
            throw new IllegalArgumentException("Vazia");
        }

        Celula<E> removida = inicio.getProximo();

        E item = removida.getItem();

        inicio.setProximo(removida.getProximo());

        if (fim == removida) {
            fim = inicio;
        }

        return item;

    }

/* Questão 1 - Implemente o método double calcularValorMedio(Function<E, Double> extrator, int
quantidade) na classe Fila<E>. Esse método deve ser capaz de calcular e retornar o valor médio de um dos
atributos dos elementos da fila, utilizando uma função de extração fornecida pelo parâmetro extrator, que extrai
um valor numérico (Double) de cada elemento da fila. Devem ser extraídos valores de apenas os primeiros
elementos armazenados na fila, de acordo com o parâmetro quantidade.

 */

    public double calcularValorMedio(Function <E, Double> extrator, int quantidade){

        double soma = 0;
        Celula<E> temp = inicio.getProximo();

            for(int i = 0; i < quantidade; i++){
                temp.getItem();
                extrator.apply(temp.getItem());
                
                temp.getProximo();
                temp = temp.getProximo();

            }

        /*Function <E, Double> (){

        } */
    }

}
