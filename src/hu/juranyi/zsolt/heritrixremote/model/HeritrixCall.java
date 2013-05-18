package hu.juranyi.zsolt.heritrixremote.model;

import hu.juranyi.zsolt.heritrixremote.communication.CURL;
import hu.juranyi.zsolt.heritrixremote.communication.IRESTClient;
import java.io.File;

/**
 * Simplifies the REST request building process.
 *
 * @author Zsolt Jurányi
 */
public class HeritrixCall {

    private final IRESTClient restClient = new CURL();
    private final Heritrix heritrix;

    public HeritrixCall(Heritrix heritrix) {
        this.heritrix = heritrix;
        restClient.setTargetURL("https://" + heritrix.getHostPort() + "/engine");
        restClient.setUsernameAndPassword(heritrix.getUserPass());
        restClient.setMethod("POST");
    }

    public HeritrixCall path(String path) {
        restClient.setTargetURL("https://" + heritrix.getHostPort() + "/engine/" + path);
        return this;
    }

    public HeritrixCall data(String data) {
        restClient.setData(data);
        restClient.setMethod("POST");
        restClient.setFileToSend(null);
        return this;
    }

    public HeritrixCall file(File file) {
        restClient.setFileToSend(file);
        restClient.setMethod("PUT");
        restClient.setData(null);
        return this;
    }

    public String getResponse() throws Exception {
        restClient.request();
        return restClient.getResponse();
    }
}
