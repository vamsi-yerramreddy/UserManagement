package com.user.usermanagement.repository;

import com.user.usermanagement.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
 Optional<Session> findByToken(String token);

}
