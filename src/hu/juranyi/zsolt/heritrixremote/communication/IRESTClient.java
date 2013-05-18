package hu.juranyi.zsolt.heritrixremote.communication;

import java.io.File;
import java.util.Map;

/**
 * Defines what HeritrixRemote need from a REST client.
 *
 * @author Zsolt Jurányi
 */
public interface IRESTClient {

    void setTargetURL(String targetURL);

    void setUsernameAndPassword(String usernameAndPassword);

    void setMethod(String method);

    void setData(String parameters);

    void setFileToSend(File file);

    void request() throws Exception;

    String getResponse();
}
