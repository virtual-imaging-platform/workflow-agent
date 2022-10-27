package fr.insalyon.creatis.agent.workflow;

import fr.insalyon.creatis.agent.workflow.command.Command;
import fr.insalyon.creatis.agent.workflow.command.KillCommand;
import fr.insalyon.creatis.agent.workflow.database.JobDatabase;
import org.apache.log4j.Logger;

public class WorkflowAgent {
    public static final Logger logger = Logger.getLogger(WorkflowAgent.class);

    public WorkflowAgent() {
    }

    public static void main(String[] args) {
        if (args.length < 3 || args[0].equals("--help") || args[0].equals("-h")) {
            printUsage();
            System.exit(0);
        }

        Configuration.getInstance();
        String cmd = args[0];
        String workflowsPath = args[1];
        String workflowId = args[2];
        JobDatabase db = new JobDatabase(workflowsPath, workflowId);
        Command command = null;
        if (cmd.toLowerCase().equals("kill")) {
            logger.info("Received kill signal to '" + workflowId + "'");
            command = new KillCommand(db);
        }

        if (command != null) {
            command.run();
        } else {
            logger.error("Command '" + cmd + "' not recognized.");
        }

    }

    private static void printUsage() {
        System.out.println("Usage: java -jar workflow-agent-0.1.jar <command> <workflows_home> <workflow_id>");
        System.out.println("Commands: kill");
        System.out.println("");
    }
}
