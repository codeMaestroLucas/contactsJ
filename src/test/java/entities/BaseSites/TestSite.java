package entities.BaseSites;

import org.example.src.entities.BaseSites.Site;
import org.example.src.sites.to_test.*;
import org.example.src.utils.Validations;
import java.lang.reflect.Field;

class MyTestPage extends _Template {
    public static final boolean HEADLESS = true;

    public MyTestPage() { super(); }

    public void searchForLawyers() throws Exception {
        try {
            Field totalPagesField = Site.class.getDeclaredField("totalPages");
            totalPagesField.setAccessible(true);

            Field maxLawyersField = Site.class.getDeclaredField("maxLawyersForSite");
            maxLawyersField.setAccessible(true);

            maxLawyersField.setInt(this, 100);
            if (totalPagesField.getInt(this) != 1) {
                totalPagesField.setInt(this, 100);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Reflection failed: " + e.getMessage(), e);
        }

        super.searchForLawyers(true);
    }
}

public class TestSite {
    public static void main(String[] args) throws Exception {
        org.example.src.entities.MyDriver.setHeadless(MyTestPage.HEADLESS);

        Validations.enableTestMode();

        MyTestPage test = new MyTestPage();
        test.searchForLawyers();
    }
}
