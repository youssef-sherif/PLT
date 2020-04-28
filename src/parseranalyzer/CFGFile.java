package parseranalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CFGFile {

    public CFGFile(String fileName) {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(
                    new File(fileName)
            ));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                // read next line
                line = reader.readLine();
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
