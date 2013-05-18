package hu.juranyi.zsolt.heritrixremote;

import hu.juranyi.zsolt.heritrixremote.model.Heritrix;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

/**
 * With HeritrixRemote you can easily control your running Heritrix especially
 * when you've got a lot of crawling jobs to manage.
 *
 * @author Zsolt Jurányi
 */
public class HeritrixRemote {
    // TODO Usage.txt
    // TODO tudni kéne detektálni ha nincs CURL telepítve
    // TODO a *Command() metódusokban csekkolni a jobState-et, hogy végrehajtható-e a parancs
    // TODO a create-ban rescan-t is kell hívni!
    // TODO illetve exit code-okkal kéne kilépni ha hiba van!
    // TODO esetleg a statusnál is lehetne valami exit code, pl. 100=UNBUILT, 101=PAUSED, ...
    // TODO XML put-nál üres body jön ha sikeres volt

    public static final String VERSION = "1.0b";
    public static Heritrix heritrix;
    public static String[] arguments;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        printHeader();
        if (args.length < 4) {
            printUsage();
        } else {
            arguments = args;
            heritrix = new Heritrix(args[0], args[1]);
            try {
                Method commandMethod = HeritrixRemote.class.getDeclaredMethod(args[2] + "Command", null);
                commandMethod.invoke(new HeritrixRemote(), null);
            } catch (NoSuchMethodException ex) {
                printUsage();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void printHeader() {
        System.out.println("HeritrixRemote by Zsolt Juranyi");
        System.out.println("Version " + VERSION);
        System.out.println();
    }

    private static void printUsage() {
        InputStream usageIS = HeritrixRemote.class.getResourceAsStream("Usage.txt");
        BufferedReader r = new BufferedReader(new InputStreamReader(usageIS));
        try {
            while (r.ready()) {
                System.out.println(r.readLine());
            }
        } catch (IOException ex) {
        }
    }

    private static void statusCommand() {
    }

    private static void createCommand() {
    }

    private static void buildCommand() {
    }

    private static void launchCommand() {
    }

    private static void unpauseCommand() {
    }

    private static void pauseCommand() {
    }

    private static void teardownCommand() {
    }

    private static void terminateCommand() {
    }
}
