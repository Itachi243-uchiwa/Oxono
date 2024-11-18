package dev3.projet.oxono_g63888.model;



import dev3.projet.oxono_g63888.model.Observer.Observable;
import dev3.projet.oxono_g63888.model.Observer.ObservableEvent;
import dev3.projet.oxono_g63888.model.Observer.Observer;
import dev3.projet.oxono_g63888.model.Observer.OxonoEvent;
import dev3.projet.oxono_g63888.model.commands.Command;
import dev3.projet.oxono_g63888.model.commands.CommandManager;
import dev3.projet.oxono_g63888.model.commands.InsertPawnCommand;
import dev3.projet.oxono_g63888.model.commands.MoveTotemCommand;
import dev3.projet.oxono_g63888.model.strategy.MinMaxAIStrategy;
import dev3.projet.oxono_g63888.model.strategy.Move;
import dev3.projet.oxono_g63888.model.strategy.RandomAIStrategy;

import java.util.ArrayList;
import java.util.List;

    public class Game implements Observable {
        private Board board;
        private Player[] players;
        private Player currentPlayer;
        private AIPlayer aiPlayer;
        private Player humanPlayer;
        private Totem[] totems;
        private Position totemXposition;
        private Position totemOposition;
        private Totem lastTotemPlay;
        private CommandManager invoker;
        private Position lastPawnPosition;
        private List<Observer> observers;
        private boolean isUndoRedoInProgress;
        private GameState state;

        public Game() {
            this.observers = new ArrayList<>();
            this.isUndoRedoInProgress = false;
            this.state = GameState.STARTED;
        }

        public void initializeGame(int sizeBoard, int playerNumber) {
            this.board = new Board(sizeBoard);
            humanPlayer = new Player(ColorPawn.PINK);

            switch (playerNumber) {
                case 2 -> {
                    aiPlayer = new AIPlayer(ColorPawn.BLACK, new RandomAIStrategy(ColorPawn.BLACK));
                    players = new Player[]{humanPlayer, aiPlayer};
                }
                case 3 -> {
                    aiPlayer = new AIPlayer(ColorPawn.BLACK, new MinMaxAIStrategy());
                    players = new Player[]{humanPlayer, aiPlayer};
                }
                case 4 -> {
                    Player aiPlayer1 = new AIPlayer(ColorPawn.PINK, new RandomAIStrategy(ColorPawn.PINK));
                    Player aiPlayer2 = new AIPlayer(ColorPawn.BLACK, new RandomAIStrategy(ColorPawn.BLACK));
                    players = new Player[]{aiPlayer1, aiPlayer2};
                }
                default -> players = new Player[]{humanPlayer, playerNumber == 1 ? humanPlayer : aiPlayer};
            }

            this.totems = new Totem[]{new Totem(Mark.X), new Totem(Mark.O)};
            this.totemXposition = board.getTotemPosition(Mark.X);
            this.totemOposition = board.getTotemPosition(Mark.O);
            currentPlayer = players[0];
            this.lastTotemPlay = null;
            this.lastPawnPosition = new Position(0, 0);
            this.invoker = new CommandManager();

            notifyObservers(new OxonoEvent(ObservableEvent.GAME_START)
                    .addData("boardSize", board.getSize())
                    .addData("players", players)
                    .addData("currentPlayer", currentPlayer)
            );
            this.state = GameState.WAITING_FOR_TOTEM;
        }

        public void playAITurn() throws OxonoException {
            if (!isCurrentPlayerAI()) {
                throw new OxonoException("Current player is not AI");
            }
            if (isUndoRedoInProgress) {
                return;
            }

            aiPlayer = (AIPlayer) currentPlayer;

            try {
                    executeTotemMove(aiPlayer.getNextTotemMove(board));
                    executePawnMove(aiPlayer.getNextMove(board));
                    state = GameState.WAITING_FOR_TOTEM;
                } catch (OxonoException e) {
                    System.err.println("Error executing AI turn: " + e.getMessage());
                }

        }

        private void executeTotemMove(Move totemMove) throws OxonoException {
            Token token = totemMove.token();
            Position newPosTotem = totemMove.movePosition();
            Position totemPos = getTotemPositionForMove(token.getMark());

            if (!board.isValidMove(newPosTotem, totemPos)) {
                throw new OxonoException("Invalid totem move");
            }

            Command command = new MoveTotemCommand(board, (Totem) token, newPosTotem, totemPos);
            invoker.executeCommand(command);
            lastTotemPlay = (Totem) token;
            updateTotemPosition(lastTotemPlay, newPosTotem);

            notifyObservers(new OxonoEvent(ObservableEvent.MOVE_TOTEM)
                    .addData("totem", lastTotemPlay)
                    .addData("oldPosition", totemPos)
                    .addData("newPosition", newPosTotem)
            );

        }

        private void executePawnMove(Move pawnMove) throws OxonoException {
            Token token = pawnMove.token();
            Position pawnPos = pawnMove.movePosition();
            Position totemPos = getTotemPositionForMove(token.getMark());

            if (lastTotemPlay == null || lastTotemPlay.getMark() != token.getMark()) {
                throw new OxonoException("Invalid mark, the pawn's mark must match the last moved totem");
            }

            if (!board.isValidInsertion(pawnPos, totemPos)) {
                throw new OxonoException("Invalid pawn move");
            }

            Command command = new InsertPawnCommand(board, (Pawn) token, currentPlayer, totemPos, pawnPos);
            invoker.executeCommand(command);
            lastPawnPosition = pawnPos;

            notifyObservers(new OxonoEvent(ObservableEvent.PLACE_PAWN)
                    .addData("pawn", token)
                    .addData("position", pawnPos)
                    .addData("currentPlayer", currentPlayer)
            );
        }

        private Position getTotemPositionForMove(Mark mark) {
            return mark == Mark.X ? totemXposition : totemOposition;
        }

        private void updateTotemPosition(Totem totem, Position newPos) {
            if (totem.getMark() == Mark.X) {
                totemXposition = newPos;
            } else {
                totemOposition = newPos;
            }
        }

        public void processTotemInput(String totemInput) throws OxonoException {
            processInput(totemInput, true);
            state = GameState.WAITING_FOR_PAWN;

        }

        public void processPawnInput(String pawnInput) throws OxonoException {
            processInput(pawnInput, false);
            state = GameState.WAITING_FOR_TOTEM;
        }

        private void processInput(String input, boolean isTotem) throws OxonoException {
            String[] parts = input.split(" ");
            if (parts.length != 3) {
                throw new OxonoException("Invalid input format. Use 'totem [row] [column]' or 'pawn [row] [column]'");
            }

            try {
                Mark mark = Mark.valueOf(isTotem ? parts[0] : parts[0].substring(1));
                Position newPosition = new Position(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                Position actualTotemPos = getTotemPositionForMove(mark);

                Command command = isTotem ? processTotemMove(mark, newPosition, actualTotemPos)
                        : processPawnMove(mark, newPosition, actualTotemPos);
                invoker.executeCommand(command);
                if (isTotem){
                    notifyObservers(new OxonoEvent(ObservableEvent.MOVE_TOTEM)
                            .addData("totem", lastTotemPlay)
                            .addData("oldPosition", actualTotemPos)
                            .addData("newPosition", newPosition));
                } else {
                    Pawn pawn = new Pawn(humanPlayer.getColor(), mark);
                    notifyObservers(new OxonoEvent(ObservableEvent.PLACE_PAWN)
                            .addData("pawn", pawn)
                            .addData("oldPosition", lastPawnPosition)
                            .addData("newPosition", newPosition));
                }

            } catch (IllegalArgumentException e) {
                throw new OxonoException("Invalid input data");
            }
        }

    private Command processTotemMove(Mark mark, Position newPosition, Position actualTotemPos) throws OxonoException {
        Totem totem = new Totem(mark);

        if (!board.isValidMove(newPosition, actualTotemPos)) {
            throw new OxonoException("Invalid totem move");
        }

        lastTotemPlay = totem;
        updateTotemPosition(totem, newPosition);
        return new MoveTotemCommand(board, totem, newPosition, actualTotemPos);
    }

    private Command processPawnMove(Mark mark, Position newPosition, Position actualTotemPos) throws OxonoException {
        if (lastTotemPlay.getMark() != mark) {
            throw new OxonoException("Invalid mark, choose the pawn whose mark is equal to the totem moved");
        }

        if (!board.isValidInsertion(newPosition, actualTotemPos)) {
            throw new OxonoException("Invalid Pawn move");
        }
        lastPawnPosition = newPosition;
        Pawn pawn = new Pawn(humanPlayer.getColor(), mark);
        return new InsertPawnCommand(board, pawn, currentPlayer, actualTotemPos, newPosition);
    }


        public boolean checkWinCondition() {
            Token token = board.getToken(lastPawnPosition);
            if (!board.isTotem(token)) {
                boolean isWin = board.checkWin((Pawn) token, lastPawnPosition);
                if (isWin) {
                    state = GameState.GAME_OVER;
                    notifyObservers(new OxonoEvent(ObservableEvent.WIN)
                            .addData("winner", currentPlayer)
                            .addData("winningPosition", lastPawnPosition)
                    );
                }

                return isWin;
            }
            return false;
        }


        public void switchPlayer() {
        currentPlayer = (currentPlayer == players[0]) ? players[1] : players[0];
    }

        public boolean isDraw() {
            if (!checkWinCondition()) {
                boolean isDraw = !((players[0].hasPawn(Mark.X) && players[0].hasPawn(Mark.O)) ||
                        (players[1].hasPawn(Mark.X) && players[1].hasPawn(Mark.O)));
                if (isDraw) {
                    notifyObservers(new OxonoEvent(ObservableEvent.DRAW));
                    state = GameState.GAME_OVER;
                }
                return isDraw;
            }
            return false;
        }



        public void undo() throws OxonoException {
            if (state == GameState.GAME_OVER || !canUndo()) {
                throw new OxonoException("Cannot undo after game is finished");
            }

            Command command = invoker.undo();

            isUndoRedoInProgress = true;
            try {
                if (command instanceof InsertPawnCommand pawnCommand) {
                    lastPawnPosition = pawnCommand.getPawnPosition();
                    switchPlayer();

                    if (invoker.getUndoPeek() instanceof MoveTotemCommand) {
                        lastTotemPlay = new Totem(invoker.getUndoPeek().getMovedMark());
                    }

                    state = GameState.WAITING_FOR_PAWN;

                    notifyObservers(new OxonoEvent(ObservableEvent.UNDO)
                            .addData("type", "pawn")
                            .addData("currentPlayer", currentPlayer)
                            .addData("position", pawnCommand.getPawnPosition()));
                } else if (command instanceof MoveTotemCommand totemCommand) {
                    Position oldPosition = totemCommand.getOldPosition();
                    Totem totem = totemCommand.getTotem();
                    updateTotemPosition(totem, oldPosition);
                    lastTotemPlay = null;

                    state = GameState.WAITING_FOR_TOTEM;

                    notifyObservers(new OxonoEvent(ObservableEvent.UNDO)
                            .addData("type", "totem")
                            .addData("totem", totem)
                            .addData("position", oldPosition));
                }
            } finally {
                isUndoRedoInProgress = false;
            }
        }


        public void redo() throws OxonoException {
            if (checkWinCondition() || isDraw() || !canRedo()) {
                throw new OxonoException("Cannot redo after game is finished or no move in stack");
            }

            Command command = invoker.redo();

            isUndoRedoInProgress = true;
            try {
                if (command instanceof MoveTotemCommand totemCommand) {
                    lastTotemPlay = totemCommand.getTotem();
                    Position newPosition = totemCommand.getNewPosition();
                    updateTotemPosition(lastTotemPlay, newPosition);

                    state = GameState.WAITING_FOR_PAWN;

                    notifyObservers(new OxonoEvent(ObservableEvent.REDO)
                            .addData("type", "totem")
                            .addData("totem", lastTotemPlay)
                            .addData("position", newPosition));
                } else if (command instanceof InsertPawnCommand pawnCommand) {
                    lastPawnPosition = pawnCommand.getPawnPosition();
                    switchPlayer();

                    state = GameState.WAITING_FOR_TOTEM;

                    notifyObservers(new OxonoEvent(ObservableEvent.REDO)
                            .addData("type", "pawn")
                            .addData("pawn", pawnCommand.getPawn())
                            .addData("position", pawnCommand.getPawnPosition())
                            .addData("currentPlayer", currentPlayer));
                }
            } finally {
                isUndoRedoInProgress = false;
            }
        }
        public boolean canUndo() {
            return invoker.canUndo() && !isUndoRedoInProgress;
        }

        public boolean canRedo() {
            return invoker.canRedo() && !isUndoRedoInProgress;
        }

        public boolean isUndoRedoInProgress() {
            return isUndoRedoInProgress;
        }


    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isCurrentPlayerAI() {
        return currentPlayer instanceof AIPlayer;
    }

    public void surrender() {
        System.out.println("Player " + currentPlayer.getColor() + " surrendered! " +
                (currentPlayer == players[0] ? "AI wins!" : "Human wins!"));

        notifyObservers(new OxonoEvent(ObservableEvent.WIN));
    }
    public List<Position> positionsInsert(Position pos){
        return board.getInsertionPositions(pos);
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(OxonoEvent event) {
        for (Observer observer : observers) {
            observer.update(this, event);
        }
    }

    public int[] getRemainingPawns(){
        int pawnsXplayerPink = players[0].getRemainingPawns(Mark.X);
        int pawns0playerPink = players[0].getRemainingPawns(Mark.O);
        int pawnsXplayerBlack = players[1].getRemainingPawns(Mark.X);
        int pawns0playerBlack = players[1].getRemainingPawns(Mark.O);
        return new int[]{pawnsXplayerPink, pawns0playerPink, pawnsXplayerBlack, pawns0playerBlack};
    }

    public GameState getGameState() {return state;}
    public void setGameState(GameState state) {this.state = state;}


}
