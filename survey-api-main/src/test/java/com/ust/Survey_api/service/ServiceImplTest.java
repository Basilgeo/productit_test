package com.ust.Survey_api.service;


import com.ust.Survey_api.exception.SetNotFoundException;
import com.ust.Survey_api.feign.AssessmentClient;
import com.ust.Survey_api.feign.FullResponse;
import com.ust.Survey_api.feign.SetNameDto;
import com.ust.Survey_api.feign.SurveyRequestDto;
import com.ust.Survey_api.model.Email;
import com.ust.Survey_api.model.Status;
import com.ust.Survey_api.model.Survey;
import com.ust.Survey_api.repository.EmailRepository;
import com.ust.Survey_api.repository.SurveyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceImplTest {

    @Mock
    private AssessmentClient client;

    @Mock
    private SurveyRepository repo;

    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private ServiceImpl service;

    private Survey survey;
    private SurveyRequestDto surveyRequestDto;
    private FullResponse fullResponse;
    private List<SetNameDto> setNameDtoList;

    @BeforeEach
    void setUp() {
        survey = new Survey(1L, "John Doe", "Tech Corp", 1L, LocalDate.now(), LocalDate.now().plusDays(30), new ArrayList<>());
        surveyRequestDto = new SurveyRequestDto(1L, "John Doe", "Tech Corp", 1L);
        fullResponse = new FullResponse(1L, 1L, "John Doe", "Tech Corp", 1L, LocalDate.now(), LocalDate.now().plusDays(30), new ArrayList<>());
        setNameDtoList = new ArrayList<>();
        setNameDtoList.add(new SetNameDto(1L, "Question 1", new ArrayList<>()));
    }

    @Test
    void testAddSurvey() {
        when(repo.save(any(Survey.class))).thenReturn(survey);
        when(client.getSet(anyLong())).thenReturn(ResponseEntity.of(Optional.of(setNameDtoList)));

        FullResponse response = service.addSurvey(surveyRequestDto);

        assertNotNull(response);
        assertEquals(survey.getSurveyid(), response.getSurveyid());
        assertEquals(survey.getRequestor(), response.getRequestor());
        assertEquals(survey.getCompanyName(), response.getCompanyName());
        assertEquals(survey.getSetid(), response.getSetId());
        verify(repo, times(1)).save(any(Survey.class));
    }

    @Test
    void testGetSurveys() {
        List<Survey> surveys = new ArrayList<>();
        surveys.add(survey);

        when(repo.findAll()).thenReturn(surveys);
        when(client.getSet(anyLong())).thenReturn(ResponseEntity.of(Optional.of(setNameDtoList)));

        List<FullResponse> responseList = service.getSurveys();

        assertNotNull(responseList);
        assertFalse(responseList.isEmpty());
        assertEquals(survey.getSurveyid(), responseList.get(0).getSurveyid());
        verify(repo, times(1)).findAll();
    }

    @Test
    void testGetSurveyById() {
        when(repo.findBySurveyid(anyLong())).thenReturn(survey);
        when(client.getSet(anyLong())).thenReturn(ResponseEntity.of(Optional.of(setNameDtoList)));

        FullResponse response = service.getSurveyById(1L);

        assertNotNull(response);
        assertEquals(survey.getSurveyid(), response.getSurveyid());
        verify(repo, times(1)).findBySurveyid(anyLong());
    }

    @Test
    void testAddEmails() {
        List<String> emails = List.of("test1@example.com", "test2@example.com");
        List<Email> emailList = new ArrayList<>();

        when(repo.findBySurveyid(anyLong())).thenReturn(survey);
        when(emailRepository.saveAll(anyList())).thenReturn(emailList);

        List<Email> response = service.addEmails(1L, emails);

        assertNotNull(response);
        verify(repo, times(1)).findBySurveyid(anyLong());
        verify(emailRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testGetEmails() {
        List<Email> emails = new ArrayList<>();
        emails.add(new Email(1L, "test@example.com", 1L, Status.PENDING));

        when(emailRepository.findBySurveyid(anyLong())).thenReturn(emails);

        List<Email> response = service.getEmails(1L);

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(emails.get(0).getEmail(), response.get(0).getEmail());
        verify(emailRepository, times(1)).findBySurveyid(anyLong());
    }

    @Test
    void testAddSurvey_SetNotFoundException() {
        when(client.getSet(anyLong())).thenThrow(new SetNotFoundException("Set not found"));

        assertThrows(SetNotFoundException.class, () -> service.addSurvey(surveyRequestDto));
        verify(repo, never()).save(any(Survey.class));
    }

    @Test
    void testGetSurveyById_SetNotFoundException() {
        when(repo.findBySurveyid(anyLong())).thenReturn(null);

        assertThrows(SetNotFoundException.class, () -> service.getSurveyById(1L));
        verify(client, never()).getSet(anyLong());
    }

    @Test
    void testAddEmails_SetNotFoundException() {
        when(repo.findBySurveyid(anyLong())).thenReturn(null);

        assertThrows(SetNotFoundException.class, () -> service.addEmails(1L, List.of("test@example.com")));
        verify(emailRepository, never()).saveAll(anyList());
    }
}
