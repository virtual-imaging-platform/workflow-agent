package fr.insalyon.creatis.agent.workflow;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

public class Configuration {
    private static final Logger logger = Logger.getLogger(Configuration.class);
    private static Configuration instance;
    private String h2DBServer;
    private int h2DBPort;

    public static synchronized Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }

        return instance;
    }

    private Configuration() {
        try {
            PropertiesConfiguration config = new PropertiesConfiguration("workflow-agent.conf");
            this.h2DBServer = config.getString("db.h2.server", "localhost");
            this.h2DBPort = config.getInt("db.h2.port", 9092);
            config.setProperty("db.h2.server", this.h2DBServer);
            config.setProperty("db.h2.port", this.h2DBPort);
        } catch (ConfigurationException var2) {
            logger.error(var2);
        }

    }

    public int getH2DBPort() {
        return this.h2DBPort;
    }

    public String getH2DBServer() {
        return this.h2DBServer;
    }
}
