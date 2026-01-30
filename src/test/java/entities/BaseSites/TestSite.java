package entities.BaseSites;

import org.example.src.entities.BaseSites.Site;
import org.example.src.sites.byPage.BuddleFindlay;
import org.example.src.utils.Validations;

import java.lang.reflect.Field;

/**
 * Class used to test a new Site
 * 
 * IMPORTANT: This test class uses a separate error logger (test_log.txt) to isolate
 * test execution from production logs. This prevents test runs from clearing or
 * interfering with the main log.txt file used by the production Main class.
 * 
 * TEST MODE: All validations will ALWAYS fail to prevent actual lawyer registration.
 * This allows you to visualize the site extraction without making real registrations.
 */

class MyTestPage extends BuddleFindlay {

    /**
     * Constructor that initializes with test logger to avoid affecting production logs
     */
    public MyTestPage() {
        super();
        // Use test logger instance to isolate test execution logs
        this.errorLogger = org.example.src.utils.ErrorLogger.getTestInstance();
    }
    
    /**
     * Change the values of MaxLawyersForSite and totalPages and show the logs
     */
    public void searchForLawyers() throws Exception {
        try {
            // Access fields from the correct class (Site)
            Field totalPagesField = Site.class.getDeclaredField("totalPages");
            totalPagesField.setAccessible(true);

            Field maxLawyersField = Site.class.getDeclaredField("maxLawyersForSite");
            maxLawyersField.setAccessible(true);

            // Note: These might still fail if the fields are final
            maxLawyersField.setInt(this, 100);
            if (totalPagesField.getInt(this) != 1) totalPagesField.setInt(this, 100);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        super.searchForLawyers(true);
    }
}

public class TestSite {
    public static void main(String[] args) throws Exception {
        Validations.enableTestMode();
        
        MyTestPage test = new MyTestPage();
        test.searchForLawyers();
    }
}
