package com.example.myapplication.model;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuzzleManager {
    private static final Map<String, List<Puzzle>> moodPuzzles = new HashMap<>();

    static {
        // Initialize all puzzles when class loads
        initializePuzzles();
    }

    private static void initializePuzzles() {
        // Happy Mood Puzzles (6 puzzles)
        moodPuzzles.put("happy", Arrays.asList(
                new Puzzle("Happy Puzzle 1", "What has keys but can't open locks?", "piano", "happy"),
                new Puzzle("Happy Puzzle 2", "I'm tall when young and short when old. What am I?", "candle", "happy"),
                new Puzzle("Happy Puzzle 3", "What has many teeth but can't bite?", "comb", "happy"),
                new Puzzle("Happy Puzzle 4", "What gets wetter as it dries?", "towel", "happy"),
                new Puzzle("Happy Puzzle 5", "What has a neck but no head?", "bottle", "happy"),
                new Puzzle("Happy Puzzle 6", "What can you catch but not throw?", "cold", "happy")
        ));

        // Sad Mood Puzzles (6 puzzles)
        moodPuzzles.put("sad", Arrays.asList(
                new Puzzle("Sad Puzzle 1", "The more you take, the more you leave behind. What am I?", "footsteps", "sad"),
                new Puzzle("Sad Puzzle 2", "What has a heart that doesn't beat?", "artichoke", "sad"),
                new Puzzle("Sad Puzzle 3", "What can travel around the world while staying in a corner?", "stamp", "sad"),
                new Puzzle("Sad Puzzle 4", "What has hands but can't clap?", "clock", "sad"),
                new Puzzle("Sad Puzzle 5", "What has a head, a tail, but no body?", "coin", "sad"),
                new Puzzle("Sad Puzzle 6", "What has cities but no houses, forests but no trees?", "map", "sad")
        ));

        // Angry Mood Puzzles (6 puzzles)
        moodPuzzles.put("angry", Arrays.asList(
                new Puzzle("Angry Puzzle 1", "What gets sharper the more you use it?", "brain", "angry"),
                new Puzzle("Angry Puzzle 2", "What belongs to you but others use it more?", "name", "angry"),
                new Puzzle("Angry Puzzle 3", "What has many needles but doesn't sew?", "pine tree", "angry"),
                new Puzzle("Angry Puzzle 4", "What can you hold in right hand but not left?", "left elbow", "angry"),
                new Puzzle("Angry Puzzle 5", "What has words but never speaks?", "book", "angry"),
                new Puzzle("Angry Puzzle 6", "What has a thumb and fingers but isn't alive?", "glove", "angry")
        ));

        // Neutral/Default Puzzles (6 puzzles)
        moodPuzzles.put("neutral", Arrays.asList(
                new Puzzle("Puzzle 1", "What can run but can't walk?", "river", "neutral"),
                new Puzzle("Puzzle 2", "I'm light as a feather but hard to hold. What am I?", "breath", "neutral"),
                new Puzzle("Puzzle 3", "What gets bigger when more is taken away?", "hole", "neutral"),
                new Puzzle("Puzzle 4", "What has keys but no locks, space but no room?", "keyboard", "neutral"),
                new Puzzle("Puzzle 5", "What can you break without touching it?", "promise", "neutral"),
                new Puzzle("Puzzle 6", "What goes up but never comes down?", "age", "neutral")
        ));
    }

    /**
     * Gets all puzzles for a specific mood
     * @param mood The mood to get puzzles for
     * @return List of puzzles for the mood (returns neutral puzzles if mood not found)
     */
    public static List<Puzzle> getPuzzlesForMood(String mood) {
        String normalizedMood = mood.toLowerCase();
        if (!moodPuzzles.containsKey(normalizedMood)) {
            normalizedMood = "neutral";
        }
        return new ArrayList<>(moodPuzzles.get(normalizedMood));
    }

    /**
     * Gets shuffled puzzles for a specific mood
     * @param mood The mood to get puzzles for
     * @return Shuffled list of puzzles (returns neutral if mood not found)
     */
    public static List<Puzzle> getShuffledPuzzlesForMood(String mood) {
        List<Puzzle> puzzles = getPuzzlesForMood(mood);
        Collections.shuffle(puzzles);
        return puzzles;
    }

    /**
     * Gets a single random puzzle for a mood
     * @param mood The mood to get puzzle for
     * @return A random puzzle (returns neutral if mood not found)
     */
    public static Puzzle getRandomPuzzle(String mood) {
        List<Puzzle> puzzles = getPuzzlesForMood(mood);
        return puzzles.get((int)(Math.random() * puzzles.size()));
    }
}
