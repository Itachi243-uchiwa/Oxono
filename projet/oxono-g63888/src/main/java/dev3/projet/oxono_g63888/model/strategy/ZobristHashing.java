package dev3.projet.oxono_g63888.model.strategy;

import dev3.projet.oxono_g63888.model.ColorPawn;
import dev3.projet.oxono_g63888.model.Mark;

import java.util.Random;

public class ZobristHashing {
    private final long[][][] pawnTable;
    private final long[][][] totemTable;
    private static final int PIECE_TYPES = 2;
    private static final int MARK_TYPES = 2;
    private final int size;

    public ZobristHashing(int size) {
        this.size = size;
        this.pawnTable = new long[size][size][PIECE_TYPES];
        this.totemTable = new long[size][size][MARK_TYPES];
        initializeHashKeys();
    }

    private void initializeHashKeys() {
        Random random = new Random(123456789L); // Fixed seed for reproducibility

        // Initialize pawn hash keys
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                for (int piece = 0; piece < PIECE_TYPES; piece++) {
                    pawnTable[row][col][piece] = random.nextLong();
                }
            }
        }

        // Initialize totem hash keys
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                for (int mark = 0; mark < MARK_TYPES; mark++) {
                    totemTable[row][col][mark] = random.nextLong();
                }
            }
        }
    }

    public long getHashForPawn(int row, int col, ColorPawn color) {
        return pawnTable[row][col][color == ColorPawn.BLACK ? 0 : 1];
    }

    public long getHashForTotem(int row, int col, Mark mark) {
        return totemTable[row][col][mark == Mark.X ? 0 : 1];
    }
}