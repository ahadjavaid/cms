package com.abdulahad.cms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abdulahad.cms.dto.ContactCreateDto;
import com.abdulahad.cms.dto.ContactDto;
import com.abdulahad.cms.dto.ContactUpdateDto;
import com.abdulahad.cms.security.JwtUser;
import com.abdulahad.cms.service.ContactService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/contacts")
@Validated
public class ContactController {

    private final ContactService contactService;

    public  ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ContactDto> createContact(@Valid @RequestBody ContactCreateDto contactCreateDto, @AuthenticationPrincipal JwtUser jwtUser) {
        return new ResponseEntity<>(contactService.createContact(contactCreateDto, jwtUser.getUserId()), HttpStatus.CREATED);
    }


    @PutMapping("/{contactId}")
    public ResponseEntity<ContactDto> updateContact(@PathVariable int contactId,@Valid @RequestBody ContactUpdateDto contactUpdateDto, @AuthenticationPrincipal JwtUser jwtUser) {
        return new ResponseEntity<>(contactService.updateContact(contactUpdateDto, contactId, jwtUser.getUserId()), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ContactDto>> findByUser(@AuthenticationPrincipal JwtUser jwtUser) {
        List<ContactDto> contacts = contactService.findAllByUserId(jwtUser.getUserId());
        return new ResponseEntity<>(contacts,HttpStatus.OK);
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> deleteContact(@PathVariable int contactId, @AuthenticationPrincipal JwtUser jwtUser) {
        contactService.deleteByIdAndUserId(contactId, jwtUser.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ContactDto>> searchContacts(@RequestParam String query, @AuthenticationPrincipal JwtUser jwtUser) {
        List<ContactDto> contacts = contactService.searchContactsByUserId(query, jwtUser.getUserId());
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }
}
