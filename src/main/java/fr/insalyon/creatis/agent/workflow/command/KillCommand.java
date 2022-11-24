package fr.insalyon.creatis.agent.workflow.command;

import fr.insalyon.creatis.agent.workflow.database.JobDatabase;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

public class KillCommand implements Command {
    private static final Logger logger = Logger.getLogger(KillCommand.class);
    private JobDatabase jobDB;

    public KillCommand(JobDatabase jobDB) {
        this.jobDB = jobDB;
    }

    public void run() {
        List<String> jobIds = this.jobDB.getJobIds();

        for (String id : jobIds) {
            try {
                logger.info("Killing job id '" + id + "'");
                String exec = "dirac-wms-job-kill " + id;
                Process process = Runtime.getRuntime().exec(exec);
                process.waitFor();
                if (process.exitValue() != 0) {
                    logger.error("Unable to kill job id '" + id + "'");
                } else {
                    this.jobDB.updateStatus(id);
                }
            } catch (InterruptedException | IOException e) {
                logger.error("Error killing job " + id, e);
            }
        }

        this.jobDB.close();
    }
}