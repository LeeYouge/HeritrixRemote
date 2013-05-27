package hu.juranyi.zsolt.heritrixremote.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jsoup.Jsoup;
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
    private String CXML;
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
        dir = URLtoDirectoryName(seedURLs.get(0));
    }

    public static String URLtoDirectoryName(String URL) {
        return URL.toLowerCase().replaceAll("[^a-z]+", "-").replaceAll("-$", "");
    }

    public Heritrix getHeritrix() {
        return heritrix;
    }

    public String getDir() {
        return dir;
    }

    public String getCXML() {
        if (null == CXML) {
            CXML = fetchCXML();
        }
        return CXML;
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

    private String fetchCXML() {
        try {
            return new HeritrixCall(heritrix).path("job/" + getDir() + "/jobdir/crawler-beans.cxml").getResponse();
        } catch (Exception ex) {
            new ErrorHandler(ErrorType.FAILED_TO_FETCH_CXML);
            return null;
        }
    }

    private List<String> fetchSeedURLs() { // TODO test fetch seed urls
        ArrayList<String> seedURLs = new ArrayList<String>();
        Elements props = Jsoup.parse(getCXML()).select("prop[key=seeds.textSource.value]");
        if (props.isEmpty()) {
            new ErrorHandler(ErrorType.FAILED_TO_PARSE_SEED_URLS);
        } else {
            String seedURLsStr = props.first().text();
            for (String seedURL : seedURLsStr.split("\n")) {
                seedURL = seedURL.trim();
                if (!seedURL.startsWith("#")) {
                    seedURLs.add(seedURL);
                }
            }
        }
        return seedURLs;
    }

    private JobState fetchState() {
        try {
            String response = new HeritrixCall(heritrix).path("job/" + getDir()).getResponse();
            Elements elements = Jsoup.parse(response).select("h2");
            for (Element e : elements) {
                if (e.hasText() && e.text().startsWith("Job is ")) {
                    return JobState.parseFromStatusString(e.text().trim());
                }
            }
            new ErrorHandler(ErrorType.FAILED_TO_PARSE_JOB_STATE);
            return null;
        } catch (Exception ex) {
            new ErrorHandler(ErrorType.FAILED_TO_FETCH_JOB_PAGE);
            return null;
        }
    }

    private Date fetchStartDate() { // TODO test start date with jobs configured with MirrorWriter !!!
        try {
            String response = new HeritrixCall(heritrix).path("jobsdir/" + getDir() + "/job.log").getResponse();
            String date = null;
            for (String line : response.split("\n")) {
                if (line.matches(".* INFO [^ ]+ \\d{14}$")) {
                    date = line.replaceAll(".* INFO [^ ]+ ", "");
                }
            }
            if (null != date) {
                return new SimpleDateFormat("yyyyMMddHHmmss").parse(date);
            }
        } catch (Exception ex) {
            new ErrorHandler(ErrorType.FAILED_TO_PARSE_JOB_START_DATE);
            return null;
        }
        return null;
    }
}
