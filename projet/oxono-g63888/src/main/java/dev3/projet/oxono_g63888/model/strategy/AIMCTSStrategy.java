package dev3.projet.oxono_g63888.model.strategy;

import dev3.projet.oxono_g63888.model.*;

import java.util.*;

public class AIMCTSStrategy implements AIStrategy {
    private final ColorPawn color;
    private final int simulationsCount;
    private final double explorationConstant;
    private Position lastTotemPosition;
    private TranspositionTable transpositionTable;

    private static final double WIN_SCORE = 10000.0;
    private static final double BLOCK_SCORE = 5000.0;
    private static final double NEAR_WIN_SCORE = 100.0;
    private static final double PREVENT_NEAR_WIN_SCORE = 1200.0;

    public AIMCTSStrategy(ColorPawn color) {
        this.color = color;
        this.simulationsCount = 1000; // Nombre de simulations par décision
        this.explorationConstant = Math.sqrt(2); // Constante d'exploration UCT
        transpositionTable = new TranspositionTable();
    }

    @Override
    public Move getNextMove(Board board) {
        // Création du nœud racine pour le placement du pion
        MCTSNode rootNode = new MCTSNode(null, null, board.copy(), null);

        // Exécution des simulations MCTS
        for (int i = 0; i < simulationsCount; i++) {
            MCTSNode selectedNode = selection(rootNode);
            MCTSNode expandedNode = expansion(selectedNode);
            double result = simulation(expandedNode);
            backpropagation(expandedNode, result);
        }

        // Sélection du meilleur coup
        MCTSNode bestChild = getBestChild(rootNode, 0); // Exploitation pure
        lastTotemPosition = bestChild.getTotemPosition();
        return new Move(new Pawn(color, board.getTotem(lastTotemPosition).getMark()),
                bestChild.getMovePosition());
    }

    @Override
    public Move getNextTotemMove(Board board, AIPlayer ai) {
        // Détermination du meilleur totem à jouer
        Mark markToPlay = chooseBestTotem(board, ai);
        Position currentTotemPos = board.getTotemPosition(markToPlay);

        MCTSNode rootNode = new MCTSNode(null, null, board.copy(), null);

        // Exécution des simulations MCTS pour le mouvement du totem
        for (int i = 0; i < simulationsCount; i++) {
            MCTSNode selectedNode = selection(rootNode);
            MCTSNode expandedNode = expansion(selectedNode);
            double result = simulation(expandedNode);
            backpropagation(expandedNode, result);
        }

        MCTSNode bestChild = getBestChild(rootNode, 0);
        Totem totem = board.getTotem(currentTotemPos);
        lastTotemPosition = bestChild.getTotemPosition();

        return new Move(totem, lastTotemPosition);
    }

    // Méthodes privées pour MCTS
    private MCTSNode selection(MCTSNode node) {
        while (!node.isTerminal() && node.isFullyExpanded()) {
            node = getBestChild(node, explorationConstant);
        }
        return node;
    }

    private MCTSNode expansion(MCTSNode node) {
        if (node.isTerminal()) {
            return node;
        }

        List<Move> possibleMoves = node.getUntriedMoves();
        if (possibleMoves.isEmpty()) {
            return node;
        }

        Move move = possibleMoves.remove(0);
        Board newBoard = node.getBoard().copy();
        newBoard.moveTotem(move.token(), move.movePosition());

        MCTSNode newNode = new MCTSNode(move, node, newBoard, possibleMoves);
        node.addChild(newNode);

        return newNode;
    }

    private void backpropagation(MCTSNode node, double result) {
        while (node != null) {
            node.incrementVisits();
            node.updateScore(result);
            node = node.getParent();
        }
    }

    private MCTSNode getBestChild(MCTSNode node, double explorationValue) {
        return node.getChildren().stream()
                .max(Comparator.comparingDouble(child ->
                        calculateUCT(child, explorationValue)))
                .orElseThrow(() -> new IllegalStateException("No children found"));
    }

    private double calculateUCT(MCTSNode node, double explorationValue) {
        if (node.getVisits() == 0) {
            return Double.MAX_VALUE;
        }

        double exploitation = node.getScore() / node.getVisits();
        double exploration = Math.sqrt(Math.log(node.getParent().getVisits()) / node.getVisits());

        return exploitation + explorationValue * exploration;
    }

    // Méthodes utilitaires
    private Mark chooseBestTotem(Board board, AIPlayer ai) {
        if (ai.hasPawn(Mark.X) && !ai.hasPawn(Mark.O)) return Mark.X;
        if (!ai.hasPawn(Mark.X) && ai.hasPawn(Mark.O)) return Mark.O;

        // Si les deux totems sont disponibles, évaluer le meilleur
        double scoreX = evaluateTotemPosition(board, Mark.X);
        double scoreO = evaluateTotemPosition(board, Mark.O);

        return scoreX >= scoreO ? Mark.X : Mark.O;
    }

    private double evaluateTotemPosition(Board board, Mark mark) {
        Position totemPos = board.getTotemPosition(mark);
        List<Position> possibleMoves = board.getMovesPossibles(totemPos);

        return possibleMoves.stream()
                .mapToDouble(pos -> evaluatePosition(board))
                .average()
                .orElse(0.0);
    }

    private double evaluatePosition(Board board) {
        double score = 0.0;

        // Vérifie d'abord si on peut gagner
        score += evaluateWinningMoves(board, color) * WIN_SCORE;

        // Vérifie si l'adversaire peut gagner et bloque
        ColorPawn opponentColor = (color == ColorPawn.BLACK) ? ColorPawn.PINK : ColorPawn.BLACK;
        score -= evaluateWinningMoves(board, opponentColor) * BLOCK_SCORE;

        // Évalue les séquences de 3 pions
        score += evaluateNearWinningPositions(board, color) * NEAR_WIN_SCORE;
        score -= evaluateNearWinningPositions(board, opponentColor) * PREVENT_NEAR_WIN_SCORE;

        // Évalue les alignements de marques
        score += evaluateMarkAlignments(board) * 200.0;

        // Ajoute les autres évaluations avec des poids moins importants
        score += evaluateAlignments(board) * 50.0;
        score += evaluateCenterControl(board) * 30.0;
        score += evaluateMobility(board) * 20.0;

        return score;
    }
    private int evaluateWinningMoves(Board board, ColorPawn playerColor) {
        int winningMoves = 0;

        // Vérifie toutes les positions vides pour des coups gagnants potentiels
        for (Position pos : board.allPositionsEmpty()) {
            // Vérifie pour chaque marque (X et O)
            for (Mark mark : Mark.values()) {
                Pawn testPawn = new Pawn(playerColor, mark);
                // Simule le placement du pion
                board.insertPawn(testPawn, pos, board.getTotemPosition(mark));
                if (board.checkWin(testPawn, pos)) {
                    winningMoves++;
                }
                if (!board.isEmpty(pos)) {
                    board.removePawn(pos);
                }
            }
        }
        return winningMoves;
    }
    private int evaluateNearWinningPositions(Board board, ColorPawn playerColor) {
        int nearWins = 0;

        // Vérifie les séquences de 3 pions qui peuvent devenir 4
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Position pos = new Position(row, col);
                Token token = board.getToken(pos);

                if (token instanceof Pawn && ((Pawn) token).getColor() == playerColor) {
                    nearWins += checkNearWinSequences(board, pos, playerColor);
                }
            }
        }
        return nearWins;
    }

    private int checkNearWinSequences(Board board, Position pos, ColorPawn color) {
        int sequences = 0;
        Pawn pawn = (Pawn) board.getToken(pos);

        // Vérifie horizontalement
        sequences += checkDirectionalSequence(board, pos, Direction.RIGHT, color, pawn.getMark());
        // Vérifie verticalement
        sequences += checkDirectionalSequence(board, pos, Direction.DOWN, color, pawn.getMark());

        return sequences;
    }

    private double evaluateAlignments(Board board) {
        double score = 0.0;
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Position pos = new Position(row, col);
                Token token = board.getToken(pos);
                if (token instanceof Pawn) {
                    Pawn pawn = (Pawn) token;
                    if (pawn.getColor() == color) {
                        // Check horizontal and vertical alignments
                        List<Position> horizontalAlignments = checkAlignment(board, pos, Direction.RIGHT);
                        List<Position> verticalAlignments = checkAlignment(board, pos, Direction.DOWN);

                        score += calculateAlignmentScore(horizontalAlignments.size());
                        score += calculateAlignmentScore(verticalAlignments.size());
                    }
                }
            }
        }
        return score;
    }

    private double calculateAlignmentScore(int length) {
        return switch (length) {
            case 2 -> 1.0;
            case 3 -> 5.0;
            case 4 -> 100.0;
            default -> 0.0;
        };
    }

    private double evaluateCenterControl(Board board) {
        double score = 0.0;
        int center = board.getSize() / 2;
        int radius = 1;

        for (int row = center - radius; row <= center + radius; row++) {
            for (int col = center - radius; col <= center + radius; col++) {
                Position pos = new Position(row, col);
                Token token = board.getToken(pos);
                if (token instanceof Pawn && ((Pawn) token).getColor() == color) {
                    score += 2.0;
                }
            }
        }
        return score;
    }

    private double evaluateMobility(Board board) {
        double score = 0.0;
        // Score for totem mobility
        for (Mark mark : Mark.values()) {
            Position totemPos = board.getTotemPosition(mark);
            List<Position> possibleMoves = board.getMovesPossibles(totemPos);
            score += possibleMoves.size() * 0.5;
        }

        // Score for pawn placement options
        for (Mark mark : Mark.values()) {
            Position totemPos = board.getTotemPosition(mark);
            List<Position> insertionPositions = board.getInsertionPositions(totemPos);
            score += insertionPositions.size() * 0.3;
        }
        return score;
    }

    private boolean isGameOver(Board board) {
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Position pos = new Position(row, col);
                Token token = board.getToken(pos);
                if (token instanceof Pawn) {
                    if (board.checkWin((Pawn) token, pos)) {
                        return true;
                    }
                }
            }
        }
        // Check if board is full
        return board.allPositionsEmpty().isEmpty();
    }
    private List<Position> checkAlignment(Board board, Position start, Direction dir) {
        List<Position> aligned = new ArrayList<>();
        Position current = start;
        Token startToken = board.getToken(start);

        while (isValidPosition(current, board.getSize())) {
            Token token = board.getToken(current);
            if (token instanceof Pawn &&
                    ((Pawn) token).getColor() == ((Pawn) startToken).getColor()) {
                aligned.add(current);
                current = new Position(
                        current.row() + dir.getDeltaX(),
                        current.column() + dir.getDeltaY()
                );
            } else {
                break;
            }
        }
        return aligned;
    }
    private int checkDirectionalSequence(Board board, Position start, Direction dir, ColorPawn color, Mark mark) {
        int count = 1; // Commence à 1 pour inclure le pion de départ
        boolean hasEmptySpace = false;
        List<Position> sequence = new ArrayList<>();
        sequence.add(start);

        // Vérifie dans la direction donnée
        Position current = new Position(start.row() + dir.getDeltaX(), start.column() + dir.getDeltaY());

        while (isValidPosition(current, board.getSize()) && count < 5) {
            Token token = board.getToken(current);

            if (token == null) {
                hasEmptySpace = true;
                if (isValidPawnPlacement(board, current, mark)) {
                    sequence.add(current);
                }
            } else if (token instanceof Pawn) {
                Pawn currentPawn = (Pawn) token;
                if (currentPawn.getColor() == color) {
                    count++;
                    sequence.add(current);
                } else {
                    break;
                }
            } else {
                break;
            }

            current = new Position(current.row() + dir.getDeltaX(), current.column() + dir.getDeltaY());
        }

        // Vérifie si la séquence peut devenir gagnante
        return (count == 3 && hasEmptySpace && sequence.size() >= 3) ? 1 : 0;
    }

    private boolean isValidPawnPlacement(Board board, Position pos, Mark mark) {
        Position totemPos = board.getTotemPosition(mark);
        return board.isValidInsertion(pos, totemPos);
    }
    private void updateTranspositionTable(Board board, int depth, double score, Position bestMove) {
        long hash = board.calculateZobristHash();
        transpositionTable.store(hash, depth, (int)(score * 1000), bestMove);
    }

    private TranspositionTable.Entry probeTranspositionTable(Board board) {
        long hash = board.calculateZobristHash();
        return transpositionTable.probe(hash);
    }

    private double evaluateMarkAlignments(Board board) {
        double score = 0.0;

        // Vérifie les alignements de marques (X ou O)
        for (Mark mark : Mark.values()) {
            score += evaluateMarkSequences(board, mark);
        }

        return score;
    }

    private double evaluateMarkSequences(Board board, Mark mark) {
        double score = 0.0;

        // Parcours horizontal et vertical
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Position pos = new Position(row, col);
                Token token = board.getToken(pos);

                if (token instanceof Pawn && ((Pawn) token).getMark() == mark) {
                    // Vérifie les séquences horizontales
                    score += evaluateDirectionalMarkSequence(board, pos, Direction.RIGHT, mark);
                    // Vérifie les séquences verticales
                    score += evaluateDirectionalMarkSequence(board, pos, Direction.DOWN, mark);
                }
            }
        }

        return score;
    }

    private double evaluateDirectionalMarkSequence(Board board, Position start, Direction dir, Mark mark) {
        int count = 1;
        int maxSequence = 0;
        Position current = new Position(start.row() + dir.getDeltaX(), start.column() + dir.getDeltaY());

        while (isValidPosition(current, board.getSize())) {
            Token token = board.getToken(current);
            if (token instanceof Pawn && ((Pawn) token).getMark() == mark) {
                count++;
                maxSequence = Math.max(maxSequence, count);
            } else {
                break;
            }
            current = new Position(current.row() + dir.getDeltaX(), current.column() + dir.getDeltaY());
        }

        // Attribution des scores selon la longueur de la séquence
        return switch (maxSequence) {
            case 2 -> 10.0;
            case 3 -> 50.0;
            case 4 -> 1000.0;
            default -> 0.0;
        };
    }

    private boolean isValidPosition(Position pos, int size) {
        return pos.row() >= 0 && pos.row() < size &&
                pos.column() >= 0 && pos.column() < size;
    }

    // Modifie la méthode de simulation pour utiliser la nouvelle évaluation
    private double simulation(MCTSNode node) {
        Board simulationBoard = node.getBoard().copy();
        TranspositionTable.Entry entry = probeTranspositionTable(simulationBoard);

        if (entry != null) {
            return entry.score() / 1000.0;
        }

        int moveCount = 0;
        final int MAX_MOVES = 50;
        double bestScore = Double.NEGATIVE_INFINITY;

        while (!isGameOver(simulationBoard) && moveCount < MAX_MOVES) {
            List<Move> possibleMoves = getAllPossibleMoves(simulationBoard);
            if (possibleMoves.isEmpty()) break;

            // Évalue chaque coup possible
            Move bestMove = null;
            for (Move move : possibleMoves) {
                Board tempBoard = simulationBoard.copy();
                tempBoard.moveTotem(move.token(), move.movePosition());
                double score = evaluatePosition(tempBoard);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }

            // Joue le meilleur coup trouvé
            if (bestMove != null) {
                simulationBoard.moveTotem(bestMove.token(), bestMove.movePosition());
            } else {
                // Si aucun coup n'est clairement meilleur, joue aléatoirement
                Move randomMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
                simulationBoard.moveTotem(randomMove.token(), randomMove.movePosition());
            }

            moveCount++;
        }

        double finalScore = evaluatePosition(simulationBoard);
        updateTranspositionTable(simulationBoard, moveCount, finalScore,
                node.getMovePosition() != null ? node.getMovePosition() : node.getTotemPosition());

        return finalScore;
    }

    private List<Move> getAllPossibleMoves(Board board) {

            List<Move> moves = new ArrayList<>();
            // Get all possible totem moves
            for (Mark mark : Mark.values()) {
                Position totemPos = board.getTotemPosition(mark);
                List<Position> possibleMoves = board.getMovesPossibles(totemPos);
                for (Position newPos : possibleMoves) {
                    moves.add(new Move(board.getTotem(totemPos), newPos));
                }
            }
            return moves;

    }
}