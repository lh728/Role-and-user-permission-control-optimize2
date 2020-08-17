package com.example.Service;


import com.example.Dao.AuthorityDao;

import com.example.Dao.UserDao;
import com.example.Dao.RoleDao;
import com.example.Entity.Authority;
import com.example.Entity.User;
import com.example.Entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@Service
public class EntityService {
    @Autowired
    private UserDao userRepository;
    @Autowired
    private RoleDao roleRepository;
    @Autowired
    private AuthorityDao authorityRepository;

    @Transactional
    public void deleteByUserName(String userName) {
        List<User> users = userRepository.findAllByUserName(userName);
        //如果删除维护端数据，只是把维护端的List清空
        for (User user : users) {
            user.getRoles().clear();
            userRepository.save(user); //执行save()之后才会保存到数据库中
        }
        userRepository.deleteByUserName(userName);
    }

    @Transactional
    public void deleteByRoleId(Integer roleId) {
        List<Role> roles = roleRepository.findAllByRoleId(roleId);
        List<User> users = userRepository.findAll();
        for (User user : users) {
            List<Role> userRole = user.getRoles();
            for (Role role : roles) {
                if (userRole.contains(role)) {
                    userRole.remove(role);
                }
                role.getAuthorities().clear();
                roleRepository.save(role);
            }
            userRepository.save(user);
        }
        roleRepository.deleteByRoleId(roleId);
    }

    @Transactional
    public void deleteByAuthorityName(String authName) {
        List<Authority> authorities = authorityRepository.findAllByAuthorityName(authName);
        List<Role> roles = roleRepository.findAll();
        //如果删除被维护端的数据，则把用户（维护端）的List中要移除的角色（被维护端）都remove掉
        for (Role role : roles) {
            List<Authority> roleAuthoritis = role.getAuthorities();
            for (Authority authority : authorities) {
                if (roleAuthoritis.contains(authority)) {
                    roleAuthoritis.remove(authority);
                }
            }
            roleRepository.save(role);
        }
        authorityRepository.deleteByAuthorityName(authName);
    }


    public Page<User> findAllByUser(int pageNum,User user) {
        Pageable pageable = PageRequest.of(pageNum - 1, 6);
        Page<User> uList =userRepository.findAll(new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                    if (user.getUserId() != null && !user.getUserId().equals("")) {
                    predicates.add(criteriaBuilder.like(root.get("userId").as(String.class), "%" + user.getUserId() + "%"));
                    }
                    if (user.getUserName() != null && !user.getUserName().equals("")) {
                    predicates.add(criteriaBuilder.like(root.get("userName").as(String.class), "%" + user.getUserName() + "%"));
                    }
                    Predicate[] pre = new Predicate[predicates.size()];
                    criteriaQuery.where(predicates.toArray(pre));
                    return criteriaBuilder.and(predicates.toArray(pre));
            }
        },pageable);
        return uList;
    }
    @Transactional
    public void deleteUser(List<Integer> userIds) {

        userRepository.deleteUserById(userIds);
    }
    @Transactional
    public void deleteUserRole(List<Integer> userIds){
        userRepository.deleteUserRole(userIds);
    }
    public Page<Role> findAllByRole(int pageNum,Role role) {
        Pageable pageable = PageRequest.of(pageNum - 1, 6);
        Page<Role> uList =roleRepository.findAll(new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (role.getRoleId() != null && !role.getRoleId().equals("")) {
                    predicates.add(criteriaBuilder.like(root.get("roleId").as(String.class), "%" + role.getRoleId() + "%"));
                }
                if (role.getRoleName() != null && !role.getRoleName().equals("")) {
                    predicates.add(criteriaBuilder.like(root.get("roleName").as(String.class), "%" + role.getRoleName() + "%"));
                }
                Predicate[] pre = new Predicate[predicates.size()];
                criteriaQuery.where(predicates.toArray(pre));
                return criteriaBuilder.and(predicates.toArray(pre));
            }
        },pageable);
        return uList;
    }
    @Transactional
    public void deleteRole(List<Integer> roleIds) {

        roleRepository.deleteRoleById(roleIds);
    }
    @Transactional
    public void deleteRoleAuth(List<Integer> roleIds){
        roleRepository.deleteRoleAuth(roleIds);
    }

}
