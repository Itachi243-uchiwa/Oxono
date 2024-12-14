package dev3.projet.oxono_g63888.model.strategy;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    private final Map<Long, TranspositionEntry> table;

    public TranspositionTable() {
        this.table = new HashMap<>();
    }

    public TranspositionEntry get(long hash) {
        return table.get(hash);
    }

    public void put(long hash, TranspositionEntry entry) {
        table.put(hash, entry);
    }

    public static class TranspositionEntry {
        final int score;
        final int depth;
        final int flag;

        public static final int EXACT = 0;
        public static final int LOWER_BOUND = 1;
        public static final int UPPER_BOUND = 2;

        public TranspositionEntry(int score, int depth, int flag) {
            this.score = score;
            this.depth = depth;
            this.flag = flag;
        }
    }
}
