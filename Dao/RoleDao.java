package com.example.Dao;

import com.example.Entity.Role;
import com.example.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface RoleDao extends JpaRepository<Role, Integer> {
    List<Role> findAllByRoleName(String roleName);
    List<Role> findAllByRoleId(Integer roleId);
    void deleteByRoleId(Integer roleId);
    Page<Role> findAll(Specification<Role> var1, Pageable var2);

    @Modifying
    @Transactional
    @Query(nativeQuery = true ,value = "delete from role_table where role_table.role_Id in (:roleIds) ")
    void deleteRoleById(@Param("roleIds") List<Integer> roleIds);
    @Modifying
    @Transactional
    @Query(nativeQuery = true ,value = "delete from role_auth where role_auth.role_Id in (:roleIds) ")
    void deleteRoleAuth(@Param("roleIds") List<Integer> roleIds);
}
