package hu.juranyi.zsolt.heritrixremote.communication;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import sun.misc.BASE64Encoder;

/**
 * RESTClient implementation based on Rally Software's code. Unfortunately that
 * caused exceptions when using HTTPS connection, something with the
 * certificates. Tried to get rid of them, but tough they disappeared, a 401
 * error code came back to me as my reward even if the user:pass was correct.
 * 
 * @author Zsolt Jurányi
 */
@Deprecated
public class RESTClient implements IRESTClient {

    private String targetURL;
    private String usernameAndPassword;
    private String method;
    private Map<String, String> parameters;
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
        this.method = method;
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public void setFileToSend(File file) {
        this.file = file;
    }

    @Override
    public void request() throws Exception {
        /*
         * Thanks to Rally Software for the most parts of this method!
         * 
         * Source:
         * https://prod.help.rallydev.com/basic-rest-client-operations-java
         */

        bypassSSLCertificate();

        URL url = new URL(targetURL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        // write auth header
        BASE64Encoder encoder = new BASE64Encoder();
        String encodedCredential = encoder.encode(usernameAndPassword.getBytes());
        connection.setRequestProperty("Authorization", "BASIC " + encodedCredential);

        // write parameters
        InputStream body = null;
        if (file != null) {
            body = new FileInputStream(file);
        } else if (parameters != null) {
            StringBuffer parametersString = new StringBuffer();
            for (String k : parameters.keySet()) {
                parametersString.append(k);
                parametersString.append("=");
                parametersString.append(parameters.get(k));
                parametersString.append("&");
            }
            body = new ByteArrayInputStream(parametersString.toString().getBytes());
        }

        // write body if we're doing POST or PUT
        byte buffer[] = new byte[8192];
        int read = 0;
        if (body != null) {
            connection.setDoOutput(true);

            OutputStream output = connection.getOutputStream();
            while ((read = body.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        }

        // do request
        connection.connect();

        InputStream responseBodyStream = connection.getInputStream();
        StringBuffer responseBody = new StringBuffer();
        while ((read = responseBodyStream.read(buffer)) != -1) {
            responseBody.append(new String(buffer, 0, read));
        }
        connection.disconnect();

        // dump body
        response = responseBody.toString();
    }

    @Override
    public String getResponse() {
        return response;
    }

    private void bypassSSLCertificate() throws Exception {
        // bypass SSL certificate
        // http://www.coderanch.com/t/134384/Security/error-subject-alternative-names-secure

        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        // bypass SSL certificate - part II
        // https://code.google.com/p/misc-utils/wiki/JavaHttpsUrl

        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};

        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }
}
