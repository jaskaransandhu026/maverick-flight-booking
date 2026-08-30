package com.maverick.flightadminservice.commons.webservicelog.service;

import com.maverick.flightadminservice.commons.webservicelog.entities.WebServiceLog;
import com.maverick.flightadminservice.commons.webservicelog.repository.WebServiceLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebServiceLogService {

    private final WebServiceLogRepository repository;

    public WebServiceLogService(
            WebServiceLogRepository repository
    ) {
        this.repository = repository;
    }

    /**
     * REQUIRES_NEW: You still want the access log written separately.
     * So the webservice logging transaction is independent.
     *
     * @param webServiceLog
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(WebServiceLog webServiceLog) {
        repository.save(webServiceLog);
    }
}