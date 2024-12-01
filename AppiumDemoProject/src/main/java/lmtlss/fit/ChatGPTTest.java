package lmtlss.fit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGPTTest {
	
	// TODO: Security concern - will be a security issue later on, fix this and put in the .gitignore!
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
    public static String createPayload(String base64Image) {
        JSONObject imageUrlObject = new JSONObject();
        imageUrlObject.put("url", "data:image/jpeg;base64," + base64Image);

        JSONObject contentObject = new JSONObject();
        contentObject.put("type", "text");
        //contentObject.put("text", "Is this image about fitness? Please answer only yes or no.");
        contentObject.put("text", "I am a fitness influencer. Generate only one short motivational comment I should leave on this post.");

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

	public static void main(String[] args) throws IOException {
        try {
            // OpenAI API Key
            String apiKey = OPENAI_API_KEY;

            // Path to your image
            // String imagePath = "C:\\Users\\jackd\\Pictures\\sample_workout_image.jpg";
            String imagePath = "C:\\Users\\jackd\\eclipse-workspace\\AppiumDemoProject\\screenshots\\screenshot20.png";
            
            // Getting the base64 string
            String base64Image = encodeImage(imagePath);

            // Creating the payload
            String payload = createPayload(base64Image);

            // Sending the POST request and getting the response
            String response = sendPostRequest(payload, apiKey);

            // Extracting the chatbot response
            JSONObject jsonString = new JSONObject(response.toString());
            JSONArray choicesArr = (JSONArray)jsonString.get("choices");
            JSONObject firstElement = (JSONObject)choicesArr.get(0);
            JSONObject messageObj = (JSONObject)firstElement.get("message");
            String chatbotResponse = messageObj.getString("content");
            System.out.println(chatbotResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
