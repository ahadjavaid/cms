package com.abdulahad.cms.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abdulahad.cms.dto.ContactCreateDto;
import com.abdulahad.cms.dto.ContactDto;
import com.abdulahad.cms.dto.ContactUpdateDto;
import com.abdulahad.cms.entity.Contact;
import com.abdulahad.cms.exceptions.CMSNotFoundException;
import com.abdulahad.cms.exceptions.ContactAlreadyExistException;
import com.abdulahad.cms.exceptions.UnauthorizedException;
import com.abdulahad.cms.repository.ContactRepository;
import com.abdulahad.cms.service.impl.ContactServiceImpl;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    private ContactCreateDto createDto;
    private ContactUpdateDto updateDto;
    private Contact contact;
    private ContactDto contactDto;
    private int userId = 1;
    private int contactId = 10;

    @BeforeEach
    void setUp() {
        createDto = new ContactCreateDto();
        createDto.setFirstName("John");
        createDto.setLastName("Doe");
        createDto.setEmail("john.doe@example.com");
        createDto.setPhoneNumber("+1987654321");

        updateDto = new ContactUpdateDto();
        updateDto.setFirstName("John Updated");
        updateDto.setLastName("Doe Updated");
        updateDto.setEmail("john.doe.updated@example.com");
        updateDto.setPhoneNumber("+19876543210");

        contact = new Contact(contactId, userId, "John", "Doe", "john.doe@example.com", "+1987654321");

        contactDto = new ContactDto(contactId, userId, "John", "Doe", "john.doe@example.com", "+1987654321");
    }

    @Test
    void createContact_Success() {
        when(contactRepository.findByEmailIgnoreCaseAndUserId(anyString(), anyInt())).thenReturn(Optional.empty());
        when(contactRepository.findByPhoneNumberAndUserId(anyString(), anyInt())).thenReturn(Optional.empty());
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> {
            Contact saved = invocation.getArgument(0);
            saved.setId(contactId); // Simulate ID generation
            return saved;
        });

        ContactDto result = contactService.createContact(createDto, userId);

        assertNotNull(result);
        assertEquals(contactId, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(createDto.getFirstName(), result.getFirstName());
        assertEquals(createDto.getEmail(), result.getEmail());

        verify(contactRepository, times(1)).findByEmailIgnoreCaseAndUserId(createDto.getEmail(), userId);
        verify(contactRepository, times(1)).findByPhoneNumberAndUserId(createDto.getPhoneNumber(), userId);
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void createContact_AlreadyExists_Email() {
        when(contactRepository.findByEmailIgnoreCaseAndUserId(anyString(), anyInt())).thenReturn(Optional.of(contact));

        assertThrows(ContactAlreadyExistException.class, () -> contactService.createContact(createDto, userId));

        verify(contactRepository, times(1)).findByEmailIgnoreCaseAndUserId(createDto.getEmail(), userId);
        verify(contactRepository, never()).findByPhoneNumberAndUserId(anyString(), anyInt());
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void createContact_AlreadyExists_Phone() {
        when(contactRepository.findByEmailIgnoreCaseAndUserId(anyString(), anyInt())).thenReturn(Optional.empty());
        when(contactRepository.findByPhoneNumberAndUserId(anyString(), anyInt())).thenReturn(Optional.of(contact));

        assertThrows(ContactAlreadyExistException.class, () -> contactService.createContact(createDto, userId));

        verify(contactRepository, times(1)).findByEmailIgnoreCaseAndUserId(createDto.getEmail(), userId);
        verify(contactRepository, times(1)).findByPhoneNumberAndUserId(createDto.getPhoneNumber(), userId);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void updateContact_Success() {
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContactDto result = contactService.updateContact(updateDto, contactId, userId);

        assertNotNull(result);
        assertEquals(contactId, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(updateDto.getFirstName(), result.getFirstName()); // Check updated value
        assertEquals(updateDto.getEmail(), result.getEmail()); // Check updated value

        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(any(Contact.class));
        // Verify fields were updated before save
        assertEquals(updateDto.getFirstName(), contact.getFirstName());
        assertEquals(updateDto.getEmail(), contact.getEmail());
    }

    @Test
    void updateContact_NotFound() {
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        assertThrows(CMSNotFoundException.class, () -> contactService.updateContact(updateDto, contactId, userId));

        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void updateContact_Unauthorized() {
        int wrongUserId = 99;
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact)); // Contact belongs to userId 1

        assertThrows(UnauthorizedException.class, () -> contactService.updateContact(updateDto, contactId, wrongUserId));

        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void findAllByUserId_Success() {
        when(contactRepository.findAllByUserId(userId)).thenReturn(List.of(contact));

        List<ContactDto> results = contactService.findAllByUserId(userId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(contactId, results.get(0).getId());

        verify(contactRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void findAllByUserId_NoContacts() {
        when(contactRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        List<ContactDto> results = contactService.findAllByUserId(userId);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(contactRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void deleteByIdAndUserId_Success() {
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        doNothing().when(contactRepository).deleteById(contactId);

        assertDoesNotThrow(() -> contactService.deleteByIdAndUserId(contactId, userId));

        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).deleteById(contactId);
    }

    @Test
    void deleteByIdAndUserId_NotFound() {
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        assertThrows(CMSNotFoundException.class, () -> contactService.deleteByIdAndUserId(contactId, userId));

        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteByIdAndUserId_Unauthorized() {
        int wrongUserId = 99;
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact)); // Belongs to userId 1

        assertThrows(UnauthorizedException.class, () -> contactService.deleteByIdAndUserId(contactId, wrongUserId));

        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, never()).deleteById(anyInt());
    }

    @Test
    void searchContactsByUserId_Success() {
        String query = "john";
        when(contactRepository.searchByUserIdAndQuery(userId, query.toLowerCase())).thenReturn(List.of(contact));

        List<ContactDto> results = contactService.searchContactsByUserId(query, userId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(contactId, results.get(0).getId());

        verify(contactRepository, times(1)).searchByUserIdAndQuery(userId, query.toLowerCase());
    }

    @Test
    void searchContactsByUserId_NoResults() {
        String query = "nonexistent";
        when(contactRepository.searchByUserIdAndQuery(userId, query.toLowerCase())).thenReturn(Collections.emptyList());

        List<ContactDto> results = contactService.searchContactsByUserId(query, userId);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(contactRepository, times(1)).searchByUserIdAndQuery(userId, query.toLowerCase());
    }
}
