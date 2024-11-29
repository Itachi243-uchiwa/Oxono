
package dev3.projet.oxono_g63888.model.strategy;

import dev3.projet.oxono_g63888.model.*;
import java.util.*;

/**
 * Advanced AI strategy using enhanced Negamax algorithm with sophisticated evaluation.
 */
public class SmartAIStrategy implements AIStrategy {
    private final ColorPawn color;
    private Move moveTotem;
    private Move movePawn;

    // Scoring constants with refined values
    private static final int WIN_SCORE = 10000000;
    private static final int BLOCK_WIN_SCORE = 800000;
    private static final int POTENTIAL_WIN_SCORE = 300000;
    private static final int DANGEROUS_ALIGNMENT_SCORE = 250000;
    private static final int POTENTIAL_ALIGNMENT_SCORE = 150000;
    private static final int TWO_COLOR_ALIGNMENT_RISK = 700000;
    private static final int CENTER_CONTROL_SCORE = 500;
    private static final int CORNER_PENALTY = -300;
    private static final int MOBILITY_SCORE = 200;

    private static final int MAX_DEPTH = 4; // Increased depth for better lookahead
    private int recursiveCalls;
    private final Map<String, Integer> transpositionTable;

    public SmartAIStrategy(ColorPawn color) {
        this.color = color;
        this.movePawn = null;
        this.moveTotem = null;
        this.recursiveCalls = 0;
        this.transpositionTable = new HashMap<>();
    }

    @Override
    public Move getNextMove(Board board) {
        return movePawn;
    }

    @Override
    public Move getNextTotemMove(Board board, AIPlayer ai) {
        long startTime = System.currentTimeMillis();
        int bestScore = iterativeDeepeningSearch(board, ai)/ recursiveCalls;

        Object[] opponentBlockScore = simulationBlockOpponent(board, ai);
        int scoreOpp = (int) opponentBlockScore[0];

        if (scoreOpp > bestScore) {
            if (opponentBlockScore[1] instanceof Move && opponentBlockScore[2] instanceof Move) {
                movePawn = (Move) opponentBlockScore[1];
                moveTotem = (Move) opponentBlockScore[2];
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
        System.out.println("Nodes explored: " + recursiveCalls);
        System.out.println(moveTotem.toString());
        return moveTotem;
    }

    /**
     * Implements iterative deepening search to find the best move within time constraints.
     */
    private int iterativeDeepeningSearch(Board board, AIPlayer ai) {
        int bestScore = Integer.MIN_VALUE;
        int currentDepth = 1;
        long startTime = System.currentTimeMillis();
        long timeLimit = 20000; // 5 seconds time limit

        while (currentDepth <= MAX_DEPTH) {
            int score = negamaxSearch(board, ai, currentDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

            if (System.currentTimeMillis() - startTime > timeLimit) {
                break;
            }

            bestScore = score;
            currentDepth++;
        }

        return bestScore;
    }

    private int negamaxSearch(Board board, AIPlayer ai, int depth, int alpha, int beta, boolean maximizingPlayer) {
        recursiveCalls++;

        String boardHash = getBoardHash(board);
        if (transpositionTable.containsKey(boardHash)) {
            return transpositionTable.get(boardHash);
        }

        if (depth == 0) {
            int score = evaluateBoard(board);
            transpositionTable.put(boardHash, score);
            return score;
        }

        List<Move[]> possibleMoves = generateMoves(board, ai);
        if (possibleMoves.isEmpty()) {
            return evaluateBoard(board);
        }

        int bestScore = Integer.MIN_VALUE;

        for (Move[] moves : possibleMoves) {
            Board boardCopy = board.copy();
            applyMoves(boardCopy, moves[0], moves[1]); // Apply totem and pawn moves

            if (isWinningMove(boardCopy, moves[1])) {
                moveTotem = moves[0];
                movePawn = moves[1];
                return WIN_SCORE;
            }

            int score = -negamaxSearch(boardCopy, ai, depth - 1, -beta, -alpha, !maximizingPlayer);

            if (score > bestScore) {
                bestScore = score;
                   if (depth == MAX_DEPTH){
                    moveTotem = moves[0];
                    movePawn = moves[1];
                   }

            }

            alpha = Math.max(alpha, score);
            if (alpha >= beta) {
                break;
            }
        }

        transpositionTable.put(boardHash, bestScore);
        return bestScore;
    }

    private List<Move[]> generateMoves(Board board, AIPlayer ai) {
        List<Move[]> moves = new ArrayList<>();

        for (Mark mark : Mark.values()) {
            if (!ai.hasPawn(mark)) continue;

            Position currentPos = board.getTotemPosition(mark);
            Totem totem = new Totem(mark);
            List<Position> totemMoves = board.getMovesPossibles(currentPos);

            for (Position totemPos : totemMoves) {
                Board tempBoard = board.copy();
                tempBoard.moveTotem(totem, totemPos);
                List<Position> pawnMoves = tempBoard.getInsertionPositions(totemPos);

                for (Position pawnPos : pawnMoves) {
                    Pawn pawn = new Pawn(color, mark);
                    moves.add(new Move[]{
                            new Move(totem, totemPos),
                            new Move(pawn, pawnPos)
                    });
                }

            }
        }

        // Sort moves based on preliminary evaluation
        moves.sort((m1, m2) -> {
            Board b1 = board.copy();
            Board b2 = board.copy();
            applyMoves(b1, m1[0], m1[1]);
            applyMoves(b2, m2[0], m2[1]);
            return Integer.compare(evaluateBoard(b2), evaluateBoard(b1));
        });

        return moves;
    }

    private void applyMoves(Board board, Move totemMove, Move pawnMove) {
        board.moveTotem((Totem)totemMove.token(), totemMove.movePosition());
        board.insertPawn((Pawn)pawnMove.token(), pawnMove.movePosition(), totemMove.movePosition());
    }

    private boolean isWinningMove(Board board, Move pawnMove) {
        return board.checkWin((Pawn)pawnMove.token(), pawnMove.movePosition());
    }

    private String getBoardHash(Board board) {
        StringBuilder hash = new StringBuilder();
        int size = board.getSize();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Position pos = new Position(i, j);
                Token token = board.getToken(pos);
                if (token instanceof Pawn pawn) {
                    hash.append(pawn.getColor()).append(pawn.getMark());
                } else if (token instanceof Totem totem) {
                    hash.append("T").append(totem.getMark());
                } else {
                    hash.append(".");
                }
            }
        }

        return hash.toString();
    }

    private int evaluateBoard(Board board) {
        int score = 0;
        int boardSize = board.getSize();

        // Strategic position evaluation
        score += evaluatePositionalAdvantage(board);

        // Threat analysis
        score += evaluateThreats(board);

        // Mobility evaluation
        score += evaluateMobility(board);

        // Pattern recognition
        score += evaluatePatterns(board);

        return score;
    }

    private int evaluatePositionalAdvantage(Board board) {
        int score = 0;
        int boardSize = board.getSize();
        int center = boardSize / 2;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Position pos = new Position(row, col);
                Pawn pawn = isPawn(pos, board);

                if (pawn != null) {
                    // Center control bonus
                    int distanceToCenter = Math.abs(row - center) + Math.abs(col - center);
                    int positionScore = CENTER_CONTROL_SCORE / (distanceToCenter + 1);

                    // Corner penalty
                    if ((row == 0 || row == boardSize - 1) && (col == 0 || col == boardSize - 1)) {
                        positionScore += CORNER_PENALTY;
                    }

                    score += pawn.getColor() == color ? positionScore : -positionScore;
                }
            }
        }

        return score;
    }

    private int evaluateThreats(Board board) {
        int score = 0;
        ColorPawn opponentColor = (color == ColorPawn.BLACK) ? ColorPawn.PINK : ColorPawn.BLACK;

        // Evaluate immediate threats
        score += evaluateImmediateThreats(board, color) - evaluateImmediateThreats(board, opponentColor);

        // Evaluate potential threats
        score += evaluatePotentialThreats(board, color) - evaluatePotentialThreats(board, opponentColor);

        return score;
    }

    private int evaluateImmediateThreats(Board board, ColorPawn playerColor) {
        int score = 0;
        int boardSize = board.getSize();

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Position pos = new Position(row, col);
                Pawn pawn = isPawn(pos, board);

                if (pawn != null && pawn.getColor() == playerColor) {
                    // Check for three in a row
                    if (hasThreeAligned(board, pos, playerColor) > 0) {
                        score += DANGEROUS_ALIGNMENT_SCORE;
                    }

                    // Check for potential winning moves
                    if (checkTwoColorAlignmentRisk(board, pos, pawn) >= 2) {
                        score += POTENTIAL_WIN_SCORE;
                    }
                }
            }
        }

        return score;
    }

    private int evaluatePotentialThreats(Board board, ColorPawn playerColor) {
        int score = 0;
        int boardSize = board.getSize();

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Position pos = new Position(row, col);
                if (board.getToken(pos) == null) {
                    // Evaluate empty positions for potential threats
                    score += evaluateEmptyPosition(board, pos, playerColor);
                }
            }
        }

        return score;
    }

    private int evaluateEmptyPosition(Board board, Position pos, ColorPawn playerColor) {
        int score = 0;

        // Check all directions for potential alignments
        for (Direction dir : Direction.values()) {
            int consecutive = countConsecutiveSameColor(board, pos, dir.getDeltaX(), dir.getDeltaY(), playerColor);
            if (consecutive >= 2) {
                score += POTENTIAL_ALIGNMENT_SCORE * consecutive;
            }
        }

        return score;
    }

    private int evaluateMobility(Board board) {
        int score = 0;
        int boardSize = board.getSize();

        // Count available moves for both players
        int myMoves = 0;
        int opponentMoves = 0;

        for (Mark mark : Mark.values()) {
            Position totemPos = board.getTotemPosition(mark);
            if (totemPos != null) {
                List<Position> moves = board.getMovesPossibles(totemPos);
                if (board.getToken(totemPos) instanceof Totem) {
                    myMoves += moves.size();
                } else {
                    opponentMoves += moves.size();
                }
            }
        }

        score += (myMoves - opponentMoves) * MOBILITY_SCORE;
        return score;
    }

    private int evaluatePatterns(Board board) {
        int score = 0;

        // Evaluate diagonal patterns
        score += evaluateDiagonalPatterns(board);

        // Evaluate horizontal and vertical patterns
        score += evaluateOrthogonalPatterns(board);

        return score;
    }

    private int evaluateDiagonalPatterns(Board board) {
        int score = 0;
        int boardSize = board.getSize();

        // Main diagonal
        for (int i = 0; i < boardSize - 2; i++) {
            score += evaluatePattern(board,
                    new Position(i, i),
                    new Position(i + 1, i + 1),
                    new Position(i + 2, i + 2));
        }

        // Anti-diagonal
        for (int i = 0; i < boardSize - 2; i++) {
            score += evaluatePattern(board,
                    new Position(i, boardSize - 1 - i),
                    new Position(i + 1, boardSize - 2 - i),
                    new Position(i + 2, boardSize - 3 - i));
        }

        return score;
    }

    private int evaluateOrthogonalPatterns(Board board) {
        int score = 0;
        int boardSize = board.getSize();

        // Horizontal patterns
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize - 2; col++) {
                score += evaluatePattern(board,
                        new Position(row, col),
                        new Position(row, col + 1),
                        new Position(row, col + 2));
            }
        }

        // Vertical patterns
        for (int col = 0; col < boardSize; col++) {
            for (int row = 0; row < boardSize - 2; row++) {
                score += evaluatePattern(board,
                        new Position(row, col),
                        new Position(row + 1, col),
                        new Position(row + 2, col));
            }
        }

        return score;
    }

    private int evaluatePattern(Board board, Position p1, Position p2, Position p3) {
        Pawn pawn1 = isPawn(p1, board);
        Pawn pawn2 = isPawn(p2, board);
        Pawn pawn3 = isPawn(p3, board);

        if (pawn1 == null || pawn2 == null || pawn3 == null) {
            return 0;
        }

        int score = 0;

        // Check for color patterns
        if (pawn1.getColor() == pawn2.getColor() && pawn2.getColor() == pawn3.getColor()) {
            score += pawn1.getColor() == color ? DANGEROUS_ALIGNMENT_SCORE : -DANGEROUS_ALIGNMENT_SCORE;
        }

        // Check for mark patterns
        if (pawn1.getMark() == pawn2.getMark() && pawn2.getMark() == pawn3.getMark()) {
            score -= DANGEROUS_ALIGNMENT_SCORE;
        }

        return score;
    }

    // Existing helper methods remain the same
    private Pawn isPawn(Position pos, Board board) {
        if (board.getToken(pos) instanceof Pawn pawn) {
            return pawn;
        }
        return null;
    }


    /**
     * Évalue le risque d'alignement pour deux pions de même couleur.
     * @param board Plateau de jeu
     * @param pos Position du pion
     * @param pawn Pion à évaluer
     * @return Score du risque d'alignement
     */
    private int checkTwoColorAlignmentRisk(Board board, Position pos, Pawn pawn) {
        int risk = 0;

        for (Direction dir : Direction.values()) {
            int consecutiveSame = countConsecutiveSameColor(board, pos, dir.getDeltaX(), dir.getDeltaY(), pawn.getColor());
            int emptySpaces = countEmptySpacesForPotentialWin(board, pos, dir.getDeltaX(), dir.getDeltaY(), pawn.getColor());

            if (consecutiveSame == 2 && emptySpaces >= 1) {
                risk += 1;
            }
        }

        return risk;
    }

    /**
     * Compte le nombre de pions consécutifs de même couleur.
     * @param board Plateau de jeu
     * @param startPos Position de départ
     * @param dx Déplacement horizontal
     * @param dy Déplacement vertical
     * @param color Couleur à compter
     * @return Nombre de pions consécutifs
     */
    private int countConsecutiveSameColor(Board board, Position startPos, int dx, int dy, ColorPawn color) {
        int count = 1; // Le pion de départ
        int x = startPos.row();
        int y = startPos.column();

        // Vérification dans une direction
        for (int i = 1; i < board.getSize(); i++) {
            x += dx;
            y += dy;
            if (x < 0 || y < 0 || x >= board.getSize() || y >= board.getSize()) break;

            Pawn pawn = isPawn(new Position(x, y), board);
            if (pawn == null || pawn.getColor() != color) break;
            count++;
        }

        // Réinitialisation pour vérifier l'autre direction
        x = startPos.row();
        y = startPos.column();

        // Vérification dans la direction opposée
        for (int i = 1; i < board.getSize(); i++) {
            x -= dx;
            y -= dy;
            if (x < 0 || y < 0 || x >= board.getSize() || y >= board.getSize()) break;

            Pawn pawn = isPawn(new Position(x, y), board);
            if (pawn == null || pawn.getColor() != color) break;
            count++;
        }

        return count;
    }

    /**
     * Compte les espaces vides permettant une potentielle victoire.
     * @param board Plateau de jeu
     * @param startPos Position de départ
     * @param dx Déplacement horizontal
     * @param dy Déplacement vertical
     * @param color Couleur à vérifier
     * @return Nombre d'espaces vides
     */
    private int countEmptySpacesForPotentialWin(Board board, Position startPos, int dx, int dy, ColorPawn color) {
        int emptySpaces = 0;
        int x = startPos.row();
        int y = startPos.column();

        // Vérification des espaces vides dans une direction
        for (int i = 1; i < board.getSize(); i++) {
            x += dx;
            y += dy;
            if (x < 0 || y < 0 || x >= board.getSize() || y >= board.getSize()) break;

            Pawn pawn = isPawn(new Position(x, y), board);
            if (pawn == null) emptySpaces++;
            else if (pawn.getColor() != color) break;
        }

        // Réinitialisation pour vérifier l'autre direction
        x = startPos.row();
        y = startPos.column();

        // Vérification des espaces vides dans la direction opposée
        for (int i = 1; i < board.getSize(); i++) {
            x -= dx;
            y -= dy;
            if (x < 0 || y < 0 || x >= board.getSize() || y >= board.getSize()) break;

            Pawn pawn = isPawn(new Position(x, y), board);
            if (pawn == null) emptySpaces++;
            else if (pawn.getColor() != color) break;
        }

        return emptySpaces;
    }




    private int hasThreeAlignedmark(Board board, Position pos, Mark mark) {
        int rows = board.getSize(); // Nombre de lignes du plateau
        int cols = board.getSize(); // Nombre de colonnes du plateau
        int score = 0;

        // Directions horizontale et verticale
        int[][] directions = {
                {0, 1},  // Droite
                {1, 0}   // Bas
        };

        for (int[] dir : directions) {
            int count = 1; // Inclut la position actuelle
            int gap = 0;   // Pour compter les cases vides séparatrices

            // Vérification dans une direction
            count += countAlignedmark(board, pos, dir[0], dir[1], mark, rows, cols, gap);

            // Vérification dans la direction opposée
            count += countAlignedmark(board, pos, -dir[0], -dir[1], mark, rows, cols, gap);

            if (count >= 3) {
                score += DANGEROUS_ALIGNMENT_SCORE; // Trois pions alignés trouvés
            }
        }

        return score;
    }

    private int countAlignedmark(Board board, Position startPos, int dx, int dy, Mark mark, int rows, int cols, int gap) {
        int x = startPos.row() + dx;
        int y = startPos.column() + dy;
        int count = 0;

        while (x >= 0 && y >= 0 && x < rows && y < cols) {
            Pawn pawn = isPawn(new Position(x, y), board);

            Mark current = pawn != null ? pawn.getMark() : null;


            if (current == null) {
                gap++;
                if (gap > 1) break; // S'il y a plus d'une case vide, on stoppe
            } else if (current.equals(mark)) {
                count++;
            } else {
                break; // Si le pion est d'une autre couleur, on arrête
            }

            x += dx;
            y += dy;
        }

        return count;
    }


    private Object[] simulationBlockOpponent(Board board, AIPlayer ai){
        int score = 0;
        Object[] tabsimulation = new Object[3];
        ColorPawn opponentColor = color == ColorPawn.BLACK ? ColorPawn.PINK : ColorPawn.BLACK;

        for (Mark mark : Mark.values()){

            if (!ai.hasPawn(mark)){
                continue;
            }
            Position currentPosition = board.getTotemPosition(mark);

            Totem totem = board.getTotem(currentPosition);

            List<Position> positionsForTotem = board.getMovesPossibles(currentPosition);
            for (Position posTotem : positionsForTotem){

                Board boardCopy = board.copy();

                boardCopy.moveTotem(totem, posTotem);

                List<Position> positionsForPawn = boardCopy.getInsertionPositions(posTotem);

                for (Position posPawn : positionsForPawn){
                    Pawn pawn = new Pawn(opponentColor, mark);

                    boardCopy.insertPawn(pawn, posPawn, posTotem);

                    if (boardCopy.checkWin(pawn, posPawn)){
                        score += BLOCK_WIN_SCORE;
                        tabsimulation[0] = score;
                        tabsimulation[1] = new Move(new Pawn(color, mark), posPawn);
                        tabsimulation[2] = new Move(totem, posTotem);
                    } else if (checkTwoColorAlignmentRisk(boardCopy, posPawn, pawn) >= 3 ){
                        score += TWO_COLOR_ALIGNMENT_RISK;
                        tabsimulation[0] = score;
                        tabsimulation[1] = new Move(new Pawn(color, mark), posPawn);
                        tabsimulation[2] = new Move(totem, posTotem);
                    }
                    boardCopy.removePawn(posPawn);
                }
            }
        }
        if (score == 0){
            Arrays.fill(tabsimulation, 0);
        }
        return tabsimulation;
    }


private int hasThreeAligned(Board board, Position pos, ColorPawn color) {
        int rows = board.getSize(); // Nombre de lignes du plateau
        int cols = board.getSize(); // Nombre de colonnes du plateau
        int score = 0;

        // Directions horizontale et verticale
        int[][] directions = {
                {0, 1},  // Droite
                {1, 0}   // Bas
        };

        for (int[] dir : directions) {
            int count = 1; // Inclut la position actuelle
            int gap = 0;   // Pour compter les cases vides séparatrices

            // Vérification dans une direction
            count += countAligned(board, pos, dir[0], dir[1], color, rows, cols, gap);

            // Vérification dans la direction opposée
            count += countAligned(board, pos, -dir[0], -dir[1], color, rows, cols, gap);

            if (count >= 3) {
                score += POTENTIAL_WIN_SCORE; // Trois pions alignés trouvés
            }
        }

        return score;
    }

    private int countAligned(Board board, Position startPos, int dx, int dy, ColorPawn color, int rows, int cols, int gap) {
        int x = startPos.row() + dx;
        int y = startPos.column() + dy;
        int count = 0;

        while (x >= 0 && y >= 0 && x < rows && y < cols) {
            Pawn pawn = isPawn(new Position(x, y), board);

            ColorPawn current = pawn != null ? pawn.getColor() : null;


            if (current == null) {
                gap++;
                if (gap > 1) break; // S'il y a plus d'une case vide, on stoppe
            } else if (current.equals(color)) {
                count++;
            } else {
                break; // Si le pion est d'une autre couleur, on arrête
            }

            x += dx;
            y += dy;
        }

        return count;
    }
    }

