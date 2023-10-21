package com.yeon.colorpalette.palette.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yeon.colorpalette.palette.domain.Palette;

public interface PaletteRepository extends JpaRepository<Palette, Long> {

	boolean existsBySignature(String signature);

	@Modifying
	@Query(value = "UPDATE Palette p SET p.isDeleted = true WHERE id = :id ")
	int deleteSoftlyById(long id);

}
