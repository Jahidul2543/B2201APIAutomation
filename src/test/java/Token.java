import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import okhttp3.*;
import okhttp3.Request.Builder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static io.restassured.RestAssured.given;

public class Token {

    public static void main(String[] args) throws IOException {
//         String token =   getToken();
//        System.out.println("Token " + token);
        submit();
    }

    public static String  getToken() throws IOException {
            /**
             * 1. Type of call - POST/PUT/DELETE/GET
             * 2. Protocol, Host Name, Port, Resource - url
             * 3. Body
             * 4. Headers
             * 5. Execute an API call
             * Responses
             * 1. Response Body
             *    - Process response body and get the token
             * 2. Response Code
             * 3. Response Header
             * 4. Response Cookies
             *
             * */
            String hostName = "https://izaan-test.auth.us-east-1.amazoncognito.com/";
            String endpoint = "/oauth2/token";
            String url = hostName + endpoint;

            OkHttpClient client = new OkHttpClient.Builder().build();

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "scope=izaan_test/post_info&grant_type=client_credentials");

            String encoding = Base64.getEncoder().encodeToString(("1u5io4va9sr45n79fceg2damjf:1qbkthvp7lbc7aavuhhmfg8f2crekor9h2h7abu2oru1nlpj71fe").getBytes("UTF-8"));

            String authorization = "Basic " + encoding;

        Request request = new Request.Builder()
                            .url(url)
                            .method("POST", body)
                            .addHeader("Authorization", authorization)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();
        Response response  = client.newCall(request).execute();

        String responseBody = response.body().string();
        int responseCode = response.code();
        System.out.println(responseBody + " Code " + responseCode);

        JsonPath jsonPath = new JsonPath(responseBody);
        String token = jsonPath.get("access_token");

        System.out.println("Token " + token);

        client.connectionPool().evictAll();

        return token;
    }

    public static void submit() throws IOException {
        String url = "https://5x9m5ed0tj.execute-api.us-east-1.amazonaws.com/test/submit";
        String submitPayload = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + "/payloads/submit.json")));
        // We will use RestAssured Library
        RequestSpecification requestSpecification = given().body(submitPayload);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", getToken());

        io.restassured.response.Response response = requestSpecification.post(url);

        String responseBody = response.asString();
        System.out.println(responseBody);
        JsonPath jsonPath = new JsonPath(responseBody);

        String message = jsonPath.get("message");
        System.out.println("Message: " + message);
        System.out.println("Response Code: " + response.statusCode());

    }
}
