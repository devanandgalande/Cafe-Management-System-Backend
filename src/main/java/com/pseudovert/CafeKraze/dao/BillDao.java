package com.pseudovert.CafeKraze.dao;

import com.pseudovert.CafeKraze.POJO.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillDao extends JpaRepository<Bill, Integer> {
    @Query(value = "select b from Bill b where b.createdBy = :username order by b.id desc")
    List<Bill> getBillsByUsername(@Param("username") String username);
}
