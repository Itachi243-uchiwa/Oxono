package dev3.projet.oxono_g63888.model.strategy.search;

import dev3.projet.oxono_g63888.model.strategy.Move;

public record SearchResult(int score, Move totemMove, Move pawnMove, boolean isThree) {}

