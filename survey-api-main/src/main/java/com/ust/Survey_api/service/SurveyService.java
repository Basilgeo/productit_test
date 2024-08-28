package com.ust.Survey_api.service;

import com.ust.Survey_api.feign.FullResponse;
import com.ust.Survey_api.feign.SetNameDto;
import com.ust.Survey_api.feign.SurveyRequestDto;
import com.ust.Survey_api.model.Email;
import com.ust.Survey_api.model.Survey;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SurveyService {

    FullResponse addSurvey(SurveyRequestDto survey);

    List<FullResponse> getSurveys();

    FullResponse getSurveyById(Long surveyId);

    List<Email> addEmails(Long surveyId, List<String> emails);

    List<Email> getEmails(Long surveyId);


//    List<SetNameDto> getSet(String setName);
}
