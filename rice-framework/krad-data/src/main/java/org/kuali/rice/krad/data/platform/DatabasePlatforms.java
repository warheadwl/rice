package org.kuali.rice.krad.data.platform;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Contains constants and utilities related to the supported database platforms.
 *
 * We use a String to represent the platform name as opposed to an Enum because this allows for the potential to
 * configure and use custom platforms at runtime without requiring internal code modification to support a new platform.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class DatabasePlatforms {

    public static final String ORACLE = "Oracle";
    public static final String MYSQL = "MySQL";

    private static final ConcurrentMap<DataSource, DatabasePlatformInfo> platformCache =
            new ConcurrentHashMap<DataSource, DatabasePlatformInfo>();

    public static DatabasePlatformInfo detectPlatform(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource must not be null.");
        }
        DatabasePlatformInfo platformInfo = platformCache.get(dataSource);
        if (platformInfo == null) {
            JdbcTemplate template = new JdbcTemplate(dataSource);
            platformInfo = platformCache.putIfAbsent(dataSource, template.execute(
                    new ConnectionCallback<DatabasePlatformInfo>() {
                        @Override
                        public DatabasePlatformInfo doInConnection(
                                Connection connection) throws SQLException, DataAccessException {
                            DatabaseMetaData metadata = connection.getMetaData();
                            String vendorName = metadata.getDatabaseProductName();
                            int version = metadata.getDatabaseMajorVersion();
                            return new DatabasePlatformInfo(vendorName, version);
                        }
                    }));
            if (platformInfo == null) {
                platformInfo = platformCache.get(dataSource);
            }
        }
        return platformInfo;
    }

    private DatabasePlatforms() {}

}
