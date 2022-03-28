package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.ActivityRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class LobbyService {
    private ActivityRepository dtBase;
    private Map<Integer, Game> games;
    private int idCounter;
    private Map<String, Player> tempPlayers;
    private Set<String> names;
    private int singlePlayerID;

    /**
     * constructor for the lobby-service class
     * there are two types of id - idCounter and singlePlayerID
     * idCounter is for multiplayer and is > 0
     * singlePlayerID is for singlePlayer and goes from -1 to -INF
     * @param dtBase
     */
    @Autowired
    public LobbyService(ActivityRepository dtBase) {
        this.dtBase = dtBase;
        games = new HashMap<>();
        idCounter = 1;
        tempPlayers = new HashMap<>();
        names = new HashSet<>();
        singlePlayerID = -1;
    }

    /**
     * Start the game by creating a game instance and adding it to the map of games
     * @param gameType Single-player or multi-player
     * @return false if no players there
     */
    public boolean startGame(int gameType) {
        if(names.size() == 0) return false;
        Game tempGame = new Game(Map.copyOf(tempPlayers), idCounter, gameType, dtBase, new ArrayList<>());
        Thread t = new Thread(tempGame);
        t.start();

        games.put(idCounter++, tempGame);
        tempPlayers = new HashMap<>();
        names = new HashSet<>();

        System.out.println(tempGame);
        return true;
    }

    /**
     * Adds the person with name {name} to the list of waiting players in the queue
     * @param name
     * @return true if player successfully added
     */
    public boolean addPlayer(String name) {
        if(names.add(name)) {
            Player person = new Player(name);
            tempPlayers.put(name, person);
            return true;
        }
        return false;
    }

    /**
     * Removes a player with given name from the lobby
     * @param name
     * @return true if the player with the given name was removed successfully, false otherwise
     */
    public boolean removePlayer(String name) {
        if(names.contains(name)) {
            names.remove(name);
            tempPlayers.remove(name);
            return true;
        }
        return false;
    }

    /**
     * returns the idCounter
     * @return the idCounter
     */
    public int getIdCounter() {
        return idCounter;
    }

    /**
     * Creating a game for one player
     * @param name - name of the player
     * @return id of the game
     */
    public int createSinglePlayerGame(String name) {
        Player person = new Player(name);
        Map<String, Player> players = new HashMap<>();
        players.put(name, person);
        Game newGame = new Game(players, singlePlayerID, 0, dtBase, new ArrayList<>());
        games.put(singlePlayerID--, newGame);
        return singlePlayerID+1;
    }

    /**
     * Return game object by this id
     * @param id - id of the game
     * @return game object with this id
     */
    public Game getGameByID(int id){
        return games.get(id);
    }

    /**
     * Returns the list of all players in the lobby
     * @return list of names
     */
    public List<String> getNames() {
        return new ArrayList<>(names);
    }
}
