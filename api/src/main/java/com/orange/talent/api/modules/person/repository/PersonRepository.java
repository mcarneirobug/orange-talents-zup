package com.orange.talent.api.modules.person.repository;

import com.orange.talent.api.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query(value = "FROM Person p "
            + "WHERE p.email = :email"
    )
    Optional<Person> findByEmail(@Param("email") String email);
}
