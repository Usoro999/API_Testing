package reqres.in.Specification.apiTest;

import reqres.in.Specification.Specification;
import org.junit.Assert;
import org.junit.Test;
import pojoClasses.*;
import reqres.in.Specification.pojoClasses.*;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

// Video with Testing  https://www.youtube.com/watch?v=gxzXOMxIt4w&t=1s


public class ReqresTest {
    private final static String URL = "https://reqres.in/";

    // GET test
    @Test
    public void checkAvatarAndIdTest(){
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.responseSpec200()); // Use Specification.class
        List< UserData > users = given()
                .when()
        //        .contentType(ContentType.JSON)                                     // if type will be Json ->>
                .get("reqres/in/Specification/apiTest/users?page=2")                                        // GET data from the page
                .then().log().all()                                                // then show log with all data from Json in the console
                .extract().body().jsonPath().getList("data", UserData.class); // extract result (list) to Pojo class/ Start extract AFTER 'data' field to which class

        // Check Avatar is the same as id. Email contains '@reqres.in'

        // Way 1
        users.forEach(x-> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));  // Check Avatar name is the same with id

        Assert.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in"))); // Check all users that their email is with '@reqres.in'


        // Way 2
        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList()); //Create list with all avatars
        List<String> ids = users.stream().map(x->x.getId().toString()).collect(Collectors.toList()); //Create list with all id. use lamda get, because id is integer
        List<String> emails = users.stream().map(UserData::getEmail).collect(Collectors.toList());   //Create list with all emails
        for(int i = 0; i < avatars.size(); i++){                                                     // Check all you need
            Assert.assertTrue(avatars.get(i).contains(ids.get(i)));                                  // Check Avatar name is the same with id
            Assert.assertTrue(emails.get(i).endsWith("@reqres.in"));                                 // Check all users that their email is with '@reqres.in'
        }
    }



    // Check the Registration. POST test
    // 1. POST id and email in the request
    // 2. Check the response from server
    @Test
    public void successRegTest(){

        Specification.installSpecification(Specification.requestSpecification(URL), Specification.responseSpec200());

        // Response data after POST
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        // Create user with email and password
        Register user = new Register("eve.holt@reqres.in", "pistol");

        // POST user data to server
        SuccessReg successReg = given()
                .body(user)   // what we want to post
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);

        // Check the response is not null
        Assert.assertNotNull(successReg.getId());
        Assert.assertNotNull(successReg.getToken());

        // Compare response with our data
        Assert.assertEquals(id, successReg.getId());
        Assert.assertEquals(token, successReg.getToken());
    }

    // Check the Registration with invalid password (empty field).
    // 1. POST id and email in the request
    // 2. Check the response from server
    @Test
    public void unSuccessRegTest(){
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.responseSpec400());

        // Create user with email without password
        Register user = new Register("sydney@fife", "");
        unSuccessReg unSuccessReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(unSuccessReg.class);
        Assert.assertEquals("Missing password", unSuccessReg.getError());

    }

    // Check the dates (years) are sorted in list

    @Test
    public void sortedYearsTest(){
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.responseSpec200());
        // Create list with all data
        List<ColorsData> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorsData.class);

        // Create list only with colors
        List<Integer> yearsList = colors.stream().map(x->x.getYear()).collect(Collectors.toList());

        // Sort our list
        List<Integer> sortedYears = yearsList.stream().sorted().collect(Collectors.toList());

        Assert.assertEquals(sortedYears, colors.stream().map(ColorsData::getYear).collect(Collectors.toList()));
    }

    // DELETE user
    @Test
    public void deleteUser(){
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.responseSpec204());
        given()
                .when()
                .delete("api/users/2")
                .then().log().all();


    }


    // Verify time is the same as UPDATEd time
    @Test
    public void timeTest(){
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.responseSpec200());
        UserTime userTime = new UserTime("morpheus", "zion resident");
        UserTimeResponse userTimeResponse= given()
                .body(userTime)
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String regex = "(.{8})$";
        String regex1 = "(.{14})$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex1, "");

        Assert.assertEquals(currentTime, userTimeResponse.getUpdatedAt().replaceAll(regex,""));

    }





}
