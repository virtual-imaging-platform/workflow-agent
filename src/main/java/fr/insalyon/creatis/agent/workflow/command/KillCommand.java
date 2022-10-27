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
        Iterator i$ = jobIds.iterator();

        while(i$.hasNext()) {
            String id = (String)i$.next();

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
            } catch (InterruptedException var6) {
                logger.error(var6);
            } catch (IOException var7) {
                logger.error(var7);
            }
        }

        this.jobDB.close();
    }
}