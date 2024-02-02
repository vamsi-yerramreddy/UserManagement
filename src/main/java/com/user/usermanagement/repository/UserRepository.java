package com.user.usermanagement.repository;

import com.user.usermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yaml.snakeyaml.events.Event;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Event.ID> {

    Optional<User> findUserByEmailId(String email);
}
