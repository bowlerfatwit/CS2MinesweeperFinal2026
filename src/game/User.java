package game;

public class User {
    
    private int userID;
    private String username;
    private int totalScore;
    private int gamesPlayed;
    private int gamesWon;
    private long bestTime;

    
    public User(int userID, String username) {
        this.userID = userID;
        this.username = username;
        this.totalScore = 0;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.bestTime = -1;

    }
    
    public void recordGame(boolean won, long elapsedSeconds) {
        this.gamesPlayed++;
        if (won) {
            this.gamesWon++;
            int score = Math.max(100, 1000 - (int)(elapsedSeconds * 5));
            this.totalScore += score;
            if (bestTime == -1 || elapsedSeconds < bestTime) {
                bestTime = elapsedSeconds;
            }
        }
    }

    
    public int getUserID()      { return userID; }
    public String getUsername() { return username; }
    public int getTotalScore()  { return totalScore; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getGamesWon()    { return gamesWon; }
    public long getBestTime()   { return bestTime; }
    
    public String getWinRate() {
        if (gamesPlayed == 0) return "0%";
        return String.format("%.0f%%", (gamesWon / (double) gamesPlayed) * 100);
    }
    
    public String getBestTimeString() {
        if (bestTime == -1) return "N/A";
        return bestTime + "s";
    }
}

