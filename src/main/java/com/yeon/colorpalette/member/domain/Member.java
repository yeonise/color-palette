package com.yeon.colorpalette.member.domain;

import com.yeon.colorpalette.auth.domain.Provider;
import com.yeon.colorpalette.auth.domain.Role;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
public class Member {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;
	private String nickname;
	private String password;

	@Enumerated(EnumType.STRING)
	private Provider provider;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Embedded
	private Bookmarks bookmarks;

	private boolean isDeleted;

	public boolean registerBookmark(Bookmark bookmark) {
		return this.bookmarks.addBookmark(bookmark);
	}

	public boolean unregisterBookmark(Long bookmarkId) {
		return this.bookmarks.removeBookmark(bookmarkId);
	}

}
