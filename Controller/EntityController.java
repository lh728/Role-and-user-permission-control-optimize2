package com.example.Controller;


import com.example.Dao.AuthorityDao;
import com.example.Dao.RoleDao;
import com.example.Dao.UserDao;
import com.example.Entity.Authority;
import com.example.Entity.Role;
import com.example.Entity.User;
import com.example.Service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.*;

@Controller


public class EntityController {
    @Autowired
    private UserDao userRepository;
    @Autowired
    private RoleDao roleRepository;
    @Autowired
    private AuthorityDao authorityRepository;
    @Autowired
    private EntityService entityService;


    @RequestMapping("/adduser")
    public String addUser(@RequestParam(value = "userName") String userName,
                              @RequestParam(value = "roleName") String roleName
                              ) {
        User user = new User();
        Role role = roleRepository.findAllByRoleName(roleName).get(0);
        user.setUserName(userName);
        user.setRoles(new ArrayList<>());
        user.getRoles().add(role);//给用户设置权限
        userRepository.save(user);
        return "redirect:/user";
    }


    @RequestMapping("/addrole")

    public String addRole(
            @RequestParam(value = "roleName") String roleName,
            @RequestParam(value = "authName") String authName) {
        Role role = new Role();
        Authority authority = authorityRepository.findAllByAuthorityName(authName).get(0);
        role.setRoleName(roleName);
        role.setAuthorities(new ArrayList<>());
        role.getAuthorities().add(authority);
        roleRepository.save(role);
        return "redirect:/role";
    }

    /*
    在前端显示用户，角色和权限
*/
    @RequestMapping("/quanxian")
    public String Ask(Model model) {
        List<User> findUser = userRepository.findAll();
        List<Role> findRole = roleRepository.findAll();
        List<Authority> findAuth = authorityRepository.findAll();
        model.addAttribute("findUser", findUser);
        model.addAttribute("findRole", findRole);
        model.addAttribute("findAuth", findAuth);
        return "/quanxian";
    }
    /*
关联前端
*/
    @RequestMapping("/user")
    public String user(@PathParam("userId") Integer userId,@PathParam("userName") String userName, Model model,Integer pageNum,User user){
        if(userId == null && userName == null){
            if (pageNum == null){ pageNum = 1; }
            else if(pageNum < 1){ pageNum = 1; }
        Pageable pageable = PageRequest.of(pageNum - 1, 6);
        Page<User> list= userRepository.findAll(pageable);
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("findUser", list);
        }else {
            if (pageNum == null){ pageNum = 1; }
            else if(pageNum < 1){ pageNum = 1; }
            Page<User> list= entityService.findAllByUser(pageNum,user);
            model.addAttribute("pageNum", pageNum);
            model.addAttribute("findUser", list);
            model.addAttribute("userId", userId);
            model.addAttribute("userName", userName);
        }
        return "/user";
    }
    @GetMapping("/deletes")
    public String deletes(String fitList,RedirectAttributes attributes,HttpServletResponse response) throws IOException {
        String t = fitList.replaceAll("\\\"","");
        String replaceAll = t .replace("[", "").replace("]","");
        System.out.println(replaceAll);
        String[] str = replaceAll.split(",");
        List<Integer> list= new ArrayList<>();
        for(String l:str) {
            Integer i = Integer.valueOf(l);
            list.add(i);
        }
        entityService.deleteUserRole(list);
        entityService.deleteUser(list);
        response.getWriter().write("success");
        attributes.addFlashAttribute("message", "操作成功");
        return "redirect:/user";

    }
    @GetMapping("/deleteByUserName/{userName}")
    public ModelAndView deleteByUserName(@PathVariable("userName") String userName){
        System.out.println("username="+userName);
        entityService.deleteByUserName(userName);
        return  new ModelAndView("redirect:/user");
    }

    @GetMapping("updateUserInfo/{userId}")
    public ModelAndView updateUserInfo(@PathVariable("userId") Integer userId, Model model) {
        model.addAttribute("userInfo",userRepository.findAllByUserId(userId));
        return new ModelAndView("/updateUserInfo","model",model);
    }
    @RequestMapping("/updateUserInfo")
    public String updateUserInfo(@RequestParam(value = "userId")Integer userId, @RequestParam(value = "userName")String userName, @RequestParam(value = "roleName")String roleName){
        User use = new User();
        Role role = roleRepository.findAllByRoleName(roleName).get(0);
        use.setUserId(userId);
        use.setUserName(userName);
        use.setRoles(new ArrayList<>());
        use.getRoles().add(role);
        userRepository.save(use);
        return "redirect:/user";
    }
    @RequestMapping("/role")
    public String role(@PathParam("roleId") Integer roleId,@PathParam("roleName") String roleName, Model model,Integer pageNum,Role role){
        if(roleId == null && roleName == null){
            if (pageNum == null){ pageNum = 1; }
            else if(pageNum < 1){ pageNum = 1; }
            Pageable pageable = PageRequest.of(pageNum - 1, 6);
            Page<Role> list= roleRepository.findAll(pageable);
            model.addAttribute("pageNum", pageNum);
            model.addAttribute("findRole", list);
        }else {
            if (pageNum == null){ pageNum = 1; }
            else if(pageNum < 1){ pageNum = 1; }
            Page<Role> list= entityService.findAllByRole(pageNum,role);
            model.addAttribute("pageNum", pageNum);
            model.addAttribute("findRole", list);
            model.addAttribute("roleId", roleId);
            model.addAttribute("roleName", roleName);
        }
        return "/role";
    }
    @GetMapping("/deletess")
    public String deletess(String fitList,RedirectAttributes attributes,HttpServletResponse response) throws IOException {
        String t = fitList.replaceAll("\\\"","");
        String replaceAll = t .replace("[", "").replace("]","");
        System.out.println(replaceAll);
        String[] str = replaceAll.split(",");
        List<Integer> list= new ArrayList<>();
        for(String l:str) {
            Integer i = Integer.valueOf(l);
            list.add(i);
        }
        entityService.deleteRoleAuth(list);
        entityService.deleteRole(list);
        response.getWriter().write("success");
        attributes.addFlashAttribute("message", "操作成功");
        return "redirect:/role";
    }
    @GetMapping("/deleteByRoleId/{roleId}")
    public ModelAndView deleteByRoleId(@PathVariable("roleId") Integer roleId){
        entityService.deleteByRoleId(roleId);
        return  new ModelAndView("redirect:/role");
    }
    @GetMapping("updateRoleInfo/{roleId}")
    public ModelAndView updateRoleInfo(@PathVariable("roleId") Integer roleId, Model model) {
        model.addAttribute("roleInfo",roleRepository.findAllByRoleId(roleId));
        return new ModelAndView("/updateRoleInfo","model",model);
    }
    @RequestMapping("/updateRoleInfo")
    public String updateRoleInfoThird(@RequestParam("roleId")Integer roleId, @RequestParam("roleName")String roleName, @RequestParam("authName")String authName){
        Role role = new Role();
        Authority authority = authorityRepository.findAllByAuthorityName(authName).get(0);
        role.setRoleName(roleName);
        role.setRoleId(roleId);
        role.setAuthorities(new ArrayList<>());
        role.getAuthorities().add(authority);
        roleRepository.save(role);
        return "redirect:/role";
    }

    

}


