package nameofpackage;

public class User {
    
    private int userID;
    private String username;
    private String email;
    private int totalScore;
    private int gamesPlayed;

    
    public User(int userID, String username, String email) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.totalScore = 0;
        this.gamesPlayed = 0;
    }

    
    public void register() {
        System.out.println("User " + username + " registered successfully.");
    }

    public boolean login() {
        System.out.println(username + " logged in.");
        return true;
    }

    public void startGame() {
        this.gamesPlayed++;
        System.out.println("Starting a new game for " + username);
    }

    public void joinGameSession() {
        System.out.println(username + " joined a game session.");
    }

    public void updateScore(int points) {
        this.totalScore += points;
        System.out.println("New total score: " + totalScore);
    }

    
    public int getUserID() { return userID; }
    public String getUsername() { return username; }
}

