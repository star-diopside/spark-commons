package jp.gr.java_conf.star_diopside.spark.commons.web.session.servlet;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import jp.gr.java_conf.star_diopside.spark.commons.web.session.event.StoredHttpSessionDestroyedEvent;

import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * セッション破棄時に{@link StoredHttpSessionDestroyedEvent}イベントを発行するリスナー
 */
public class SessionStoreListener implements ServletContextListener, HttpSessionListener {

    private static final String SESSION_MAP_KEY = SessionStoreListener.class.getName() + ".SESSION_MAP";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute(SESSION_MAP_KEY, new ConcurrentHashMap<HttpSession, StoredHttpSession>());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute(SESSION_MAP_KEY);
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        getSessionMap(session.getServletContext()).put(session, new StoredHttpSession(session));
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        StoredHttpSession storedHttpSession = getSessionMap(session.getServletContext()).remove(session);
        if (storedHttpSession != null) {
            StoredHttpSessionDestroyedEvent event = new StoredHttpSessionDestroyedEvent(storedHttpSession);
            WebApplicationContextUtils.getRequiredWebApplicationContext(session.getServletContext()).publishEvent(event);
        }
    }

    @SuppressWarnings("unchecked")
    private static ConcurrentHashMap<HttpSession, StoredHttpSession> getSessionMap(ServletContext sc) {
        return (ConcurrentHashMap<HttpSession, StoredHttpSession>) sc.getAttribute(SESSION_MAP_KEY);
    }

    /**
     * セッションオブジェクトをラッピングした{@link StoredHttpSession}オブジェクトを取得する。
     * 
     * @param session セッションオブジェクト
     * @return セッションオブジェクトをラッピングした{@link StoredHttpSession}オブジェクト
     */
    public static StoredHttpSession getStoredHttpSession(HttpSession session) {
        ConcurrentHashMap<HttpSession, StoredHttpSession> sessionMap = getSessionMap(session.getServletContext());
        StoredHttpSession storedHttpSession = sessionMap.get(session);
        if (storedHttpSession == null) {
            storedHttpSession = new StoredHttpSession(session);
            sessionMap.put(session, storedHttpSession);
        }
        return storedHttpSession;
    }
}
