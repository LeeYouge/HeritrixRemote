package hu.juranyi.zsolt.heritrixremote;

import hu.juranyi.zsolt.heritrixremote.model.Heritrix;
import hu.juranyi.zsolt.heritrixremote.model.HeritrixCall;
import hu.juranyi.zsolt.heritrixremote.model.Job;
import hu.juranyi.zsolt.heritrixremote.model.JobState;
import hu.juranyi.zsolt.heritrixremote.model.ObjectCounter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * With HeritrixRemote you can easily control your running Heritrix especially
 * when you've got a lot of crawling jobs to manage.
 *
 * @author Zsolt Jurányi
 */
public class HeritrixRemote {
    // TODO detect if cURL installed (?) - if !heritrix.getIndexPage().isEmpty() ??
    // TODO error codes - implementation A - enum ErrorType with int member, call: System.exit(ErrorType.getErrorCode(ErrorType.ERROR_TYPE));
    // TODO error codes - implementation B - methods in other classes throw exceptions, exceptions hold error code (static final int member), main method calls System.exit(#);
    // TODO error codes - implementation C - Error(ErrorType et) { sout et.getMessage(); system.exit(et.getExitCode())}
    // - where ErrorType is enum, and has exitCode and message members
    // - this seems to be my favourite implementation :-)
    // TODO javadoc, make all private -> protected

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
                // calling xxxCommand() - it should exit when an error occurs!
                Method commandMethod = HeritrixRemote.class.getDeclaredMethod(args[2] + "Command", (Class<?>[]) null);
                commandMethod.invoke(null, (Object[]) null);
            } catch (NoSuchMethodException ex) {
                printUsage();
            } catch (Exception ex) { // some bug crawls back up to here somehow
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

    private static List<Job> fetchNeededJobs() {
        if (arguments[3].equalsIgnoreCase("all")) {
            // need all jobs
            return heritrix.getJobs(); // itt will handle if empty
        } else {
            // need jobs by state or id
            ArrayList<Job> neededJobs = new ArrayList<Job>();
            try {
                JobState neededState = JobState.valueOf(arguments[3]); // throws exception on invalid enum value

                // valid job state -> job filter
                for (Job job : heritrix.getJobs()) {
                    if (job.getState().equals(neededState)) {
                        neededJobs.add(job);
                    }
                }
            } catch (IllegalArgumentException ex) {
                // invalid job state -> job id
                String jobdir = arguments[3]; // id is the directory name
                if (jobdir.matches("(https?|ftp)://.+\\..+")) { // id is the first seed URL
                    jobdir = Job.URLtoDirectoryName(jobdir);
                }
                for (Job job : heritrix.getJobs()) {
                    if (job.getDir().equals(jobdir)) {
                        neededJobs.add(job);
                        break;
                    }
                }
            }

            if (neededJobs.isEmpty()) { // we can't do much with no jobs
                System.out.println("No jobs match the given id or filter.");
                System.exit(1); // TODO EXIT WITH ERROR CODE
            }
            return neededJobs;
        }
    }

    private static void basicAction(String action, JobState[] allowedJobStates) {
        List<JobState> allowed = Arrays.asList(allowedJobStates);
        for (Job job : fetchNeededJobs()) {
            if (allowed.contains(job.getState())) { // job has appropriate state to perform action
                System.out.print("Action '" + action + "' on job '" + job.getDir() + "' ... ");
                try {
                    new HeritrixCall(heritrix).path("job/" + job.getDir()).data("action=" + action).getResponse();
                    // TODO should I check something here?
                } catch (Exception ex) {
                    System.out.println("ERROR: Failed to execute command '" + action + "' on job '" + job.getDir() + "'.");
                    System.exit(1); // TODO EXIT WITH ERROR CODE
                }
            } else { // job has inappropriate state
                System.out.println("Skipping " + job.getDir() + ", its state is " + job.getState().toString());
            }
        }
    }

    /*
     * xxxCommand() methods: they will be called from main() where xxx is arg[2]
     * -------------------------------------------------------------------------
     */
    private static void statusCommand() {
        String lineFormat = "%-8s | %-15s | %s\n";

        // table header
        System.out.printf(lineFormat, "STATE", "START TIME", "JOB DIRECTORY");
        for (int i = 0; i < 78; i++) {
            System.out.print(((9 == i || 27 == i) ? "+" : "-"));
        }
        System.out.println();

        // table rows
        ObjectCounter<JobState> counter = new ObjectCounter<JobState>();
        for (Job job : fetchNeededJobs()) {
            Date startDate = job.getStartDate();
            JobState state = job.getState();
            counter.add(state);
            String startDateStr = (null == startDate) ? "N/A" : new SimpleDateFormat("yyyyMMdd HHmmss").format(startDate);
            System.out.printf(lineFormat, state.toString(), startDateStr, job.getDir());
        }

        // table footer
        // TODO test stats summary
        String sumFormat = "%6d %s\n";
        System.out.printf(sumFormat, heritrix.getJobs().size(), "TOTAL JOBS");
        for (JobState state : JobState.values()) {
            if (counter.getMap().keySet().contains(state)) {
                System.out.printf(sumFormat, counter.get(state), state.toString());
            }
        }
    }

    private static void createCommand() { // TODO createCommand()
        // will not use fetchNeededJobs nor basicAction
        // receives:
        // arg[3] = URL1[,URL2,...]
        // and optionally arg[4] = "use" and arg[5] = CXML_NAME
        // hibaüzi, ha nem szabványos URL: (https?|ftp)://.+\..+        
        // hibaüzi, ha már van ilyen job
        // call create
        // if no "use" parameter
        // - cxml = get new job's cxml
        // else
        // - cxml = load user specified cxml
        // URL-ekben cserélni "&" -> "&amp;"
        // insert seed urls
        // put cxml
        // XML put-nál üres body jön ha sikeres volt
        // call rescan!
    }

    private static void buildCommand() {
        basicAction("build", new JobState[]{JobState.UNBUILT});
    }

    private static void launchCommand() {
        basicAction("launch", new JobState[]{JobState.READY});
        // TODO wait (?)
    }

    private static void unpauseCommand() {
        basicAction("unpause", new JobState[]{JobState.PAUSED});
    }

    private static void pauseCommand() {
        basicAction("pause", new JobState[]{JobState.RUNNING});
    }

    private static void teardownCommand() {
        basicAction("teardown", new JobState[]{JobState.READY, JobState.PAUSED, JobState.FINISHED});
    }

    private static void terminateCommand() {
        basicAction("terminate", new JobState[]{JobState.PAUSED, JobState.RUNNING});
        // TODO wait (?)        
    }

    private static void storeCommand() { // TODO storeCommand()
        // args: store jobfilter/id archive-directory
        // check-olni létrezik-e az archive directory, ha nem -> hiba
        // check-olni, elérem-e a jobs directory-t, ha nem -> hiba
        // job state must be finished
        // az archive directory-ban létrehoz egy dátum mappát startDate alapján
        // oda áthelyezi (system command-dal?) a jobdir/mirror mappa tartalmát
    }
}
