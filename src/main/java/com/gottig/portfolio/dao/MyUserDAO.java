package com.gottig.portfolio.dao;

import com.gottig.portfolio.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MyUserDAO extends JpaRepository<MyUser, Long>{
    
    //@Query("SELECT userName, userMail, userPassword, userRole FROM MyUser WHERE userName=\'gottigjavier\'")
    MyUser findUserByUsername(String username);
    
}
