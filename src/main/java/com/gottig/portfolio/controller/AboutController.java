package com.gottig.portfolio.controller;

import com.gottig.portfolio.dto.dtomodel.AboutDTO;
import com.gottig.portfolio.dto.mapperinteface.CommonMapper;
import com.gottig.portfolio.model.About;
import com.gottig.portfolio.service.crudinterface.CRUDServiceInterface;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("about")
public class AboutController {
    
    @Autowired
    private CRUDServiceInterface<About> aboutService;
    
    @Autowired
    private CommonMapper<AboutDTO, About> aboutMapper;
        
    @GetMapping("/list")
    @CrossOrigin(origins = "${cross.origin.value}")
    @ResponseBody
    public ResponseEntity getAll(){
            return getList();
    }
    
    @GetMapping("/{id}")
    @CrossOrigin(origins = "${cross.origin.value}")
    @ResponseBody
    public ResponseEntity getOne(@PathVariable Long id){
        return singleGet(id);
    }
    
    @PostMapping
    @CrossOrigin(origins = "${cross.origin.value}")
    @ResponseBody
    public ResponseEntity create(@RequestBody AboutDTO aboutDTO){
        if(!aboutService.create(aboutMapper.toEntity(aboutDTO))){
            return new ResponseEntity<>("About Not Created", HttpStatus.BAD_REQUEST);
        }
        return getList();
    }
    
    @PutMapping
    @CrossOrigin(origins = "${cross.origin.value}")
    @ResponseBody
    public ResponseEntity update(@RequestBody AboutDTO aboutDTO){
        if(!aboutService.update(aboutMapper.toEntity(aboutDTO))){
            return new ResponseEntity<>("About Not Updated", HttpStatus.NOT_MODIFIED);
        }
        return singleGet(aboutDTO.getAboutId());
    }
    
    @PutMapping("/list")
    @CrossOrigin(origins = "${cross.origin.value}")
    @ResponseBody
    public ResponseEntity updateList(@RequestBody List<AboutDTO> aboutListDTO){
        for(AboutDTO aboutDTO : aboutListDTO){
         if(!aboutService.update(aboutMapper.toEntity(aboutDTO))){
             return new ResponseEntity<>("About id: (" + aboutDTO.getAboutId() +  ") Not Updated", HttpStatus.NOT_MODIFIED);
         }   
        }
        return getList();
    }
    
    @DeleteMapping("/{id}")
    @CrossOrigin(origins = "${cross.origin.value}")
    @ResponseBody
    public ResponseEntity delete(@PathVariable Long id){  
        if(!aboutService.delete(id)){
            return new ResponseEntity<>("About Not Deleted", HttpStatus.CONFLICT);
        }
        return getList();
    }
    
    
    public ResponseEntity getList(){
        List<AboutDTO> list = aboutMapper.toDtoAll(aboutService.getAll());
        if(list.size()<1){
            return new ResponseEntity<>("About List Empty", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    public ResponseEntity singleGet(Long id){
        AboutDTO obj= aboutMapper.toDto(aboutService.getOne(id));
        if(obj == null){
            return new ResponseEntity<>("About Not Found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
    
}