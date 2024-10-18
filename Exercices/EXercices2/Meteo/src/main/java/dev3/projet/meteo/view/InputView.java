package dev3.projet.meteo.view;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class InputView {

    private TextField text;
    private DatePicker date;

    public InputView() {
        this.text = new TextField();
        this.date = new DatePicker();
    }

    public TextField getText() {
        return text;
    }
    public DatePicker getDate() {
        return date;
    }
}
