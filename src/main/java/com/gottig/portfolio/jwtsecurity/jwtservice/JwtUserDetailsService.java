package com.gottig.portfolio.jwtsecurity.jwtservice;

import com.gottig.portfolio.dao.MyUserDAO;
import com.gottig.portfolio.model.MyUser;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    
    @Autowired
    MyUserDAO userDao;
    
    MyUser user;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            List<MyUser> userList = userDao.findAll();
            for(MyUser elem: userList){
                if(!elem.getUsername().equalsIgnoreCase(username)){
                    throw new UsernameNotFoundException("User not found with username: " + username);
                }else{
                    user= elem;
                }
            }
            System.out.println("User Jwt " + user);
            return new User(user.getUsername(), user.getUserPassword(),
					new ArrayList<>());
	}

}