package com.example.cpr;

import com.example.chat.ChatAtmosphereHandler;
import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.cpr.*;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;

import org.atmosphere.container.JSR356AsyncSupport;

import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AtmosphereServlet that use Servlet 3.0 Async API when available, and fallback to native comet support if not available.
 * For Tomcat6/7 and JBossWeb Native support, use {@link AtmosphereNativeCometServlet}.
 * <p></p>
 * If Servlet 3.0 or Native API not found, Atmosphere will use {@link org.atmosphere.container.BlockingIOCometSupport}
 *
 * @author Jeanfrancois Arcand
 */
public class JiraAtmosphereServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(JiraAtmosphereServlet.class);
    protected AtmosphereFramework framework;

    /**
     * Create an Atmosphere Servlet.
     */
    public JiraAtmosphereServlet() {

        this(false);
    }

    /**
     * Create an Atmosphere Servlet.
     *
     * @param isFilter true if this instance is used as an {@link org.atmosphere.cpr.AtmosphereFilter}
     */
    public JiraAtmosphereServlet(boolean isFilter) {

        this(isFilter, true);
    }

    /**
     * Create an Atmosphere Servlet.
     *
     * @param isFilter true if this instance is used as an {@link org.atmosphere.cpr.AtmosphereFilter}
     */
    public JiraAtmosphereServlet(boolean isFilter, boolean autoDetectHandlers) {


        framework = new AtmosphereFramework(isFilter, autoDetectHandlers);



        ChatAtmosphereHandler handler = new ChatAtmosphereHandler();
//        SimpleHandler handler = new SimpleHandler();

//        framework.addAtmosphereHandler("/chat", handler);
//        framework.addAtmosphereHandler("/chat*", handler);
//        framework.addAtmosphereHandler("/jira/plugins/servlet/chat", handler);
        framework.addAtmosphereHandler("/plugins/servlet/chat", handler);
//        framework.addAtmosphereHandler("/servlet/chat", handler);
//        framework.addAtmosphereHandler("chat", handler);

        framework.setBroadcasterCacheClassName(UUIDBroadcasterCache.class.getName());

//        logger.debug("Adding interceptor.. " + framework.getBroadcasterFactory().addBroadcasterListener());

        AtmosphereResourceLifecycleInterceptor inter = new AtmosphereResourceLifecycleInterceptor();

        BroadcastOnPostAtmosphereInterceptor interceptor = new BroadcastOnPostAtmosphereInterceptor();


//        framework.interceptor(inter);
        framework.interceptor(interceptor);
    }

    @Override
    public void destroy() {

        framework.destroy();
    }

    public void init(ServletConfig sc) throws ServletException {

        super.init(sc);

        sc.getServletContext().setAttribute("org.atmosphere.useWebSocket", Boolean.FALSE);
        sc.getServletContext().setAttribute("org.atmosphere.useWebSocketAndServlet3", Boolean.FALSE);


        framework.init(sc);
    }

    public AtmosphereFramework framework() {

        return framework;
    }

    /**
     * Delegate the request processing to an instance of {@link org.atmosphere.cpr.AsyncSupport}
     *
     * @param req the {@link javax.servlet.http.HttpServletRequest}
     * @param res the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doHead(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        doPost(req, res);
    }

    /**
     * Delegate the request processing to an instance of {@link org.atmosphere.cpr.AsyncSupport}
     *
     * @param req the {@link javax.servlet.http.HttpServletRequest}
     * @param res the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        doPost(req, res);
    }

    /**
     * Delegate the request processing to an instance of {@link org.atmosphere.cpr.AsyncSupport}
     *
     * @param req the {@link javax.servlet.http.HttpServletRequest}
     * @param res the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doTrace(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        doPost(req, res);
    }

    /**
     * Delegate the request processing to an instance of {@link org.atmosphere.cpr.AsyncSupport}
     *
     * @param req the {@link javax.servlet.http.HttpServletRequest}
     * @param res the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        doPost(req, res);
    }

    /**
     * Delegate the request processing to an instance of {@link org.atmosphere.cpr.AsyncSupport}
     *
     * @param req the {@link javax.servlet.http.HttpServletRequest}
     * @param res the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        doPost(req, res);
    }

    /**
     * Delegate the request processing to an instance of {@link org.atmosphere.cpr.AsyncSupport}
     *
     * @param req the {@link javax.servlet.http.HttpServletRequest}
     * @param res the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        doPost(req, res);
    }

    /**
     * Delegate the request processing to an instance of {@link org.atmosphere.cpr.AsyncSupport}
     *
     * @param req the {@link javax.servlet.http.HttpServletRequest}
     * @param res the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {



        logger.error("Calling " + framework.getAsyncSupport().getContainerName() + "    --    " + framework.getAsyncSupport().getClass());

        framework.doCometSupport(AtmosphereRequest.wrap(req), AtmosphereResponse.wrap(res));
    }
}
