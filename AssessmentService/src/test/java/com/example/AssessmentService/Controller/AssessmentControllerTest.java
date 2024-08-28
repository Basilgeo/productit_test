package com.example.AssessmentService.Controller;


import com.example.AssessmentService.controller.AssessmentController;
import com.example.AssessmentService.dto.AssessmentDTO;
import com.example.AssessmentService.dto.AnswerDTO;
import com.example.AssessmentService.model.Assessment;
import com.example.AssessmentService.model.Question;
import com.example.AssessmentService.service.AssessmentService;
import com.example.AssessmentService.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AssessmentControllerTest {

    @Mock
    private AssessmentService assessmentService;

    @InjectMocks
    private AssessmentController assessmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAssessments() {
        List<Assessment> mockAssessments = Arrays.asList(new Assessment(), new Assessment());
        when(assessmentService.getAllAssessments()).thenReturn(mockAssessments);

        ResponseEntity<List<Assessment>> response = assessmentController.getAllAssessments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockAssessments, response.getBody());
    }

    @Test
    void testCreateAssessment() {
        AssessmentDTO mockAssessmentDTO = new AssessmentDTO();
        Assessment mockAssessment = new Assessment();
        when(assessmentService.createAssessment(any(AssessmentDTO.class))).thenReturn(mockAssessment);

        ResponseEntity<?> response = assessmentController.createAssessment(mockAssessmentDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockAssessment, response.getBody());
    }

    @Test
    void testGetQuestionsBySetName() {
        List<Question> mockQuestions = Arrays.asList(new Question(), new Question());
        when(assessmentService.getQuestionsSetName("Java")).thenReturn(mockQuestions);

        ResponseEntity<List<Question>> response = assessmentController.getQuestionsBySetName("Java");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockQuestions, response.getBody());
    }

    @Test
    void testGetQuestionsBySetid() {
        List<Question> mockQuestions = Arrays.asList(new Question(), new Question());
        when(assessmentService.getQuestionsSetId(1L)).thenReturn(mockQuestions);

        ResponseEntity<List<Question>> response = assessmentController.getQuestionsBySetid(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockQuestions, response.getBody());
    }

    @Test
    void testUpdateQuestion() {
        List<AnswerDTO> mockAnswers = Arrays.asList(new AnswerDTO("value1", "suggestion1"), new AnswerDTO("value2", "suggestion2"));
        when(assessmentService.updateQuestion(anyLong(), anyLong(), anyList())).thenReturn("Question updated");

        ResponseEntity<String> response = assessmentController.updateQuestion(1L, 1L, mockAnswers);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Question updated", response.getBody());
    }

    @Test
    void testDeleteQuestion() {
        Map<String, String> mockResponse = Map.of("status", "deleted");
        when(assessmentService.deleteQuestion(1L, 1L)).thenReturn(mockResponse);

        ResponseEntity<Map<String, String>> response = assessmentController.deleteQuestion(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testFetchQuestion() {
        Optional<Question> mockQuestion = Optional.of(new Question());
        when(assessmentService.fetchques(1L)).thenReturn(mockQuestion);

        Optional<Question> response = assessmentController.fetchques(1L);

        assertTrue(response.isPresent());
        assertEquals(mockQuestion.get(), response.get());
    }

    @Test
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        ResponseEntity<String> response = assessmentController.handleResourceNotFoundException(exception);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Resource not found", response.getBody());
    }

    @Test
    void testHandleDataIntegrityException() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Data integrity violation");

        ResponseEntity<String> response = assessmentController.handleDataIntegrityException(exception);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("set alredy exists", response.getBody());
    }
}
