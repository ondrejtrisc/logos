package cz.ondrejtrisc.logos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ondrejtrisc.logos.model.Evaluation;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByQueryContaining(String query);
    List<Evaluation> findByCommentContaining(String text);
}