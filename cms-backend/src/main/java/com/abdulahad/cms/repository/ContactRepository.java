package com.abdulahad.cms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.abdulahad.cms.entity.Contact;

public interface ContactRepository  extends JpaRepository<Contact,Integer> {

    Optional<Contact> findByEmailIgnoreCaseAndUserId(String email, int userId);
    Optional<Contact> findByPhoneNumberAndUserId(String phoneNumber, int userId);
    List<Contact> findAllByUserId(int userId);

    @Query("SELECT c FROM Contact c WHERE c.userId = :userId AND " +
           "(LOWER(c.firstName) LIKE %:query% OR " +
           "LOWER(c.lastName) LIKE %:query% OR " +
           "LOWER(c.email) LIKE %:query% OR " +
           "c.phoneNumber LIKE %:query%)")
    List<Contact> searchByUserIdAndQuery(@Param("userId") int userId, @Param("query") String query);
}
