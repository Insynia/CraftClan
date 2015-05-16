package fr.insynia.craftclan;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sharowin on 14/05/15.
 */


public class FilesManagerCC {
    private static final String DELIMITER = ",";

    public static List fileReadtoListCC(String folder, String filename) {
        List<String> lines = new ArrayList<String>();

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

        for (String bit : lines) {
            Bukkit.getLogger().info(bit);
        }
        return lines;
    }
    public static void fileWriteFromLineCC(String folder, String filename, String line) {

        try {
            File file = new File(folder + "/" + filename);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write (line +  "\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<Object> fileParse(int nbLine, List<String> listToParse) {
        List<Object> parsedLine = new ArrayList<Object>();
        int i = 0;
        String[] tokens = listToParse.get(nbLine).split(DELIMITER);
        while (i < tokens.length) {
            parsedLine.add(tokens[i]);
            i += 1;
        }
        return parsedLine;
    }
}
