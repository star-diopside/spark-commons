package jp.gr.java_conf.star_diopside.spark.commons.web.session.service;

import java.sql.Types;
import java.util.concurrent.TimeUnit;

import jp.gr.java_conf.star_diopside.spark.commons.web.session.servlet.SessionStoreHttpServletRequest;
import jp.gr.java_conf.star_diopside.spark.commons.web.session.servlet.StoredHttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * セッション永続化にデータソースを使用する{@link SessionStoreService}クラス
 */
public class JdbcSessionStore extends JdbcDaoSupport implements SessionStoreService {

    private static final Log LOG = LogFactory.getLog(JdbcSessionStore.class);

    private String sessionTableName = "sessions";
    private String sessionIdColumnName = "id";
    private String sessionDataColumnName = "data";
    private String sessionModifiedTimeColumnName = "modified_time";
    private String sessionLastAccessedTimeColumnName = "last_accessed_time";
    private String sessionMaxInactiveIntervalColumnName = "max_inactive_interval";

    private String countSessionSql;
    private String selectSessionDataSql;
    private String selectSessionModifiedTimeSql;
    private String selectSessionListSql;
    private String insertSessionSql;
    private String updateSessionSql;
    private String deleteSessionSql;
    private String deleteForceSessionSql;

    public void setSessionTableName(String sessionTableName) {
        this.sessionTableName = sessionTableName;
    }

    public void setSessionIdColumnName(String sessionIdColumnName) {
        this.sessionIdColumnName = sessionIdColumnName;
    }

    public void setSessionDataColumnName(String sessionDataColumnName) {
        this.sessionDataColumnName = sessionDataColumnName;
    }

    public void setSessionModifiedTimeColumnName(String sessionModifiedTimeColumnName) {
        this.sessionModifiedTimeColumnName = sessionModifiedTimeColumnName;
    }

    public void setSessionLastAccessedTimeColumnName(String sessionLastAccessedTimeColumnName) {
        this.sessionLastAccessedTimeColumnName = sessionLastAccessedTimeColumnName;
    }

    public void setSessionMaxInactiveIntervalColumnName(String sessionMaxInactiveIntervalColumnName) {
        this.sessionMaxInactiveIntervalColumnName = sessionMaxInactiveIntervalColumnName;
    }

    @Override
    protected void initDao() throws Exception {
        super.initDao();
        countSessionSql = "select count(" + sessionIdColumnName + ") from " + sessionTableName + " where "
                + sessionIdColumnName + " = ?";
        selectSessionDataSql = "select " + sessionDataColumnName + " from " + sessionTableName + " where "
                + sessionIdColumnName + " = ?";
        selectSessionModifiedTimeSql = "select " + sessionModifiedTimeColumnName + " from " + sessionTableName
                + " where " + sessionIdColumnName + " = ?";
        selectSessionListSql = "select " + sessionIdColumnName + ", " + sessionModifiedTimeColumnName + ", "
                + sessionLastAccessedTimeColumnName + ", " + sessionMaxInactiveIntervalColumnName + " from "
                + sessionTableName;
        insertSessionSql = "insert into " + sessionTableName + " (" + sessionIdColumnName + ", "
                + sessionDataColumnName + ", " + sessionModifiedTimeColumnName + ", "
                + sessionLastAccessedTimeColumnName + ", " + sessionMaxInactiveIntervalColumnName
                + ") values (?, ?, ?, ?, ?)";
        updateSessionSql = "update " + sessionTableName + " set " + sessionDataColumnName + " = ?, "
                + sessionModifiedTimeColumnName + " = ?, " + sessionLastAccessedTimeColumnName + " = ?, "
                + sessionMaxInactiveIntervalColumnName + " = ? where " + sessionIdColumnName + " = ?";
        deleteSessionSql = "delete from " + sessionTableName + " where " + sessionIdColumnName + " = ? and "
                + sessionModifiedTimeColumnName + " = ?";
        deleteForceSessionSql = "delete from " + sessionTableName + " where " + sessionIdColumnName + " = ?";
    }

    @Override
    public void readSession(SessionStoreHttpServletRequest request) {

        String requestedSessionId = request.getRequestedSessionId();

        if (requestedSessionId == null) {
            return;
        }

        try {
            // データベースに保存されたセッション最終アクセス時刻を取得する。
            long modifiedTime = getJdbcTemplate().queryForObject(selectSessionModifiedTimeSql,
                    new Object[] { requestedSessionId }, new int[] { Types.VARCHAR }, (rs, rowNum) -> rs.getLong(1));

            StoredHttpSession session = (StoredHttpSession) request.getSession();

            if (session.getSerializedModifiedTime() != modifiedTime) {
                // データベースからセッション情報を取得する。
                byte[] data = getJdbcTemplate().queryForObject(selectSessionDataSql,
                        new Object[] { requestedSessionId }, new int[] { Types.VARCHAR },
                        (rs, rowNum) -> rs.getBytes(1));

                // セッションをデシリアライズする。
                session.deserialize(data);
            }

            // データベースからセッション情報を削除する。
            if (!request.isRequestedSessionIdValid()) {
                getJdbcTemplate().update(deleteForceSessionSql, new Object[] { requestedSessionId },
                        new int[] { Types.VARCHAR });
            }

        } catch (EmptyResultDataAccessException e) {
            LOG.debug("Not found session for id = " + requestedSessionId, e);
        }
    }

    @Override
    public void storeSession(SessionStoreHttpServletRequest request) {

        StoredHttpSession session = (StoredHttpSession) request.getSession(false);

        if (session == null) {
            return;
        }

        // セッションをシリアライズする。
        byte[] data = session.serialize();

        // データベースにセッション情報を登録する。
        String sessionId = session.getId();
        Integer count = getJdbcTemplate().queryForObject(countSessionSql, new Object[] { sessionId },
                new int[] { Types.VARCHAR }, Integer.class);

        if (count == 0) {
            getJdbcTemplate().update(
                    insertSessionSql,
                    new Object[] { sessionId, new SqlLobValue(data), session.getModifiedTime(),
                            session.getLastAccessedTime(), session.getMaxInactiveInterval() },
                    new int[] { Types.VARCHAR, Types.BLOB, Types.BIGINT, Types.BIGINT, Types.INTEGER });
        } else {
            getJdbcTemplate().update(
                    updateSessionSql,
                    new Object[] { new SqlLobValue(data), session.getModifiedTime(), session.getLastAccessedTime(),
                            session.getMaxInactiveInterval(), sessionId },
                    new int[] { Types.BLOB, Types.BIGINT, Types.BIGINT, Types.INTEGER, Types.VARCHAR });
        }
    }

    @Override
    public void removeSession(StoredHttpSession session) {

        String sessionId = session.getId();

        // データベースに保存されたセッション最終アクセス時刻を取得する。
        try {
            long modifiedTime = getJdbcTemplate().queryForObject(selectSessionModifiedTimeSql,
                    new Object[] { sessionId }, new int[] { Types.VARCHAR }, (rs, rowNum) -> rs.getLong(1));

            // データベースからセッション情報を削除する。
            if (session.getSerializedModifiedTime() == modifiedTime) {
                getJdbcTemplate().update(deleteSessionSql, new Object[] { sessionId, modifiedTime },
                        new int[] { Types.VARCHAR, Types.BIGINT });
            }

        } catch (EmptyResultDataAccessException e) {
            LOG.debug("Not found session for id = " + sessionId, e);
        }
    }

    @Override
    public void removeInvalidSession() {

        // セッション一覧を取得し、無効なセッションを削除する。
        SqlRowSet srs = getJdbcTemplate().queryForRowSet(selectSessionListSql);
        long current = System.currentTimeMillis();

        while (srs.next()) {
            // データベースのセッション情報を取得する。
            String sessionId = srs.getString(1);
            long modifiedTime = srs.getLong(2);
            long lastAccessedTime = srs.getLong(3);
            int maxInactiveInterval = srs.getInt(4);

            // 無効なセッションの場合、レコードを削除する。
            if (lastAccessedTime + TimeUnit.SECONDS.toMillis(maxInactiveInterval) < current) {
                getJdbcTemplate().update(deleteSessionSql, new Object[] { sessionId, modifiedTime },
                        new int[] { Types.VARCHAR, Types.BIGINT });
            }
        }
    }
}
