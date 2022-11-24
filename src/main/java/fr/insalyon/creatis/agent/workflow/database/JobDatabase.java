package fr.insalyon.creatis.agent.workflow.database;

import fr.insalyon.creatis.agent.workflow.Configuration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class JobDatabase {
    private static final Logger logger = Logger.getLogger(JobDatabase.class);
    private Connection connection;
    private String workflowsPath;
    private String workflowId;

    public JobDatabase(String workflowsPath, String workflowId) {
        this.workflowsPath = workflowsPath;
        this.workflowId = workflowId;
        this.connect();
    }

    public List<String> getJobIds() {
        try {
            PreparedStatement ps = this.connection.prepareStatement("SELECT id FROM Jobs WHERE status='SUCCESSFULLY_SUBMITTED' OR status='QUEUED' OR status='RUNNING'");
            ResultSet rs = ps.executeQuery();
            ArrayList jobIds = new ArrayList();

            while(rs.next()) {
                jobIds.add(rs.getString("id"));
            }

            return jobIds;
        } catch (SQLException e) {
            logger.error("Error getting job ids", e);
            return null;
        }
    }

    public void updateStatus(String jobId) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("UPDATE Jobs SET status = ? WHERE id = ?");
            ps.setString(1, "CANCELLED");
            ps.setString(2, jobId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            logger.error("Error updating status for job " + jobId, e);
        }

    }

    private synchronized void connect() {
        try {
            Class.forName("org.h2.Driver");
            this.connection = DriverManager.getConnection("jdbc:h2:tcp://" + Configuration.getInstance().getH2DBServer() + ":" + Configuration.getInstance().getH2DBPort() + "/" + this.workflowsPath + "/" + this.workflowId + "/db/jobs;MVCC=TRUE", "gasw", "gasw");
            this.connection.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("error connecting to h2 database", e);
        }

    }

    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            logger.error("Error closing h2 connection ", e);
        }

    }
}
