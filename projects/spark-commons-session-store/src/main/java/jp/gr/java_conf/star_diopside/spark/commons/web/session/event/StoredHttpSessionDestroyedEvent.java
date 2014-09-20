package jp.gr.java_conf.star_diopside.spark.commons.web.session.event;

import jp.gr.java_conf.star_diopside.spark.commons.web.session.servlet.StoredHttpSession;

import org.springframework.context.ApplicationEvent;

/**
 * セッション破棄イベントクラス
 */
@SuppressWarnings("serial")
public class StoredHttpSessionDestroyedEvent extends ApplicationEvent {

    /**
     * コンストラクタ
     * 
     * @param session セッションオブジェクト
     */
    public StoredHttpSessionDestroyedEvent(StoredHttpSession session) {
        super(session);
    }

    /**
     * セッションオブジェクトを取得する。
     * 
     * @return セッションオブジェクト
     */
    public StoredHttpSession getSession() {
        return (StoredHttpSession) getSource();
    }
}
