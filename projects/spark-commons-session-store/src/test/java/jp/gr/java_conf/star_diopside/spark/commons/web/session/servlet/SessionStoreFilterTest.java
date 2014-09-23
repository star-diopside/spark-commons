package jp.gr.java_conf.star_diopside.spark.commons.web.session.servlet;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.function.BiConsumer;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.gr.java_conf.star_diopside.spark.commons.web.session.service.SessionStoreService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

public class SessionStoreFilterTest {

    private MockServletContext servletContext;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private SessionStoreListener listener;
    private SessionStoreFilter filter;
    private SessionStoreService service;

    @Before
    public void setUp() throws Exception {
        servletContext = new MockServletContext();
        listener = new SessionStoreListener();
        listener.contextInitialized(new ServletContextEvent(servletContext));
        service = mock(SessionStoreService.class);
        filter = new SessionStoreFilter();
        filter.setSessionStoreService(service);
        filter.init(new MockFilterConfig(servletContext, "sessionStore"));
        request = new MockHttpServletRequest(servletContext);
        response = new MockHttpServletResponse();
    }

    @After
    public void tearDown() {
        filter.destroy();
        listener.contextDestroyed(new ServletContextEvent(servletContext));
    }

    @Test
    public void testNotExistSession() throws Exception {
        filter.doFilter(request, response, new MockFilterChain(new ConsumerServlet((req, res) -> {
        })));

        SessionStoreHttpServletRequest req = buildSessionStoreHttpServletRequest(request);
        verify(service, never()).readSession(req);
        verify(service, never()).storeSession(req);
    }

    @Test
    public void testNewSession() throws Exception {
        filter.doFilter(request, response, new MockFilterChain(new ConsumerServlet((req, res) -> {
            req.getSession();
        })));

        SessionStoreHttpServletRequest req = buildSessionStoreHttpServletRequest(request);
        verify(service, never()).readSession(req);
        verify(service).storeSession(req);
    }

    @Test
    public void testNopExistSession() throws Exception {
        request.getSession();
        filter.doFilter(request, response, new MockFilterChain(new ConsumerServlet((req, res) -> {
        })));

        SessionStoreHttpServletRequest req = buildSessionStoreHttpServletRequest(request);
        verify(service).readSession(req);
        verify(service, never()).storeSession(req);
    }

    @Test
    public void testUpdateExistSession() throws Exception {
        request.getSession();
        filter.doFilter(request, response, new MockFilterChain(new ConsumerServlet((req, res) -> {
            req.getSession().setAttribute("key", "value");
        })));

        SessionStoreHttpServletRequest req = buildSessionStoreHttpServletRequest(request);
        verify(service).readSession(req);
        verify(service).storeSession(req);
    }

    @Test
    public void testInvalidSession() throws Exception {
        request.getSession();
        filter.doFilter(request, response, new MockFilterChain(new ConsumerServlet((req, res) -> {
            req.getSession().invalidate();
        })));

        SessionStoreHttpServletRequest req = buildSessionStoreHttpServletRequest(request);
        verify(service).readSession(req);
        verify(service, never()).storeSession(req);
    }

    @SuppressWarnings("serial")
    private static class ConsumerServlet extends GenericServlet {

        private BiConsumer<HttpServletRequest, HttpServletResponse> action;

        public ConsumerServlet(BiConsumer<HttpServletRequest, HttpServletResponse> action) {
            this.action = action;
        }

        @Override
        public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
            action.accept((HttpServletRequest) req, (HttpServletResponse) res);
        }
    }

    private static SessionStoreHttpServletRequest buildSessionStoreHttpServletRequest(HttpServletRequest request) {
        return new SessionStoreHttpServletRequest(request) {
            @Override
            public boolean equals(Object obj) {
                return obj instanceof SessionStoreHttpServletRequest ? getRequest().equals(
                        ((SessionStoreHttpServletRequest) obj).getRequest()) : false;
            }
        };
    }
}
