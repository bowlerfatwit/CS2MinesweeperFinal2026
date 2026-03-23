package game;

public class Game {
	
	private int[][] gameBoard; //this will have the bomb locations and nums stored in it using the below methods
	private Cell[][] cell;
    private boolean won;
    private int numOfMines;
    private int flagLimit;
    private boolean gameOver;
    private long startTime;
    
    public boolean getGameOver(){ return gameOver; }
    public int getFlagLimit(){ return flagLimit; }
    public int getBoardSize(){ return gameBoard.length; }
    public int getCellValue(int row, int col){ return gameBoard[row][col]; }
    public boolean isCellClicked(int row, int col){ return cell[row][col].getClicked(); }
    public boolean isCellFlagged(int row, int col){ return cell[row][col].getFlagged(); }
    public long getElapsedSeconds(){ return (System.currentTimeMillis() - startTime) / 1000; }
    
    public boolean clickCell(int row, int col){
        if (gameOver || won) return false;
        if (!checkSpot(row, col)) return false;
        if (cell[row][col].getFlagged()) return false;
        if (cell[row][col].getClicked()) return false;
        
        if (gameBoard[row][col] == 9){
            setBombsToClicked();
            return true;
        }
        
        floodFill(row, col);
        checkFlags();
        checkWin();
        return false;
    }
    
    public void toggleFlag(int row, int col){
        if (gameOver || won) return;
        if (!checkSpot(row, col)) return;
        if (cell[row][col].getClicked()) return;
        
        if (cell[row][col].getFlagged()){
            cell[row][col].setFlagged(false);
            flagLimit++;
        } else {
            if (flagLimit <= 0) return;
            cell[row][col].setFlagged(true);
            flagLimit--;
        }
    }
    
    public Game(){
    	this.gameBoard = new int[16][16]; //nums here can change depending on how big we want the board to be, maybe make it a variable thats passed through the constructor
    	this.cell = new Cell[gameBoard.length][gameBoard[0].length];
    	this.numOfMines = 41; //this can also be whatever/be changed 
    	this.flagLimit = this.numOfMines; //needs to be same, minus 1 whenever a flag is placed, if at 0 cant place any more flags
    	this.gameOver = false;
    	
    	this.gameOver = false;
        this.won = false;

        initializeBooleans(this.cell);
        setRandomBombs(this.gameBoard, this.numOfMines);
        updateGameBoard(this.gameBoard);
        this.startTime = System.currentTimeMillis();
    	
    }//end Game constructor

    public boolean getWon(){return won;}
    
    public void initializeBooleans(Cell tempArray[][]){ //sets all the cells in the 2D array to have false values so you can use them later
        for (int i = 0; i < tempArray.length; i++) {
            for (int j = 0; j < tempArray[0].length; j++) { 
            	tempArray[i][j] = new Cell(false, false);
            }
        }
    }//end initializeBooleans
    
    public void setRandomBombs(int tempArray[][], int mines){ //picks random spots on the given array to put bombs (i used 9 to represent a bomb - can be changed)
        int bombCount = 0;
        do {
            int row = (int)(Math.random()*tempArray.length); 
            int col = (int)(Math.random()*tempArray[0].length);

            if (tempArray[row][col] != 9){ //make sure you don't use up a spot thats alreadu taken
                tempArray[row][col] = 9; //make it a bomb (represented w a 9)
                bombCount++;
            }
        } while (bombCount < mines);
    }//end setRandomBombs
    
    public void checkFlags(){ //makes sure you don't have a flag on a clicked square
        for (int row = 0; row < this.cell.length; row ++){
            for (int col = 0; col < this.cell[0].length; col ++){
                if (this.cell[row][col].getFlagged() && this.cell[row][col].getClicked()){ //if a square is clicked and flagged at the same time
                    this.cell[row][col].setFlagged(false); //remove the flag
                    this.flagLimit += 1;
                }
            }
        }
    }//end checkFlags

    public void checkWin(){ //checks if every square is revealed/every bomb is flagged
        won = true;
        for (int row = 0; row < this.cell.length; row++){
            for (int col = 0; col < this.cell[0].length; col++){
                if (!this.cell[row][col].getClicked() && this.gameBoard[row][col] != 9){
                    won = false;
                    return;
                }
            }
        }
        if (won) gameOver = true;
    }//end checkWin
    
    public void updateGameBoard(int tempArray[][]){ //updates the nums of each square depending on how many bombs are surrounding it
        for (int i = 0; i < tempArray.length; i++) {
            for (int j = 0; j < tempArray[0].length; j++) {
                if (tempArray[i][j] == 9) { //if that spot is a bomb
                    for (int row = i - 1; row <= i + 1; row++) {
                        for (int col = j - 1; col <= j + 1; col++) { //going through all the surrounding squares
                            if (checkSpot(row, col) && (row != i || col != j)) { //making sure they are in bounds (like if your checking a corner for example)
                                if (tempArray[row][col] != 9) { //make sure you don't update a square thats a bomb
                                    tempArray[row][col] += 1;
                                }
                            }
                        }
                    }
                }
            }
        }
    }//end updateGameBoard

    public boolean checkSpot(int x, int y) { //checks if the given spot is within the gameBoard's bounds
        return x >= 0 && x < this.gameBoard.length && y >= 0 && y < this.gameBoard[0].length;
    }//end checkSpot
    
    public void setBombsToClicked(){//if you click a bomb (aka lose the game), reveal where all other bombs were located
        for (int row = 0; row < this.gameBoard.length; row++){
            for (int col = 0; col < this.gameBoard[0].length; col++){
                if (this.gameBoard[row][col] == 9){ //of its a bomb
                    this.cell[row][col].setClicked(true); //make it clicked (reveal it)
                }
            }//end inner for
        }//end outer for
        this.gameOver = true; //if you want to show a "game over" screen or smth, you can use this
    }//end setBombsToClicked
    
    private void floodFill(int row, int col){ //if you click on a spot that is a "0" (no bombs around it), it will check all squares around it and continue to check until it stops hitting other zero spots)
        if (checkSpot(row, col) && this.gameBoard[row][col] == 0 &&!this.cell[row][col].getClicked()) { //if the spot is valid, the square is a zero, and it wasnt already clicked
            this.cell[row][col].setClicked(true); //set it to clicked
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) { //check the square around it
                    if (i!= row || j!= col) { //makes sure you don't call the method on the same cell again
                        floodFill(i, j); //uses recursion to keep checking surrounding squares till you hit a wall/a numbered square
                    }
                }
            }
        } else if (checkSpot(row, col) && !this.cell[row][col].getClicked() && this.gameBoard[row][col] != 9) {
            this.cell[row][col].setClicked(true);
        }
    }//end floodFill
}//end Game class
