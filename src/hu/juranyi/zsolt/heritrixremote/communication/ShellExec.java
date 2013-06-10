package hu.juranyi.zsolt.heritrixremote.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Communication with the OS shell: command execution and output fetching.
 *
 * @author Zsolt Jurányi
 */
public class ShellExec {

    private final String command;
    private String stdout;
    private String stderr;
    private int exitCode;

    public ShellExec(String command) {
        this.command = command;
    }

    public void exec() throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec(command);
        proc.waitFor(); // TODO BUG: when there are more than 338 jobs, this freezes when fetching Heritrix index page

        BufferedReader reader;
        StringBuilder builder;
        String line;

        reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }
        stdout = builder.toString();

        reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }
        stderr = builder.toString();

        exitCode = proc.exitValue();
    }

    public String getCommand() {
        return command;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public int getExitCode() {
        return exitCode;
    }
}
