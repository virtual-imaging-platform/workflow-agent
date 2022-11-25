package fr.insalyon.creatis.agent.workflow.command;

import fr.insalyon.creatis.agent.workflow.database.JobDatabase;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class KillCommand implements Command {
    private static final Logger logger = Logger.getLogger(KillCommand.class);
    private JobDatabase jobDB;

    public KillCommand(JobDatabase jobDB) {
        this.jobDB = jobDB;
    }

    public void run() {
        Map<String,String> jobIds = this.jobDB.getJobsToKill();

        for (String id : jobIds.keySet()) {
            // kill running jobs, delete waiting and succesfully submitted
            boolean mustDelete = ! jobIds.get(id).equalsIgnoreCase("RUNNING");
            killJob(id, mustDelete);
        }

        this.jobDB.close();
    }

    public void killJob(String id, boolean mustDelete) {
        try {
            String command = mustDelete ? "delete" : "kill";
            logger.info("Doing " + command + "  on job id '" + id + "'");
            String exec = "dirac-wms-job-" + command + " " + id;
            Process process = Runtime.getRuntime().exec(exec);
            process.waitFor();
            if (process.exitValue() != 0) {
                logger.error("Unable to " + command + " job id '" + id + "' with dirac");
            } else if (mustDelete) {
                this.jobDB.updateStatusToDeleted(id);
            } else {
                this.jobDB.updateStatusToCancelled(id);
            }
        } catch (InterruptedException | IOException e) {
            logger.error("Error killing job " + id, e);
        }
    }
}