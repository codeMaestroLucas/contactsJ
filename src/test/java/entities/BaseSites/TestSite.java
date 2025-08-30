package entities.BaseSites;


import org.example.src.sites.byNewPage.Cobalt;

/**
 * New Page test for a site
 */
class MyTestPage extends Cobalt {
    @Override
    public void searchForLawyers() {
        this.totalPages = 100;
        this.maxLawyersForSite = 100;

        super.searchForLawyers();
    }
}

public class TestSite {
    public static void main(String[] args) {
        MyTestPage test = new MyTestPage();
        test.searchForLawyers();
    }
}
