package com.yeon.colorpalette.palette.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yeon.colorpalette.palette.domain.Palette;

public interface PaletteRepository extends JpaRepository<Palette, Long> {
}
