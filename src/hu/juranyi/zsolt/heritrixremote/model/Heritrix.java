package hu.juranyi.zsolt.heritrixremote.model;

import java.util.List;

public class Heritrix {

    private final String hostPort, userPass;
    private List<Job> jobs;

    public Heritrix(String hostPort, String userPass) {
        this.hostPort = hostPort;
        this.userPass = userPass;
    }

    public String getHostPort() {
        return hostPort;
    }

    public String getUserPass() {
        return userPass;
    }

    public List<Job> getJobs() {
        if (null == jobs) {
            jobs = fetchJobs();
        }
        return jobs;
    }

    private List<Job> fetchJobs() {
        // TODO fetchJobs()
        // a főoldalról lekéri a job linkeket, és létrehozza őket dir alapján
        return null;
    }
}
