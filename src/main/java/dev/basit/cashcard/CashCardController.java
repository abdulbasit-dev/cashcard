package dev.basit.cashcard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
//import java.util.List;
import java.security.Principal;
import java.util.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/cashcards")
public class CashCardController {

    private final CashCardRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public CashCardController(CashCardRepository repository) {
        this.repository = repository;
    }


    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable,Principal principal) {
        Page<CashCard> page = repository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC,"amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CashCard> findById(@PathVariable int id, Principal principal) {

        logger.info("Finding cash card with id: {}", id);

        Optional<CashCard> cashCard = Optional.ofNullable(repository.findByIdAndOwner(id, principal.getName()));

        if(cashCard.isPresent()){
           return ResponseEntity.ok(cashCard.get());
        }

        logger.info("Cash card with id: {} not found", id);
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard, UriComponentsBuilder ucb,Principal principal) {
        CashCard cashCardWithOwner = new CashCard(null,cashCard.amount(),principal.getName());
        CashCard newCashCard = repository.save(cashCardWithOwner);
        URI locationOfNewCashCard = ucb.path("/api/cashcards/{id}")
                .buildAndExpand(newCashCard.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }
}
