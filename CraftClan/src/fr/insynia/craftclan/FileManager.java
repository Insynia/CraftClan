package fr.insynia.craftclan;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sharowin on 14/05/15.
 * Modified by Sharowin on 18/05/15.
 */


public class FileManager {
    private static final String DELIMITER = ",";

    public static List<String> fileReadtoListCC(String folder, String filename) {
        List<String> lines = new ArrayList<>();

        BufferedReader br;
        String curline;

        try {
            br = new BufferedReader(new FileReader(folder + "/" + filename));

            while ((curline = br.readLine()) != null) {
                lines.add(curline);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
    public static void writeLineToFile(String folder, String filename, String line) {

        try {
            File file = new File(folder + "/" + filename);
            File cFolder = new File(folder);

            if (!cFolder.exists()) {
                cFolder.mkdir();
            }
            if (!file.exists()) file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write (line +  "\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<String> parseLine(String lineToParse) {
        List<String> parsedLine = new ArrayList<>();
        int i = 0;
        String[] tokens = lineToParse.split(DELIMITER);
        while (i < tokens.length) {
            parsedLine.add(tokens[i]);
            i += 1;
        }
        return parsedLine;
    }
}
