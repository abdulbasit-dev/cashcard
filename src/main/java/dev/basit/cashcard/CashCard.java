package dev.basit.cashcard;


import org.springframework.data.annotation.Id;

public record CashCard(
        @Id
        Integer id,
        Double amount
) {
}
