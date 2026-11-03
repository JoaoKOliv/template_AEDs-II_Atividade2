import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    static Produto[] produtosPorDescricao;
    static double tempoIndiceMs;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Pilha para consultas aos pedidos mais recentes (LIFO). */
    static Pilha<Pedido> pilhaPedidos = new Pilha<>();

    /** Fila para consultas em ordem de chegada (FIFO). */
    static Fila<Pedido> filaPedidos = new Fila<>();
        
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
   
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Listar produtos dos pedidos mais recentes (Pilha)");
        System.out.println("7 - Procurar produtos pelo início do nome (Buscas)");
        System.out.println("8 - Comparar desempenho das buscas (Buscas)");
        System.out.println("9 - Valor médio dos N primeiros pedidos (Fila)");
        System.out.println("10 - Primeiros pedidos com valor acima de X (Fila)");
        System.out.println("11 - Primeiros pedidos que contêm produto por código (Fila)");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        try {
            return Integer.parseInt(teclado.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida.");
            return -1;
        }
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null 
     */

static Produto localizarProduto() {
    cabecalho();
    Integer codigo = lerOpcao("Digite o código identificador do produto desejado:", Integer.class);
    if (codigo == null) { System.out.println("Código inválido."); return null; }
    Buscador<Produto, Integer> binaria = new Buscador<>(p -> p.hashCode());
    Buscador<Produto, Integer> sequencial = new Buscador<>(p -> p.hashCode());
    int posicao = binaria.buscaBinaria(produtosCadastrados, quantosProdutos, codigo);
    sequencial.buscaSequencial(produtosCadastrados, quantosProdutos, codigo);
    System.out.printf("Comparações - busca sequencial: %d | busca binária: %d%n",
            sequencial.getComparacoes(), binaria.getComparacoes());
    return posicao < 0 ? null : produtosCadastrados[posicao];
}

static Produto localizarProdutoDescricao() {
    cabecalho();
    System.out.println("Digite o nome ou a descrição do produto desejado:");
    String descricao = teclado.nextLine().toLowerCase(java.util.Locale.ROOT);
    Buscador<Produto, String> binaria = new Buscador<>(p -> p.descricao.toLowerCase(java.util.Locale.ROOT));
    Buscador<Produto, String> sequencial = new Buscador<>(p -> p.descricao.toLowerCase(java.util.Locale.ROOT));
    int posicao = binaria.buscaBinaria(produtosPorDescricao, quantosProdutos, descricao);
    sequencial.buscaSequencial(produtosCadastrados, quantosProdutos, descricao);
    System.out.printf("Comparações - busca sequencial: %d | busca binária: %d%n",
            sequencial.getComparacoes(), binaria.getComparacoes());
    return posicao < 0 ? null : produtosPorDescricao[posicao];
}

    private static void mostrarProduto(Produto produto) {
    	
        cabecalho();
        String mensagem = "Dados inválidos para o produto!";
        
        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto);
        }
        
        System.out.println(mensagem);
    }
    
    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {
    	
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < quantosProdutos; i++) {
        	System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }
    
    /** 
     * Inicia um novo pedido.
     * Permite ao usuário escolher e incluir produtos no pedido.
     * @return O novo pedido
     */
    public static Pedido iniciarPedido() {
    	
    	int formaPagamento = lerOpcao("Digite a forma de pagamento do pedido, sendo 1 para pagamento à vista e 2 para pagamento a prazo", Integer.class);
    	Pedido pedido = new Pedido(LocalDate.now(), formaPagamento);
    	Produto produto;
    	int numProdutos;
    	int quantidade;
    	
    	listarTodosOsProdutos();
    	System.out.println("Incluindo produtos no pedido...");
    	numProdutos = lerOpcao("Quantos produtos serão incluídos no pedido?", Integer.class);
        for (int i = 0; i < numProdutos; i++) {
        	produto = localizarProdutoDescricao();
        	if (produto == null) {
        		System.out.println("Produto não encontrado");
        		i--;
        	} else {
        		quantidade = lerOpcao("Quantos itens desse produto serão incluídos no pedido?", Integer.class);
        		pedido.incluirProduto(produto, quantidade);
        	}
        }
    	
        return pedido;
    }
    
    /**
     * Finaliza um pedido, momento no qual ele deve ser armazenado em uma pilha de pedidos.
     * @param pedido O pedido que deve ser finalizado.
     */
    public static void finalizarPedido(Pedido pedido) {
    	        if (pedido == null) {
            System.out.println("Não há pedido em aberto para finalizar.");
            return;
        }
        pilhaPedidos.empilhar(pedido);
        filaPedidos.enfilar(pedido);
        System.out.println("Pedido finalizado e armazenado na pilha e na fila!");
    }
public static void listarProdutosPedidosRecentes() {
    if (pilhaPedidos.vazia()) { System.out.println("Nenhum pedido finalizado."); return; }
    Integer n = lerOpcao("Quantos pedidos recentes deseja consultar?", Integer.class);
    if (n == null || n < 1 || n > pilhaPedidos.tamanho()) {
        System.out.println("Quantidade inválida. Total: " + pilhaPedidos.tamanho()); return;
    }
    Pilha<Pedido> recentes = pilhaPedidos.subPilha(n);
    while (!recentes.vazia()) System.out.println(recentes.desempilhar());
}

private static Integer lerQuantidade() {
    Integer n = lerOpcao("Quantos primeiros pedidos deseja consultar?", Integer.class);
    if (n == null || n < 1 || n > filaPedidos.tamanho()) {
        System.out.println("Quantidade inválida. Há " + filaPedidos.tamanho() + " pedidos.");
        return null;
    }
    return n;
}

public static void exibirValorMedio() {
    Integer n = lerQuantidade();
    if (n == null) return;
    double media = filaPedidos.calcularValorMedio(p -> p.valorFinal(), n);
    System.out.printf("Valor total médio dos %d primeiros pedidos: R$ %.2f%n", n, media);
}

public static void exibirPedidosAcimaDoValor() {
    Integer n = lerQuantidade();
    if (n == null) return;
    Double minimo = lerOpcao("Informe o valor mínimo (R$):", Double.class);
    if (minimo == null) { System.out.println("Valor inválido."); return; }
    imprimirPedidos(filaPedidos.filtrar(p -> p.valorFinal() > minimo, n));
}

public static void exibirPedidosComProduto() {
    Integer n = lerQuantidade();
    if (n == null) return;
    Integer codigo = lerOpcao("Código do produto desejado:", Integer.class);
    if (codigo == null) { System.out.println("Código inválido."); return; }
    imprimirPedidos(filaPedidos.filtrar(p -> {
        ItemDePedido[] itens = p.getItensDoPedido();
        for (int i = 0; i < itens.length; i++) {
            if (itens[i] != null && itens[i].getProduto().hashCode() == codigo) return true;
        }
        return false;
    }, n));
}

private static void imprimirPedidos(Fila<Pedido> pedidos) {
    int total = pedidos.tamanho();
    while (!pedidos.filaVazia()) System.out.println(pedidos.desenfilar());
    System.out.println("Pedidos encontrados: " + total);
}
static void procurarPorInicioDoNome() {
    System.out.println("Digite o início do nome do produto:");
    String prefixo = teclado.nextLine().toLowerCase(java.util.Locale.ROOT);
    Buscador<Produto, String> buscador = new Buscador<>(p -> p.descricao.toLowerCase(java.util.Locale.ROOT));
    int inicio = buscador.primeiraPosicaoMaiorOuIgual(produtosPorDescricao, quantosProdutos, prefixo);
    int total = 0;
    for (int i = inicio; i < quantosProdutos; i++) {
        if (!produtosPorDescricao[i].descricao.toLowerCase(java.util.Locale.ROOT).startsWith(prefixo)) break;
        System.out.println(produtosPorDescricao[i]);
        total++;
    }
    System.out.println("Produtos encontrados: " + total);
    System.out.println("Comparações na busca da primeira posição: " + buscador.getComparacoes());
}

static void compararDesempenhoDasBuscas() {
    System.out.println("Critério | Método | Encontrados | Comparações (total) | Média | Tempo (ms)");
    benchmarkCodigo(false);
    benchmarkCodigo(true);
    benchmarkDescricao(false);
    benchmarkDescricao(true);
    System.out.printf(java.util.Locale.ROOT, "Tempo para criar o índice por descrição: %.3f ms%n", tempoIndiceMs);
}

private static void benchmarkCodigo(boolean binaria) {
    Buscador<Produto, Integer> buscador = new Buscador<>(p -> p.hashCode());
    long comparacoes = 0;
    int encontrados = 0;
    long inicio = System.nanoTime();
    for (int i = 0; i < quantosProdutos + 1000; i++) {
        int chave = i < quantosProdutos ? produtosCadastrados[i].hashCode() : i - quantosProdutos + 1;
        int pos = binaria ? buscador.buscaBinaria(produtosCadastrados, quantosProdutos, chave)
                          : buscador.buscaSequencial(produtosCadastrados, quantosProdutos, chave);
        if (pos >= 0) encontrados++;
        comparacoes += buscador.getComparacoes();
    }
    double tempo = (System.nanoTime() - inicio) / 1_000_000.0;
    imprimirBenchmark("Código", binaria, encontrados, comparacoes, quantosProdutos + 1000, tempo);
}

private static void benchmarkDescricao(boolean binaria) {
    Buscador<Produto, String> buscador = new Buscador<>(p -> p.descricao.toLowerCase(java.util.Locale.ROOT));
    Produto[] dados = binaria ? produtosPorDescricao : produtosCadastrados;
    long comparacoes = 0;
    int encontrados = 0;
    long inicio = System.nanoTime();
    for (int i = 0; i < quantosProdutos + 1000; i++) {
        String chave = i < quantosProdutos ? produtosCadastrados[i].descricao.toLowerCase(java.util.Locale.ROOT)
                                            : "inexistente " + (i - quantosProdutos + 1);
        int pos = binaria ? buscador.buscaBinaria(dados, quantosProdutos, chave)
                          : buscador.buscaSequencial(dados, quantosProdutos, chave);
        if (pos >= 0) encontrados++;
        comparacoes += buscador.getComparacoes();
    }
    double tempo = (System.nanoTime() - inicio) / 1_000_000.0;
    imprimirBenchmark("Descrição", binaria, encontrados, comparacoes, quantosProdutos + 1000, tempo);
}

private static void imprimirBenchmark(String criterio, boolean binaria, int encontrados, long comparacoes, int buscas, double ms) {
    System.out.printf(java.util.Locale.ROOT, "%s | %s | %d | %d | %.3f | %.3f%n",
            criterio, binaria ? "Binária" : "Sequencial", encontrados, comparacoes, comparacoes / (double) buscas, ms);
}

	public static void main(String[] args) {
		
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        
		nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        if (produtosCadastrados == null) {
            System.out.println("Não foi possível ler produtos.txt. Execute na raiz do projeto.");
            return;
        }
        long inicioIndice = System.nanoTime();
        produtosPorDescricao = new Produto[quantosProdutos];
        for (int i = 0; i < quantosProdutos; i++) produtosPorDescricao[i] = produtosCadastrados[i];
        Ordenacao.ordenar(produtosPorDescricao, quantosProdutos, p -> p.descricao.toLowerCase(java.util.Locale.ROOT));
        tempoIndiceMs = (System.nanoTime() - inicioIndice) / 1_000_000.0;
        
        Pedido pedido = null;
        
        int opcao = -1;
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> mostrarProduto(localizarProduto());
                case 3 -> mostrarProduto(localizarProdutoDescricao());
                case 4 -> pedido = iniciarPedido();
                case 5 -> {
                    finalizarPedido(pedido);
                    pedido = null;
                }
                case 6 -> listarProdutosPedidosRecentes();
                case 7 -> procurarPorInicioDoNome();
                case 8 -> compararDesempenhoDasBuscas();
                case 9 -> exibirValorMedio();
                case 10 -> exibirPedidosAcimaDoValor();
                case 11 -> exibirPedidosComProduto();
            }
            pausa();
        }while(opcao != 0);       

        teclado.close();    
    }
}
