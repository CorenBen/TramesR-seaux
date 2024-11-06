package fr.corentin.controllers.viewcontrollers;

import fr.corentin.controllers.MainController;
import fr.corentin.dto.Info;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class DataViewController extends DefaultViewController {

    private static final String UML_START = "@startuml\n";
    private static final String UML_END = "@enduml\n";

    public DataViewController(MainController main) { super(main); }

    public String generateSvg(List<Info> infos) {

        StringBuilder source = new StringBuilder(UML_START);
        for (Info info : infos)
            source.append((!Objects.isNull(info.getSourceIp())) ? info.getSourceIp() : "")
                    .append(" --> ").append((!Objects.isNull(info.getDestinationIp())) ? info.getDestinationIp() : "")
                    .append(": \"").append(info.toString()).append("\"\n");
        source.append(UML_END);

        System.out.println(source);

        StringBuilder filename = new StringBuilder("out");
        while (new File(filename + ".svg").isFile()) filename.append("_1");
        File file = new File(filename + ".svg");

        try {
            FileWriter fw = new FileWriter(file);

            SourceStringReader reader = new SourceStringReader(source.toString());

            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            if (reader.generateImage(os, new FileFormatOption(FileFormat.SVG)) == null) {
                throw new RuntimeException("TODO");
            }

            os.close();
            final String svg_content = os.toString("utf-8");
            fw.write(svg_content);

            fw.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
