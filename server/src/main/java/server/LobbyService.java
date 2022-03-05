package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.ActivityRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LobbyService {
    private ActivityRepository dtBase;
    private Map<Integer, Game> games;
    private int idCounter;
    private List<Player> tempPlayers;

    @Autowired
    public LobbyService(ActivityRepository dtBase) {
        this.dtBase = dtBase;
        games = new HashMap<>();
        idCounter = 0;
        tempPlayers = new ArrayList<>();
    }

    /**
     * Start the game by creating a game instance and adding it to the map of games
     * @param gameType Single-player or multi-player
     */
    public void startGame(int gameType) {
        Game tempGame = new Game(List.copyOf(tempPlayers), idCounter, gameType, dtBase);
        Thread t = new Thread(tempGame);
        t.start();

        games.put(idCounter++, tempGame);
        tempPlayers = new ArrayList<>();

        System.out.println(tempGame);
    }

    /**
     * Adds the person with name {name} to the list of waiting players in the queue
     * @param name
     */
    public void addPlayer(String name) {
        Player person = new Player(name);
        tempPlayers.add(person);
    }

    public int getIdCounter() {
        return idCounter;
    }
}