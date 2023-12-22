package com.bortolo.softwarequalityclass.repository;

import com.bortolo.softwarequalityclass.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {


    @Query("SELECT m FROM Message m ORDER BY m.dateCreationTime DESC")
    Page<Message> listMessages(Pageable pageable);
}
