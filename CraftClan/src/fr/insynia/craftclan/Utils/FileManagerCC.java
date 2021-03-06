package fr.insynia.craftclan.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sharowin on 14/05/15.
 * Modified by Sharowin on 25/05/15.
 */


public class FileManagerCC {
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

    public static boolean checkFileAndFolderExist(String folder, String filename) {
        File cFolder = new File(folder);
        if (!cFolder.exists() || !cFolder.isDirectory()) {
            return false;
        } else {
            File file = new File(folder + "/" + filename);
            return !(!file.exists() || !file.canRead());
        }
    }

    public static boolean deleteFileOrFolder(File file) {

        if (file.exists()) {
            if (file.isFile())
                return(file.delete());

            File files[] = file.listFiles();

            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFileOrFolder(f);
                } else {
                    f.delete();
                }
            }
            return (file.delete());
        }
        return false;
    }
}
