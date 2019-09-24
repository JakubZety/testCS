package com.test.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Jakub on 24.09.2019.
 */
public interface EventLogRepository extends CrudRepository<EventLog, Long>{
    List<EventLog> findByIdLog(String idLog);
}
