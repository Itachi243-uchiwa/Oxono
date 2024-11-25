package dev3.projet.oxono_g63888.model;

public abstract class Token {

    private Mark mark;

    public Token(Mark mark) {
        this.mark = mark;
    }

    public Mark getMark() {
        return mark;
    }

    public abstract Token copy();

    @Override
    public String toString() {
        return "Piece{" + "mark=" + mark + '}';
    }
}
