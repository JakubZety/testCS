package com.test.service;

import ch.qos.logback.classic.Logger;
import com.test.LogReader;
import com.test.domain.EventLog;
import com.test.domain.EventLogJson;
import com.test.domain.EventLogRepository;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by Jakub on 24.09.2019.
 */
public class EventLogService {

    private static Logger logger = (Logger) LoggerFactory.getLogger(EventLogService.class);

    public static void processLogEntry(EventLogJson eventLogJson, EventLogRepository eventLogRep){
        EventLog eventLog = null;
        logger.debug("eventLogJson.Id = "+ eventLogJson.getId());

        List<EventLog> lst = eventLogRep.findByIdLog(eventLogJson.getId());
        if(!lst.isEmpty()){
            eventLog = lst.get(0);
            logger.debug("found row for "+ eventLogJson.getId());
            switch (eventLogJson.getState()){
                case "STARTED":
                    eventLog.setTimestamp_started(eventLogJson.getTimestamp());
                    break;
                case "FINISHED":
                    eventLog.setTimestamp_finished(eventLogJson.getTimestamp());
                    break;
            }
            eventLog.setTime_duration(eventLog.getTimestamp_finished()-eventLog.getTimestamp_started());
            eventLog.setAlert(Long.compare(eventLog.getTime_duration(),4)==1);
            logger.debug("set alert for "+ eventLogJson.getId() + " as " + eventLog.isAlert());
        }else{
            eventLog = new EventLog();
            logger.debug("not found row for "+ eventLogJson.getId());
            eventLog.setIdLog(eventLogJson.getId());
            switch (eventLogJson.getState()){
                case "STARTED":
                    eventLog.setTimestamp_started(eventLogJson.getTimestamp());
                    break;
                case "FINISHED":
                    eventLog.setTimestamp_finished(eventLogJson.getTimestamp());
                    break;
            }
        }
        if (eventLogJson.getHost()!=null && !eventLogJson.getHost().equals(""))
            eventLog.setHost(eventLogJson.getHost());
        if (eventLogJson.getType()!=null && !eventLogJson.getType().equals(""))
            eventLog.setType(eventLogJson.getType());

        eventLogRep.save(eventLog);
    }
}
