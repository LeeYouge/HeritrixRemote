package hu.juranyi.zsolt.heritrixremote.model;

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
        try {
            String response = new HeritrixCall(heritrix).path("jobs/" + dir + "/crawler-beans.cxml").getResponse();
            // TODO parse seedURLs
        } catch (Exception ex) {
        }

        return null;
    }

    private JobState fetchState() { // TODO TEST fetchState()
        try {
            String response = new HeritrixCall(heritrix).path("jobs/" + dir).getResponse();
            Document doc = Jsoup.parse(response);
            Elements elements = doc.select("h2");
            for(Element e : elements) {
               if (e.hasText() && e.text().startsWith("Job is ")) {
                   state = JobState.parseFromStatusString(e.text().trim());
               }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    private Date fetchStartDate() {
        // TODO fetchStartDate()
        // lekéri a job megfelelő logfájlját, kiparszolja a legutóbbi RUNNING előtti dátumot
        return null;
    }
}
