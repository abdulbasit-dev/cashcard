package dev.basit.cashcard;


import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "password")

                .withBasicAuth("sarah1", "password")
                .getForEntity("/api/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();
        assertThat(id).isEqualTo(99);

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(123.45);
    }

    @Test
    void ShouldNotReturnACasHCardWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "password")
                .getForEntity("/api/cashcards/999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    @DirtiesContext
    void ShouldCreateANewCashCard() {
        CashCard newCashCard = new CashCard(null, 250.00, null);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sarah1", "password")
                .postForEntity("/api/cashcards", newCashCard, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewCashCard = response.getHeaders().getLocation();

        ResponseEntity<String> cashCardResponseEntity = restTemplate
                .withBasicAuth("sarah1", "password")
                .getForEntity(locationOfNewCashCard, String.class);
        assertThat(cashCardResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(cashCardResponseEntity.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");

        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(250.00);
    }

    @Test
    void shouldReturnAllCashCardsWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "password")
                .getForEntity("/api/cashcards", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        Integer cashCardCount = documentContext.read("$.length()");
        assertThat(cashCardCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.00, 150.00);
    }

    @Test
    void shouldReturnAPageOfCashCards() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "password")
                .getForEntity("/api/cashcards?page=0&size=1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);

    }

    @Test
    void shouldReturnASortedPageOfCashCards() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "password")
                .getForEntity("/api/cashcards?page=0&size=1&sort=amount,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);

        double amount = documentContext.read("$[0].amount");
        assertThat(amount).isEqualTo(150.00);
    }

    @Test
    void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "password")
                .getForEntity("/api/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactly(1.00, 123.45, 150.00);
    }

    @Test
    void shouldNotReturnACashCardWhenUsingBadCredentials(){
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("basit", "BAD-PASSWORD")
                .getForEntity("/api/cashcards/99", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        response = restTemplate
                .withBasicAuth("BAD-USER","password")
                .getForEntity("/api/cashcards/99",String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectUsersWhoAreNotCardOwners(){
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("hank", "password")
                .getForEntity("/api/cashcards/99", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldNotAllowToAccessCashCardsTheyDoNotOwn(){
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "password")
                .getForEntity("/api/cashcards/102", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test
    void shouldUpdateAnExistingCashCard(){
        CashCard cashCardUpdate = new CashCard(null, 19.99, null);
        HttpEntity<CashCard> request = new HttpEntity<>(cashCardUpdate);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sarah1", "password")
                .exchange("/api/cashcards/99", HttpMethod.PUT, request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("sarah1", "password")
                .getForEntity("/api/cashcards/99", String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");
        assertThat(id).isEqualTo(99);
        assertThat(amount).isEqualTo(19.99);
    }

    @Test
    void shouldNotUpdateACashCardThatDoesNotExist(){
        CashCard unknownCashCard = new CashCard(null, 19.99, null);
        HttpEntity<CashCard> request = new HttpEntity<>(unknownCashCard);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sarah1", "password")
                .exchange("/api/cashcards/99999", HttpMethod.PUT, request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void  shouldNotUpdateACashCardThatIsOwnedBySomeoneElse(){
        CashCard kumarCard = new CashCard(null, 333.333, null);
        HttpEntity<CashCard> request = new HttpEntity<>(kumarCard);
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sarah1", "password")
                .exchange("/api/cashcards/102", HttpMethod.PUT, request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldDeleteAnExistingCashCard(){
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sarah1", "password")
                .exchange("/api/cashcards/99", HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("sarah1", "password")
                .getForEntity("/api/cashcards/99", String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteACashCardThatDoesNotExist(){
        ResponseEntity<Void> response = restTemplate
                .withBasicAuth("sarah1", "password")
                .exchange("/api/cashcards/99999", HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAllowDeletionOfCashCardsTheyDoNotOwn() {
        ResponseEntity<Void> deleteResponse = restTemplate
                .withBasicAuth("sarah1", "password")
                .exchange("/api/cashcards/102", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("kumar2", "password")
                .getForEntity("/api/cashcards/102", String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
