package tests;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.AssertJUnit;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ContactListApiTest {

    // Shared between tests (flow: Add User -> ... -> Logout)
    static String token;
    static String contactId;

    // Unique emails so the test can be re-run without "email already in use"
    static long ts = System.currentTimeMillis();
    static String email = "test" + ts + "@fake.com";
    static String newEmail = "test2" + ts + "@fake.com";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://thinking-tester-contact-list.herokuapp.com";
    }

    @Test(priority = 1)
    public void addUser() {
        String body = "{"
                + "\"firstName\": \"Test\","
                + "\"lastName\": \"User\","
                + "\"email\": \"" + email + "\","
                + "\"password\": \"myPassword\""
                + "}";

        Response res = given()
                .contentType(ContentType.JSON)
                .body(body)
            .when()
                .post("/users")
            .then()
                .statusCode(201)
                .statusLine(containsString("Created"))
                .body("token", notNullValue())
                .body("user.firstName", equalTo("Test"))
                .extract().response();

        token = res.jsonPath().getString("token");
        System.out.println("New User created with status code: "+res.statusCode());
    }

    @Test(priority = 2)
    public void getUserProfile() {
       Response res= given()
                .header("Authorization", "Bearer " + token)
            .when()
                .get("/users/me")
            .then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .body("email", equalTo(email))
                .extract().response();
        
        System.out.println("User Profile get createed with id: "+res.statusCode());
    }

    @Test(priority = 3)
    public void updateUser() {
        String body = "{"
                + "\"firstName\": \"Updated\","
                + "\"lastName\": \"Username\","
                + "\"email\": \"" + newEmail + "\","
                + "\"password\": \"myNewPassword\""
                + "}";

       Response res= given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(body)
            .when()
                .patch("/users/me")
            .then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .body("firstName", equalTo("Updated"))
                .body("email", equalTo(newEmail))
                .extract().response();
        
        System.out.println("User updated with status code : "+res.statusCode());
    }

    @Test(priority = 4)
    public void loginUser() {
        String body = "{"
                + "\"email\": \"" + newEmail + "\","
                + "\"password\": \"myNewPassword\""
                + "}";

        Response res = given()
                .contentType(ContentType.JSON)
                .body(body)
            .when()
                .post("/users/login")
            .then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .body("token", notNullValue())
                .extract().response();

        token = res.jsonPath().getString("token");   // use fresh token from now on
        
        System.out.println("User login with status code: "+res.statusCode());
    }

    @Test(priority = 5)
    public void addContact() {
        String body = "{"
                + "\"firstName\": \"John\","
                + "\"lastName\": \"Doe\","
                + "\"birthdate\": \"1970-01-01\","
                + "\"email\": \"jdoe@fake.com\","
                + "\"phone\": \"8005555555\","
                + "\"street1\": \"1 Main St.\","
                + "\"street2\": \"Apartment A\","
                + "\"city\": \"Anytown\","
                + "\"stateProvince\": \"KS\","
                + "\"postalCode\": \"12345\","
                + "\"country\": \"USA\""
                + "}";

        Response res = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(body)
            .when()
                .post("/contacts")
            .then()
                .statusCode(201)
                .statusLine(containsString("Created"))
                .body("firstName", equalTo("John"))
                .extract().response();

        contactId = res.jsonPath().getString("_id");
        AssertJUnit.assertNotNull(contactId, "Contact id should not be null");
  	  System.out.println("User contact created with status code: "+res.statusCode());

    }

    @Test(priority = 6)
    public void getContactList() {
        Response res = given()
                .header("Authorization", "Bearer " + token)
            .when()
                .get("/contacts")
            .then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .body("size()", greaterThan(0))
                .extract().response();

        System.out.println("Contact list with code: " + res.statusCode());
    }

    @Test(priority = 7)
    public void getContact() {
     Response res=   given()
                .header("Authorization", "Bearer " + token)
            .when()
                .get("/contacts/" + contactId)
            .then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .body("_id", equalTo(contactId))
                .extract().response();
     System.out.println("Contact get with code: " + res.statusCode());
        
    }

    @Test(priority = 8)
    public void updateContactFull() {
        String body = "{"
                + "\"firstName\": \"Amy\","
                + "\"lastName\": \"Miller\","
                + "\"birthdate\": \"1992-02-02\","
                + "\"email\": \"amiller@fake.com\","
                + "\"phone\": \"8005554242\","
                + "\"street1\": \"13 School St.\","
                + "\"street2\": \"Apt. 5\","
                + "\"city\": \"Washington\","
                + "\"stateProvince\": \"QC\","
                + "\"postalCode\": \"A1A1A1\","
                + "\"country\": \"Canada\""
                + "}";

       Response res= given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(body)
            .when()
                .put("/contacts/" + contactId)
            .then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .body("email", equalTo("amiller@fake.com"))
                .extract().response();
       System.out.println("Contact updated with code: " + res.statusCode());
    }

    @Test(priority = 9)
    public void updateContactPartial() {
    Response res=    given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{\"firstName\": \"Anna\"}")
            .when()
                .patch("/contacts/" + contactId)
            .then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .body("firstName", equalTo("Anna"))
                .extract().response();
    System.out.println("Contact updated with code: " + res.statusCode());
    }

    @Test(priority = 10)
    public void logoutUser() {
       Response res= given()
                .header("Authorization", "Bearer " + token)
            .when()
                .post("/users/logout")
            .then()
                .statusCode(200)
                .statusLine(containsString("OK"))
                .extract().response();
       System.out.println("User logout with code: " + res.statusCode());
    }
}