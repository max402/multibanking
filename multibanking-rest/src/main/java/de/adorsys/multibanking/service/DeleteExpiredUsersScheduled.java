package de.adorsys.multibanking.service;

import de.adorsys.multibanking.pers.spi.repository.BankAccessRepositoryIf;
import de.adorsys.multibanking.pers.spi.repository.UserRepositoryIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author alexg on 12.07.17.
 * @author fpo 2018-03-23 04:43
 */
@Configuration
@EnableScheduling
public class DeleteExpiredUsersScheduled {

    @Autowired
    UserRepositoryIf userRepository;
    @Autowired
    BankAccessRepositoryIf bankAccessRepository;
    @Autowired
    BankAccessService bankAccessService;

    private static final Logger LOG = LoggerFactory.getLogger(DeleteExpiredUsersScheduled.class);

    @Scheduled(fixedDelay = 2*60*1000)
    public void deleteJob() {
        AtomicInteger count = new AtomicInteger(0);

        userRepository.findExpiredUser().stream().forEach(userId -> {
            bankAccessRepository.findByUserId(userId).forEach(bankAccessEntity -> {
                bankAccessService.deleteBankAccess(bankAccessEntity.getId());
            });
            userRepository.delete(userId);
            count.incrementAndGet();
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
