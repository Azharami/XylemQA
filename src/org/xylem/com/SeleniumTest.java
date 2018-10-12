package org.xylem.com;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.im4java.core.CompareCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.StandardStream;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.common.io.Files;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

public class SeleniumTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String currentDir = System.getProperty("user.dir");
		
		String parentScreenShotsLocation = currentDir + "\\ScreenShots\\";
		
		String parentDifferencesLocation = currentDir + "\\Differences\\";	
		
		
		
		String baselineScreenShotPath;
		String actualScreenShotPath;
		String differenceScreenShotPath;
		
		File baselineImageFile;
		File actualImageFile;
		File differenceImageFile;
		File differenceFileForParent;
		
		createFolder(parentScreenShotsLocation);
	    createFolder(parentDifferencesLocation);
	    
	    File differencesFolder = new File(parentDifferencesLocation);
        FileUtils.cleanDirectory(differencesFolder);
        
        String testName = "Test Run";
        System.out.println("Test Name: " + testName + "\n");

        //Create a specific directory for a test
        String testScreenShotDirectory = parentScreenShotsLocation + testName + "\\";
        createFolder(testScreenShotDirectory);

        //Declare element screenshot paths
        //Concatenate with the test name.
        
        baselineScreenShotPath = testScreenShotDirectory + testName+"_Baseline.png";
        actualScreenShotPath = testScreenShotDirectory + testName+"_Actual.png";
        differenceScreenShotPath = testScreenShotDirectory + testName + "_Diff.png";
         
        //BaseLine, Actual Photo Files
        baselineImageFile = new File(baselineScreenShotPath);
        actualImageFile = new File(actualScreenShotPath);
        differenceImageFile = new File (differenceScreenShotPath);

        //For copying difference to the parent Difference Folder
        differenceFileForParent = new File (parentDifferencesLocation + testName + "_Diff.png");

		
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\FASX935\\Documents\\Selenium\\Chromedriver\\chromedriver.exe");
		
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://uat.xylem.com/en-us");
		
		WebElement headerElement = driver.findElement(By.cssSelector(".component-header>.wrapper .start-page-logo img"));
		
		Screenshot screenshot = new AShot().takeScreenshot(driver, headerElement);
		
		ImageIO.write(screenshot.getImage(), "PNG", baselineImageFile);
		System.out.println("baseline image is saved");
		
		driver.get("https://test.xylem.com/en-us");
		WebElement headerElementT = driver.findElement(By.cssSelector(".component-header>.wrapper .start-page-logo img"));
		Screenshot screenshotT = new AShot().takeScreenshot(driver, headerElementT);
		ImageIO.write(screenshotT.getImage(), "PNG", actualImageFile);
		System.out.println("Actual image is saved");
		
		
		driver.close();
		//Compare the images
		
		String expected = baselineScreenShotPath;
		String actual = actualScreenShotPath;
		String diff = differenceScreenShotPath;
		
		CompareCmd compare = new CompareCmd();
		compare.setErrorConsumer(StandardStream.STDERR);
		
		IMOperation cmOp = new IMOperation();
		cmOp.fuzz(5.0);
		cmOp.metric("AE");
		cmOp.addImage(expected);
		cmOp.addImage(actual);
		cmOp.addImage(diff);
		try {
            //Do the compare
            System.out.println ("Comparison Started!");
            compare.run(cmOp);
            System.out.println ("Comparison Finished!");
        }
        catch (Exception ex) {
            System.out.print(ex);
            System.out.println ("Comparison Failed!");
            //Put the difference image to the global differences folder
            Files.copy(differenceImageFile,differenceFileForParent);
            throw ex;
        }
		
		
		
	}
	
	 public static void createFolder (String path) {
	        File testDirectory = new File(path);
	        if (!testDirectory.exists()) {
	            if (testDirectory.mkdir()) {
	                System.out.println("Directory: " + path + " is created!" );
	            } else {
	                System.out.println("Failed to create directory: " + path);
	            }
	        } else {
	            System.out.println("Directory already exists: " + path);
	        }
	    }

}
