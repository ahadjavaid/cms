package com.abdulahad.cms.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.abdulahad.cms.dto.ContactCreateDto;
import com.abdulahad.cms.dto.ContactDto;
import com.abdulahad.cms.dto.ContactUpdateDto;
import com.abdulahad.cms.entity.Contact;
import com.abdulahad.cms.exceptions.CMSNotFoundException;
import com.abdulahad.cms.exceptions.ContactAlreadyExistException;
import com.abdulahad.cms.exceptions.UnauthorizedException;
import com.abdulahad.cms.repository.ContactRepository;
import com.abdulahad.cms.service.ContactService;

import jakarta.transaction.Transactional;

@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Transactional
    @Override
    public ContactDto createContact(ContactCreateDto createDto, int userId) {
        log.info("Attempting to create contact for user ID: {}", userId);
        if(contactRepository.findByEmailIgnoreCaseAndUserId(createDto.getEmail(), userId).isPresent() ||
                contactRepository.findByPhoneNumberAndUserId(createDto.getPhoneNumber(), userId).isPresent()) {
            log.warn("Contact creation failed for user ID {}: Email {} or phone {} already exists for this user.", userId, createDto.getEmail(), createDto.getPhoneNumber());
            throw new ContactAlreadyExistException("Contact with this email or phone number already exist.");
        }

        Contact contact = new Contact();
        contact.setUserId(userId);
        contact.setFirstName(createDto.getFirstName());
        contact.setLastName(createDto.getLastName());
        contact.setEmail(createDto.getEmail());
        contact.setPhoneNumber(createDto.getPhoneNumber());
        contact = contactRepository.save(contact);
        log.info("Contact created successfully with ID: {} for user ID: {}", contact.getId(), userId);
        return makeContactDto(contact);
    }

    @Transactional
    @Override
    public ContactDto updateContact(ContactUpdateDto updateDto, int contactId, int userId) {
        log.info("Attempting to update contact ID: {} for user ID: {}", contactId, userId);
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> {
                    log.warn("Contact update failed: Contact not found with ID: {}", contactId);
                    return new CMSNotFoundException("Contact not found with id: " + contactId);
                });

        if (contact.getUserId() != userId) {
            log.warn("Contact update failed: User ID {} does not have permission to update contact ID {}", userId, contactId);
            throw new UnauthorizedException("User does not have permission to update this contact.");
        }

        contact.setFirstName(updateDto.getFirstName());
        contact.setLastName(updateDto.getLastName());
        contact.setEmail(updateDto.getEmail());
        contact.setPhoneNumber(updateDto.getPhoneNumber());
        contact = contactRepository.save(contact);
        log.info("Contact updated successfully with ID: {} for user ID: {}", contact.getId(), userId);
        return makeContactDto(contact);
    }

    @Override
    public List<ContactDto> findAllByUserId(int userId) {
        log.debug("Fetching all contacts for user ID: {}", userId);
        List<Contact> contacts = contactRepository.findAllByUserId(userId);
        List<ContactDto> contactDtos = contacts.stream()
                .map(this::makeContactDto)
                .collect(Collectors.toList());
        log.debug("Found {} contacts for user ID: {}", contactDtos.size(), userId);
        return contactDtos;
    }


    @Override
    @Transactional
    public void deleteByIdAndUserId(int contactId, int userId) {
        log.info("Attempting to delete contact ID: {} for user ID: {}", contactId, userId);
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> {
                     log.warn("Contact deletion failed: Contact not found with ID: {}", contactId);
                     return new CMSNotFoundException("Contact not found with id: " + contactId);
                });
        if (contact.getUserId() != userId) {
            log.warn("Contact deletion failed: User ID {} does not have permission to delete contact ID {}", userId, contactId);
            throw new UnauthorizedException("User does not have permission to delete this contact.");
        }
        contactRepository.deleteById(contactId);
        log.info("Contact deleted successfully with ID: {} by user ID: {}", contactId, userId);
    }

    @Override
    public List<ContactDto> searchContactsByUserId(String query, int userId) {
        log.debug("Searching contacts for user ID: {} with query: '{}'", userId, query);
        List<Contact> contacts = contactRepository.searchByUserIdAndQuery(userId, query.toLowerCase());
        List<ContactDto> contactDtos = contacts.stream()
            .map(this::makeContactDto)
            .collect(Collectors.toList());
        log.debug("Found {} contacts matching query '{}' for user ID: {}", contactDtos.size(), query, userId);
        return contactDtos;
    }

    private ContactDto makeContactDto(Contact contact) {
        return new ContactDto(
                contact.getId(),
                contact.getUserId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getEmail(),
                contact.getPhoneNumber()
        );
    }
}
