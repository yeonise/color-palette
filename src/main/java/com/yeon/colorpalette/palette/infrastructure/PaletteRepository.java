package com.yeon.colorpalette.palette.infrastructure;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yeon.colorpalette.palette.domain.Palette;
import com.yeon.colorpalette.palette.domain.Tag;

public interface PaletteRepository extends JpaRepository<Palette, Long> {

	boolean existsBySignature(String signature);

	Optional<Palette> findByIdAndIsDeleted(long id, boolean isDeleted);

	@Query(value = "SELECT palette FROM Palette palette "
		+ "LEFT JOIN FETCH palette.member "
		+ "LEFT JOIN FETCH palette.tag "
		+ "WHERE (:color IS NULL OR :color IN (palette.color1, palette.color2, palette.color3, palette.color4)) "
		+ "AND (:tag IS NULL OR palette.tag = :tag) "
		+ "AND palette.isDeleted = false ")
	Slice<Palette> findSlice(Pageable pageable, String color, Tag tag);

	@Modifying
	@Query(value = "UPDATE Palette p SET p.isDeleted = true WHERE id = :id ")
	int deleteSoftlyById(long id);

}
