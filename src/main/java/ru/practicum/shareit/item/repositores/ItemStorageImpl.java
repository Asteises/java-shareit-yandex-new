package ru.practicum.shareit.item.repositores;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorageCustom {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Item> findByNameAndDescription(String name, String description) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Item> cq = cb.createQuery(Item.class);
        Root<Item> item = cq.from(Item.class);
        Predicate namePredicate = cb.like(cb.lower(item.get("name")), name.toLowerCase());
        Predicate descriptionPredicate = cb.like(cb.lower(item.get("description")), description.toLowerCase());
        cq.where(cb.or(namePredicate, descriptionPredicate));
        TypedQuery<Item> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}
