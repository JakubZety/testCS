import com.test.LogReader;
import com.test.domain.EventLog;
import com.test.domain.EventLogJson;
import com.test.domain.EventLogRepository;
import com.test.service.EventLogService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by Jakub on 24.09.2019.
 */
public class MyTest {
    @Autowired
    EventLogRepository eventLogRepository;

    @Test
    public void helloWorld() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LogReader.print(new PrintStream(out), "Hello, World!");
        String s = out.toString();
        Assert.assertArrayEquals("Hello, World!\r\n".toCharArray(), s.toCharArray());
    }

    @Test
    public void jsonMapping() throws Exception{
        JSONParser jsonParser = new JSONParser();
        String json = "{\"id\":\"test_jsonMapping\", \"state\":\"STARTED\", \"timestamp\":100}";
        EventLogJson eventLogJson = new EventLogJson((JSONObject)jsonParser.parse(json));

        Assert.assertEquals(eventLogJson.getId(),"test_jsonMapping");
        Assert.assertEquals(eventLogJson.getState(),"STARTED");
        Assert.assertEquals(eventLogJson.getTimestamp(),Long.valueOf(100));
        Assert.assertNull(eventLogJson.getType());
        Assert.assertNull(eventLogJson.getHost());
    }

    @Test
    public void database() throws Exception{
        if(eventLogRepository!=null) {
            JSONParser jsonParser = new JSONParser();
            String json = "{\"id\":\"test_jsonMapping\", \"state\":\"STARTED\", \"timestamp\":100}";
            EventLogJson eventLogJson = new EventLogJson((JSONObject) jsonParser.parse(json));
            EventLogService.processLogEntry(eventLogJson, eventLogRepository);

            json = "{\"id\":\"test_jsonMapping\", \"state\":\"FINISHED\", \"timestamp\":101}";
            eventLogJson = new EventLogJson((JSONObject) jsonParser.parse(json));
            EventLogService.processLogEntry(eventLogJson, eventLogRepository);

            EventLog eventLog = eventLogRepository.findByIdLog("test_jsonMapping").get(0);

            Assert.assertEquals(eventLog.getId(), "test_jsonMapping");
            Assert.assertEquals(Long.valueOf(eventLog.getTimestamp_started()), Long.valueOf(100));
            Assert.assertEquals(Long.valueOf(eventLog.getTimestamp_finished()), Long.valueOf(101));
            Assert.assertEquals(Long.valueOf(eventLog.getTime_duration()), Long.valueOf(1));
            Assert.assertFalse(eventLog.isAlert());
            Assert.assertNull(eventLog.getType());
            Assert.assertNull(eventLog.getHost());

            json = "{\"id\":\"test_jsonMapping_1\", \"state\":\"STARTED\", \"timestamp\":100}";
            eventLogJson = new EventLogJson((JSONObject) jsonParser.parse(json));
            EventLogService.processLogEntry(eventLogJson, eventLogRepository);

            json = "{\"id\":\"test_jsonMapping_1\", \"state\":\"FINISHED\", \"timestamp\":106}";
            eventLogJson = new EventLogJson((JSONObject) jsonParser.parse(json));
            EventLogService.processLogEntry(eventLogJson, eventLogRepository);

            eventLog = eventLogRepository.findByIdLog("test_jsonMapping_1").get(0);

            Assert.assertEquals(eventLog.getId(), "test_jsonMapping_1");
            Assert.assertEquals(Long.valueOf(eventLog.getTimestamp_started()), Long.valueOf(100));
            Assert.assertEquals(Long.valueOf(eventLog.getTimestamp_finished()), Long.valueOf(106));
            Assert.assertEquals(Long.valueOf(eventLog.getTime_duration()), Long.valueOf(1));
            Assert.assertTrue(eventLog.isAlert());
            Assert.assertNull(eventLog.getType());
            Assert.assertNull(eventLog.getHost());

            eventLogRepository.deleteByIdLog("test_jsonMapping");
            eventLogRepository.deleteByIdLog("test_jsonMapping_1");
        }
    }

    @Test
    public  void file() throws Exception{
        boolean test = true;
        try {
            LogReader.main(new String[]{"src\\testFiles\\logfile.txt"});
        }catch (IllegalArgumentException e){
            test = false;
        }
        Assert.assertTrue(test);
    }
}