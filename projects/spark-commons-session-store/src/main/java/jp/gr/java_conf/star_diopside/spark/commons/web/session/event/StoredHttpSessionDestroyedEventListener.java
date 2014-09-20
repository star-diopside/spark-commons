package jp.gr.java_conf.star_diopside.spark.commons.web.session.event;

import jp.gr.java_conf.star_diopside.spark.commons.web.session.service.SessionStoreService;

import org.springframework.context.ApplicationListener;

/**
 * セッション破棄時の処理を行うイベントリスナー
 */
public class StoredHttpSessionDestroyedEventListener implements ApplicationListener<StoredHttpSessionDestroyedEvent> {

    private SessionStoreService sessionStoreService;

    public void setSessionStoreService(SessionStoreService sessionStoreService) {
        this.sessionStoreService = sessionStoreService;
    }

    @Override
    public void onApplicationEvent(StoredHttpSessionDestroyedEvent event) {
        sessionStoreService.removeSession(event.getSession());
    }
}
