package hu.juranyi.zsolt.heritrixremote.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Describes a Heritrix crawling job.
 *
 * @author Zsolt Jurányi
 */
public class Job {

    private final Heritrix heritrix;
    private final String dir;
    private List<String> seedURLs;
    private JobState state;
    private Date startDate;

    public Job(Heritrix heritrix, String dir) {
        this.heritrix = heritrix;
        this.dir = dir;
    }

    public Job(Heritrix heritrix, List<String> seedURLs) {
        this.heritrix = heritrix;
        this.seedURLs = seedURLs;
        dir = seedURLs.get(0).toLowerCase().replaceAll("[^a-z]+", "-");
    }

    public String getDir() {
        return dir;
    }

    public List<String> getSeedURLs() {
        if (null == seedURLs) {
            seedURLs = fetchSeedURLs();
        }
        return seedURLs;
    }

    public JobState getState() {
        if (null == state) {
            state = fetchState();
        }
        return state;
    }

    public Date getStartDate() {
        if (null == startDate) {
            startDate = fetchStartDate();
        }
        return startDate;
    }

    private List<String> fetchSeedURLs() {
        ArrayList<String> seedURLs = new ArrayList<String>();
        try {
            String response = new HeritrixCall(heritrix).path("jobs/" + dir + "/crawler-beans.cxml").getResponse();
            // TODO parse seedURLs
        } catch (Exception ex) {
            System.out.println("ERROR: Failed to fetch crawler-beans.cxml.");
            System.exit(1); // TODO EXIT WITH ERROR CODE
        }
        return seedURLs;
    }

    private JobState fetchState() { // TODO TEST fetchState()
        try {
            String response = new HeritrixCall(heritrix).path("job/" + dir).getResponse();
            Elements elements = Jsoup.parse(response).select("h2");
            for (Element e : elements) {
                if (e.hasText() && e.text().startsWith("Job is ")) {
                    return JobState.parseFromStatusString(e.text().trim());
                }
            }
            System.out.println("ERROR: Failed to parse job state.");
            System.exit(1); // TODO EXIT WITH ERROR CODE
            return null;
        } catch (Exception ex) {
            System.out.println("ERROR: Failed to fetch job page.");
            System.exit(1); // TODO EXIT WITH ERROR CODE
            return null;
        }
    }

    private Date fetchStartDate() {
        // TODO fetchStartDate()
        // file: jobs/jobdir/job.log
        // needed data: last line matching ".* INFO [^ ]+ \d{14}$", we need the first 8 digits of that sequence
        return new Date();
    }
}
