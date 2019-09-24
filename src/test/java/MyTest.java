import com.test.LogReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by Jakub on 24.09.2019.
 */
public class MyTest {
    @Test
    public void name() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LogReader.print(new PrintStream(out), "Hello, World!");
        String s = out.toString();
        Assert.assertArrayEquals("Hello, World!\r\n".toCharArray(), s.toCharArray());
    }
    @Test
    public  void file() throws Exception{
        LogReader.main(new String[]{"src\\testFiles\\logfile.txt"});
        Assert.assertTrue(true);
    }
}