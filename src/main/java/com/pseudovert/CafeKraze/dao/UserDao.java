package com.pseudovert.CafeKraze.dao;

import com.pseudovert.CafeKraze.POJO.User;
import com.pseudovert.CafeKraze.wrapper.UserWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {


    User findByEmailId(@Param("email") String email);
    List<UserWrapper> getAllUsers();
    @Transactional
    @Modifying
    Integer updateStatus(@Param("id") Integer id, @Param("status") String status);

    List<String> getAllAdmins();
}
