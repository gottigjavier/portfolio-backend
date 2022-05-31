package com.gottig.portfolio.jwtsecurity.jwtcontroller;

import com.gottig.portfolio.dto.dtomodel.MyUserDTO;
import com.gottig.portfolio.dto.mapperinteface.CommonMapper;
import com.gottig.portfolio.jwtsecurity.jwtutil.JwtRequest;
import com.gottig.portfolio.jwtsecurity.jwtutil.JwtResponse;
import com.gottig.portfolio.jwtsecurity.jwtutil.JwtTokenUtil;
import com.gottig.portfolio.model.MyUser;
import com.gottig.portfolio.service.crudinterface.CRUDServiceInterface;
import static com.mysql.cj.conf.PropertyKey.logger;
import java.util.HashMap;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import static org.hibernate.annotations.common.util.impl.LoggerFactory.logger;
import org.springframework.security.core.AuthenticationException;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    
    private final String CROSSORIGIN = "http://localhost:4200";
    
    @Autowired
    private CRUDServiceInterface<MyUser> userService;
    
    @Autowired
    private CommonMapper<MyUserDTO, MyUser> userMapper;
    

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsService userDetailsService;

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> generateAuthenticationToken(@RequestBody MyUserDTO authenticationRequest)
			throws Exception {
            

		authenticate(authenticationRequest.getUserName(), authenticationRequest.getUserPassword());

		final UserDetails userDetails;
                userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUserName());
                System.out.println("autenyic " + userDetails);

		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(token));
	}

	private void authenticate(String username, String password) throws Exception {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);
                        System.out.println("pasa");
		//try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//		} catch (DisabledException e) {
//			throw new Exception("USER_DISABLED", e);
//		} catch (BadCredentialsException e) {
//			throw new Exception("INVALID_CREDENTIALS", e);
//		}
	}
        
        @PostMapping("/register")
        public boolean saveUser(@RequestBody MyUserDTO userDTO) {
        //Map<String, Object> responseMap = new HashMap<>();
        //MyUser user = new User();
        userDTO.setUserPassword(new BCryptPasswordEncoder().encode(userDTO.getUserPassword()));
        userDTO.setUserRole("USER");
        return userService.create(userMapper.toEntity(userDTO));
    }
        
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody MyUserDTO userDTO) {
        Map<String, Object> responseMap = new HashMap<>();
        //try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getUserName()
                    , userDTO.getUserPassword()));
        System.out.println("dotss " + auth);
            if (auth.isAuthenticated()) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userDTO.getUserName());
                String token = jwtTokenUtil.generateToken(userDetails);
                responseMap.put("error", false);
                responseMap.put("message", "Logged In");
                responseMap.put("token", token);
                System.out.println("responsemap " + responseMap.toString());
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("error", true);
                responseMap.put("message", "Invalid Credentials");
                System.out.println("responsemap " + responseMap.toString());
                return ResponseEntity.status(401).body(responseMap);
            }
//        } catch (DisabledException e) {
//            responseMap.put("error", true);
//            responseMap.put("message", "User is disabled");
//            return ResponseEntity.status(500).body(responseMap);
//        }// catch (BadCredentialsException e) {
//            responseMap.put("error", true);
//            responseMap.put("message", "Invalid Credentials");
//            return ResponseEntity.status(401).body(responseMap);
//        } catch (AuthenticationException e) {
//            responseMap.put("error", true);
//            responseMap.put("message", "Something went wrong");
//            return ResponseEntity.status(500).body(responseMap);
//        }
    }

}