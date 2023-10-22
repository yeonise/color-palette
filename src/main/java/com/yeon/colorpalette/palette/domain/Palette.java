package com.yeon.colorpalette.palette.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;

import com.yeon.colorpalette.member.domain.Bookmark;
import com.yeon.colorpalette.member.domain.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
public class Palette {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	private String color1;
	private String color2;
	private String color3;
	private String color4;
	private String signature;

	@ManyToOne(fetch = FetchType.LAZY)
	private Tag tag;

	private int views;

	@OneToMany(mappedBy = "palette", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Bookmark> bookmarks = new ArrayList<>();

	@CreatedDate
	private LocalDateTime createdAt;

	private boolean isDeleted;

	public String findCreator() {
		return member.getNickname();
	}

	public String findTagName() {
		return tag == null ? null : tag.getName();
	}

}
