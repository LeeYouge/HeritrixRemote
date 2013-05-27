package hu.juranyi.zsolt.heritrixremote.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple text file handling.
 *
 * @author Zsolt Jurányi
 */
public class TextFile {

    private final String fileName;
    private final List<String> lines = new ArrayList<String>();

    public TextFile(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getLines() {
        return lines;
    }

    public boolean load() {
        boolean result = true;
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = r.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ex) {
            System.out.println("IO ERROR reading " + fileName);
            result = false;
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException ex) {
                }
            }
        }
        return result;
    }

    public boolean save() {
        boolean result = true;
        BufferedWriter w = null;
        try {
            w = new BufferedWriter(new FileWriter(fileName));
            for (String line : lines) {
                w.write(line);
                w.newLine();
            }
        } catch (IOException ex) {
            System.out.println("IO ERROR writing " + fileName);
            result = false;
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException ex) {
                }
            }
        }
        return result;
    }
}
