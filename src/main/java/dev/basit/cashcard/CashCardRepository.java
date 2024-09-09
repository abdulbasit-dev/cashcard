package dev.basit.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashCardRepository extends CrudRepository<CashCard,Integer> , PagingAndSortingRepository<CashCard,Integer> {

    CashCard findByIdAndOwner(Integer id, String owner);
    Page<CashCard> findByOwner(String owner, PageRequest pageRequest);
}
