// TranspositionTable.java
package dev3.projet.oxono_g63888.model.strategy;

import dev3.projet.oxono_g63888.model.Position;
import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    private static final int DEFAULT_SIZE = 1000000;
    private final Map<Long, Entry> table;
    private final int maxSize;

    public TranspositionTable() {
        this(DEFAULT_SIZE);
    }

    public TranspositionTable(int maxSize) {
        this.maxSize = maxSize;
        this.table = new HashMap<>();
    }

    public void store(long zobristHash, int depth, int score, Move moveTotem, Move movePawn) {
        if (table.size() >= maxSize) {
            // Simple stratégie de remplacement : supprimer une entrée aléatoire
            if (!table.isEmpty()) {
                table.remove(table.keySet().iterator().next());
            }
        }
        table.put(zobristHash, new Entry(depth, score, moveTotem, movePawn));
    }

    public Entry probe(long zobristHash) {
        return table.get(zobristHash);
    }

    public record Entry(int depth, int score, Move moveTotem, Move movePawn) {}
}