package dev3.projet.oxono_g63888.model;

public class Totem extends Token {


    public Totem(Mark mark) {
        super(mark);

    }
    @Override
    public Token copy() {
        return new Totem(this.getMark());
    }

}
