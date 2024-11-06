package fr.corentin.controllers.viewcontrollers;

import fr.corentin.analyser.Analyzer;
import fr.corentin.analyser.Converter;
import fr.corentin.controllers.MainController;

import java.io.File;
import java.io.IOException;

public class InputViewController extends DefaultViewController {

    public InputViewController(MainController main) {
        super(main);
    }

    public void process_file(File file) {

        try {
            Converter converter = new Converter(file);
            Analyzer analyzer = new Analyzer(converter.getTrameList());

            this._main.toTable(analyzer.getInfoList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}