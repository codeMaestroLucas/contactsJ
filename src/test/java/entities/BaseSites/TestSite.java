package entities.BaseSites;

import org.example.src.entities.BaseSites.Site;
import org.example.src.sites._standingBy.Kennedys;
import org.example.src.sites.byPage.CliffordChance;
import org.example.src.sites.byPage.MorganLewis;
import org.example.src.sites.byPage.SheppardMullin;
import org.example.src.sites.byPage.Skadden;

import java.lang.reflect.Field;

/**
 * Class used to test a new Site
 */

    class MyTestPage extends CliffordChance {
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
        MyTestPage test = new MyTestPage();
        test.searchForLawyers();
    }
}
