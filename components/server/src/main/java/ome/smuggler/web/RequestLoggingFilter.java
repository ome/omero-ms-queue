package ome.smuggler.web;

import ome.smuggler.web.imports.ImportController;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Logs every import request and, in addition, if the application was started
 * with the DEBUG log level then any other request will be logged too.
 */
public class RequestLoggingFilter extends AbstractRequestLoggingFilter {

    /**
     * Creates a new instance.
     */
    public RequestLoggingFilter() {
        setIncludeClientInfo(true);
        setIncludeQueryString(true);
        setMaxPayloadLength(4096);
        setIncludePayload(true);
    }

    private boolean isImportRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getRequestURI())
                       .map(path -> path.endsWith(ImportController.ImportUrl))
                       .orElse(false);
    }

    private String maskSensitiveData(String message) {
        return message.replaceAll("sessionKey.{3,10}", "sessionKey:•");
    }
    /* NOTE. Despicable me! Come up with something better!
     * What if the sessionKey field is renamed? What if...well, don't get me
     * started on listing the 10,000 drawbacks of this cheap hack!
     */

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return isImportRequest(request) || logger.isDebugEnabled();
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        if (isImportRequest(request)) {
            logger.info(maskSensitiveData(message));
        }
        logger.debug(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        if (isImportRequest(request)) {
            logger.info(maskSensitiveData(message));
        }
        logger.debug(message);
    }

}
/* NOTE. Quick solution to logging requests.
 * Better than nothing, but far from optimal. If needed we can improve on this.
 */