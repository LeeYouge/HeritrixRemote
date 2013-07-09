/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.juranyi.zsolt.heritrixremote.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Reads an InputStream and collects its lines into a StringBuilder
 *
 * @author Zsolt Jurányi
 */
public class StreamGobbler extends Thread {

    InputStream is;
    StringBuilder sb = new StringBuilder();

    StreamGobbler(InputStream is) {
        this.is = is;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public String getStreamAsString() {
        return sb.toString();
    }
}
