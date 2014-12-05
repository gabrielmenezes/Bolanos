/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connectfourchampionship;

/**
 *
 * @author ricardomsj
 */
public class Bolanos implements IPlayer{

    int valor_jogador = 1;
    
    int[][] mascara = new int[5][5];
    
    @Override
    public int getNextMove(int[][] gameBoard) {
        //Se primeira jogada
        if (verificaPrimeiraJogada(gameBoard)){
            int a = gameBoard[0].length - 1;
            preencheMascara();
            valor_jogador = 1;
            int primeira = a%2==0? a/2: (a+1)/2;
            return primeira;
        }
       
        int check = gameBoard[0].length - 1;
        int meio = check%2 == 0 ? check/2: (check + 1)/2;
        if (gameBoard[gameBoard.length-1][meio] == 0)
            return meio;
        else
            return retonarColuna(gameBoard);
    }

    @Override
    public String getTeamName() {
       return "Pelo Bolanos";
    }
    
    
    private boolean verificaPrimeiraJogada(int[][] gB){
        
        for (int i = (gB[0].length - 1); i >= 0; i--){
            if (gB[gB.length-1][i] != 0){
                return false;
            }
        }
        return true;
        
    }
    
    private int retonarColuna(int[][] gB){
        
        int checaVitoria = verificaVitoria(gB);
        
        if (checaVitoria >= 0){
            return checaVitoria;
        }
        else
        {
            int[] colunas = funcao_heuristica(gB);
            return retornaMaximo(colunas);
        }
    }
    
    private int[] funcao_heuristica(int[][] gB){
       
        int[] retorno = new int[7];
        
        for(int i = gB.length-1; i >= 0; i--){
            for (int j = 0; j < gB[i].length; j++){
                if(i+1 >= 0 && i+1 < gB.length){
                    if(gB[i][j] == 0 && gB[i+1][j] != 0) {
                        retorno[j] = verificaCusto(gB, i, j);
                    }
                }
            }
        }
        
        return retorno;
    }

    private void preencheMascara() {
        mascara[0][0] = 2;
        mascara[0][1] = 0;
        mascara[0][2] = 2;
        mascara[0][3] = 0;
        mascara[0][4] = 2;
        mascara[1][0] = 0;
        mascara[1][1] = 1;
        mascara[1][2] = 1;
        mascara[1][3] = 1;
        mascara[1][4] = 0;
        mascara[2][0] = 2;
        mascara[2][1] = 1;
        mascara[2][2] = 0;
        mascara[2][3] = 1;
        mascara[2][4] = 2;
        mascara[3][0] = 0;
        mascara[3][1] = 1;
        mascara[3][2] = 1;
        mascara[3][3] = 1;
        mascara[3][4] = 0;
        mascara[4][0] = 2;
        mascara[4][1] = 0;
        mascara[4][2] = 2;
        mascara[4][3] = 0;
        mascara[4][4] = 2;
    }

    private int verificaCusto(int[][] gB, int i, int j) {
        int valor_retorno = 0;
        int meio_mascara = 2;
        
        for(int x = -2; x <= 2; x++){
            for(int y = -2; y <= 2; y++){
                //verificar restricoes de i+x, i-x, j+y, j-y.
                if(i+x >= 0 && i+x < gB.length && j+y >= 0 && j+y < gB[i].length){
                    //verificar campos utilizando a mascara.
                    if(gB[i+x][j+y] == valor_jogador){
                        //somar valores em valor_retorno.
                        valor_retorno+=mascara[meio_mascara+x][meio_mascara+y];
                    }
                    else if(gB[i+x][j+y] == (valor_jogador*-1)){
                        valor_retorno-=2*mascara[meio_mascara+x][meio_mascara+y];
                    }
                }     
            }
        }
        
        return valor_retorno;
    }

    private int retornaMaximo(int[] colunas) {
        int coluna = 0;
        int verificaDisposicao = verificarColunasZero(colunas);
        
        if (verificaDisposicao == 0 ){
            int maior_valor = 0;
            for (int i = 0; i < colunas.length; i++){
                if (maior_valor < colunas[i]){
                        maior_valor = colunas[i];
                        coluna = i;
                }
            }
            return coluna;    
        }
        else if (verificaDisposicao == 1){
            int menor_valor = colunas.length * colunas.length;
            for (int i = 0; i < colunas.length; i++){
                if (menor_valor > colunas[i]){
                        menor_valor = colunas[i];
                        coluna = i;
                }
            }
             return coluna;
        }
        else if (verificaDisposicao == 2){
            coluna = colunas.length%2 == 0 ? colunas.length/2 : (colunas.length+1)/2;
            return coluna;
        }
        return 0;
    }

    /*Retorna 0, se tiver maior que zero.
    Retorna 1, se tiver menor que zero, e nao tiver maior que zero.
    Retorna 2, nao tiver maior nem menor que zero.*/
    private int verificarColunasZero(int[] colunas) {
        int menorQue = 0;
        int maiorQue = 0;
        
        for(int i = 0; i < colunas.length; i++){
            if(colunas[i] > 0)
                maiorQue++;
            else if(colunas[i] < 0)
                menorQue++;
        }
        
        if (maiorQue > 0){
            return 0;
        }
        else if(menorQue > 0 && maiorQue == 0){
            return 1;
        }
        else if(menorQue == 0 && maiorQue == 0){
            return 2;
        }
        return 0;
    }

    private int verificaVitoria(int[][] gB) {
        //check for win horizontally
        for (int row = 0; row < gB.length; row++) {
            for (int col = 0; col < gB[row].length - 3; col++) { //0 to 3
                if (gB[row][col] == valor_jogador
                        && gB[row][col + 1] == valor_jogador 
                        && gB[row][col + 2] == valor_jogador
                        && gB[row][col + 3] == 0){
                    return col+3;
                }
                if (gB[row][col] == valor_jogador
                        && gB[row][col + 1] == valor_jogador 
                        && gB[row][col + 2] == 0
                        && gB[row][col + 3] == valor_jogador){
                    return col+2;
                }
                if (gB[row][col] == valor_jogador
                        && gB[row][col + 1] == 0
                        && gB[row][col + 2] == valor_jogador 
                        && gB[row][col + 3] == valor_jogador){
                    return col+1;
                }
                if (gB[row][col] == 0
                        && gB[row][col + 1] == valor_jogador 
                        && gB[row][col + 2] == valor_jogador 
                        && gB[row][col + 3] == valor_jogador){
                    return col;
                }
            }
        }
        //check for win vertically
        for (int row = 0; row < gB.length - 3; row++) { //0 to 2
            for (int col = 0; col < gB[row].length; col++) {
                if (gB[row][col] == valor_jogador
                        && gB[row + 1][col] == valor_jogador
                        && gB[row + 2][col] == valor_jogador
                        && gB[row + 3][col] == 0) {
                    return col;
                }
                if (gB[row][col] == valor_jogador
                        && gB[row + 1][col] == valor_jogador
                        && gB[row + 2][col] == 0
                        && gB[row + 3][col] == valor_jogador) {
                    return col;
                }
                if (gB[row][col] == valor_jogador
                        && gB[row + 1][col] == 0
                        && gB[row + 2][col] == valor_jogador
                        && gB[row + 3][col] == valor_jogador) {
                    return col;
                }
                if (gB[row][col] == 0
                        && gB[row + 1][col] == valor_jogador
                        && gB[row + 2][col] == valor_jogador
                        && gB[row + 3][col] == valor_jogador) {
                    return col;
                }
            }
        }
        //check for win diagonally (upper left to lower right)
        for (int row = 0; row < gB.length - 3; row++) { //0 to 2
            for (int col = 0; col < gB[row].length - 3; col++) { //0 to 3
                if (gB[row][col] == valor_jogador
                        && gB[row + 1][col + 1] == valor_jogador
                        && gB[row + 2][col + 2] == valor_jogador
                        && gB[row + 3][col + 3] == 0){
                    return col+3;
                }
                if (gB[row][col] == valor_jogador
                        && gB[row + 1][col + 1] == valor_jogador
                        && gB[row + 2][col + 2] == 0
                        && gB[row + 3][col + 3] == valor_jogador){
                    return col+2;
                }
                if (gB[row][col] == valor_jogador
                        && gB[row + 1][col + 1] == 0
                        && gB[row + 2][col + 2] == valor_jogador
                        && gB[row + 3][col + 3] == valor_jogador){
                    return col+1;
                }
                if (gB[row][col] == 0
                        && gB[row + 1][col + 1] == valor_jogador
                        && gB[row + 2][col + 2] == valor_jogador
                        && gB[row + 3][col + 3] == valor_jogador){
                    return col;
                }
                
            }
        }
        //check for win diagonally (lower left to upper right)
        for (int row = 3; row < gB.length; row++) { //3 to 5
            for (int col = 0; col < gB[row].length - 3; col++) { //0 to 3
                if (gB[row][col] == valor_jogador
                        && gB[row - 1][col + 1] == valor_jogador
                        && gB[row - 2][col + 2] == valor_jogador
                        && gB[row - 3][col + 3] == 0){
                    return col+3;
                }
                if (gB[row][col] == valor_jogador
                        && gB[row - 1][col + 1] == valor_jogador
                        && gB[row - 2][col + 2] == 0
                        && gB[row - 3][col + 3] == valor_jogador){
                    return col+2;
                }
                if (gB[row][col] == valor_jogador
                        && gB[row - 1][col + 1] == 0
                        && gB[row - 2][col + 2] == valor_jogador
                        && gB[row - 3][col + 3] == valor_jogador){
                    return col+1;
                }
                if (gB[row][col] == 0
                        && gB[row - 1][col + 1] == valor_jogador
                        && gB[row - 2][col + 2] == valor_jogador
                        && gB[row - 3][col + 3] == valor_jogador){
                    return col;
                }   
            }
        }
        return -1;
    }
}
