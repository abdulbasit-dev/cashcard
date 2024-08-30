package dev.basit.cashcard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cashcards")
public class CashCardController {

    private final CashCardRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public CashCardController(CashCardRepository repository) {
        this.repository = repository;
    }


    @GetMapping("")
    public List<CashCard> findAll() {
        return (List<CashCard>) repository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<CashCard> findById(@PathVariable int id) {

        logger.info("Finding cash card with id: {}", id);

        Optional<CashCard> cashCard = repository.findById(id);

        if(cashCard.isPresent()){
           return ResponseEntity.ok(cashCard.get());
        }

        logger.info("Cash card with id: {} not found", id);
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard, UriComponentsBuilder ucb) {
        CashCard savedCashCard = repository.save(cashCard);
        URI locationOfNewCashCard = ucb.path("/api/cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }
}
