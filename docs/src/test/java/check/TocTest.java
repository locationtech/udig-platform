package check;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Check that each rst file is mentinoed in the toc.xml file.
 * 
 * @author Jody Garnett
 * @since 1.3.2
 */
public class TocTest {

    @Test
    public void duplicatePageCheck() throws Exception {
        File docs = new File(".");
        File toc = new File( new File( docs, "user" ), "toc.xml");
        assertTrue( "toc.xml found", toc.exists() );
        
        String line;
        Matcher matcher;
        
        // Test with http://www.regexplanet.com/advanced/java/index.html
        // href="EN/(.+?).html"
        Pattern hrefPattern = Pattern.compile(".*href=\"EN/(.+?).html\".*");
        line = "        <topic label=\"uDig Overview\" href=\"EN/uDig Overview.html\">";
        matcher = hrefPattern.matcher(line);
        assertTrue( "hrefPattern match", matcher.matches() );
        assertEquals("hrefPattern Page extract", "uDig Overview", matcher.group(1) );
        
        // topic="EN/(.+?).html"
        Pattern topicPattern = Pattern.compile(".*topic=\"EN/(.+?).html\".*");
        line = "<toc label=\"Users Guide\" topic=\"EN/index.html\">";
        matcher = topicPattern.matcher(line);
        assertTrue( "topicPattern match", matcher.matches() );
        assertEquals("topicPattern Page extract", "index", matcher.group(1) );
        
        
        Map<String,String> pages = new HashMap<String,String>();
        BufferedReader reader = new BufferedReader(new FileReader(toc));
        try {
            int number=0;
            while ((line = reader.readLine()) != null) {
                number++;
                System.out.print( number );
                System.out.print(":");
                System.out.println( line );
                
                matcher = hrefPattern.matcher(line);
                if( matcher.matches() ){
                    String page = matcher.group(0);
                    if( pages.containsKey( page )){
                        conflict( number, page, line, pages.get(page));
                    }
                }
                matcher = topicPattern.matcher(line);
                if( matcher.matches() ){
                    String page = matcher.group(0);
                    if( pages.containsKey( page )){
                        conflict( number, page, line, pages.get(page));
                    }
                }
            }
        }
        finally {
            if( reader != null ) reader.close();
        }
        System.out.println("Checked "+pages.size()+" pages!" );
    }

    private void conflict(int number, String page, String reference, String origional) {
        System.out.print( "Line " );
        System.out.print( number );
        System.out.print( ".rst) duplicate page reference for ");
        System.out.print( page );
        System.out.println(".rst");
        System.out.println( "origional:"+origional );
        System.out.println( "reference:"+reference );
    }

}
