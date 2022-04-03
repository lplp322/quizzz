package server.database;

import server.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Repository
public interface LeaderboardRepository extends JpaRepository<LeaderboardEntry, Long> {

    /**
     * Selects all entries ordered by score
     * @return LinkedList containing all entries in sorted order
     */
    @Query("SELECT a FROM LeaderboardEntry a ORDER BY a.score DESC")
    LinkedList<LeaderboardEntry> getAllLeaderboardEntriesOrderedByScore();

    /**
     * Deletes all entries
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM LeaderboardEntry")
    void deleteAllEntries();

    /**
     * Select all leaderboard entries by name
     * @param name The name to select
     * @return LinkedList with all leaderboard entries for the name
     */
    @Query("SELECT a FROM LeaderboardEntry a WHERE a.name = :name")
    LinkedList<LeaderboardEntry> getLeaderboardEntriesByName(@Param("name") String name);

    /**
     * Select all leaderboard entries with score lower than the given value
     * @param max the max score
     * @return LinkedList with all the leaderboard entries with score lower than max
     */
    @Query("SELECT a FROM LeaderboardEntry a WHERE a.score < :max")
    LinkedList<LeaderboardEntry> getLeaderboardEntriesScoresLowerThan(@Param("max") int max);

    /**
     * Select all leaderboard entries with score higher than the given value
     * @param min the min score
     * @return LinkedList with all the leaderboard entries with score higher than min
     */
    @Query("SELECT a FROM LeaderboardEntry a WHERE a.score > :min")
    LinkedList<LeaderboardEntry> getLeaderboardEntriesScoresHigherThan(@Param("min") int min);

    /**
     * Select distinct names of leaderboard entries
     * @return List of distinct names
     */
    @Query("SELECT distinct a.name FROM LeaderboardEntry a")
    List<String> getDistinctNames();

    /**
     * Gets the count of leaderboard entries with distinct names
     * @return Count of distinct leaderboard entries
     */
    @Query("SELECT COUNT(DISTINCT a.name) FROM LeaderboardEntry a")
    int getDistinctNamesCount();
}