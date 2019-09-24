package com.test;

import ch.qos.logback.classic.Logger;
import com.test.domain.EventLogRepository;
import com.test.service.EventLogService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

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
            throw new IllegalArgumentException("No path to log file.");
        }
    }

    public static void print(PrintStream out, String txt) {
        out.println(txt);
    }

    public void readFile(String pathFile){
        JSONParser jsonParser = new JSONParser();
        logger.info("File to read : " + pathFile);

        logger.debug("Start reading file");
        try(LineIterator lineIterator = FileUtils.lineIterator(new File(pathFile),"UTF-8")){
            String string = null;
            while (lineIterator.hasNext()){
                string = lineIterator.next();
                try {
                    JSONObject eventLogJson = (JSONObject) jsonParser.parse(string);
                    logger.debug("Read JSON from file : " + eventLogJson.toJSONString());
                    //EventLogService.processLogEntry(eventLogJson, eventLogRepository);
                    (new Thread(new ProcessLogEntryRunnable(eventLogJson))).start();
                } catch (ParseException e) {
                    //e.printStackTrace();
                    logger.error(e.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("Finished reading file");
    }

    private class ProcessLogEntryRunnable implements Runnable{
        JSONObject eventLogJson;
        public ProcessLogEntryRunnable(JSONObject eventLogJson){
            this.eventLogJson = eventLogJson;
        }
        @Override
        public void run() {
            try {
                EventLogService.processLogEntry(eventLogJson, eventLogRepository);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                logger.error(e.toString());
            }
        }
    }
}
