package server.api;

import commons.LeaderboardEntry;
import commons.TrimmedGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.LobbyService;

import java.util.LinkedList;


@RestController
@RequestMapping("/")
public class LobbyController {
    private LobbyService lobbyService;

    /**
     * Creates a new LobbyController
     * @param lobbyService the lobbyService instance
     */
    @Autowired
    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
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

        // Hardcoded to start directly
        Thread t1 = new Thread(lobbyService.getGameByID(id));
        t1.start();
        return id;
    }

    /**
     * Return game info object(now full game object sent just to check)
     * @param gameID - id of the game
     * @param player - name of the requesting player
     * @return game info object
     */
    @GetMapping("{gameID}/{player}/getGameInfo")
    public TrimmedGame getGameInfo(@PathVariable int gameID, @PathVariable String player){
        //System.out.println( gameID + " connected");
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
     * Check for the current player answer to this round question
     * @param gameID
     * @param name
     * @param round
     * @param answer
     * @return returns a string based on wether or not the answer was correct or not
     */
    @PutMapping("/{gameID}/{name}/checkAnswer/{round}/{answer}")
    //CHECKSTYLE:OFF
    public String checkAnswer(@PathVariable int gameID, @PathVariable String name,
                              @PathVariable int round, @PathVariable int answer){
        System.out.println(answer);

        //CHECKSTYLE:ON

        if(lobbyService.getGameByID(gameID).checkPlayerAnswer(name, round, answer)){
            return "correct";
        }
        return "incorrect";
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
     * @return returns a linked list of entries that store the information
     * of the leaderboard
     */
    @GetMapping("leaderboard")
    public LinkedList<LeaderboardEntry> getGameInfo(){
        LinkedList<LeaderboardEntry> leaderboardList = new LinkedList();
        LeaderboardEntry entry = new LeaderboardEntry("Ivan", 2000);
        LeaderboardEntry entry1 = new LeaderboardEntry("Chris", 10000);
        leaderboardList.add(entry);
        leaderboardList.add(entry1);
        return leaderboardList;

    }

}