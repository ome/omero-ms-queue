package ome.smuggler.jclient.providers.http;

import static ome.smuggler.jclient.providers.http.JsonEntity.fromEntity;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;


/**
 * {@link ResponseHandler}s to read or ignore the body of a successful response
 * into a JSON object. Any response error (status code greater or equal to 300)
 * will be turned into a runtime exception.
 */
public class JsonResponseHandlers {

    private static void assert2xx(HttpResponse response)
            throws HttpResponseException {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() >= 300) {
            String detail = null;
            try {
                String body = EntityUtils.toString(response.getEntity());
                detail = String.format("[%s] %s",
                                       statusLine.getReasonPhrase(), body);
            } catch (Exception e) {
                detail = statusLine.getReasonPhrase();
            }
            throw new HttpResponseException(statusLine.getStatusCode(), detail);
        }
    }

    private static HttpEntity fetchEntity(HttpResponse response)
            throws ClientProtocolException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            throw new ClientProtocolException("Response contains no entity.");
        }
        return entity;
    }

    private static <R> R readResponseBody(HttpResponse response,
                                          Class<R> responseType)
            throws IOException {
        assert2xx(response);
        HttpEntity entity = fetchEntity(response);
        return fromEntity(entity, responseType);
    }

    static <R> ResponseHandler<R> ignoreResponseBodyHandler() {
        return new ResponseHandler<R>() {
            @Override
            public R handleResponse(HttpResponse response) throws IOException {
                assert2xx(response);
                return null;
            }
        };
    }

    static <R> ResponseHandler<R> readResponseBodyHandler(
            final Class<R> responseType) {
        return new ResponseHandler<R>() {
            @Override
            public R handleResponse(HttpResponse response) throws IOException {
                return readResponseBody(response, responseType);
            }
        };
    }

}
