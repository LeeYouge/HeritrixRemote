package hu.juranyi.zsolt.heritrixremote;

import hu.juranyi.zsolt.heritrixremote.model.ErrorHandler;
import hu.juranyi.zsolt.heritrixremote.model.ErrorType;
import hu.juranyi.zsolt.heritrixremote.model.Heritrix;
import hu.juranyi.zsolt.heritrixremote.model.HeritrixCall;
import hu.juranyi.zsolt.heritrixremote.model.Job;
import hu.juranyi.zsolt.heritrixremote.model.JobState;
import hu.juranyi.zsolt.heritrixremote.model.ObjectCounter;
import hu.juranyi.zsolt.heritrixremote.model.TextFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * With HeritrixRemote you can easily control your running Heritrix especially
 * when you've got a lot of crawling jobs to manage.
 *
 * @author Zsolt Jurányi
 */
public class HeritrixRemote {
    // TODO javadoc, make all private -> protected
    // TODO error handling - wouldn't it be prettier using HRException(ErrorType) instead of ErrorHandler? and exceptions crawl back to main where they be handled?

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
                new ErrorHandler(ErrorType.UNKNOWN_BUG, ex);
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
            new ErrorHandler(ErrorType.FAILED_TO_LOAD_RESOURCE, ex);
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
                new ErrorHandler(ErrorType.NO_MATCHING_JOBS);
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
                    new ErrorHandler(ErrorType.FAILED_TO_PERFORM_ACTION);
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
        System.out.println();
        String sumFormat = "%6d %s\n";
        System.out.printf(sumFormat, heritrix.getJobs().size(), "TOTAL JOBS");
        for (JobState state : JobState.values()) {
            if (counter.getMap().keySet().contains(state)) {
                System.out.printf(sumFormat, counter.get(state), state.toString());
            }
        }
    }

    private static void createCommand() {
        List<String> URLs = new ArrayList<String>();
        for (String URL : arguments[3].split(",")) {
            if (URL.matches("(https?|ftp)://.+\\..+")) {
                URLs.add(URL);
            } else {
                System.out.println("Removed invalid URL: " + URL);
            }
        }
        if (URLs.isEmpty()) {
            new ErrorHandler(ErrorType.NO_VALID_URLS_SPECIFIED);
        } else {
            String jobdir = Job.URLtoDirectoryName(URLs.get(0));
            // TODO getJobs, determine if already exists - if yes -> JOB_ALREADY_EXISTS
            // it's not too important, recreating does not cause errors :-)

            System.out.println("Creating job '" + jobdir + "'");
            try {
                new HeritrixCall(heritrix).data("action=create&createpath=" + jobdir).getResponse();
            } catch (Exception ex) {
                new ErrorHandler(ErrorType.FAILED_TO_PERFORM_ACTION);
            }

            System.out.println("Fetching CXML...");
            String[] oldCXML = null;
            if (arguments.length >= 6 && arguments[4].equalsIgnoreCase("use")) {
                TextFile f = new TextFile(arguments[5]);
                if (!f.load()) {
                    new ErrorHandler(ErrorType.FAILED_TO_LOAD_YOUR_CXML);
                } else {
                    f.getLines().toArray();
                }
            } else {
                oldCXML = new Job(heritrix, jobdir).getCXML().split("\n");
            }

            System.out.println("Inserting URLs...");
            TextFile newCXML = new TextFile("temp.cxml");
            boolean inProp = false;
            for (String line : oldCXML) {
                if (inProp) {
                    if (line.matches(".*</prop>.*")) {
                        inProp = false;
                    } else {
                        continue; // skipping old seed URL lines
                    }
                }
                
                if (line.matches(".*metadata.operatorContactUrl=.*")) {
                    line = "metadata.operatorContactUrl=http://127.0.0.1/"; // TODO contact URL from argument
                }
                
                newCXML.getLines().add(line);
                
                if (line.matches(".*<prop[^>]+key=\"?seeds.textSource.value\"?.*")) {
                    inProp = true;
                    for (String URL : URLs) {
                        newCXML.getLines().add(URL.replaceAll("&", "&amp;"));
                    }
                }
            }
            newCXML.save();

            // push it
            System.out.println("Pushing CXML...");
            try {
                String response = new HeritrixCall(heritrix).path("job/" + jobdir + "/jobdir/crawler-beans.cxml").file(new File("temp.cxml")).getResponse();
                if (null != response && !response.isEmpty()) {
                    new ErrorHandler(ErrorType.FAILED_TO_PUSH_CXML);
                }
            } catch (Exception ex) {
                new ErrorHandler(ErrorType.FAILED_TO_PUSH_CXML);
            }

            try {
                new HeritrixCall(heritrix).data("action=rescan").getResponse();
            } catch (Exception ex) {
                new ErrorHandler(ErrorType.FAILED_TO_PERFORM_ACTION);
            }
        }
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

    private static void storeCommand() { // TODO IMPLEMENT storeCommand()
        // args: store jobfilter/id archive-directory
        // check-olni létrezik-e az archive directory, ha nem -> hiba
        // check-olni, elérem-e a jobs directory-t, ha nem -> hiba
        // job state must be finished
        // az archive directory-ban létrehoz egy dátum mappát startDate alapján
        // oda áthelyezi (system command-dal?) a jobdir/mirror mappa tartalmát
    }
}
