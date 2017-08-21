package net.bitnine.security;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;

/**
 * Simple implementation of <tt>RedirectStrategy</tt> which is the default used throughout
 * the framework.
 *
 * @author Luke Taylor
 * @since 3.0
 */
public class CustomRedirectStrategy implements RedirectStrategy {

    protected final Log logger = LogFactory.getLog(getClass());

    private boolean contextRelative;

    /**
     * Redirects the response to the supplied URL.
     * <p>
     * If <tt>contextRelative</tt> is set, the redirect value will be the value after the
     * request context path. Note that this will result in the loss of protocol
     * information (HTTP or HTTPS), so will cause problems if a redirect is being
     * performed to change to HTTPS, for example.
     */
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response,
            String url) throws IOException {
        String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
        redirectUrl = response.encodeRedirectURL(redirectUrl);

        if (logger.isDebugEnabled()) {
            logger.debug("Redirecting to '" + redirectUrl + "'");
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher(redirectUrl);
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        response.sendRedirect(redirectUrl);
    }

    protected String calculateRedirectUrl(String contextPath, String url) {
        if (!UrlUtils.isAbsoluteUrl(url)) {
            if (isContextRelative()) {
                return url;
            }
            else {
                return contextPath + url;
            }
        }

        // Full URL, including http(s)://

        if (!isContextRelative()) {
            return url;
        }

        // Calculate the relative URL from the fully qualified URL, minus the last
        // occurrence of the scheme and base context.
        url = url.substring(url.lastIndexOf("://") + 3); // strip off scheme
        url = url.substring(url.indexOf(contextPath) + contextPath.length());

        if (url.length() > 1 && url.charAt(0) == '/') {
            url = url.substring(1);
        }

        return url;
    }

    /**
     * If <tt>true</tt>, causes any redirection URLs to be calculated minus the protocol
     * and context path (defaults to <tt>false</tt>).
     */
    public void setContextRelative(boolean useRelativeContext) {
        this.contextRelative = useRelativeContext;
    }

    /**
     * Returns <tt>true</tt>, if the redirection URL should be calculated
     * minus the protocol and context path (defaults to <tt>false</tt>).
     */
    protected boolean isContextRelative() {
        return contextRelative;
    }
}
