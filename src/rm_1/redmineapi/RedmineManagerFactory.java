package rm_1.redmineapi;

import java.io.IOException;
import rm_1.redmineapi.internal.Transport;
import rm_1.redmineapi.internal.URIConfigurator;

import rm_1.MySSLSocketFactory;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.InputStream;
import java.io.FileInputStream;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import rm_1.SSLUtilities;

import rm_1.redmineapi.internal.comm.betterssl.BetterSSLFactory;
import rm_1.redmineapi.internal.comm.naivessl.NaiveSSLFactory;

/**
 * <strong>Entry point</strong> for the API. Use this class to communicate with Redmine servers.
 * <p>
 * Collection of creation methods for the redmine. Method number may grow as
 * grows number of requirements. However, having all creation methods in one
 * place allows us to refactor RemineManager internals without changing this
 * external APIs. Moreover, we can create "named constructor" for redmine
 * instances. This will allow us to have many construction methods with the same
 * signature.
 * <p>
 * Sample usage:
 * <pre>
 RedmineManager redmineManager = RedmineManagerFactory.createWithUserAuth(redmineURI, login, password);
 * </pre>
 *
 * @see RedmineManager
 */
public final class RedmineManagerFactory {
    /**
     * Prevent construction of this object even with use of dirty tricks.
     */
    private RedmineManagerFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a non-authenticating redmine manager.
     *
     * @param uri redmine manager URI.
     */
    public static RedmineManager createUnauthenticated(String uri) {
        return createUnauthenticated(uri, createDefaultHttpClient());
    }

    /**
     * Creates a non-authenticating redmine manager.
     *
     * @param uri    redmine manager URI.
     * @param httpClient you can provide your own pre-configured HttpClient if you want
     *                     to control connection pooling, manage connections eviction, closing, etc.
     */
    public static RedmineManager createUnauthenticated(String uri,
                                                HttpClient httpClient) {
        return createWithUserAuth(uri, null, null, httpClient);
    }

    /**
     * Creates an instance of RedmineManager class. Host and apiAccessKey are
     * not checked at this moment.
     *
     * @param uri          complete Redmine server web URI, including protocol and port
     *                     number. Example: http://demo.redmine.org:8080
     * @param apiAccessKey Redmine API access key. It is shown on "My Account" /
     *                     "API access key" webpage (check
     *                     <i>http://redmine_server_url/my/account</i> URL). This
     *                     parameter is <strong>optional</strong> (can be set to NULL) for Redmine
     *                     projects, which are "public".
     */
    public static RedmineManager createWithApiKey(String uri,
                                                  String apiAccessKey) {
        return createWithApiKey(uri, apiAccessKey,
                createDefaultHttpClient());
    }

    /**
     * Creates an instance of RedmineManager class. Host and apiAccessKey are
     * not checked at this moment.
     *
     * @param uri          complete Redmine server web URI, including protocol and port
     *                     number. Example: http://demo.redmine.org:8080
     * @param apiAccessKey Redmine API access key. It is shown on "My Account" /
     *                     "API access key" webpage (check
     *                     <i>http://redmine_server_url/my/account</i> URL). This
     *                     parameter is <strong>optional</strong> (can be set to NULL) for Redmine
     *                     projects, which are "public".
     * @param httpClient   Http Client. you can provide your own pre-configured HttpClient if you want
     *                     to control connection pooling, manage connections eviction, closing, etc.
     */
    public static RedmineManager createWithApiKey(String uri,
                                                  String apiAccessKey, HttpClient httpClient) {
        return new RedmineManager(new Transport(new URIConfigurator(uri,
                apiAccessKey), httpClient));
    }

    /**
     * Creates a new RedmineManager with user-based authentication.
     *
     * @param uri      redmine manager URI.
     * @param login    user's name.
     * @param password user's password.
     */
    public static RedmineManager createWithUserAuth(String uri, String login,
                                                    String password) {
        return createWithUserAuth(uri, login, password,
                createDefaultHttpClient());
    }

    /**
     * Creates a new redmine managen with user-based authentication.
     *
     * @param uri      redmine manager URI.
     * @param login    user's name.
     * @param password user's password.
     * @param httpClient you can provide your own pre-configured HttpClient if you want
     *                     to control connection pooling, manage connections eviction, closing, etc.
     */
    public static RedmineManager createWithUserAuth(String uri, String login,
                                                    String password, HttpClient httpClient) {
        final Transport transport = new Transport(
                new URIConfigurator(uri, null), httpClient);
        transport.setCredentials(login, password);
        return new RedmineManager(transport);
    }

    /**
     * Creates default insecure connection manager.
     *
     * @return default insecure connection manager.
     * @deprecated Use better key-managed factory with additional keystores.
     */
    @Deprecated
    public static ClientConnectionManager createInsecureConnectionManager() throws KeyStoreException, IOException, KeyManagementException, NoSuchAlgorithmException {
        return createConnectionManager(NaiveSSLFactory.createNaiveSSLSocketFactory());
        /*KeyStore trustStore=KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            trustStore.load(null);
        } catch (CertificateException ex) {
            Logger.getLogger(RedmineManagerFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        Collection<KeyStore> trustStores=(Collection<KeyStore>) trustStore;
                    
        
        return createConnectionManager(BetterSSLFactory.createSocketFactory(trustStores));
      */
    }
    
    /**
     * Creates a connection manager with extended trust relations. It would 
     * use both default system trusted certificates as well as all certificates
     * defined in the <code>trustStores</code>.
     * @param trustStores list of additional trust stores.
     * @return connection manager with extended trust relationship.
     */
    public static ClientConnectionManager createConnectionManagerWithExtraTrust(Collection<KeyStore> trustStores) throws KeyManagementException, KeyStoreException, IOException, NoSuchAlgorithmException {
    	/*KeyStore trustStore=KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            trustStore.load(null);
        } catch (CertificateException ex) {
            Logger.getLogger(RedmineManagerFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        trustStores=(Collection<KeyStore>) trustStore;
          */          
        
        return createConnectionManager(BetterSSLFactory.createSocketFactory(trustStores));
        //return createConnectionManager(NaiveSSLFactory.createNaiveSSLSocketFactory());
    }

    /**
     * Creates default connection manager.
     */
    public static ClientConnectionManager createDefaultConnectionManager() {
        return createConnectionManager(SSLSocketFactory.getSocketFactory());
    }

    /**
     * Creates system default connection manager. Takes in account system
     * properties for SSL configuration.
     *
     * @return default insecure connection manager.
     */
    public static ClientConnectionManager createSystemDefaultConnectionManager() {
        return createConnectionManager(SSLSocketFactory.getSystemSocketFactory());
    }

    public static ClientConnectionManager createConnectionManager(SSLSocketFactory sslSocketFactory) {
     //   try {
            /*.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\security\\cacerts");
            System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
                    
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());*/
            /*InputStream readStream = new FileInputStream("C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\security\\cacerts");
            char[] pass={'c','h','a','n','g','e','i','t'};
            trustStore.load(readStream, pass);*/
           /* trustStore.load(null, null);
            
            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            */
           /* HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
*/
            SSLUtilities.trustAllHostnames();
            SSLUtilities.trustAllHttpsCertificates();
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
            registry.register(new Scheme("https", 443, sslSocketFactory));
            //registry.register(new Scheme("https", 443, sf));
            return new BasicClientConnectionManager(registry);
           // return new ThreadSafeClientConnManager(params, registry);
          //  ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

//            return new DefaultHttpClient(ccm, params);
      /*  } catch (KeyStoreException ex) {
            Logger.getLogger(RedmineManagerFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RedmineManagerFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RedmineManagerFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(RedmineManagerFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(RedmineManagerFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(RedmineManagerFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;*/
    }

    public static HttpClient createDefaultHttpClient() {
        try {
            return getNewHttpClient(createSystemDefaultConnectionManager());
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultHttpClient();
        }
    }

    /**
     * Helper method to create an http client from connection manager. This new
     * client is configured to use system proxy (if any).
     */
    public static HttpClient getNewHttpClient(ClientConnectionManager connectionManager) {
        try {

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            final DefaultHttpClient result = new DefaultHttpClient(
                    connectionManager, params);
            configureProxy(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultHttpClient();
        }
    }

    private static void configureProxy(DefaultHttpClient httpclient) {
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        if (proxyHost != null && proxyPort != null) {
            int port;
            try {
                port = Integer.parseInt(proxyPort);
            } catch (NumberFormatException e) {
                throw new RedmineConfigurationException("Illegal proxy port "
                        + proxyPort, e);
            }
            HttpHost proxy = new HttpHost(proxyHost, port);
            httpclient.getParams().setParameter(
                    org.apache.http.conn.params.ConnRoutePNames.DEFAULT_PROXY,
                    proxy);
            String proxyUser = System.getProperty("http.proxyUser");
            if (proxyUser != null) {
                String proxyPassword = System.getProperty("http.proxyPassword");
                httpclient.getCredentialsProvider().setCredentials(
                        new AuthScope(proxyHost, port),
                        new UsernamePasswordCredentials(proxyUser,
                                proxyPassword));
            }
        }
    }
}
