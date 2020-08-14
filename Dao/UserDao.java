package com.example.Dao;

import com.example.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

public interface UserDao extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    List<User> findAllByUserName(String userName);
    void deleteByUserName(String userName);
    List<User> findAllByUserId(Integer userId);
    Page<User> findAll(Specification<User> var1, Pageable var2);
    @Modifying
    @Transactional
    @Query(nativeQuery = true ,value = "delete from user_table where user_table.user_Id in (:userIds) ")
    void deleteUserById(@Param("userIds") List<Integer> userIds);
    @Modifying
    @Transactional
    @Query(nativeQuery = true ,value = "delete from user_role where user_role.user_Id in (:userIds) ")
    void deleteUserRole(@Param("userIds") List<Integer> userIds);
}
