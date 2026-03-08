package game;

public class Cell {
	
	private boolean clicked;
	private boolean flagged;

	public Cell(boolean clicked, boolean flagged){
		this.clicked = clicked;
		this.flagged = flagged;
	}//end Cell constructor
	
	public boolean getClicked(){return this.clicked;}
	public boolean getFlagged(){return this.flagged;}
	
    public void setClicked(boolean clicked){this.clicked = clicked;}
    public void setFlagged(boolean flagged){this.flagged = flagged;}
	
}//end Cell class