package dev3.projet.oxono_g63888.model;

public record Position(int row, int column) {

    public Position move(int x, int y){
        return new Position(row + x, column + y);
    }
}
