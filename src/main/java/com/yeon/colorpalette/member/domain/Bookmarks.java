package com.yeon.colorpalette.member.domain;

import java.util.ArrayList;
import java.util.List;

import com.yeon.colorpalette.exception.palette.BookmarkAlreadyExistsException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Embeddable
public class Bookmarks {

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Bookmark> bookmarks = new ArrayList<>();

	public boolean addBookmark(Bookmark bookmark) {
		if (bookmarks.stream().filter(b -> b.getPalette().getId().equals(bookmark.getPalette().getId())).findAny().isEmpty()) {
			return bookmarks.add(bookmark);
		} else {
			throw new BookmarkAlreadyExistsException();
		}
	}

	public boolean removeBookmark(Long bookmarkId) {
		return bookmarks.removeIf(b -> b.getId().equals(bookmarkId));
	}

}
