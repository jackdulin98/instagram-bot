package tests;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.touch.offset.PointOption;

public class BaseClass {
	
	AppiumDriver<MobileElement> driver;
	
	@BeforeTest
	public void setup() throws InterruptedException, AWTException{
//		System.setProperty("webdriver.chrome.driver", "C:\\Users\\jackd\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
//		ChromeDriver chromeDriver = new ChromeDriver();
////		// TODO: Huge problem - this link is for non-commercial use only! Long term, use your own AI solution. Don't rely on some website.
//		// TODO: This link also lasts for only 72 hours, scrape the new URL's from the output of Command Prompt. 
//		chromeDriver.get("https://1397113ebcec5ed0e9.gradio.live/");
//		Thread.sleep(20000);
//			
//		WebElement element = chromeDriver.findElement(By.tagName("html"));
//
//		// Below will return a list of all elements inside element
//		List<WebElement> webEleList = element.findElements(By.xpath(".//*"));
//
//		//For above given HTML it will print 5. As there are 5 elements inside div
//		element.findElement(By.xpath("//div[@data-testid='image']")).click();
//		
//		Thread.sleep(5000);
//	
//		// TODO: Replace the temp string with a dynamic path. 
//		// TODO: Don't hard-code the value of Thread.sleep().
//		Thread.sleep(10000);
//		Robot robot = new Robot();
//		String tempString = "\\screenshot1.png";
//		String userDirectory = System.getProperty("user.dir") + tempString;
//		System.out.println("User directory: " + userDirectory);
//		// TODO: Handle every single possible case on the keyboard if you want this to ship.
//		for(int i = 0; i < userDirectory.length(); i++) {
//			char letter = userDirectory.charAt(i);
//			switch(letter) {
//				case 'A': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_A);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'B': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_B);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'C': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_C);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'D': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_D);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'E': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_E);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'F': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_F);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'G': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_G);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'H': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_H);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'I': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_I);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'J': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_J);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'K': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_K);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'L': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_L);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'M': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_M);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'N': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_N);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'O': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_O);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'P': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_P);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'Q': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_Q);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'R': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_R);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'S': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_S);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'T': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_T);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'U': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_U);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'V': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_V);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'W': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_W);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'X': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_X);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'Y': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_Y);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'Z': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//					robot.keyPress(KeyEvent.VK_Z);
//					robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case 'a': 
//					robot.keyPress(KeyEvent.VK_A);
//					break;
//				case 'b': 
//					robot.keyPress(KeyEvent.VK_B);
//					break;
//				case 'c': 
//					robot.keyPress(KeyEvent.VK_C);
//					break;
//				case 'd': 
//					robot.keyPress(KeyEvent.VK_D);
//					break;
//				case 'e': 
//					robot.keyPress(KeyEvent.VK_E);
//					break;
//				case 'f': 
//					robot.keyPress(KeyEvent.VK_F);
//					break;
//				case 'g': 
//					robot.keyPress(KeyEvent.VK_G);
//					break;
//				case 'h': 
//					robot.keyPress(KeyEvent.VK_H);
//					break;
//				case 'i': 
//					robot.keyPress(KeyEvent.VK_I);
//					break;
//				case 'j': 
//					robot.keyPress(KeyEvent.VK_J);
//					break;
//				case 'k': 
//					robot.keyPress(KeyEvent.VK_K);
//					break;
//				case 'l': 
//					robot.keyPress(KeyEvent.VK_L);
//					break;
//				case 'm': 
//					robot.keyPress(KeyEvent.VK_M);
//					break;
//				case 'n': 
//					robot.keyPress(KeyEvent.VK_N);
//					break;
//				case 'o': 
//					robot.keyPress(KeyEvent.VK_O);
//					break;
//				case 'p': 
//					robot.keyPress(KeyEvent.VK_P);
//					break;
//				case 'q': 
//					robot.keyPress(KeyEvent.VK_Q);
//					break;
//				case 'r': 
//					robot.keyPress(KeyEvent.VK_R);
//					break;
//				case 's': 
//					robot.keyPress(KeyEvent.VK_S);
//					break;
//				case 't': 
//					robot.keyPress(KeyEvent.VK_T);
//					break;
//				case 'u': 
//					robot.keyPress(KeyEvent.VK_U);
//					break;
//				case 'v': 
//					robot.keyPress(KeyEvent.VK_V);
//					break;
//				case 'w': 
//					robot.keyPress(KeyEvent.VK_W);
//					break;
//				case 'x': 
//					robot.keyPress(KeyEvent.VK_X);
//					break;
//				case 'y': 
//					robot.keyPress(KeyEvent.VK_Y);
//					break;
//				case 'z': 
//					robot.keyPress(KeyEvent.VK_Z);
//					break;
//				case '0': 
//					robot.keyPress(KeyEvent.VK_0);
//					break;
//				case '1': 
//					robot.keyPress(KeyEvent.VK_1);
//					break;
//				case '2': 
//					robot.keyPress(KeyEvent.VK_2);
//					break;
//				case '3': 
//					robot.keyPress(KeyEvent.VK_3);
//					break;
//				case '4': 
//					robot.keyPress(KeyEvent.VK_4);
//					break;
//				case '5': 
//					robot.keyPress(KeyEvent.VK_5);
//					break;
//				case '6': 
//					robot.keyPress(KeyEvent.VK_6);
//					break;
//				case '7': 
//					robot.keyPress(KeyEvent.VK_7);
//					break;
//				case '8': 
//					robot.keyPress(KeyEvent.VK_8);
//					break;
//				case '9': 
//					robot.keyPress(KeyEvent.VK_9);
//					break;
//				case ':': 
//					robot.keyPress(KeyEvent.VK_SHIFT);
//			        robot.keyPress(KeyEvent.VK_SEMICOLON);
//			        robot.keyRelease(KeyEvent.VK_SEMICOLON);
//			        robot.keyRelease(KeyEvent.VK_SHIFT);
//					break;
//				case '\\': 
//					robot.keyPress(KeyEvent.VK_BACK_SLASH);
//					break;
//				case '.':
//					robot.keyPress(KeyEvent.VK_PERIOD);
//					break;
//				case '-':
//					robot.keyPress(KeyEvent.VK_MINUS);
//					break;
//				default: 
//					break;
//			}
//		}
//		robot.keyPress(KeyEvent.VK_ENTER);
//		
//		Thread.sleep(5000);
//		
//		element.findElement(By.xpath("//button[text()='Upload & Start Chat']")).click();
//		
//		Thread.sleep(100000);
//		
//		webEleList = element.findElements(By.xpath(".//*"));
//		
//		element.findElement(By.xpath("//textarea[@data-testid='textbox']")).sendKeys("What does this picture contain?" + Keys.ENTER);
//		
//		Thread.sleep(500000);
//		
//		String response1 = element.findElement(By.xpath("//button[@data-testid='bot']")).getText();
//		// TODO: Utilize the fluent wait. 
//		System.out.println("First response: " + response1);
		
		// StringConcatFactory shows up when you concatenate strings (JRE 11)
		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
		cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
		cap.setCapability(MobileCapabilityType.PLATFORM_VERSION, "10");
		cap.setCapability(MobileCapabilityType.DEVICE_NAME, "Galaxy S10");
		cap.setCapability(MobileCapabilityType.UDID, "R38M30ED93H");
		cap.setCapability("appPackage", "com.sec.android.app.launcher");
		cap.setCapability("appActivity", "com.sec.android.app.launcher.activities.LauncherActivity");
		
		URL url = null;
		try {
			url = new URL("http://127.0.0.1:4723/");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		driver = new AppiumDriver<MobileElement>(url, cap);	
		
		// TODO: Run the "wall of shame" once per week, story watching twice per day.
		
		// Check to see if the phone is unlocked.
		List<MobileElement> unlockField = driver.findElements(By.xpath("//android.widget.TextView[@content-desc=\"Swipe to unlock\"]"));
		if(unlockField.size() > 0) {
			// Swipe up to start unlocking the phone.
			Dimension dim = driver.manage().window().getSize();
			int height = dim.getHeight();
			int width = dim.getWidth();		
			int x = (2 * width) / 3;
			int top_y = (int)(height * 0.10);
			int bottom_y = (int)(height * 0.30);
			
			TouchAction swipeUp = new TouchAction(driver);
			PointOption top_pt = new PointOption();
			PointOption bottom_pt = new PointOption();
			top_pt.withCoordinates(x, top_y);
			bottom_pt.withCoordinates(x, bottom_y);
			swipeUp.press(bottom_pt).moveTo(top_pt).release().perform();
			
			// Activate the unlock pattern for the phone.
			Point[] cordinates = new Point[3];
		    cordinates[0] = new Point(326,1428);
		    cordinates[1] = new Point(326,1879);
		    cordinates[2] = new Point(768,1879);
		    
		    // TODO: Must switch from Maven to Gradle to use the related dependency. 
		    swipe(cordinates, 10);
		    
			
		}
		
		driver.activateApp("com.instagram.android");
		generateWallOfShame(driver);
		
		// TODO: Get the automatic mouse scroll to keep the computer on for scrollStories(). 
		// TODO: It eventually led to someone's live story. How to avoid it?
		// scrollStories(driver);	
		
//		try {
//			generateMeaningsOfPictures(driver, "fitness");
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
	}
	
	// TODO: First goal is to capture the meanings of each and every image found. 
	public void generateMeaningsOfPictures(AppiumDriver<MobileElement> driver, String hashtag) throws InterruptedException, IOException, AWTException {
//		driver.findElement(By.xpath("//android.widget.TextView[contains(@content-desc,\"Instagram\")]")).click();
//		Thread.sleep(3000);
		driver.findElement(By.xpath("//android.widget.FrameLayout[@content-desc=\"Search and explore\"]/android.widget.ImageView")).click();
		Thread.sleep(3000);
		MobileElement searchBar = driver.findElement(By.id("com.instagram.android:id/action_bar_search_edit_text"));
		searchBar.click();
		Thread.sleep(2000);
		searchBar = driver.findElement(By.id("com.instagram.android:id/action_bar_search_edit_text"));
		searchBar.sendKeys(hashtag + "\n");
		Thread.sleep(3000);
		driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "done"));
		driver.findElement(By.xpath("//androidx.recyclerview.widget.RecyclerView/android.widget.Button")).click();
		Thread.sleep(5000);
		TakesScreenshot screenshot = (TakesScreenshot)driver;
		File sourcePath = screenshot.getScreenshotAs(OutputType.FILE);
		int imageCounter = 1;
		File destinationPath = new File(System.getProperty("user.dir") + "/screenshot" + String.valueOf(imageCounter) + ".png");
		FileUtils.copyFile(sourcePath, destinationPath);
		while(imageCounter <= 20) {
			// First action is to swipe up.
			Dimension dim = driver.manage().window().getSize();
			int height = dim.getHeight();
			int width = dim.getWidth();		
			int x = (2 * width) / 3;
			int top_y = (int)(height * 0.20);
			int bottom_y = (int)(height * 0.80);
			
			TouchAction swipeUp = new TouchAction(driver);
			PointOption top_pt = new PointOption();
			PointOption bottom_pt = new PointOption();
			top_pt.withCoordinates(x, top_y);
			bottom_pt.withCoordinates(x, bottom_y);
			swipeUp.longPress(bottom_pt).moveTo(top_pt).release().perform();
			
			Thread.sleep(3000);
			
			// Second action is to take the screenshot.
			screenshot = (TakesScreenshot)driver;
			sourcePath = screenshot.getScreenshotAs(OutputType.FILE);
			destinationPath = new File(System.getProperty("user.dir") + "/screenshots/screenshot" + String.valueOf(imageCounter) + ".png");
			FileUtils.copyFile(sourcePath, destinationPath);
			
			Thread.sleep(3000);
			
			// TODO: Upload the screenshot to captioning service, open up Chrome Driver for this one.
			
			imageCounter++;
		}
	}
	
	// TODO: Check for the visibility of the messages icon to see if the stories have stopped.
	public void scrollStories(AppiumDriver<MobileElement> driver) throws InterruptedException {
		driver.findElement(By.xpath("//android.widget.TextView[contains(@content-desc,\"Instagram\")]")).click();
		Thread.sleep(3000);
		driver.findElement(By.xpath("//android.widget.FrameLayout[@content-desc=\"Home\"]/android.view.ViewGroup/android.widget.FrameLayout/android.widget.ImageView")).click();
		Thread.sleep(2000);
		List<MobileElement> stories = driver.findElements(By.id("com.instagram.android:id/avatar_image_view"));
		stories.get(1).click();
		Thread.sleep(5000);
		TouchAction touch = new TouchAction(driver);
		Dimension dim = driver.manage().window().getSize();
		int height = dim.getHeight();
		int width = dim.getWidth();		
		int x = 3 * width / 4;
		int y = 5 * height / 6;
		PointOption pt = new PointOption();
		pt.withCoordinates(x, y);
		boolean messageIconSeen = false;
		while(messageIconSeen == false) {
			touch.press(pt).release().perform();
			int randomTime = (int)(2 * Math.random() + 3) * 1000 + (int)(Math.random() * 1000);
			System.out.println("random time: " + randomTime);
			Thread.sleep(randomTime);
			List<MobileElement> messageIcon = driver.findElements(By.xpath("//android.widget.Button[@content-desc=\"No unread messages\"]"));
			if(messageIcon.size() != 0) {
				messageIconSeen = true;
			}
		}
	}
	
	public void generateWallOfShame(AppiumDriver<MobileElement> driver) throws InterruptedException {
//		driver.findElement(By.xpath("//android.widget.TextView[contains(@content-desc,\"Instagram\")]")).click();
//		Thread.sleep(3000);
		driver.findElement(By.xpath("//android.widget.FrameLayout[@content-desc=\"Search and explore\"]/android.widget.ImageView")).click();
		Thread.sleep(3000);
		MobileElement searchBar = driver.findElement(By.id("com.instagram.android:id/action_bar_search_edit_text"));
		searchBar.click();
		Thread.sleep(2000);
		searchBar = driver.findElement(By.id("com.instagram.android:id/action_bar_search_edit_text"));
		searchBar.sendKeys("lmtlss.fit" + "\n");
		Thread.sleep(3000);
		// TODO: make this into a more dynamic xpath.
		driver.findElement(By.xpath("/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout[2]/android.widget.FrameLayout[1]/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/android.widget.FrameLayout[2]/android.widget.LinearLayout/android.widget.LinearLayout")).click();
		Thread.sleep(5000);
		MobileElement followersButton = driver.findElement(By.id("com.instagram.android:id/row_profile_header_textview_followers_count"));
		int numFollowers = Integer.parseInt(followersButton.getText().replace(",", ""));
		followersButton.click();
		Thread.sleep(5000);
		List<String> followerNames = new ArrayList<>();
		List<MobileElement> followers = driver.findElements(By.xpath("//android.widget.TextView[@resource-id='com.instagram.android:id/follow_list_username']"));
		for(MobileElement follower : followers) {
			followerNames.add(follower.getText());
			System.out.println("Follower added: " + follower.getText());		
		}
		int counter = 0; 
		int lastListSize = 0;
		int currListSize = 0;
		int numSameSizeIter = 0;
		while(followerNames.size() < numFollowers) {
			if(counter == 10) {
				mouseGlide(100, 100, 200, 200, 3000, 100);
				counter = 0;
			}
			System.out.println("Current list size: " + followerNames.size());
			currListSize = followerNames.size();
			if(currListSize == lastListSize) {
				numSameSizeIter++;
			}
			else {
				numSameSizeIter = 0;
			}
			if(numSameSizeIter == 5) {
				break;
			}
			lastListSize = followerNames.size();
			captureNames(driver, followerNames);
			counter++;
		}
		counter = 0;
		System.out.println("Size of the followers list: " + followerNames.size());
		for(String name : followerNames) {
			System.out.println("Follower: " + name);
		}
		Thread.sleep(10000);
		driver.findElement(By.xpath("//android.widget.ImageView[@content-desc=\"Back\"]")).click();
		Thread.sleep(8000);
		MobileElement followingButton = driver.findElement(By.id("com.instagram.android:id/row_profile_header_textview_following_count"));
		int numFollowing = Integer.parseInt(followingButton.getText().replace(",", ""));
		followingButton.click();
		Thread.sleep(5000);
		List<String> followingNames = new ArrayList<>();
		List<MobileElement> following = driver.findElements(By.xpath("//android.widget.TextView[@resource-id='com.instagram.android:id/follow_list_username']"));
		for(MobileElement follow : following) {
			followingNames.add(follow.getText());
			System.out.println("Following added: " + follow.getText());
		}
		lastListSize = 0;
		currListSize = 0;
		numSameSizeIter = 0;
		while(followingNames.size() < numFollowing) {
			if(counter == 10) {
				mouseGlide(100, 100, 200, 200, 3000, 100);
				counter = 0;
			}
			System.out.println("Current list size: " + followingNames.size());
			currListSize = followingNames.size();
			if(currListSize == lastListSize) {
				numSameSizeIter++;
			}
			else {
				numSameSizeIter = 0;
			}
			// BUG FIX: Forgot to add this condition, scrolling didn't stop for a day. 
			if(numSameSizeIter == 5) {
				break;
			}
			lastListSize = followingNames.size();
			captureNames(driver, followingNames);
			counter++;
		}
		System.out.println("Size of the following list: " + followingNames.size());
		for(String name : followingNames) {
			System.out.println("Following: " + name);
		}
		Thread.sleep(15000);
		System.out.println("Now here comes the wall of shame ...");
		for(String oneOfMyFollowing : followingNames) {
			if(!followerNames.contains(oneOfMyFollowing)) {
				System.out.println("instagram.com/" + oneOfMyFollowing);
			}
		}
	}
	
	// captures a list of names while scrolling down
	// TODO: check for the "See more" list
	public void captureNames(AppiumDriver<MobileElement> driver, List<String> userNames) throws InterruptedException {
		scrollDown(driver);
		WebDriverWait wait = new WebDriverWait(driver, 5);
		List<MobileElement> seeMoreBtn = driver.findElements(By.id("com.instagram.android:id/see_more_button"));
		int randWait = Math.abs((int)Math.random() + 2) * 2000;
		Thread.sleep(randWait);
		if(seeMoreBtn.size() > 0) {
			seeMoreBtn.get(0).click();
		}
		List<MobileElement> followers = driver.findElements(By.xpath("//android.widget.TextView[@resource-id='com.instagram.android:id/follow_list_username']"));
		for(MobileElement follower : followers) {
			if(!userNames.contains(follower.getText())) {
				userNames.add(follower.getText());
				System.out.println(follower.getText());
			}
		}
	}
	
	public void mouseGlide(int x1, int y1, int x2, int y2, int t, int n) {
	    try {
	        Robot r = new Robot();
	        double dx = (x2 - x1) / ((double) n);
	        double dy = (y2 - y1) / ((double) n);
	        double dt = t / ((double) n);
	        for (int step = 1; step <= n; step++) {
	            Thread.sleep((int) dt);
	            r.mouseMove((int) (x1 + dx * step), (int) (y1 + dy * step));
	        }
	    } catch (AWTException e) {
	        e.printStackTrace();
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	}
	
	public void scrollDown(AppiumDriver<MobileElement> driver) {
		Dimension dim = driver.manage().window().getSize();
		int height = dim.getHeight();
		int width = dim.getWidth();	
		int x = width / 2;
		int top_y = (int)(height * 0.90);
		int bottom_y = (int)(height * 0.40);
		TouchAction swipeUp = new TouchAction(driver);
		PointOption top_pt = new PointOption();
		PointOption bottom_pt = new PointOption();
		top_pt.withCoordinates(x, top_y);
		bottom_pt.withCoordinates(x, bottom_y);
		swipeUp.press(top_pt).moveTo(bottom_pt).release().perform();
	}
	
	public void scrollUp(AppiumDriver<MobileElement> driver) {
		Dimension dim = driver.manage().window().getSize();
		int height = dim.getHeight();
		int width = dim.getWidth();	
		int x = width / 2;
		int top_y = (int)(height * 0.80);
		int bottom_y = (int)(height * 0.40);
		TouchAction swipeUp = new TouchAction(driver);
		PointOption top_pt = new PointOption();
		PointOption bottom_pt = new PointOption();
		top_pt.withCoordinates(x, top_y);
		bottom_pt.withCoordinates(x, bottom_y);
		swipeUp.press(bottom_pt).moveTo(top_pt).release().perform();
	}

	@Test
	public void test() {
	}
	
	@AfterTest
	public void teardown() {
		
	}

}
