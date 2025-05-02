package com.abdulahad.cms.service;

import java.util.List;

import com.abdulahad.cms.dto.ContactCreateDto;
import com.abdulahad.cms.dto.ContactDto;
import com.abdulahad.cms.dto.ContactUpdateDto;

public interface ContactService {
    ContactDto createContact(ContactCreateDto createDto, int userId);
    ContactDto updateContact(ContactUpdateDto updateDto, int contactId, int userId);
    List<ContactDto> findAllByUserId(int id);
    void deleteByIdAndUserId(int id, int userId);
    List<ContactDto> searchContactsByUserId(String query, int userId);
}
