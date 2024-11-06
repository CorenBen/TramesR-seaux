package fr.corentin.views;

import fr.corentin.controllers.MainController;
import fr.corentin.controllers.viewcontrollers.DataViewController;
import fr.corentin.dto.Info;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataView extends DefaultView {

    private final DataViewController _controller;

    private final List<Info> _infos;
    private final ObservableList<Info> _table_info = FXCollections.observableArrayList();
    private final TableView<Info> _table = new TableView<>();

    private final TextField _filter_textfield = new TextField();
    private final Label _errorLabel = new Label();

    private static final Pattern _filterPattern = Pattern.compile("^(?<field>sourceIP|destIP|sourcePort|destPort|protocol)(?<test>==|!=)(?<value>[A-Za-z0-9.]+)$", Pattern.CASE_INSENSITIVE);


    public DataView(Stage stage, MainController main, List<Info> infos) {
        super(stage);

        this._controller = new DataViewController(main);

        this._infos = infos;
        this._table_info.addAll(this._infos);

        this._errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
    }

    private void filter() {
        String filter = this._filter_textfield.getText();

        if (Objects.isNull(filter) || filter.isEmpty()) {
            this._errorLabel.setText("Empty filter");
            return;
        }

        String[] elements = filter.split(" ");
        List<Info> currentList = new ArrayList<>(this._infos);

        for (String element : elements) {

            Matcher matcher = DataView._filterPattern.matcher(element);

            if (matcher.find()) {

                String field = matcher.group("field");
                assert !Objects.isNull(field);

                String test = matcher.group("test");
                assert !Objects.isNull(test);

                String value = matcher.group("value");
                assert !Objects.isNull(value);

                Predicate<String> testFunction = (test.equals("==")) ? e -> e.equals(value) : e -> !e.equals(value);

                switch (field.toLowerCase()) {
                    case "sourceip":
                        currentList = currentList.stream().filter(info -> testFunction.test(info.getSourceIp())).collect(Collectors.toList());
                        break;
                    case "destip":
                        currentList = currentList.stream().filter(info -> testFunction.test(info.getDestinationIp())).collect(Collectors.toList());
                        break;
                    case "sourceport":
                        currentList = currentList.stream().filter(info -> testFunction.test(info.getSourcePort())).collect(Collectors.toList());
                        break;
                    case "destport":
                        currentList = currentList.stream().filter(info -> testFunction.test(info.getDestinationPort())).collect(Collectors.toList());
                        break;
                    case "protocol":
                        currentList = currentList.stream().filter(info -> testFunction.test(info.getProtocol())).collect(Collectors.toList());
                        break;
                    default: throw new RuntimeException("Unreachable switch case");
                }

                this._table_info.clear();
                this._table_info.addAll(currentList);

            } else {
                this._errorLabel.setText(new StringBuilder("Element \"").append(element).append("\" is not a valid pattern").toString());
                return;
            }
        }
    }

    private void reset() {
        this._table_info.clear();
        this._table_info.addAll(this._infos);

        this._errorLabel.setText(null);
    }

    @Override
    public DataView build() {

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);

        Button button_back = new Button("<- Back");
        button_back.setOnAction(actionEvent -> this._controller.back());

        Button button_generate = new Button("Generate SVG graph");
        button_generate.setOnAction(actionEvent -> {
            String path = this._controller.generateSvg(this._table_info);
            Alert alert;
            if (Objects.isNull(path)) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error while generating SVG diagram");
            } else {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("SVG file successfully created at \"" + path + "\"");
            }

            alert.show();
        });

        buttons.getChildren().addAll(button_back, button_generate);

        VBox filters = new VBox();
        filters.setAlignment(Pos.CENTER);

        HBox filterInputs = new HBox();
        Button filterButton = new Button("Filtrer");
        filterButton.setOnAction(actionEvents -> filter());

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(actionEvents -> reset());

        filterInputs.getChildren().addAll(this._filter_textfield, filterButton, resetButton);
        HBox.setHgrow(this._filter_textfield, Priority.ALWAYS);

        filters.getChildren().addAll(this._errorLabel, filterInputs);


        TableColumn<Info, String> fromColumn = new TableColumn<Info, String>("From");
        TableColumn<Info, String> fromMacColumn = new TableColumn<Info, String>("Mac");
        TableColumn<Info, String> fromIpColumn = new TableColumn<Info, String>("IP");
        TableColumn<Info, String> fromPortColumn = new TableColumn<Info, String>("Port");
        fromColumn.getColumns().addAll(fromMacColumn, fromIpColumn, fromPortColumn);

        TableColumn<Info, String> toColumn = new TableColumn<Info, String>("To");
        TableColumn<Info, String> toMacColumn = new TableColumn<Info, String>("Mac");
        TableColumn<Info, String> toIpColumn = new TableColumn<Info, String>("IP");
        TableColumn<Info, String> toPortColumn = new TableColumn<Info, String>("Port");
        toColumn.getColumns().addAll(toMacColumn, toIpColumn, toPortColumn);


        TableColumn<Info, String> typeColumn = new TableColumn<Info, String>("Type");
        TableColumn<Info, String> protocolColumn = new TableColumn<Info, String>("Protocol");
        TableColumn<Info, String> flagsColumn = new TableColumn<Info, String>("Flags");

        TableColumn<Info, String> sequenceColumn = new TableColumn<Info, String>("Sequence N°");
        TableColumn<Info, String> acknowledgmentColumn = new TableColumn<Info, String>("ACK N°");
        TableColumn<Info, String> windowColumn = new TableColumn<Info, String>("Window");
        TableColumn<Info, String> httpColumn = new TableColumn<Info, String>("HTTP");

        fromMacColumn.setCellValueFactory(new PropertyValueFactory<>("sourceMac"));
        fromIpColumn.setCellValueFactory(new PropertyValueFactory<>("sourceIp"));
        fromPortColumn.setCellValueFactory(new PropertyValueFactory<>("sourcePort"));
        toMacColumn.setCellValueFactory(new PropertyValueFactory<>("destinationMac"));
        toIpColumn.setCellValueFactory(new PropertyValueFactory<>("destinationIp"));
        toPortColumn.setCellValueFactory(new PropertyValueFactory<>("destinationPort"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        protocolColumn.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        flagsColumn.setCellValueFactory(new PropertyValueFactory<>("flags"));
        sequenceColumn.setCellValueFactory(new PropertyValueFactory<>("sequenceNumber"));
        acknowledgmentColumn.setCellValueFactory(new PropertyValueFactory<>("acknowledgmentNumber"));
        windowColumn.setCellValueFactory(new PropertyValueFactory<>("window"));
        httpColumn.setCellValueFactory(new PropertyValueFactory<>("httpContent"));

        this._table.setItems(this._table_info);
        this._table.getColumns().addAll(fromColumn, toColumn, typeColumn, protocolColumn, flagsColumn, sequenceColumn, acknowledgmentColumn, windowColumn, httpColumn);

        root.getChildren().addAll(buttons, filters, this._table);
        super.setContent(root);

        return this;
    }
}
