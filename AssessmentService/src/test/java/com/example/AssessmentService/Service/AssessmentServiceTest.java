package com.example.AssessmentService.Service;

import com.example.AssessmentService.dto.AnswerDTO;
import com.example.AssessmentService.dto.AssessmentDTO;
import com.example.AssessmentService.exception.ResourceNotFoundException;
import com.example.AssessmentService.model.Answer;
import com.example.AssessmentService.model.Assessment;
import com.example.AssessmentService.model.Question;
import com.example.AssessmentService.repo.AnswerRepository;
import com.example.AssessmentService.repo.AssessmentRepository;
import com.example.AssessmentService.repo.QuestionRepository;
import com.example.AssessmentService.service.AssessmentService;
import com.example.AssessmentService.utils.AssessmentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AssessmentServiceTest {

    @InjectMocks
    private AssessmentService assessmentService;

    @Mock
    private AssessmentRepository assessmentRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private AssessmentUtil assessmentUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(assessmentService, "setNameIsInvalid", "Set name is invalid");
        ReflectionTestUtils.setField(assessmentService, "questionIdIsInvalid", "Question id is invalid");
    }

    @Test
    void createAssessment_shouldSaveAssessment() {
        AssessmentDTO assessmentDTO = new AssessmentDTO();
        Assessment assessment = new Assessment();

        when(assessmentUtil.MapToAssessment(any(AssessmentDTO.class))).thenReturn(assessment);
        when(assessmentRepository.save(any(Assessment.class))).thenReturn(assessment);

        Assessment result = assessmentService.createAssessment(assessmentDTO);

        assertNotNull(result);
        verify(assessmentRepository, times(1)).save(assessment);
    }

    @Test
    void getAllAssessments_shouldReturnListOfAssessments() {
        List<Assessment> assessments = new ArrayList<>();
        when(assessmentRepository.findAll()).thenReturn(assessments);

        List<Assessment> result = assessmentService.getAllAssessments();

        assertNotNull(result);
        assertEquals(assessments, result);
    }

    @Test
    void updateQuestion_shouldUpdateQuestion() {
        Long setId = 1L;
        Long questionId = 1L;
        List<AnswerDTO> answerDtos = Collections.singletonList(new AnswerDTO("Answer", "Suggestion"));

        Assessment assessment = new Assessment();
        Question question = new Question();
        question.setQuestionId(questionId);
        assessment.setQuestions(Collections.singletonList(question));

        when(assessmentRepository.findById(setId)).thenReturn(Optional.of(assessment));
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        String result = assessmentService.updateQuestion(setId, questionId, answerDtos);

        assertEquals("Question updated successfully", result);
        verify(assessmentRepository, times(1)).save(assessment);
        verify(questionRepository, times(1)).save(question);
    }

    @Test
    void updateQuestion_shouldThrowExceptionIfAssessmentNotFound() {
        Long setId = 1L;
        when(assessmentRepository.findById(setId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            assessmentService.updateQuestion(setId, 1L, Collections.emptyList());
        });

        assertEquals("Set name is invalid", exception.getMessage());
    }

    @Test
    void updateQuestion_shouldThrowExceptionIfQuestionNotFound() {
        Long setId = 1L;
        Long questionId = 1L;

        Assessment assessment = new Assessment();
        assessment.setQuestions(new ArrayList<>());

        when(assessmentRepository.findById(setId)).thenReturn(Optional.of(assessment));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            assessmentService.updateQuestion(setId, questionId, Collections.emptyList());
        });

        assertEquals("Question id is invalid", exception.getMessage());
    }

    @Test
    void deleteQuestion_shouldDeleteQuestion() {
        Long setId = 1L;
        Long questionId = 1L;

        Question question = new Question();
        question.setQuestionId(questionId);

        Assessment assessment = new Assessment();
        assessment.setQuestions(new ArrayList<>(Collections.singletonList(question)));

        when(assessmentRepository.findById(setId)).thenReturn(Optional.of(assessment));

        Map<String, String> result = assessmentService.deleteQuestion(setId, questionId);

        assertEquals("Question deleted successfully", result.get("message"));
        verify(questionRepository, times(1)).deleteById(questionId);
        verify(assessmentRepository, times(1)).save(assessment);
    }

    @Test
    void deleteQuestion_shouldThrowExceptionIfAssessmentNotFound() {
        Long setId = 1L;
        when(assessmentRepository.findById(setId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            assessmentService.deleteQuestion(setId, 1L);
        });

        assertEquals("Set name is invalid", exception.getMessage());
    }

    @Test
    void deleteQuestion_shouldThrowExceptionIfQuestionNotFound() {
        Long setId = 1L;
        Long questionId = 1L;

        Assessment assessment = new Assessment();
        assessment.setQuestions(new ArrayList<>());

        when(assessmentRepository.findById(setId)).thenReturn(Optional.of(assessment));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            assessmentService.deleteQuestion(setId, questionId);
        });

        assertEquals("question is not found", exception.getMessage());
    }



    @Test
    void getQuestionsSetName_shouldThrowExceptionIfAssessmentNotFound() {
        String setName = "Test Set";
        when(assessmentRepository.findBySetName(setName)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            assessmentService.getQuestionsSetName(setName);
        });

        assertEquals("set name is invalid", exception.getMessage());
    }

    @Test
    void getQuestionsSetId_shouldThrowExceptionIfAssessmentNotFound() {
        Long setId = 1L;
        when(assessmentRepository.findById(setId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            assessmentService.getQuestionsSetId(setId);
        });

        assertEquals("set id is invalid", exception.getMessage());
    }

    @Test
    void fetchques_shouldReturnQuestionIfExists() {
        Long questionId = 1L;
        Question question = new Question();

        when(questionRepository.findByQuestionId(questionId)).thenReturn(Optional.of(question));

        Optional<Question> result = assessmentService.fetchques(questionId);

        assertTrue(result.isPresent());
        assertEquals(question, result.get());
    }

    @Test
    void fetchques_shouldReturnEmptyIfQuestionNotFound() {
        Long questionId = 1L;
        when(questionRepository.findByQuestionId(questionId)).thenReturn(Optional.empty());

        Optional<Question> result = assessmentService.fetchques(questionId);

        assertFalse(result.isPresent());
    }
}
