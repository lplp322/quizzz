package server.api;

import commons.LeaderboardEntry;
import commons.TrimmedGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import server.AdminService;
import server.Game;
import server.LobbyService;
import server.database.LeaderboardRepository;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/")
public class LobbyController {
    private LobbyService lobbyService;
    @Autowired
    private AdminService adminService;

    private final int REACTION_DURATION = 3000;

    @Autowired
    private LeaderboardRepository lbRepo;

    /**
     * Creates a new LobbyController
     * @param lobbyService the lobbyService instance
     */
    @Autowired
    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;

    }

    /**
     * To answer, if someone is connected
     * @return connected message
     */
    @GetMapping("/")
    public String newConnection(){
        return "Connected";
    }
    /**
     * Start singleplayer game with provided name
     * @param name - name of the player
     * @return - id of the singleplayer game
     */
    /**
     * Start the game in single player mode
     * @param name - name of the player
     * @return return id of the game
     */
    @GetMapping("/singleplayer/{name}")
    public int startSinglePlayer(@PathVariable String name){
        int id = lobbyService.createSinglePlayerGame(name);
        System.out.println("Created new game with ID:" + id);
        // Hardcoded to start directly
        Thread t1 = new Thread(lobbyService.getGameByID(id));
        t1.start();
        return id;
    }

    /**
     * Returns the id of the current lobby
     * @return id of current lobby
     */
    @GetMapping("multiplayer/getLobbyId")
    public int getLobbyId() {
        return lobbyService.getIdCounter();
    }

    /**
     * Return game info object(now full game object sent just to check)
     * @param gameID - id of the game
     * @param player - name of the requesting player
     * @return game info object
     */
    @GetMapping("{gameID}/{player}/getGameInfo")
    public TrimmedGame getGameInfo(@PathVariable int gameID, @PathVariable String player){
        //System.out.println( gameID + " polls");
        return lobbyService.getGameByID(gameID).trim(player);
    }

    /**
     * //PROBLEM WITH IDs FOUND DURING TESTING(needed to be solved)
     * Adding new player to the lobby of multiplayer game
     * @param name - name of the player
     * @return - id of the game
     */
    @GetMapping("/multiplayer/{name}")
    public int addNewUserMultiPlayer(@PathVariable String name){
        if(lobbyService.addPlayer(name)){
            return lobbyService.getIdCounter();
        }
        return 0;
    }

    /**
     * Removes a player with given name from the lobby
     * @param name
     * @return true if the player was removed, false otherwise
     */
    @DeleteMapping("/multiplayer/delete/{name}")
    public boolean deleteUserMultiPlayer(@PathVariable String name) {
        //System.out.println("DASDAS");
        if(lobbyService.removePlayer(name)) {
            return true;
        }
        return false;
    }

    /**
     * PUT request to start game, that is currently in lobby
     * @return - return trimmed game object !!!(Game will be changed to TrimmedGame later)
     */
    @PutMapping("/startGame")
    public TrimmedGame startGame(){
        if(lobbyService.startGame(1) == true) {
            return lobbyService.getGameByID(lobbyService.getIdCounter() - 1).trim();
        }
        else return null;
    }

    /**
     * Returns the names of the players, currently in the lobby
     * @return - List of the players' names
     */
    @GetMapping("/pollLobby")
    public List<String> pollLobby() {
        return lobbyService.getNames();
    }

    /**
     * Check for the current player answer to this round question
     * @param gameID
     * @param name
     * @param round
     * @param answer
     * @return returns a string based on wether or not the answer was correct or not
     */
    @PutMapping("/{gameID}/{name}/checkAnswer/{round}/{answer}")
    public String checkAnswer(@PathVariable int gameID, @PathVariable String name,
                              @PathVariable int round, @PathVariable int answer){
//        System.out.println(answer);
        if(round==-1) {
            int playerScore = lobbyService.getGameByID(gameID).getPlayers().get(name).getScore();
            {lbRepo.save(new LeaderboardEntry(name, playerScore));}
            return "correct. Your score is " + playerScore;
        }
        System.out.println("eoe");
        System.out.println(round);
        if(lobbyService.getGameByID(gameID).checkPlayerAnswer(name, round, answer)){
            int playerScore = lobbyService.getGameByID(gameID).getPlayers().get(name).getScore();
            if(round == 19) {lbRepo.save(new LeaderboardEntry(name, playerScore));}
            return "correct. Your score is " + playerScore;
        }
        int score = lobbyService.getGameByID(gameID).getPlayers().get(name).getScore();
        if(round == 19) {lbRepo.save(new LeaderboardEntry(name, score));}
        return "incorrect. Your score is " + score;
    }


    /**
     * @param gameID The id of the game
     * @param name name of the player
     * @param round round of the game
     * @param joker which joker was used (string)
     * @return returns a string hardcoded for now that says it has been received
     */
    @GetMapping("/{gameID}/{name}/joker/{round}/{joker}")
    //CHECKSTYLE:OFF
    public String receiveJoker(@PathVariable int gameID, @PathVariable String name,
                               @PathVariable int round, @PathVariable String joker) {
        //CHECKSTYLE:ON
        System.out.println(joker);

        return "joker received";
    }


    /**
     * @param gameID the id of the current game
     * @param name the name of the user
     * @param round the current round that is being played
     * @param points the number of new points that should be given to the user
     * @return a string that just confirms what has taken place for testing purposes
     */
    @GetMapping("/{gameID}/{name}/updateScore/{round}/{points}")
    public int updateUserScore(@PathVariable int gameID, @PathVariable String name,
                               @PathVariable int round, @PathVariable int points) {
        System.out.println("new points received");
        this.lobbyService.getGameByID(gameID).updatePlayerScore(name, points);

        int score = this.lobbyService.getGameByID(gameID).getPlayerScore(name);
        return score;
    }

    /**
     * @return returns a linked list of entries that store the information
     * of the leaderboard
     */
    @GetMapping("leaderboard")
    public LinkedList<LeaderboardEntry> getGameInfo(){
        return lbRepo.getAllLeaderboardEntriesOrderedByScore();
    }


    /**
     * @param gameID the current gameID
     * @return a linked list containing the names and scores of the players in
     * this multiplayer game
     */
    @GetMapping("/{gameID}/getMultiplayerLeaderBoard")
    public LinkedList<LeaderboardEntry> getMultiplayerLeaderboard(@PathVariable int gameID){
        return this.lobbyService.getGameByID(gameID).getMultiplayerLeaderboard();
    }

    /**
     * Adds a reaction for the user of the given game
     * @param gameID The id of the game
     * @param player The player who sent the reaction
     * @param reaction The reaction
     */
    @PutMapping("reaction/{gameID}/{player}/{reaction}")
    public void reaction(@PathVariable int gameID, @PathVariable String player, @PathVariable String reaction) {
        Game game = lobbyService.getGameByID(gameID);
        String[] newReaction = new String[] {player, reaction};
        game.getReactions().add(newReaction);
        System.out.println("Success: " + newReaction);
        Thread t = new Thread(()->{
            try {
                Thread.sleep(REACTION_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            game.getReactions().remove(newReaction);
        });
        t.start();
    }


    /**
     *
     * @param description
     * @param usage
     * @param source
     * @param imagePath
     */
    @PutMapping("admin/new_activity/{description}/{usage}/{source}/{imagePath}")
    public void newActivity(@PathVariable String description, @PathVariable String usage,
    @PathVariable String source, @PathVariable String imagePath) {
        //System.out.println("DASDAS");
        adminService.addActivity(splitString(description), Integer.parseInt(usage),source, imagePath);
    }


    /**
     * @param input the string that is going to be split to include spaces
     * @return the string including spaces instead of !
     */
    public String splitString(String input) {
        String[] list = input.split("!");
        String result = list[0];
        for (int i = 1; i < list.length; i ++) {
            result = result + " " + list[i];
        }
        return result;
    }


    /**
     * @param id the id of the activity you would like to delete
     */
    @DeleteMapping("admin/delete_activity/{id}")
    public void deleteActivity(@PathVariable Long id) {
        Long longid = id;
        //System.out.println(id);
        adminService.deleteActivity(id);
//        System.out.println(id);
    }
}