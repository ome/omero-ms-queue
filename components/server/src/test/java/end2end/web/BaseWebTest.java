package end2end.web;

import static util.sequence.Arrayz.array;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import ome.smuggler.Main;
import ome.smuggler.config.Profiles;
import ome.smuggler.run.ImportServer;
import util.sequence.Arrayz;

import org.junit.BeforeClass;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


public class BaseWebTest {

    public static URI url(String...xs) {
        UriComponentsBuilder builder = UriComponentsBuilder
                                      .fromUriString("http://localhost:8000/");
        String[] ys = Arrayz.op(String[]::new).intersperse(() -> "/", xs);
        Arrays.asList(ys).forEach(builder::path);
        return builder.build().toUri();
    }
    
    private static final AtomicBoolean serverStarted = new AtomicBoolean(false);
    protected static Config config;
    
    @BeforeClass
    public static void startImportServer() throws Exception {
        if (!serverStarted.get()) {   // (*)
            synchronized (serverStarted) {
                if (!serverStarted.get()) {
                    serverStarted.set(true);
                    config = new Config();
                    Main.main(array(ImportServer.class.getName(), Profiles.Dev));
                }
            }
        }
    }
    /* NB JUnit may run tests concurrently.
     */
    
    protected final RestTemplate httpClient;
    
    public BaseWebTest() {
        httpClient = new TestRestTemplate();
    }
    
    protected <X, Y> ResponseEntity<Y> post(URI url, X body, 
            Class<Y> responseType, Consumer<HttpHeaders> setHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        setHeaders.accept(headers);
        
        HttpEntity<X> request = new HttpEntity<>(body, headers);
        
        return httpClient.postForEntity(url, request, responseType);
    }
    
    protected <X, Y> ResponseEntity<Y> post(URI url, X body, Class<Y> responseType) {
        return post(url, body, responseType, 
                    hs -> hs.add(HttpHeaders.ACCEPT, 
                                 MediaType.APPLICATION_JSON_VALUE));
    }
    
}
