package com.gottig.portfolio.controller;

import com.gottig.portfolio.dto.dtomodel.JobExperienceDTO;
import com.gottig.portfolio.dto.mapperinteface.CommonMapper;
import com.gottig.portfolio.model.JobExperience;
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
@RequestMapping("job-experience")
public class JobExperienceController {
    
    @Autowired
    private CRUDServiceInterface<JobExperience> jobService;
    
    @Autowired
    private CommonMapper<JobExperienceDTO, JobExperience> jobMapper;
    
    @GetMapping("/list")
    @CrossOrigin(origins = "${cross.origin.value}")
    @ResponseBody
    public ResponseEntity getAll(){
        return ResponseEntity.ok(getList());
    }
    
    @GetMapping("/{id}")
    @CrossOrigin(origins = "${cross.origin.value}")
    @ResponseBody
    public ResponseEntity getOne(@PathVariable Long id){
        return ResponseEntity.ok(singleGet(id));
    }
    
    @PostMapping("/create")
    @CrossOrigin(origins = "${cross.origin.value}")
    @ResponseBody
    public ResponseEntity create(@RequestBody JobExperienceDTO jobDTO){
        if(!jobService.create(jobMapper.toEntity(jobDTO))){
            return new ResponseEntity<>("Error: Not Created", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(getList());
    }
    
    @PutMapping("/update")
    @CrossOrigin(origins = "${cross.origin.value}")
    @ResponseBody
    public ResponseEntity update(@RequestBody JobExperienceDTO jobDTO){
        if(!jobService.update(jobMapper.toEntity(jobDTO))){
            return new ResponseEntity<>("Error: Not Updated", HttpStatus.NOT_MODIFIED);
        }
        return ResponseEntity.ok(singleGet(jobDTO.getJobId()));
    }
    
    @PutMapping("/update/list")
    @CrossOrigin(origins = "${cross.origin.value}")
    @ResponseBody
    public ResponseEntity updateList(@RequestBody List<JobExperienceDTO> jobListDTO){
        for(JobExperienceDTO jobDTO : jobListDTO){
         jobService.update(jobMapper.toEntity(jobDTO));   
        }
        return ResponseEntity.ok(getList());
    }
    
    @DeleteMapping("/delete/{id}")
    @CrossOrigin(origins = "${cross.origin.value}")
    @ResponseBody
    public ResponseEntity delete(@PathVariable Long id){  
        if(!jobService.delete(id)){
            return new ResponseEntity<>("Error: Not Deleted", HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(getList());
    }
    
    
    // Truco para que sienpre devuelva statusCode y statusCodeValue en response
    // sino solo devuelve el objeto o lista (body) en un 200
    public ResponseEntity getList(){
        List<JobExperienceDTO> list = jobMapper.toDtoAll(jobService.getAll());
        if(list.size()<1){
            return new ResponseEntity<>("Error: List Empty", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    public ResponseEntity singleGet(Long id){
        JobExperienceDTO obj= jobMapper.toDto(jobService.getOne(id));
        if(obj == null){
            return new ResponseEntity<>("Error: Not Found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
    
}
