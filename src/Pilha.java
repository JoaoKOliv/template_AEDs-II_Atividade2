import java.util.NoSuchElementException;

public class Pilha<E> {

	private Celula<E> topo;
	private Celula<E> fundo;

	public Pilha() {

		Celula<E> sentinela = new Celula<E>();
		fundo = sentinela;
		topo = sentinela;

	}

	public boolean vazia() {
		return fundo == topo;
	}

	public void empilhar(E item) {

		topo = new Celula<E>(item, topo);
	}

	public E desempilhar() {

		E desempilhado = consultarTopo();
		topo = topo.getProximo();
		return desempilhado;

	}

	public E consultarTopo() {

		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na pilha!");
		}

		return topo.getItem();

	}

	/**
	 * Cria e devolve uma nova pilha contendo os primeiros numItens elementos
	 * do topo da pilha atual.
	 * 
	 * Os elementos são mantidos na mesma ordem em que estavam na pilha original.
	 * Caso a pilha atual possua menos elementos do que o valor especificado,
	 * uma exceção será lançada.
	 *
	 * @param numItens o número de itens a serem copiados da pilha original.
	 * @return uma nova instância de Pilha<E> contendo os numItens primeiros elementos.
	 * @throws IllegalArgumentException se a pilha não contém numItens elementos.
	 */
	public Pilha<E> subPilha(int numItens) {
		
		if(numItens < 0){
			throw new IllegalArgumentException("O valor não pode ser negativo");
		}

		Object[] temp = new Object[numItens];

		Celula<E> atual = this.topo;
		int cont = 0;
		
		while (cont < numItens) {
			temp[cont] = atual.getItem();
			atual = atual.getProximo();
			cont++;
		}

		if(cont < numItens){
			throw new IllegalArgumentException("Não há pilha que condiz com numero de itens, o numero de itens é de: " +numItens);
		}

		Pilha<E> novaPilha = new Pilha<>();

		for(int i = numItens-1; i>= 0; i--){
			novaPilha.empilhar((E) temp[i]);
		}

		return novaPilha;
	}
}
