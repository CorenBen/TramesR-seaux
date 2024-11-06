package fr.corentin.analyser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Converter {
    private final List<String> _trameList = new ArrayList<>();

    public Converter(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        boolean isFirstLine = true;

        StringBuilder stringBuilder = new StringBuilder();

        String readLine;
        while ((readLine = bufferedReader.readLine()) != null) {
            String substring = readLine.substring(0, 3);

            if (substring.equals("000") && !isFirstLine) {
                this._trameList.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            } else if (!isFirstLine) stringBuilder.append(' ');

            isFirstLine = false;
            stringBuilder.append(readLine.substring(6));
        }

        this._trameList.add(stringBuilder.toString());
    }

    public List<String> getTrameList() { return this._trameList; }
}
