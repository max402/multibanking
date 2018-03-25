package de.adorsys.multibanking.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import de.adorsys.multibanking.service.base.StorageUserService;

/**
 * @author alexg on 12.07.17.
 * @author fpo 2018-03-23 04:43
 */
@Configuration
@EnableScheduling
public class DeleteExpiredUsersScheduled {

    @Autowired
    StorageUserService storageUserService;
    @Autowired
    DeleteExpiredUsersService deleteExpiredUsersService;

    private static final Logger LOG = LoggerFactory.getLogger(DeleteExpiredUsersScheduled.class);

    @Scheduled(fixedDelay = 2*60*1000)
    public void deleteJob() {
        AtomicInteger count = new AtomicInteger(0);

        deleteExpiredUsersService.findExpiredUser().stream().forEach(userId -> {
        	// TODO: We do not have the user password here.
        	// We might allow removal of user directory in absence of user password.
        	UserIDAuth userIdAuth = new UserIDAuth(new UserID(userId.getId()), null);
			storageUserService.deleteUser(userIdAuth);
        });
        LOG.info("delete job done, [{}] users deleted", count);
    }

    @Bean
    public TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("poolScheduler");
        return scheduler;
    }
}
