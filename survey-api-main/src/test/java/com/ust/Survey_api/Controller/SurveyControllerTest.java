package com.ust.Survey_api.Controller;


import com.ust.Survey_api.controller.SurveyController;
import com.ust.Survey_api.exception.SetNotFoundException;
import com.ust.Survey_api.feign.FullResponse;
import com.ust.Survey_api.feign.SurveyRequestDto;
import com.ust.Survey_api.model.Email;
import com.ust.Survey_api.model.Survey;
import com.ust.Survey_api.repository.SurveyRepository;
import com.ust.Survey_api.service.SurveyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SurveyControllerTest {

    @InjectMocks
    private SurveyController surveyController;

    @Mock
    private SurveyService surveyService;

    @Mock
    private SurveyRepository surveyRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddSurvey_Success() {
        SurveyRequestDto surveyRequest = new SurveyRequestDto(1L, "Requestor", "CompanyName", 1L);
        FullResponse surveyResponse = new FullResponse(1L, 1L, "Requestor", "CompanyName", 1L, null, null, Collections.emptyList());

        when(surveyService.addSurvey(any(SurveyRequestDto.class))).thenReturn(surveyResponse);

        ResponseEntity<FullResponse> response = surveyController.addSurvey(surveyRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getSurveyid());
    }

    @Test
    public void testAddSurvey_SetNotFoundException() {
        SurveyRequestDto surveyRequest = new SurveyRequestDto(1L, "Requestor", "CompanyName", 1L);

        when(surveyService.addSurvey(any(SurveyRequestDto.class))).thenReturn(null);

        SetNotFoundException exception = assertThrows(SetNotFoundException.class, () -> {
            surveyController.addSurvey(surveyRequest);
        });

        assertEquals("Set name not found.", exception.getMessage());
    }

    @Test
    public void testGetSurveys() {
        FullResponse surveyResponse = new FullResponse(1L, 1L, "Requestor", "CompanyName", 1L, null, null, Collections.emptyList());

        when(surveyService.getSurveys()).thenReturn(Arrays.asList(surveyResponse));

        ResponseEntity<List<FullResponse>> response = surveyController.getSurveys();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getSurveyid());
    }

    @Test
    public void testGetSurveyById_Success() {
        FullResponse surveyResponse = new FullResponse(1L, 1L, "Requestor", "CompanyName", 1L, null, null, Collections.emptyList());

        when(surveyService.getSurveyById(anyLong())).thenReturn(surveyResponse);

        ResponseEntity<?> response = surveyController.getSurveyById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof FullResponse);
        assertEquals(1L, ((FullResponse) response.getBody()).getSurveyid());
    }

    @Test
    public void testGetSurveyById_SetNotFoundException() {
        when(surveyService.getSurveyById(anyLong())).thenReturn(null);

        SetNotFoundException exception = assertThrows(SetNotFoundException.class, () -> {
            surveyController.getSurveyById(1L);
        });

        assertEquals("Invalid surveyId", exception.getMessage());
    }

    @Test
    public void testAddEmails_Success() {
        List<String> emails = Arrays.asList("test@example.com", "test2@example.com");
        Survey survey = new Survey(1L, "Requestor", "CompanyName", 1L, null, null, null);

        when(surveyRepository.findBySurveyid(anyLong())).thenReturn(survey);
        when(surveyService.addEmails(anyLong(), anyList())).thenReturn(Arrays.asList(new Email(), new Email()));

        ResponseEntity<List<Email>> response = surveyController.addEmails(1L, emails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void testAddEmails_SetNotFoundException() {
        List<String> emails = Arrays.asList("test@example.com", "test2@example.com");

        when(surveyRepository.findBySurveyid(anyLong())).thenReturn(null);

        SetNotFoundException exception = assertThrows(SetNotFoundException.class, () -> {
            surveyController.addEmails(1L, emails);
        });

        assertEquals("surveyId not found".trim(), exception.getMessage().trim());
    }
    @Test
    public void testGetEmails_Success() {
        Survey survey = new Survey(1L, "Requestor", "CompanyName", 1L, null, null, null);

        when(surveyRepository.findBySurveyid(anyLong())).thenReturn(survey);
        when(surveyService.getEmails(anyLong())).thenReturn(Arrays.asList(new Email(), new Email()));

        ResponseEntity<List<Email>> response = surveyController.getEmails(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void testGetEmails_SetNotFoundException() {
        when(surveyRepository.findBySurveyid(anyLong())).thenReturn(null);

        SetNotFoundException exception = assertThrows(SetNotFoundException.class, () -> {
            surveyController.getEmails(1L);
        });

        assertEquals("surveyId not found", exception.getMessage());
    }
}

