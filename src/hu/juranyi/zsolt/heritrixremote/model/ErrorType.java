package hu.juranyi.zsolt.heritrixremote.model;

/**
 * Describes the types of errors with their exit codes and messages.
 *
 * @author Zsolt Jurányi
 */
public enum ErrorType {

    // application scope
    UNKNOWN_BUG(1, "Unknown bug!"),
    FAILED_TO_LOAD_RESOURCE(2, "Cannot load resource."),
    INVALID_PARAMETER_LIST(3, "Invalid parameter list. Please run HeritrixRemote without parameters and read how to use it."),
    // heritrix scope
    FAILED_TO_FETCH_HERITRIX_INDEX_PAGE(10, "Failed to fetch Heritrix index page. Check your parameters, version of Heritrix, and if cURL is installed."),
    FAILED_TO_PARSE_JOBS_DIRECTORY(11, "Failed to parse jobs' directory."),
    FAILED_TO_PARSE_JOB_LIST(12, "Failed to parse job list or there isn't any jobs."),
    NO_MATCHING_JOBS(13, "No jobs match the given id or filter."),
    HERITRIX_MIRROR_DIR_NOT_FOUND(14, "Cannot find Heritrix's 'mirror' directory. Please check if you are running HeritrixRemote on the same machine as Heritrix."),
    // job scope
    FAILED_TO_PERFORM_ACTION(20, "Failed to perform action."),
    FAILED_TO_FETCH_CXML(21, "Failed to fetch crawler-beans.cxml."),
    FAILED_TO_PARSE_SEED_URLS(22, "Failed to parse seed URLs or there isn't any."),
    FAILED_TO_FETCH_JOB_PAGE(23, "Failed to fetch job page."),
    FAILED_TO_PARSE_JOB_STATE(24, "Failed to parse job state."),
    FAILED_TO_PARSE_JOB_START_DATE(25, "Failed to parse job start date."),
    NO_VALID_URLS_SPECIFIED(26, "No valid URLs specified."),
    FAILED_TO_LOAD_YOUR_CXML(27, "Failed to load your CXML file."),
    FAILED_TO_PUSH_CXML(28, "Failed to push CXML file.");
    private int exitCode = 1;
    private String message;

    private ErrorType(int exitCode, String message) {
        this.exitCode = exitCode;
        this.message = message;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getMessage() {
        return message;
    }
}
