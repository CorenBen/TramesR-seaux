package fr.corentin.controllers;

import fr.corentin.dto.Info;
import fr.corentin.views.DataView;
import fr.corentin.views.IView;
import fr.corentin.views.InputView;

import javafx.stage.Stage;

import java.util.List;

public class MainController {

    private final Stage _stage;
    private IView _view;

    public MainController(Stage stage) {
        this._stage = stage;

        this._stage.setTitle("Visualizer");
        this._stage.setWidth(1080);
        this._stage.setHeight(480);

        this._view = new InputView(stage, this).build();
    }

    public void start() { this._stage.show(); }

    public void toTable(List<Info> infos) {
        this._view = new DataView(this._stage, this, infos).build();;
    }

    public void back() {
        this._view = new InputView(this._stage, this).build();
    }
}