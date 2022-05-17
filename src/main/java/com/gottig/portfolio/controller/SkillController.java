package com.gottig.portfolio.controller;

import com.gottig.portfolio.model.Skill;
import com.gottig.portfolio.service.crudinterface.CRUDServiceInterface;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("skill")
public class SkillController {
    
    private final String CROSSORIGIN = "http://localhost:4200";
    
    @Autowired
    private CRUDServiceInterface<Skill> skillService;
    
    @GetMapping("/list")
    @CrossOrigin(origins = CROSSORIGIN)
    @ResponseBody
    public List<Skill> getAll(){
        return skillService.getAll();
    }
    
    @PostMapping("/create")
    @CrossOrigin(origins = CROSSORIGIN)
    @ResponseBody
    public String create(@RequestBody Skill skill){
        skillService.create(skill);
        return "Skill created";
    }
    
    @DeleteMapping("/delete")
    @CrossOrigin(origins = CROSSORIGIN)
    @ResponseBody
    public String delete(@RequestBody Skill skill){  
        Long id = skill.getSkillId();
        System.out.println(id);
        skillService.delete(id);
        return "Skill deleted";
    }
    
    @PutMapping("/update")
    @CrossOrigin(origins = CROSSORIGIN)
    @ResponseBody
    public String update(@RequestBody Skill skill){  
        Long id = skill.getSkillId();
        if(id != null){
            Skill currentSkill;
            currentSkill = (Skill)skillService.getOne(id);
            if(currentSkill != null){
                skillService.create(skill); // Solo existe el método save() para crear y para modificar.  
                return "Skill updated";
            }else{
                return "Skill not found";
            }
        }else{
            return "Id missing";
        }
    }
    
}
