package com.test;

import ch.qos.logback.classic.Logger;
import com.test.domain.EventLog;
import com.test.domain.EventLogRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.List;

/**
 * Created by Jakub on 24.09.2019.
 */
@SpringBootApplication
public class LogReader implements CommandLineRunner {
    @Autowired
    EventLogRepository eventLogRepository;

    Logger logger = (Logger)LoggerFactory.getLogger(LogReader.class);

    public static void main(String[] args) {
        SpringApplication.run(LogReader.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        if(strings.length > 0){
            readFile(strings[1]);
        }else{
            logger.error("Empty param string");
            //throw new IllegalArgumentException("Need path to log file.");
            readFile("src\\testFiles\\logfile.txt");
        }
    }

    public static void print(PrintStream out, String txt) {
        out.println(txt);
    }

    public void readFile(String pathFile){
        JSONParser jsonParser = new JSONParser();

        logger.info("File to read : " + pathFile);


        logger.debug("Start reading file");
        try(BufferedReader reader = new BufferedReader(new FileReader(pathFile))) {
            String stringMain = null;
            JSONObject eventLogJson = null;
            while(reader.ready()){
                stringMain = reader.readLine();
                while (!stringMain.matches("[{].*[}]")) {
                    stringMain += reader.readLine();
                }

                eventLogJson = (JSONObject) jsonParser.parse(stringMain);
                logger.debug("Read JSON from file : " + eventLogJson.toJSONString());

                processLogEntry(eventLogJson, eventLogRepository);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        logger.debug("Finished reading file");
    }

    public static void processLogEntry(JSONObject eventLogJson,EventLogRepository eventLogRep){
        EventLog eventLog = null;
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
    }
}
