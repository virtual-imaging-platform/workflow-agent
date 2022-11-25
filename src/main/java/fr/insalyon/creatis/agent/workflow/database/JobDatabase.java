package fr.insalyon.creatis.agent.workflow.database;

import fr.insalyon.creatis.agent.workflow.Configuration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String,String> getJobsToKill() {
        try {
            PreparedStatement ps = this.connection.prepareStatement("SELECT id, status FROM Jobs WHERE status='SUCCESSFULLY_SUBMITTED' OR status='QUEUED' OR status='RUNNING'");
            ResultSet rs = ps.executeQuery();
            Map<String,String> jobs = new HashMap<>();

            while(rs.next()) {
                jobs.put(rs.getString("id"),rs.getString("status"));
            }

            return jobs;
        } catch (SQLException e) {
            logger.error("Error getting job ids", e);
            return null;
        }
    }


    public void updateStatusToCancelled(String jobId) {
        updateStatus(jobId, "CANCELLED");
    }

    public void updateStatusToDeleted(String jobId) {
        updateStatus(jobId, "DELETED");
    }

    public void updateStatus(String jobId, String status) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("UPDATE Jobs SET status = ? WHERE id = ?");
            ps.setString(1, status);
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
