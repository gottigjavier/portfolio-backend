package com.gottig.portfolio.jwtsecurity.jwtcontroller;


import com.gottig.portfolio.jwtsecurity.jwtdto.JwtDTO;
import com.gottig.portfolio.jwtsecurity.jwtdto.JwtListDTO;
import com.gottig.portfolio.jwtsecurity.jwtdto.JwtNewUserDTO;
import com.gottig.portfolio.jwtsecurity.jwtdto.JwtUserLoginDTO;
import com.gottig.portfolio.jwtsecurity.jwtenums.JwtRoleName;
import com.gottig.portfolio.jwtsecurity.jwtepf.JwtProvider;
import com.gottig.portfolio.jwtsecurity.jwtmodel.JwtRole;
import com.gottig.portfolio.jwtsecurity.jwtmodel.JwtUser;
import com.gottig.portfolio.jwtsecurity.jwtservice.JwtRoleService;
import com.gottig.portfolio.jwtsecurity.jwtservice.JwtUserService;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {
    
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUserService userService;

    @Autowired
    JwtRoleService roleService;

    @Autowired
    JwtProvider jwtProvider;

    //Espera un json y lo convierte a tipo clase NuevoUsuario
    // Solo el administrador puede crear usuarios
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "${cross.origin.value}")
    @PostMapping("/newuser")
    public ResponseEntity<?> newUser(@Valid @RequestBody JwtNewUserDTO newUserDTO,
                                          BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>("Wrong field or invalid email", HttpStatus.BAD_REQUEST);
        }
        if(userService.existsByUsuario(newUserDTO.getUserName())){
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
        if(userService.existsByEmail(newUserDTO.getEmail())){
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }

        JwtUser user = new JwtUser(newUserDTO.getUserName(),
                newUserDTO.getEmail(), 
                passwordEncoder.encode(newUserDTO.getPassword()));

        Set<JwtRole> roles = new HashSet<>();
        roles.add(roleService.getByRoleName(JwtRoleName.ROLE_USER).get());
        if(newUserDTO.getRoles().contains("admin"))
            roles.add(roleService.getByRoleName(JwtRoleName.ROLE_ADMIN).get());
        user.setRoles(roles);

        userService.save(user);
        
        newUserDTO.setPassword("it is encrypted"); //To not return password to frontend

        return new ResponseEntity<>(newUserDTO, HttpStatus.CREATED);
    }
    
    @CrossOrigin(origins = "${cross.origin.value}")
    @PostMapping("/login")
    public ResponseEntity<JwtDTO> login(@Valid @RequestBody JwtUserLoginDTO loginUserDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new ResponseEntity("Wrong fields", HttpStatus.BAD_REQUEST);
        }
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginUserDTO.getUserName(),
                                loginUserDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        JwtDTO jwtDto = new JwtDTO(jwt, userDetails.getUsername(), userDetails.getAuthorities());
        return new ResponseEntity<>(jwtDto, HttpStatus.OK);
    }
    
    @CrossOrigin(origins = "${cross.origin.value}")
    @PutMapping("/changepass")
    public ResponseEntity changePass(@Valid @RequestBody JwtUserLoginDTO userDTO,
                                          BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>("Wrong fields", HttpStatus.BAD_REQUEST);
        }
        if(!userService.existsByUsuario(userDTO.getUserName())){
            return new ResponseEntity<>("User do not exists", HttpStatus.BAD_REQUEST);
        }

        JwtUser user;
        user= userService.getByUser(userDTO.getUserName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userService.save(user);
        userDTO.setPassword("Password changed");

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }
    
    
    @CrossOrigin(origins = "${cross.origin.value}")
    @GetMapping("/userlist")
    public ResponseEntity userList(){
        
        List<JwtListDTO> listDTO = new ArrayList<>();
        
        List<JwtUser> list = userService.getAll();
        if(!list.isEmpty()){
            for(JwtUser user : list){
                JwtListDTO objDTO = new JwtListDTO();
                objDTO.setUserName(user.getUserName());
                listDTO.add(objDTO);
            }
        }
        return new ResponseEntity<>(listDTO, HttpStatus.OK);
    }
    
    @CrossOrigin(origins = "${cross.origin.value}")
    @PostMapping("/delete")
    public ResponseEntity DelUser(@RequestBody JwtListDTO userDTO){

        JwtUser user;
        user= userService.getByUser(userDTO.getUserName());
        userService.delete(user.getUserId());
        userDTO.setUserName("User Deleted");
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }
}