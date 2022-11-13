package reqres.in.Specification.apiTest;

import reqres.in.Specification.Specification;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;



    /** Testing API without POJOclasses.
    https://www.youtube.com/watch?v=z9Tvxh6uQzI*/

public class ReqresNoPojoTest {
    private final static String URL = "https://reqres.in/";


    @Test
    public void checkAvatars(){
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.responseSpec200());


        //GET JSON response form server
        Response response = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .body("page", equalTo(2))
                .body("data.id", notNullValue())    // Check th data is not null
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().response();

        JsonPath jsonPath = response.jsonPath();            // Save response into json object
        List<String> emails = jsonPath.get("data.email");   // get emails from json
        List<Integer> ids = jsonPath.get("data.id");
        List<String> avatars = jsonPath.get("data.avatar");

        // Verify avatar has id
        for (int i = 0; i < avatars.size(); i++){
            Assert.assertTrue(avatars.get(i).contains(ids.get(i).toString()));
        }
        // Check Emails end with 'reqres.in'
        Assert.assertTrue(emails.stream().allMatch(x->x.endsWith("reqres.in")));

    }

    @Test
        public void successUserRegTest(){
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.responseSpec200());

        // Create user with Map
        Map<String, String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");

        // WAY 1 ---- POST request with user data and check response after
        given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .body("id", equalTo(4))                       // check the response has id - 4
                .body("token", equalTo("QpwL5tke4Pnpja7X4")); // check the token


        // WAY 2 ----- Check with Response
        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        int id = jsonPath.get("id");
        String token = jsonPath.get("token");

        Assert.assertEquals(4,id);
        Assert.assertEquals("QpwL5tke4Pnpja7X4", token);
    }


    @Test

        public void unSuccessUserRegTest(){
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.responseSpec400());

        // Way 1
        Map<String, String> user = new HashMap<>();
        user.put("email", "sydney@fife");
        // user.put("password", "");
        given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .body("error", equalTo("Missing password"));   // Checking

        //Way 2
        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        String error = jsonPath.get("error");

        Assert.assertEquals("Missing password", error);
    }

}
