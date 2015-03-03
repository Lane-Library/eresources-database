package edu.stanford.irt.eresources.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import edu.stanford.irt.eresources.ETLProcessor;
import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.EresourcesETLApp;
import edu.stanford.irt.eresources.StartDate;

public class JDBCEresourcesETLApp extends EresourcesETLApp {

    private List<String> callStatements = Collections.emptyList();

    private List<String> createStatements = Collections.emptyList();

    private DataSource dataSource;

    private String userName;

    public JDBCEresourcesETLApp(final DataSource dataSource, final List<ETLProcessor<?>> processors,
            final StartDate startDate, final boolean killPrevious) {
        super(processors, startDate, killPrevious);
        this.dataSource = dataSource;
    }
    
    public void setCallStatements(List<String> callStatements) {
        this.callStatements = callStatements;
    }
    
    public void setCreateStatements(List<String> createStatements) {
        this.createStatements = createStatements;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    protected void postProcess() {
        try (Connection connection = this.dataSource.getConnection()) {
            for (String call : this.callStatements) {
                if ((call.indexOf("{0}") > 0) && (null != this.userName)) {
                    call = MessageFormat.format(call, new Object[] { this.userName });
                }
                executeCall(connection, call);
            }
        } catch (SQLException e) {
            throw new EresourceException(e);
        }
    }

    @Override
    protected void preProcess() {
        try (Connection connection = this.dataSource.getConnection();
                Statement stmt = connection.createStatement()) {
            for (String create : this.createStatements) {
                try {
                    stmt.execute(create);
                } catch (SQLException e) {
                    int errorCode = e.getErrorCode();
                    if ((942 != errorCode) && (1418 != errorCode) && (2289 != errorCode)) {
                        throw e;
                    }
                }
            }
        } catch (SQLException e) {
            throw new EresourceException(e);
        }
    }

    private void executeCall(final Connection connection, final String call) {
        try (CallableStatement callable = connection.prepareCall(call)) {
            callable.execute();
        } catch (SQLException e) {
            throw new EresourceException(e);
        }
    }
}
