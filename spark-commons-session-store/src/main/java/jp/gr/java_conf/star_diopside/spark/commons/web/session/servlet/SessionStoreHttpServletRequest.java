package jp.gr.java_conf.star_diopside.spark.commons.web.session.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * セッションオブジェクトを{@link StoredHttpSession}にラッピングするサーブレットリクエスト
 */
public class SessionStoreHttpServletRequest extends HttpServletRequestWrapper {

    /**
     * コンストラクタ
     * 
     * @param request サーブレットリクエスト
     */
    public SessionStoreHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public StoredHttpSession getSession(boolean create) {
        HttpSession session = super.getSession(create);
        return session == null ? null : getSessionInternal(session);
    }

    @Override
    public StoredHttpSession getSession() {
        HttpSession session = super.getSession();
        return getSessionInternal(session);
    }

    private StoredHttpSession getSessionInternal(HttpSession session) {
        return SessionStoreListener.getStoredHttpSession(session);
    }
}
