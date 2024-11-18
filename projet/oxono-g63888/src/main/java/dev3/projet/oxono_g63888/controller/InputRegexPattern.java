package dev3.projet.oxono_g63888.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum InputRegexPattern {

    AI_MOVE("^$"), // Touche Enter (chaîne vide) pour l'IA
    START("^start$"),
    RESTART("^restart$"),
    UNDO("^undo$"),
    REDO("^redo$"),
    SURRENDER("^surrender$"),
    HELP("^help$"),
    QUIT("^quit$"),
    TOTEM_MOVE("^([XO])\\s+([0-9])\\s+([0-9])$"),
    PAWN_MOVE("^(R[XO])\\s+([0-9])\\s+([0-9])$");

    private final String regex;

    InputRegexPattern(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }

    public boolean matches(String command) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(command);
        return matcher.matches();
    }

    public static InputRegexPattern findCommand(String command) {
        for (InputRegexPattern c : InputRegexPattern.values()) {
            if (c.matches(command)) {
                return c;
            }
        }
        return null;
    }

    public Matcher getMatcher(String command) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(command);
    }
}