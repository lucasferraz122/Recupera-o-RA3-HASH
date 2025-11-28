# **Trabalho de Recuperação: Tabela Hash Encadeada**

Este projeto implementa uma Tabela Hash com tratamento de colisões por encadeamento separado (listas encadeadas manuais), comparando o desempenho de três funções de hash: Divisão, Multiplicação e Dobramento.

O objetivo foi analisar como o tamanho da tabela ($m$) e o tipo de função influenciam no número de colisões e no tempo de execução.

## **Pré-requisitos**

* Java JDK instalado (versão 8 ou superior).

## **Como Executar**

1. **Compile o código:**  
   javac tabelahashrecuperacao.java

2. **Execute o programa:**  
   java tabelahashrecuperacao

   *O programa exibirá os resultados em formato CSV no terminal, junto com os logs de auditoria.*  
3. (Opcional) Gerar CSV limpo:  
   Para salvar apenas os dados em um arquivo (sem os logs de auditoria):  
   java tabelahashrecuperacao \> resultados.csv 2\> NUL

   *(No Linux/Mac use 2\> /dev/null)*

## **Metodologia**

O experimento consistiu em inserir e buscar chaves inteiras aleatórias em tabelas de tamanhos primos distintos ($m \\in \\{1009, 10007, 100003\\}$) utilizando três funções de hash diferentes. Foram medidos o tempo de execução, o número de colisões e o custo de busca para validar a eficiência do encadeamento.

*Foram seguidas rigorosamente as regras de auditoria do enunciado:*
- *Sentinela impressa antes de cada execução (H_DIV/H_MUL/H_FOLD)*
- *Checksum calculado com os 10 primeiros hash values*
- *Médias de 5 repetições para todas as métricas temporais*

**Distribuições mais uniformes reduzem o custo médio no encadeamento separado.**

## **Resultados e Análise**

Abaixo estão os gráficos gerados a partir dos dados do experimento, mostrando o comportamento da tabela sob diferentes cargas.

### **1\. Colisões na Lista vs Tamanho da Tabela**
<img width="564" height="368" alt="{C92E1782-FDCA-40CE-8800-48B8BA97DB76}" src="https://github.com/user-attachments/assets/c5e3fd9c-887a-474b-8262-eb6e705d285f" />


Neste gráfico, observamos que o aumento do tamanho da tabela ($m$) reduz drasticamente as colisões para as funções de **Divisão** e **Multiplicação**, tendendo a zero. Curiosamente, a função de **Dobramento** (linha verde) manteve uma taxa de colisões constante, mostrando-se menos eficiente para este conjunto de chaves inteiras.

### **2\. Tempo de Inserção vs Tamanho do Dataset**
<img width="569" height="369" alt="{9FC8748A-FD9F-4EE2-9A51-E0D080B46376}" src="https://github.com/user-attachments/assets/1993f477-680f-44e2-bc93-6cc3fc54a06b" />


Aqui fica evidente o impacto do **Fator de Carga**. A linha roxa escura (tabela pequena, $m=1009$) dispara conforme aumentamos a quantidade de dados, pois a tabela fica cheia e o programa perde tempo percorrendo listas longas. Já as tabelas maiores (linhas inferiores) mantêm o tempo de inserção baixo e linear, pois quase não há colisões.

### **3\. Custo de Busca (Hits)**
<img width="564" height="365" alt="{DA3C35A7-37EC-463B-B29E-4DE8940A8C9B}" src="https://github.com/user-attachments/assets/6aa38825-1088-4748-8b03-9b4d3d55ba3b" />


O custo de busca confirma a eficiência da tabela hash bem dimensionada. Nas tabelas grandes (barras claras), o número médio de comparações para encontrar um item é próximo de 1 (acesso direto), enquanto na tabela pequena (barras pretas) o custo sobe significativamente.

## **Vídeo Explicativo**

Confira a explicação detalhada do código e da análise dos dados no YouTube:



## **Autor**

**Lucas Ferraz dos Santos**
