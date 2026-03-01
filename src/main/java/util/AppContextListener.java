package util;

import exception.DatabaseOperationException;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DatabaseHandler.initDb();
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to initialize DB", e);
        }
    }

}