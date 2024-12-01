package lmtlss.fit;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import javax.swing.*;
import java.awt.*;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.touch.offset.PointOption;
import io.github.cdimascio.dotenv.Dotenv;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;

public class InstagramBot {
	
	public static Dotenv dotenv = Dotenv.load();
	public static final String OPENAI_API_URL = dotenv.get("OPENAI_API_URL");
	public static final String OPENAI_API_KEY = dotenv.get("OPENAI_API_KEY");
	
    // Function to encode the image to Base64
    public static String encodeImage(String imagePath) throws IOException {
        File file = new File(imagePath);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }
	
    // Function to create the payload as a JSON string
    public static String createPayload(String base64Image, String purpose) {
        JSONObject imageUrlObject = new JSONObject();
        imageUrlObject.put("url", "data:image/jpeg;base64," + base64Image);

        JSONObject contentObject = new JSONObject();
        contentObject.put("type", "text");
        if(purpose.equals("fitness test")) {
        	contentObject.put("text", "Is this image about fitness? Please answer only yes or no.");
        } 
        else if(purpose.equals("comment generation")) {
        	contentObject.put("text", "I am a fitness influencer. Generate only one short motivational comment I should leave on this post. Please keep it to two or three words, and refer to the picture if at all possible.");
        }

        JSONObject imageUrlContentObject = new JSONObject();
        imageUrlContentObject.put("type", "image_url");
        imageUrlContentObject.put("image_url", imageUrlObject);

        JSONArray contentArray = new JSONArray();
        contentArray.put(contentObject);
        contentArray.put(imageUrlContentObject);

        JSONObject messageObject = new JSONObject();
        messageObject.put("role", "user");
        messageObject.put("content", contentArray);

        JSONArray messagesArray = new JSONArray();
        messagesArray.put(messageObject);

        JSONObject payloadObject = new JSONObject();
        payloadObject.put("model", "gpt-4o-mini");
        payloadObject.put("messages", messagesArray);
        payloadObject.put("max_tokens", 300);

        return payloadObject.toString();
    }

    // Function to send the POST request to OpenAI API
    public static String sendPostRequest(String payload, String apiKey) throws IOException {
        URL url = new URL("https://api.openai.com/v1/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (InputStream is = conn.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
    
    public static int generateRandomPort() {
    	Random random = new Random();
    	int minPortNo = 10001;
    	int maxPortNo = 10100; 
    	int randomNum = random.nextInt(maxPortNo - minPortNo + 1) + minPortNo;
    	return randomNum;
    }
    
    // Set up a driver with any random proxy from port 10001
    public static AndroidDriver setupDriverWithProxy() {
    	
    	// Smartproxy settings
        String proxyHost = "dc.smartproxy.com"; // Replace with your Smartproxy IP
        String proxyUsername = "spfre4v8nc"; // Replace with your Smartproxy username
        String proxyPassword = "Zt2q=m9j49iwiAnyHC"; // Replace with your Smartproxy password
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((proxyUsername + ":" + proxyPassword).getBytes());
        int proxyPortNum = generateRandomPort();
    	
    	BrowserMobProxyServer proxy = new BrowserMobProxyServer();
    	InetSocketAddress chainedProxyAddress = new InetSocketAddress(proxyHost, proxyPortNum);
    	proxy.setChainedProxy(chainedProxyAddress);
    	
    	proxy.addRequestFilter((request, contents, messageInfo) -> {
            request.headers().add("Proxy-Authorization", authHeader);
            return null; // No modification to the request body
        });
    	
    	// Start the proxy
        proxy.start(0); // Automatically assign a port
        int proxyPort = proxy.getPort();
        System.out.println("BrowserMob Proxy is running on port: " + proxyPort);

        // Configure Selenium Proxy
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
    	
		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
		cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
		cap.setCapability(MobileCapabilityType.PLATFORM_VERSION, "10");
		cap.setCapability(MobileCapabilityType.DEVICE_NAME, "Galaxy S10");
		cap.setCapability(MobileCapabilityType.UDID, "R38M30ED93H");
		cap.setCapability("appPackage", "com.sec.android.app.launcher");
		cap.setCapability("appActivity", "com.sec.android.app.launcher.activities.LauncherActivity");
		//cap.setCapability(MobileCapabilityType.PROXY, seleniumProxy);
		
		// Configure the proxy on your emulator or device
        String adbCommand = String.format("adb shell settings put global http_proxy %s:%d", proxyHost, proxyPort);
        
        try {
			Runtime.getRuntime().exec(adbCommand);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println("Proxy set to " + proxyHost + ":" + proxyPort);
		
		URL url = null;
		try {
			url = new URL("http://127.0.0.1:4723/");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		AndroidDriver<MobileElement> driver = new AndroidDriver<MobileElement>(cap);
        
        // Initialize Appium driver with the capabilities
        return driver;
    }
	
	public static void main(String[] args) {
		
		//showGUI();
		
	    AppiumDriver<MobileElement> driver = (AppiumDriver<MobileElement>)setupDriverWithProxy();

		driver.activateApp("com.instagram.android");
		
		// TODO: Be able to run separate scripts for different portions of the pipeline, review batch Maven script line.
		// showGUI();
		
		/*
		try {
			generateWallOfShame(driver);
		} catch(Exception e) {
			e.printStackTrace();
		}
		*/
		
		int numberOfUsersToCheck = 100;				// will be bold and do 100 users for this batch (!!!)
		int runNumber = 4; 							// TODO: make this number automatic, don't need to think about it
		try {
			unfollowUsers(driver, numberOfUsersToCheck, runNumber);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		// TODO: Get the automatic mouse scroll to keep the computer on for scrollStories(). 
		// TODO: It eventually led to someone's live story. How to avoid it?
		// scrollStories(driver);	
		
		/*
		try {
			generateComments(driver, "fitness");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		*/
	}

    private static JButton createFuturisticButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Verdana", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private static void openStoriesWindow() {
        JFrame storiesFrame = new JFrame("Watch Stories");
        storiesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        storiesFrame.setSize(600, 500);
        storiesFrame.setLocationRelativeTo(null);

        JPanel storiesPanel = new JPanel();
        storiesPanel.setLayout(new BoxLayout(storiesPanel, BoxLayout.Y_AXIS));
        storiesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headingLabel = new JLabel("How many stories do you want to scroll through in this session?");
        JTextField storiesCountField = new JTextField();
        storiesCountField.setPreferredSize(new java.awt.Dimension(50, 30)); // Smaller size for this text box
        storiesCountField.setMaximumSize(new java.awt.Dimension(100, 30));

        JLabel likeStoriesLabel = new JLabel("Would you want to like any stories while scrolling through them?");
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Centered radio buttons
        JRadioButton yesRadioButton = new JRadioButton("Yes");
        JRadioButton noRadioButton = new JRadioButton("No");
        ButtonGroup likeStoriesGroup = new ButtonGroup();
        likeStoriesGroup.add(yesRadioButton);
        likeStoriesGroup.add(noRadioButton);
        radioPanel.add(yesRadioButton);
        radioPanel.add(noRadioButton);

        JLabel typesOfStoriesLabel = new JLabel("What types of stories would you want to like? Please enter one word for each field.");
        
        JPanel textFieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField[] storyTypeFields = new JTextField[10];
        for (int i = 0; i < storyTypeFields.length; i++) {
            storyTypeFields[i] = new JTextField();
            storyTypeFields[i].setPreferredSize(new java.awt.Dimension(250, 30)); // Larger size for these text boxes
            storyTypeFields[i].setEnabled(false);
            textFieldsPanel.add(storyTypeFields[i]);
        }

        yesRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JTextField field : storyTypeFields) {
                    field.setEnabled(true);
                }
            }
        });

        noRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JTextField field : storyTypeFields) {
                    field.setEnabled(false);
                }
            }
        });

        // Center the text field and the radio buttons
        JPanel centeredTextFieldPanel = new JPanel();
        centeredTextFieldPanel.setLayout(new BoxLayout(centeredTextFieldPanel, BoxLayout.X_AXIS));
        centeredTextFieldPanel.add(Box.createHorizontalGlue());
        centeredTextFieldPanel.add(storiesCountField);
        centeredTextFieldPanel.add(Box.createHorizontalGlue());

        JPanel centeredRadioPanel = new JPanel();
        centeredRadioPanel.setLayout(new BoxLayout(centeredRadioPanel, BoxLayout.X_AXIS));
        centeredRadioPanel.add(Box.createHorizontalGlue());
        centeredRadioPanel.add(radioPanel);
        centeredRadioPanel.add(Box.createHorizontalGlue());

        storiesPanel.add(headingLabel);
        storiesPanel.add(centeredTextFieldPanel);
        storiesPanel.add(likeStoriesLabel);
        storiesPanel.add(centeredRadioPanel);
        storiesPanel.add(typesOfStoriesLabel);
        storiesPanel.add(textFieldsPanel);

        storiesFrame.getContentPane().add(storiesPanel);
        storiesFrame.setVisible(true);
    }
	
	public static void showGUI() {
		// Set a modern look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the frame
        JFrame frame = new JFrame("Instagram Bot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // Center the frame

        // Create the panel to hold the buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10)); // Add some spacing between buttons
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around the panel

        // Create the buttons with custom styles
        JButton button1 = new JButton("Generate the wall of shame");
        JButton button2 = new JButton("Scroll through my stories");
        JButton button3 = new JButton("Write comments to my fans");

        // Set button fonts and colors
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        button1.setFont(buttonFont);
        button2.setFont(buttonFont);
        button3.setFont(buttonFont);

        button1.setBackground(new Color(70, 130, 180));
        button1.setForeground(Color.WHITE);
        button2.setBackground(new Color(70, 130, 180));
        button2.setForeground(Color.WHITE);
        button3.setBackground(new Color(70, 130, 180));
        button3.setForeground(Color.WHITE);

        // Add buttons to the panel
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);

        // Add action listeners to buttons (if needed)
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add your action for button1 here
            	openVerificationWindow();
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add your action for button2 here
            	openStoriesWindow();
            }
        });

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add your action for button3 here
            	generateCommentsWindow();
                System.out.println("Generate comments button pressed");
            }
        });

        // Add the panel to the frame
        frame.getContentPane().add(panel);

        // Set the frame visibility to true
        frame.setVisible(true);
	}
	
    private static void generateCommentsWindow() {
        JFrame storiesFrame = new JFrame("Generate Comments");
        storiesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        storiesFrame.setSize(600, 500);
        storiesFrame.setLocationRelativeTo(null);

        JLabel typesOfCommentsLabel = new JLabel("What types of comments would you want to give to each user?");
        JLabel enterCommentsLabel = new JLabel("Please enter them in the text boxes below.");
        
        JPanel storiesPanel = new JPanel();
        storiesPanel.setLayout(new BoxLayout(storiesPanel, BoxLayout.Y_AXIS));
        storiesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel textFieldsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField[] storyTypeFields = new JTextField[10];
        for (int i = 0; i < storyTypeFields.length; i++) {
            storyTypeFields[i] = new JTextField();
            storyTypeFields[i].setPreferredSize(new java.awt.Dimension(200, 30)); // Larger size for these text boxes
            textFieldsPanel.add(storyTypeFields[i]);
        }

        storiesPanel.add(typesOfCommentsLabel);
        storiesPanel.add(enterCommentsLabel);
        storiesPanel.add(textFieldsPanel);

        storiesFrame.getContentPane().add(storiesPanel);
        storiesFrame.setVisible(true);
    }
	
	private static void openVerificationWindow() {
	    // Create the verification frame
	    JFrame verificationFrame = new JFrame("Verification Settings");
	    verificationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    verificationFrame.setSize(600, 400);

	    // Set a futuristic look and feel
	    try {
	        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // Create the main panel with BorderLayout
	    JPanel panel = new JPanel(new BorderLayout());
	    panel.setBackground(new Color(70, 130, 180));

	    // Create and set the heading
	    JLabel heading = new JLabel("<html>Verifying that each user belongs in the wall of shame risks getting an Instagram ban if done excessively. We recommend verifying no more than about 100 users every day. How many users do you want to verify during each verification, and how often do you want to carry out the verification process?</html>");
	    heading.setForeground(Color.WHITE);
	    heading.setFont(new Font("Verdana", Font.BOLD, 14));
	    heading.setHorizontalAlignment(SwingConstants.CENTER);
	    panel.add(heading, BorderLayout.NORTH);

	    // Create the form panel with GridLayout
	    JPanel formPanel = new JPanel(new GridLayout(1, 2, 20, 20));
	    formPanel.setBackground(new Color(70, 130, 180));
	    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

	    // Left side: Verification frequency
	    JPanel leftPanel = new JPanel();
	    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
	    leftPanel.setBackground(new Color(70, 130, 180));

	    JLabel freqLabel = new JLabel("Verification frequency:");
	    freqLabel.setForeground(Color.WHITE);
	    freqLabel.setFont(new Font("Verdana", Font.BOLD, 14));
	    leftPanel.add(freqLabel);

	    JPanel freqInputPanel = new JPanel();
	    freqInputPanel.setLayout(new BoxLayout(freqInputPanel, BoxLayout.X_AXIS));
	    freqInputPanel.setBackground(new Color(70, 130, 180));

	    JTextField freqInput = new JTextField();
	    freqInput.setMaximumSize(new java.awt.Dimension(50, 30)); // Explicitly set size
	    String[] options = {"hours", "days", "weeks", "months", "years"};
	    JComboBox<String> freqDropdown = new JComboBox<>(options);
	    freqDropdown.setMaximumSize(new java.awt.Dimension(100, 30)); // Explicitly set size

	    freqInputPanel.add(freqInput);
	    freqInputPanel.add(Box.createRigidArea(new java.awt.Dimension(10, 0))); // Add spacing between input and dropdown
	    freqInputPanel.add(freqDropdown);

	    leftPanel.add(freqInputPanel);

	    // Right side: Verification number
	    JPanel rightPanel = new JPanel();
	    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
	    rightPanel.setBackground(new Color(70, 130, 180));

	    JLabel numLabel = new JLabel("Verification number:");
	    numLabel.setForeground(Color.WHITE);
	    numLabel.setFont(new Font("Verdana", Font.BOLD, 14));
	    rightPanel.add(numLabel);

	    JTextField numInput = new JTextField();
	    numInput.setMaximumSize(new java.awt.Dimension(50, 30)); // Explicitly set size
	    rightPanel.add(numInput);

	    // Add left and right panels to the form panel
	    formPanel.add(leftPanel);
	    formPanel.add(rightPanel);

	    // Add form panel to the main panel
	    panel.add(formPanel, BorderLayout.CENTER);

	    // Create and add the verify button
	    JButton verifyButton = createFuturisticButton("Verify");
	    verifyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	    panel.add(verifyButton, BorderLayout.SOUTH);

	    // Add main panel to the frame
	    verificationFrame.add(panel);

	    // Set frame visibility
	    verificationFrame.setVisible(true);
	}
	
	// TODO: First goal is to capture the meanings of each and every image found. 
	public static void generateComments(AppiumDriver<MobileElement> driver, String hashtag) throws InterruptedException, IOException, AWTException {
		/*
		driver.findElement(By.xpath("//android.widget.FrameLayout[@content-desc=\"Search and explore\"]/android.widget.ImageView")).click();
		Thread.sleep(3000);
		MobileElement searchBar = driver.findElement(By.id("com.instagram.android:id/action_bar_search_edit_text"));
		searchBar.click();
		Thread.sleep(2000);
		searchBar = driver.findElement(By.id("com.instagram.android:id/action_bar_search_edit_text"));
		searchBar.sendKeys(hashtag + "\n");
		Thread.sleep(3000);
		*/
		driver.findElement(By.xpath("//android.widget.FrameLayout[@content-desc=\"Search and explore\"]/android.widget.ImageView")).click();
		Thread.sleep(3000);
		MobileElement searchBar = driver.findElement(By.id("com.instagram.android:id/action_bar_search_edit_text"));
		searchBar.click();
		Thread.sleep(2000);
		searchBar = driver.findElement(By.xpath("//android.widget.EditText[@resource-id=\"com.instagram.android:id/action_bar_search_edit_text\"]"));
		searchBar.sendKeys(hashtag + "\n");
		Thread.sleep(3000);
		driver.executeScript("mobile:performEditorAction", ImmutableMap.of("action", "done"));
		driver.findElement(By.xpath("//androidx.recyclerview.widget.RecyclerView/android.widget.Button")).click();
		Thread.sleep(5000);
		TakesScreenshot screenshot = (TakesScreenshot)driver;
		File sourcePath = screenshot.getScreenshotAs(OutputType.FILE);
		int imageCounter = 1;
		String outputText = System.getProperty("user.dir") + "/artifacts" + "generated_comments.txt";
		FileWriter fileWriter = new FileWriter(outputText, true);
		String firstFileName = System.getProperty("user.dir") + "/screenshot" + String.valueOf(imageCounter) + ".png";
		File destinationPath = new File(firstFileName);
		FileUtils.copyFile(sourcePath, destinationPath);
		
		Thread.sleep(3000);
		
		// Ask if the image is about fitness.
        String base64Image = encodeImage(firstFileName);
        String payload = createPayload(base64Image, "fitness test");
        String response = sendPostRequest(payload, OPENAI_API_KEY);
        JSONObject jsonString = new JSONObject(response.toString());
        JSONArray choicesArr = (JSONArray)jsonString.get("choices");
        JSONObject firstElement = (JSONObject)choicesArr.get(0);
        JSONObject messageObj = (JSONObject)firstElement.get("message");
        String chatbotResponse = messageObj.getString("content");
        
        // If the image is about fitness, generate a comment of encouragement.
        if(chatbotResponse.equals("Yes.")) {
        	payload = createPayload(base64Image, "comment generation");
            response = sendPostRequest(payload, OPENAI_API_KEY);
            jsonString = new JSONObject(response.toString());
            choicesArr = (JSONArray)jsonString.get("choices");
            firstElement = (JSONObject)choicesArr.get(0);
            messageObj = (JSONObject)firstElement.get("message");
            chatbotResponse = messageObj.getString("content");
            fileWriter.write("Image number: " + imageCounter + "\n");
            fileWriter.write("Comment generated: " + chatbotResponse + "\n");
        }
        
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
			String fileName = System.getProperty("user.dir") + "/screenshots/screenshot" + String.valueOf(imageCounter) + ".png";
			destinationPath = new File(fileName);
			FileUtils.copyFile(sourcePath, destinationPath);
			
			Thread.sleep(3000);
			
			// Ask if the image is about fitness.
            String base64Image1 = encodeImage(fileName);
            String payload1 = createPayload(base64Image1, "fitness test");
            String response1 = sendPostRequest(payload1, OPENAI_API_KEY);
            JSONObject jsonString1 = new JSONObject(response1.toString());
            JSONArray choicesArr1 = (JSONArray)jsonString1.get("choices");
            JSONObject firstElement1 = (JSONObject)choicesArr1.get(0);
            JSONObject messageObj1 = (JSONObject)firstElement1.get("message");
            String chatbotResponse1 = messageObj1.getString("content");
            
            // If the image is about fitness, generate a comment of encouragement.
            if(chatbotResponse1.equals("Yes.")) {
            	payload1 = createPayload(base64Image1, "comment generation");
                response1 = sendPostRequest(payload1, OPENAI_API_KEY);
                jsonString1 = new JSONObject(response1.toString());
                choicesArr1 = (JSONArray)jsonString1.get("choices");
                firstElement1 = (JSONObject)choicesArr1.get(0);
                messageObj1 = (JSONObject)firstElement1.get("message");
                chatbotResponse1 = messageObj1.getString("content");
                fileWriter.write("Image number: " + imageCounter + "\n");
                fileWriter.write("Comment generated: " + chatbotResponse1 + "\n");
            }
			
			imageCounter++;
		}
		
		fileWriter.close();
	}
	
	// TODO: Check for the visibility of the messages icon to see if the stories have stopped.
	public void scrollStories(AppiumDriver<MobileElement> driver) throws InterruptedException {
		//driver.findElement(By.xpath("//android.widget.TextView[contains(@content-desc,\"Instagram\")]")).click();
		//Thread.sleep(3000);
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
	
	/**
	 *	We want to follow a new batch of users and then check in 48-72 hours whether they follow us back or not. This 
	 *	expectation by the stakeholder was misunderstood for so much time, but I'm glad I was able to correct this. 
	 *	We are going to follow new people from hashtags that we find from the Internet, and keep it to a limited number
	 *	in order to avoid being banned by Instagram. Refresh the Javadoc to describe the parameters. Later on, we will
	 *	use a GUI in order to have the user decide (taking input) which hashtag they want to use. We will also have to 
	 *	decide whether to persist the usernames in a file or a database. There won't be that many users, and we want to 
	 *	reduce the number of dependencies the users will have to download, so I might just stick to a file for the time
	 *	being. 
	 *
	 *	UPDATE: ANOTHER TWIST! Humans will be performing this step. 
	 */
	public void followNewUsers(AppiumDriver<MobileElement> driver, String hashtag, int numberToFollow) {
		
	}
	
	/**
	 * Test function used to test JDBC's functionality. 
	 */
	public void testJDBC() {
		String url = "jdbc:mysql://localhost:3306/dbofshame";	// have to include the schema name at the ends
		String username = "root";
		String password = "password";							// how would we securely store this on public GitHub?
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, username, password);
			Statement statement = connection.createStatement();
			// To delete the entire table, use a WHERE clause for filtration. 
			String sqlCommand = "DELETE FROM nonfollowers";
			PreparedStatement preparedStmt = connection.prepareStatement(sqlCommand); 
			preparedStmt.execute();
			/*
			 * String[] names = {"Jack","Roque","Steven","Raveena"}; for (String name :
			 * names) { String sqlCommand = "INSERT INTO nonfollowers (username) VALUES ('"
			 * + name + "')"; PreparedStatement preparedStmt =
			 * connection.prepareStatement(sqlCommand); preparedStmt.execute(); }
			 */
			connection.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This will be a list of users that the inputted account (e.g. "lmtlss.fit") follows, but those accounts do not follow back. 
	 * This should be used in conjunction with the human process of following accounts, something that will still be humanized. 
	 * The database will be used to store the candidate names, so this function will be renamed collectNames instead of generate
	 * wall of shame because the entire wall can't be scraped through at once, unless you want to risk getting banned. 
	 * 
	 * TODO: Automate the process of installing JDBC, will be worth it for large lists of names (esp. 50K+ followers). This will
	 * also have to be done in batches, maybe that should be one of the parameters. 
	 * 
	 * @param driver: the Android driver that will be used to go through the phone
	 * @throws InterruptedException
	 */
	// OCR method
	public static void generateWallOfShame(AppiumDriver<MobileElement> driver) throws InterruptedException {
		driver.findElement(By.xpath("//android.widget.FrameLayout[@content-desc=\"Search and explore\"]/android.widget.ImageView")).click();
		Thread.sleep(3000);
		MobileElement searchBar = driver.findElement(By.id("com.instagram.android:id/action_bar_search_edit_text"));
		searchBar.click();
		Thread.sleep(2000);
		//searchBar = driver.findElement(By.id("com.instagram.android:id/action_bar_search_edit_text"));
		searchBar = driver.findElement(By.xpath("//android.widget.EditText[@resource-id=\"com.instagram.android:id/action_bar_search_edit_text\"]"));
		// TODO: Be able to make this work for any given username, since Jay told me to run it on his other account on 5/6.
		String userName = "lmtlss.fit";				// Jay's alternate account
		searchBar.sendKeys(userName + "\n");
		Thread.sleep(3000);
		// TODO: it's been made into a more dynamic xpath, now make it customizable for any username (perhaps do concatenation).
		driver.findElement(By.xpath("//android.widget.TextView[@resource-id=\"com.instagram.android:id/row_search_user_username\" and @text=\"" + userName + "\"]")).click();
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
			captureNames(driver, followerNames, "follower");
			counter++;
		}
		counter = 0;
		System.out.println("Size of the followers list: " + followerNames.size());
		for(String name : followerNames) {
			System.out.println("Follower (final list): " + name);
		}
		Thread.sleep(10000);
		// TODO: Think more about maintainability of code. See if it works now.
		//driver.findElement(By.xpath("//android.widget.ImageView[@content-desc=\"Back\"]")).click();
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
			captureNames(driver, followingNames, "following");
			counter++;
		}
		System.out.println("Size of the following list: " + followingNames.size());
		for(String name : followingNames) {
			System.out.println("Following (final list): " + name);
		}
		Thread.sleep(15000);
		driver.findElement(By.xpath("//android.widget.ImageView[@content-desc=\"Back\"]")).click();
		followersButton = driver.findElement(By.id("com.instagram.android:id/row_profile_header_textview_followers_count"));
		followersButton.click();
		Thread.sleep(5000);
		System.out.println("We will now be inserting the names into the database of shame ...");
		String url = "jdbc:mysql://localhost:3306/dbofshame";	// have to include the schema name at the ends
		String username = "root";
		String password = "password";							// how would we securely store this on public GitHub?
		Connection connection = null;							// Java might complain about this
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(url, username, password);
		} catch(Exception e) {
			e.printStackTrace();
		}
		for(String oneOfMyFollowing : followingNames) {
			if(!followerNames.contains(oneOfMyFollowing)) {
				String sqlCommand = "INSERT INTO nonfollowers (username) VALUES ('" + oneOfMyFollowing + "')";
				PreparedStatement preparedStmt;
				try {
					preparedStmt = connection.prepareStatement(sqlCommand);
					preparedStmt.execute(); 
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Do you actually want to unfollow these users, or just verify if these users follow you?
	// You still need the device in order to actually carry this out, even with the database loaded. 
	public static void unfollowUsers(AppiumDriver<MobileElement> driver, int numUsersToCheck, int runNum) throws InterruptedException {
		// TODO: This is repeatable code from above, refactor later on (you also have to take username into account).
		// TODO: Get Appium to forge the request headers, avoid getting banned. 
		// Also, don't repeat this commented code too much (especially when logged in already). 
		// Only follow these steps on the first run.
		/*
			driver.findElement(By.xpath("//android.widget.FrameLayout[@content-desc=\"Search and explore\"]/android.widget.ImageView")).click();
			Thread.sleep(3000);
			MobileElement searchBar = driver.findElement(By.id("com.instagram.android:id/action_bar_search_edit_text"));
			searchBar.click();
			Thread.sleep(2000);
			searchBar = driver.findElement(By.id("com.instagram.android:id/action_bar_search_edit_text"));
			searchBar.sendKeys("lmtlss.fit" + "\n");
			
			Thread.sleep(3000);
			// TODO: it's been made into a more dynamic xpath, now make it customizable for any username.
			// PROBLEM: The element could no longer be located.
			driver.findElement(By.xpath("//android.widget.TextView[@resource-id=\"com.instagram.android:id/row_search_user_username\" and @text=\"lmtlss.fit\"]")).click();
			Thread.sleep(5000);
		*/
		MobileElement followersButton = driver.findElement(By.id("com.instagram.android:id/row_profile_header_textview_followers_count"));
		followersButton.click();
		Thread.sleep(5000);
		
		
		// take the top n entries from the database, and then loop through them using the ResultSet object
		// determine whether that user actually follows him back or not (through the filtered list)
		String url = "jdbc:mysql://localhost:3306/dbofshame";	// have to include the schema name at the ends
		String username = "root";
		String password = "password";							// how would we securely store this on public GitHub?
		Connection connection = null;							// Java might complain about this
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(url, username, password);
		} catch(Exception e) {
			e.printStackTrace();
		}
		// We will take a given number of usernames and then check if they're actually accurate or not.
		try {
			// Thought: counting out the followers manually and putting them in a spreadsheet would probably be quicker!
			// This might be a shitty product, after all :0
			int runNumber = runNum;
			String sqlCommand = "SELECT username FROM nonfollowers LIMIT " + String.valueOf(numUsersToCheck) + " OFFSET " + String.valueOf(runNumber * numUsersToCheck);
						;
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sqlCommand);
			System.out.println("USERS TO UNFOLLOW:");
			int counter = 0;
			// Print out the list of names you're putting into this list, and back out early if you find the user.
			while(resultSet.next()) {
				String userToPossiblyUnfollow = resultSet.getString(1);		// column indices in JDBC are one-indexed
				MobileElement followerSearchBar = driver.findElement(By.xpath("//android.widget.EditText[@resource-id=\"com.instagram.android:id/row_search_edit_text\"]"));
				followerSearchBar.clear();
				if(counter == 10) {
					mouseGlide(100, 100, 200, 200, 3000, 100);
					counter = 0;
				}
				boolean userFound = false;
				// Type in one character at a time for the follower list, look like a real user. 
				for(int charIndex = 0; charIndex < userToPossiblyUnfollow.length(); charIndex++) {
					int timeBetweenChars = (int)(Math.random() + 1) * 200;
					Thread.sleep(timeBetweenChars);
					char currentChar = userToPossiblyUnfollow.charAt(charIndex);
					String followerSubstring = new StringBuilder().append(currentChar).toString();
					Actions actions = new Actions(driver);
					actions.moveToElement(followerSearchBar).click();
					// Before writing the first character, send a blank to focus on the element.
					// This was done because the first underscore/character wasn't being written into search box (bug fix).
					if(charIndex == 0) {
						actions.sendKeys("").build().perform();
					}
					actions.sendKeys(followerSubstring).build().perform();
					// Check for the user early after each character, as a shortcut.
					// This is an early escape that saves so much time during the verification process. 
					Thread.sleep(timeBetweenChars);
					List<MobileElement> matchingUser = driver.findElements(By.xpath("//android.widget.TextView[@resource-id=\"com.instagram.android:id/follow_list_username\"]"));
					if(matchingUser.size() != 0) {
						if(matchingUser.get(0).getText().equals(userToPossiblyUnfollow)) {
							userFound = true;
							break;
						}
					}
					// This occurs when the "user not found" text shows up.
					// TODO: There was a bug when it came to the "user not found" case, please see 5.23.24 notes about "fixing the bot".
					// TODO: See if you can refactor these repeated operations, somehow.
					else {
						driver.findElement(By.xpath("//android.widget.ImageView[@content-desc=\"Back\"]")).click();
						Thread.sleep(500);
						followersButton = driver.findElement(By.id("com.instagram.android:id/row_profile_header_textview_followers_count"));
						followersButton.click();
						Thread.sleep(300);
						followerSearchBar = driver.findElement(By.xpath("//android.widget.EditText[@resource-id=\"com.instagram.android:id/row_search_edit_text\"]"));
						actions = new Actions(driver);
						actions.moveToElement(followerSearchBar).click();
						actions.sendKeys("").build().perform();
						break;
					}
				}
				// Check if the user hasn't been found, that's the condition for the early loop break. 
				if(userFound == false) {
					followerSearchBar.sendKeys("\n");								// press "enter" at the end of the sequence
					System.out.println("instagram.com/" + userToPossiblyUnfollow);
				}
				// Always increment the mouse counter, no matter what. Keep the computer screen active.
				counter++;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// captures a list of names while scrolling down
	// TODO: check for the "See more" list
	public static void captureNames(AppiumDriver<MobileElement> driver, List<String> userNames, String type) throws InterruptedException {
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
				if(type.equals("follower")) {
					System.out.println("Follower added: " + follower.getText());
				}
				else {
					System.out.println("Following added: " + follower.getText());
				}
			}
		}
	}
	
	public static void mouseGlide(int x1, int y1, int x2, int y2, int t, int n) {
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
	
	// TODO: Try a 20% difference instead of a 50% difference.
	// RESULT: Go back to 50% scrolling difference.
	public static void scrollDown(AppiumDriver<MobileElement> driver) {
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
	
	public static void scrollUp(AppiumDriver<MobileElement> driver) {
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

}
