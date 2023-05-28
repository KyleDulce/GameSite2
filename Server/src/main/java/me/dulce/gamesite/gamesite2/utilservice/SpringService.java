package me.dulce.gamesite.gamesite2.utilservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Service that Wraps Static Spring Methods
 */
@Service
public class SpringService {
    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    /**
     * Exists app gracefully with a given exit code
     * Runs SpringApplication.exit(context, exitCodeProvider)
     * @param exitCode
     * @return
     */
    public int springAppExit(int exitCode) {
        return SpringApplication.exit(configurableApplicationContext, () -> exitCode);
    }
}
