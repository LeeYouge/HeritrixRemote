package hu.juranyi.zsolt.heritrixremote.model;

/**
 * Possible states of a Heritrix crawling job.
 *
 * @author Zsolt Jurányi
 */
public enum JobState {

    UNBUILT, READY, PAUSED, RUNNING, FINISHED;

    public static JobState parseFromStatusString(String s) {
        if (s.equals("Job is Unbuilt")) {
            return UNBUILT;
        } else if (s.equals("Job is Ready")) {
            return READY;
        } else if (s.matches("Job is Active: PAUS(ED|ING)")) {
            return PAUSED;
        } else if (s.equals("Job is Active: RUNNING")) {
            return RUNNING;
        } else if (s.matches("Job is Finished: [A-Z]+")) {
            return FINISHED;
        } else {
            return null; // on parse error
        }
    }
}
