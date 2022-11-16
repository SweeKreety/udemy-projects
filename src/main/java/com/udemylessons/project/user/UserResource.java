package com.udemylessons.project.user;


import org.hibernate.EntityMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderDsl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserResource {

    @Autowired
    private UserDaoService service;

    //retrieveAllUsers
    @GetMapping("/users")
    public List<User> retrieveAllUsers() {
        return service.findAll();
    }

    //retrieveUser (int id)
    @GetMapping("/users/{id}")
    public EntityModel<User> retrieveUser(@PathVariable int id) {
        User user = service.findOne(id);

        if (user == null) {
            throw new UserNotFoundException("id-" + id);
        }
            EntityModel<User> model = EntityModel.of(user);
            WebMvcLinkBuilder linkTo=
                    linkTo(methodOn(this.getClass()).retrieveAllUsers());
            model.add(linkTo.withRel("all-users"));
            return model;
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@Validated @RequestBody User user, UriComponentsBuilder builder){
        User savedUser= service.save(user);

        //returns status code back 201 created and location
        URI location = builder.path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).body(Collections.singletonMap("id", savedUser));
    }
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id){
        User user =  service.deleteById(id);
        if(user==null){
            throw new UserNotFoundException("id-" + id);
        }
    }
}
