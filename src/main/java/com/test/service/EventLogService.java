package com.test.service;

import com.test.domain.EventLog;
import com.test.domain.EventLogRepository;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by Jakub on 24.09.2019.
 */
public class EventLogService {

    public static final HashMap<String,Semaphore> mapOfsemaphores = new HashMap<>();
    public static final Semaphore semaphoreMap = new Semaphore(1);

    public static void processLogEntry(JSONObject eventLogJson, EventLogRepository eventLogRep) throws InterruptedException {
        EventLog eventLog = null;

        semaphoreMap.acquire();
        if (mapOfsemaphores.containsKey(eventLogJson.get("id"))){
                mapOfsemaphores.get(eventLogJson.get("id")).acquire();
        }else{
            mapOfsemaphores.put((String)eventLogJson.get("id"), new Semaphore(1));
        }
        semaphoreMap.release();

        boolean semToRm = false;
        List<EventLog> lst = eventLogRep.findByIdLog((String)eventLogJson.get("id"));
        if(!lst.isEmpty()){
            eventLog = lst.get(0);
            switch ( (String)eventLogJson.get("state") ){
                case "STARTED":
                    eventLog.setTimestamp_started((Long)eventLogJson.get("timestamp"));
                    break;
                case "FINISHED":
                    eventLog.setTimestamp_finished((Long)eventLogJson.get("timestamp"));
                    break;
            }
            eventLog.setTime_duration(eventLog.getTimestamp_finished()-eventLog.getTimestamp_started());
            eventLog.setAlert(Long.compare(eventLog.getTime_duration(),4)==1);
            semToRm = true;
        }else{
            eventLog = new EventLog();
            eventLog.setIdLog((String)eventLogJson.get("id"));
            switch ((String)eventLogJson.get("state")){
                case "STARTED":
                    eventLog.setTimestamp_started((Long)eventLogJson.get("timestamp"));
                    break;
                case "FINISHED":
                    eventLog.setTimestamp_finished((Long)eventLogJson.get("timestamp"));
                    break;
            }
        }
        if (eventLogJson.get("host")!=null && !eventLogJson.get("host").equals(""))
            eventLog.setHost((String)eventLogJson.get("host"));
        if (eventLogJson.get("type")!=null && !eventLogJson.get("type").equals(""))
            eventLog.setType((String)eventLogJson.get("type"));
        eventLogRep.save(eventLog);

        semaphoreMap.acquire();
        if (mapOfsemaphores.containsKey(eventLogJson.get("id"))){
            mapOfsemaphores.get(eventLogJson.get("id")).release();
            if (semToRm){
                mapOfsemaphores.remove(eventLogJson.get("id"));
            }
        }else{
            mapOfsemaphores.put((String)eventLogJson.get("id"), new Semaphore(1));
        }
        semaphoreMap.release();
    }
}
