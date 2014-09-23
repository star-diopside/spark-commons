package jp.gr.java_conf.star_diopside.spark.commons.web.session.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;

import jp.gr.java_conf.star_diopside.spark.commons.web.session.servlet.SessionStoreHttpServletRequest;
import jp.gr.java_conf.star_diopside.spark.commons.web.session.servlet.SessionStoreListener;
import jp.gr.java_conf.star_diopside.spark.commons.web.session.servlet.StoredHttpSession;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.iterators.EnumerationIterator;
import org.dbunit.database.DatabaseConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = "classpath:test-context.xml")
@TransactionConfiguration
@Transactional
public class JdbcSessionStoreTest {

    @Resource(name = "jdbcSessionStoreDataSource")
    private DataSource dataSource;

    @Autowired
    @Qualifier("sessionStoreService")
    private SessionStoreService service;

    @Autowired
    private MockServletContext servletContext;

    @Autowired
    private MockHttpServletRequest request;

    private SessionStoreListener listener;

    @Before
    public void setUp() throws Exception {
        listener = new SessionStoreListener();
        listener.contextInitialized(new ServletContextEvent(servletContext));
    }

    @After
    public void tearDown() {
        listener.contextDestroyed(new ServletContextEvent(servletContext));
    }

    @Test
    public void testReadSessionWhenNotExistSession() {
        SessionStoreHttpServletRequest req = new SessionStoreHttpServletRequest(request);
        service.readSession(req);
    }

    @Test
    public void testStoreSessionWhenNotExistSession() {
        SessionStoreHttpServletRequest req = new SessionStoreHttpServletRequest(request);
        service.storeSession(req);
    }

    @Test
    public void testRemoveSessionWhenNotStoredSession() {
        SessionStoreHttpServletRequest req = new SessionStoreHttpServletRequest(request);
        service.removeSession(req.getSession());
    }

    @Test
    public void testStoreAndReadSession() {

        Date now = new Date();

        SessionStoreHttpServletRequest req = new SessionStoreHttpServletRequest(request);
        StoredHttpSession session = req.getSession();
        session.setAttribute("strKey", "value");
        session.setAttribute("dateKey", now);
        session.setAttribute("intKey", 1);
        service.storeSession(req);

        new EnumerationIterator<>(session.getAttributeNames()).forEachRemaining(session::removeAttribute);
        assertThat(CollectionUtils.size(session.getAttributeNames()), is(0));

        MockHttpServletRequest newRequest = new MockHttpServletRequest(servletContext);
        newRequest.setRequestedSessionId(session.getId());
        SessionStoreHttpServletRequest newReq = new SessionStoreHttpServletRequest(newRequest);
        service.readSession(newReq);
        StoredHttpSession newSession = newReq.getSession();

        assertThat(CollectionUtils.size(newSession.getAttributeNames()), is(3));
        assertThat(newSession.getAttribute("strKey"), is("value"));
        assertThat(newSession.getAttribute("dateKey"), is(now));
        assertThat(newSession.getAttribute("intKey"), is(1));
    }

    @Test
    public void testStoreAndRemoveSession() throws Exception {

        DatabaseConnection conn = new DatabaseConnection(DataSourceUtils.getConnection(dataSource));
        Date now = new Date();

        SessionStoreHttpServletRequest req = new SessionStoreHttpServletRequest(request);
        StoredHttpSession session = req.getSession();
        session.setAttribute("strKey", "value");
        session.setAttribute("dateKey", now);
        session.setAttribute("intKey", 1);
        service.storeSession(req);
        assertThat(conn.getRowCount("sessions"), is(1));

        service.removeSession(session);
        assertThat(conn.getRowCount("sessions"), is(0));
    }

    @Test
    public void testRemoveInvalidSession() {
        service.removeInvalidSession();
    }
}
