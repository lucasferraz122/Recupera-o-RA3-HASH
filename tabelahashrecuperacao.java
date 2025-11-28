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

    private static final int TIPO_DIV = 0;
    private static final int TIPO_MUL = 1;
    private static final int TIPO_FOLD = 2;

    private No[] tabela;
    private int tamanhoM;

    // variaveis pra guardar as contagens de tempo e tambem de colisoa
    private long colsTabela;
    private long colsLista;
    private long somaCheck;
    private int contInsercao;

    // metricas que eu usei pra fazer a busca
    private long cmpHits;
    private long cmpMisses;

    // divisao resto da divisao pelo tamanho
    private int hDiv(int k) {
        int idx = k % this.tamanhoM;
        if (idx < 0) idx = idx * -1;
        return idx;
    }

    // multiplicacao usando a constante aurea que o enunciado deu
    private int hMul(int k) {
        double A = 0.6180339887;
        double val = k * A;
        double frac = val - (long) val;
        return (int) (this.tamanhoM * frac);
    }

    // dobramento somando de 3 em 3
    private int hFold(int k) {
        int soma = 0;
        int temp = k;
        if (temp < 0) temp = temp * -1;

        while (temp > 0) {
            int bloco = temp % 1000;
            soma = soma + bloco;
            temp = temp / 1000;
        }

        int idx = soma % this.tamanhoM;
        if (idx < 0) idx = idx * -1;
        return idx;
    }

    // ajuda a chamar a função certa
    private int calcHash(int k, int tipo) {
        if (tipo == TIPO_DIV) return hDiv(k);
        if (tipo == TIPO_MUL) return hMul(k);
        return hFold(k);
    }


    // formata o numero na mao pra nao usar biblioteca proibida tipo String.format
    private String formataNum(double valor) {
        long parteInt = (long) valor;
        double resto = valor - parteInt;
        long parteDec = (long) (resto * 10000);

        if (parteDec < 0) parteDec = parteDec * -1;

        String sDec = "" + parteDec;
        if (parteDec < 10) sDec = "000" + parteDec;
        else if (parteDec < 100) sDec = "00" + parteDec;
        else if (parteDec < 1000) sDec = "0" + parteDec;

        return parteInt + "." + sDec;
    }

    // serve pra zerar o vetor e reinicia o contador
    private void iniciar(int m) {
        this.tamanhoM = m;
        this.tabela = new No[m];

        for(int i = 0; i < m; i++) {
            this.tabela[i] = null;
        }

        this.colsTabela = 0;
        this.colsLista = 0;
        this.somaCheck = 0;
        this.contInsercao = 0;
    }

    // no final da lista ele adiciona o valor chave e conta se deu alguma colisao
    private void inserir(int k, int tipo) {
        int h = calcHash(k, tipo);

        if (this.contInsercao < 10) {
            this.somaCheck = this.somaCheck + h;
        }
        this.contInsercao = this.contInsercao + 1;

        No novo = new No(k);

        if (this.tabela[h] == null) {
            this.tabela[h] = novo;
        } else {
            this.colsTabela = this.colsTabela + 1;

            No atual = this.tabela[h];

            int saltos = 0;
            while (atual.proximo != null) {
                saltos = saltos + 1;
                atual = atual.proximo;
            }
            this.colsLista = this.colsLista + saltos + 1;

            atual.proximo = novo;
        }
    }

    // percorre a lista pra achar o valor e conta quantas comparações ele fez
    private boolean buscar(int k, int tipo) {
        int h = calcHash(k, tipo);
        No atual = this.tabela[h];
        long c = 0;
        boolean achei = false;

        while (atual != null) {
            c = c + 1;
            if (atual.chave == k) {
                achei = true;
                break;
            }
            atual = atual.proximo;
        }

        if (achei) this.cmpHits = this.cmpHits + c;
        else this.cmpMisses = this.cmpMisses + c;

        return achei;
    }

    public void executar() {
        int[] tams = {m1, m2, m3};
        int[] datas = {n1, n2, n3};
        long[] seeds = {seed1, seed2, seed3};
        int[] funcs = {TIPO_DIV, TIPO_MUL, TIPO_FOLD};
        String[] tags = {"H_DIV", "H_MUL", "H_FOLD"};

        System.out.println("m,n,func,seed,ins_ms,coll_tbl,coll_lst,find_ms_hits,find_ms_misses,cmp_hits,cmp_misses,checksum");

        for (int m : tams) {
            for (int n : datas) {
                for (int f = 0; f < 3; f++) {
                    String tag = tags[f];

                    for (long s : seeds) {
                        System.out.println(tag + " " + m + " " + s);

                        rodarTeste(m, n, funcs[f], tag, s);
                    }
                }
            }
        }

        System.out.println("RELATÓRIO - METODOLOGIA:");
        System.out.println("Distribuições mais uniformes reduzem o custo médio no encadeamento separado.");
    }

    // roda um teste 5 vezes pra poder pegar a media de tempo e imprimir a linha que pegou
    private void rodarTeste(int m, int n, int tipo, String tag, long seed) {
        int[] dados = new int[n];
        Random rnd = new Random(seed);

        for (int i = 0; i < n; i++) {
            dados[i] = 100000000 + rnd.nextInt(900000000);
        }

        long tIns = 0;
        long tHits = 0;
        long tMiss = 0;

        long sumColTab = 0;
        long sumColLst = 0;
        long ultCheck = 0;
        long sumCmpHit = 0;
        long sumCmpMiss = 0;

        for (int r = 0; r < 5; r++) {
            iniciar(m);

            long t0 = System.nanoTime();
            for (int k : dados) inserir(k, tipo);
            tIns = tIns + (System.nanoTime() - t0);

            sumColTab = sumColTab + this.colsTabela;
            sumColLst = sumColLst + this.colsLista;
            ultCheck = this.somaCheck % 1000003;

            this.cmpHits = 0;
            int metade = n / 2;

            t0 = System.nanoTime();
            for(int i=0; i<metade; i++) buscar(dados[i], tipo);
            tHits = tHits + (System.nanoTime() - t0);

            sumCmpHit = sumCmpHit + this.cmpHits;

            this.cmpMisses = 0;
            int[] misses = new int[metade];
            Random r2 = new Random(seed + r + 555);
            for(int i=0; i<metade; i++) misses[i] = 100000000 + r2.nextInt(900000000);

            t0 = System.nanoTime();
            for(int k : misses) buscar(k, tipo);
            tMiss = tMiss + (System.nanoTime() - t0);

            sumCmpMiss = sumCmpMiss + this.cmpMisses;

            tabela = null;
        }

        double msIns = (tIns / 5.0) / 1000000.0;
        double msHit = (tHits / 5.0) / 1000000.0;
        double msMiss = (tMiss / 5.0) / 1000000.0;

        long medColTab = sumColTab / 5;
        double medColLst = (double) sumColLst / 5 / n;
        double medCmpHit = (double) sumCmpHit / 5 / (n/2);
        double medCmpMiss = (double) sumCmpMiss / 5 / (n/2);

        String linha = m + "," + n + "," + tag + "," + seed + "," +
                formataNum(msIns) + "," +
                medColTab + "," +
                formataNum(medColLst) + "," +
                formataNum(msHit) + "," +
                formataNum(msMiss) + "," +
                formataNum(medCmpHit) + "," +
                formataNum(medCmpMiss) + "," +
                ultCheck;

        System.out.println(linha);
    }

    public static void main(String[] args) {
        tabelahashrecuperacao prog = new tabelahashrecuperacao();
        prog.executar();
    }
}
