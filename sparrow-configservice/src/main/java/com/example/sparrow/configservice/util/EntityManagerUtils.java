package com.example.sparrow.configservice.util;

import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Component
public class EntityManagerUtils extends EntityManagerFactoryAccessor {
    public void closeEntityManager() {
        Assert.notNull(getEntityManagerFactory(), "Can not get EntityManagerFactory");
        EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager
                .getResource(getEntityManagerFactory());
        if (emHolder == null) {
            return;
        }
        logger.debug("Closing JPA EntityManager in EntityManagerUtil");
        EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
    }
}