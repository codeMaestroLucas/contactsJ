package entities.BaseSites;

import org.example.src.entities.BaseSites.SiteUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SiteUtilsTest {

    private final SiteUtils siteUtils = SiteUtils.getINSTANCE();

    @Mock
    private WebElement lawyerElement1, lawyerElement2, lawyerElement3, roleElement;

    @Test
    void filterLawyersInPage_shouldReturnOnlyValidLawyers() {
        // Arrange
        List<WebElement> lawyers = new ArrayList<>();
        lawyers.add(lawyerElement1);
        lawyers.add(lawyerElement2);
        lawyers.add(lawyerElement3);

        By[] roleLocator = {By.className("role")};

        // Mock the element structure
        when(lawyerElement1.findElement(any())).thenReturn(roleElement);
        when(lawyerElement2.findElement(any())).thenReturn(roleElement);
        when(lawyerElement3.findElement(any())).thenReturn(roleElement);

        // Set up different role texts
        when(roleElement.getText())
                .thenReturn("Partner")        // Valid
                .thenReturn("Senior Associate")  // Valid
                .thenReturn("Associate");     // Invalid

        // Act
        List<WebElement> result = siteUtils.filterLawyersInPage(lawyers, roleLocator, true);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(lawyerElement1));
        assertTrue(result.contains(lawyerElement2));
        assertFalse(result.contains(lawyerElement3));
    }

    @Test
    void filterLawyersInPage_shouldHandleHtmlAttribute() {
        // Arrange
        List<WebElement> lawyers = new ArrayList<>();
        lawyers.add(lawyerElement1);

        By[] roleLocator = {By.className("role")};

        when(lawyerElement1.findElement(any())).thenReturn(roleElement);
        when(roleElement.getAttribute("outerHTML"))
                .thenReturn("<div class=\"role\">\n\tCounsel\n</div>");  // Valid after cleanup

        // Act
        List<WebElement> result = siteUtils.filterLawyersInPage(lawyers, roleLocator, false);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(lawyerElement1));
    }

    @Test
    void filterLawyersInPage_shouldHandleEmptyList() {
        // Arrange
        List<WebElement> lawyers = new ArrayList<>();
        By[] roleLocator = {By.className("role")};

        // Act
        List<WebElement> result = siteUtils.filterLawyersInPage(lawyers, roleLocator, true);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void filterLawyersInPage_shouldHandleMultipleLocators() {
        // Arrange
        List<WebElement> lawyers = new ArrayList<>();
        lawyers.add(lawyerElement1);

        By[] roleLocator = {By.className("lawyer"), By.className("role")};

        WebElement intermediateElement = mock(WebElement.class);
        when(lawyerElement1.findElement(By.className("lawyer"))).thenReturn(intermediateElement);
        when(intermediateElement.findElement(By.className("role"))).thenReturn(roleElement);
        when(roleElement.getText()).thenReturn("Director");  // Valid

        // Act
        List<WebElement> result = siteUtils.filterLawyersInPage(lawyers, roleLocator, true);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getContentFromTag() {
        // Implementation for this test would go here
    }
}