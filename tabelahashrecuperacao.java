import java.util.Random;

// criei o node com o proximo pra apontar para oq vem depois e a chave que é o valor
class No {
    public int chave;
    public No proximo;

    public No(int k) {
        this.chave = k;
        this.proximo = null;
    }
}

public class tabelahashrecuperacao {

    // constantes como pedido no enunciado da recuperaçãp(tamanhos, seeds e datasets)
    private static final int m1 = 1009;
    private static final int m2 = 10007;
    private static final int m3 = 100003;

    private static final int n1 = 1000;
    private static final int n2 = 10000;
    private static final int n3 = 100000;

    private static final long seed1 = 137;
    private static final long seed2 = 271828;
    private static final long seed3 = 314159;

    private static final int FUNC_DIV = 0;
    private static final int FUNC_MUL = 1;
    private static final int FUNC_FOLD = 2;

    private No[] tabela;
    private int tamanhoTabela;

    // variaveis pra guardar as contagens de tempo e tambem de colisoa
    private long totalColisoesTabela;
    private long totalColisoesLista;
    private long somaHashPrimeiros10;
    private int contadorInsercao;
    private long comparacoesHits;
    private long comparacoesMisses;

    // divisao resto da divisao pelo tamanho
    private int hashDivisao(int k) {
        int idx = k % this.tamanhoTabela;
        if (idx < 0) idx = idx * -1;
        return idx;
    }

    // multiplicacao usando a constante aurea que o enunciado deu
    private int hashMultiplicacao(int k) {
        double A = 0.6180339887;
        double produto = k * A;
        double parteFracionaria = produto - (long) produto;
        return (int) (this.tamanhoTabela * parteFracionaria);
    }

    // dobramento somando de 3 em 3
    private int hashDobramento(int k) {
        int soma = 0;
        int temp = k;

        if (temp < 0) temp = temp * -1;

        while (temp > 0) {
            int bloco = temp % 1000;
            soma = soma + bloco;
            temp = temp / 1000;
        }

        int idx = soma % this.tamanhoTabela;
        if (idx < 0) idx = idx * -1;
        return idx;
    }

    // ajuda a chamar a função certa
    private int calcularHash(int k, int tipoFunc) {
        if (tipoFunc == FUNC_DIV) return hashDivisao(k);
        if (tipoFunc == FUNC_MUL) return hashMultiplicacao(k);
        return hashDobramento(k);
    }

    // serve pra zerar o vetor e reinicia o contador
    private void inicializarTabela(int m) {
        this.tamanhoTabela = m;
        this.tabela = new No[m];

        int i = 0;
        while (i < m) {
            this.tabela[i] = null;
            i = i + 1;
        }

        this.totalColisoesTabela = 0;
        this.totalColisoesLista = 0;
        this.somaHashPrimeiros10 = 0;
        this.contadorInsercao = 0;
    }

    // no final da lista ele adiciona o valor chave e conta se deu alguma colisao
    private void inserir(int k, int tipoFunc) {
        int h = calcularHash(k, tipoFunc);

        if (this.contadorInsercao < 10) {
            this.somaHashPrimeiros10 = this.somaHashPrimeiros10 + h;
        }
        this.contadorInsercao = this.contadorInsercao + 1;

        No novo = new No(k);

        if (this.tabela[h] == null) {
            this.tabela[h] = novo;
        } else {
            this.totalColisoesTabela = this.totalColisoesTabela + 1;

            No atual = this.tabela[h];
            while (atual.proximo != null) {
                this.totalColisoesLista = this.totalColisoesLista + 1;
                atual = atual.proximo;
            }
            this.totalColisoesLista = this.totalColisoesLista + 1;

            atual.proximo = novo;
        }
    }

    // percorre a lista pra achar o valor e conta quantas comparações ele fez
    private boolean buscar(int k, int tipoFunc) {
        int h = calcularHash(k, tipoFunc);
        No atual = this.tabela[h];

        long comps = 0;
        boolean achou = false;

        while (atual != null) {
            comps = comps + 1;
            if (atual.chave == k) {
                achou = true;
                break;
            }
            atual = atual.proximo;
        }

        if (achou) {
            this.comparacoesHits = this.comparacoesHits + comps;
        } else {
            this.comparacoesMisses = this.comparacoesMisses + comps;
        }

        return achou;
    }

    public void executar() {
        // usei arrays pra facilitar o loop
        int[] tamanhos = {m1, m2, m3};
        int[] datasets = {n1, n2, n3};
        long[] seeds = {seed1, seed2, seed3};
        int[] funcoes = {FUNC_DIV, FUNC_MUL, FUNC_FOLD};
        String[] nomesFunc = {"H_DIV", "H_MUL", "H_FOLD"};

        System.out.println("m,n,func,seed,ins_ms,coll_tbl,coll_lst,find_ms_hits,find_ms_misses,cmp_hits,cmp_misses,checksum");

        for (int im = 0; im < 3; im++) {
            int m = tamanhos[im];

            for (int in = 0; in < 3; in++) {
                int n = datasets[in];

                for (int ifunc = 0; ifunc < 3; ifunc++) {
                    int func = funcoes[ifunc];
                    String nomeFunc = nomesFunc[ifunc];

                    for (int iseed = 0; iseed < 3; iseed++) {
                        long seed = seeds[iseed];

                        System.out.println("regra auditoria: Iniciando " + nomeFunc + " m=" + m + " seed=" + seed);

                        realizarTeste(m, n, func, nomeFunc, seed);
                    }
                }
            }
        }

        // coloquei aqui as frases que precisa ter no relatorio
        System.out.println("RELATÓRIO - METODOLOGIA:");
        System.out.println("Distribuições mais uniformes reduzem o custo médio no encadeamento separado.");
    }

    // roda um teste 5 vezes pra poder pegar a media de tempo e imprimir a linha que pegou
    private void realizarTeste(int m, int n, int func, String nomeFunc, long seed) {

        int[] chavesInseridas = new int[n];
        Random gerador = new Random(seed);

        for (int i = 0; i < n; i++) {
            int chave = 100000000 + gerador.nextInt(900000000);
            chavesInseridas[i] = chave;
        }

        long somaTempoIns = 0;
        long somaTempoBuscaHits = 0;
        long somaTempoBuscaMisses = 0;

        long capCollTbl = 0;
        double capCollLst = 0;
        long capCmpHits = 0;
        long capCmpMisses = 0;
        long capChecksum = 0;

        for (int rep = 0; rep < 5; rep++) {

            inicializarTabela(m);

            long inicioIns = System.nanoTime();
            for (int i = 0; i < n; i++) {
                inserir(chavesInseridas[i], func);
            }
            long fimIns = System.nanoTime();
            somaTempoIns = somaTempoIns + (fimIns - inicioIns);

            capCollTbl = this.totalColisoesTabela;
            capCollLst = (double) this.totalColisoesLista / n;
            capChecksum = this.somaHashPrimeiros10 % 1000003;

            this.comparacoesHits = 0;
            int tamBuscaMetade = n / 2;

            long t0 = System.nanoTime();
            for (int i = 0; i < tamBuscaMetade; i++) {
                buscar(chavesInseridas[i], func);
            }
            somaTempoBuscaHits += (System.nanoTime() - t0);
            capCmpHits = this.comparacoesHits / tamBuscaMetade;

            this.comparacoesMisses = 0;

            int[] missesSet = new int[tamBuscaMetade];
            Random geradorMisses = new Random(seed + rep + 100);
            for(int k=0; k<tamBuscaMetade; k++) missesSet[k] = 100000000 + geradorMisses.nextInt(900000000);

            t0 = System.nanoTime();
            for (int i = 0; i < tamBuscaMetade; i++) {
                buscar(missesSet[i], func);
            }
            somaTempoBuscaMisses += (System.nanoTime() - t0);
            capCmpMisses = this.comparacoesMisses / tamBuscaMetade;

            tabela = null;
        }

        double mediaInsMs = (somaTempoIns / 5.0) / 1000000.0;
        double mediaFindHitsMs = (somaTempoBuscaHits / 5.0) / 1000000.0;
        double mediaFindMissesMs = (somaTempoBuscaMisses / 5.0) / 1000000.0;

        System.out.println(
                m + "," +
                        n + "," +
                        nomeFunc + "," +
                        seed + "," +
                        String.format("%.4f", mediaInsMs).replace(',', '.') + "," +
                        capCollTbl + "," +
                        String.format("%.4f", capCollLst).replace(',', '.') + "," +
                        String.format("%.4f", mediaFindHitsMs).replace(',', '.') + "," +
                        String.format("%.4f", mediaFindMissesMs).replace(',', '.') + "," +
                        capCmpHits + "," +
                        capCmpMisses + "," +
                        capChecksum
        );
    }

    public static void main(String[] args) {
        tabelahashrecuperacao programa = new tabelahashrecuperacao();
        programa.executar();
    }
}
