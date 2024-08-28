package com.ust.Survey_api.repository;

import com.ust.Survey_api.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email,Long> {
    List<Email> findBySurveyid(Long surveyid);
}
