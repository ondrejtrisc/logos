package cz.ondrejtrisc.logos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.ondrejtrisc.logos.model.Chart;

public interface ChartRepository extends JpaRepository<Chart, Long> {
    List<Chart> findByFunctionCodeContaining(String text);
    List<Chart> findByCommentContaining(String text);
}