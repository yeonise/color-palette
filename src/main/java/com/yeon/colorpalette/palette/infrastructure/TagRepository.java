package com.yeon.colorpalette.palette.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yeon.colorpalette.palette.domain.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
