package hu.juranyi.zsolt.heritrixremote.communication;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

/**
 *
 * @author Zsolt Jurányi
 */
public class CURL implements IRESTClient {

    private String targetURL;
    private String usernameAndPassword;
    private String data;
    private File file;
    private String response;

    @Override
    public void setTargetURL(String targetURL) {
        this.targetURL = targetURL;
    }

    @Override
    public void setUsernameAndPassword(String usernameAndPassword) {
        this.usernameAndPassword = usernameAndPassword;
    }

    @Override
    public void setMethod(String method) {
        // not needed, cURL will handle this
    }

    @Override
    public void setData(String data) {
        this.data = data;
    }

    @Override
    public void setFileToSend(File file) {
        this.file = file;
    }

    @Override
    public void request() throws Exception { // TODO TEST request()
        String curl = "curl DATA -k -u AUTH --anyauth --location URL";
        curl = curl.replace("AUTH", usernameAndPassword).replace("URL", targetURL);
        if (null != data) {
            curl = curl.replace("DATA", "-d " + data);
        } else if (null != file) {
            curl = curl.replace("DATA", "-T " + file.getAbsolutePath());
        }

        Runtime rt = Runtime.getRuntime();
        Process proc = Runtime.getRuntime().exec(curl);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = stdInput.readLine()) != null) {
            sb.append(s);
            sb.append("\n");
        }
        response = sb.toString();
    }

    @Override
    public String getResponse() {
        return response;
    }
}
