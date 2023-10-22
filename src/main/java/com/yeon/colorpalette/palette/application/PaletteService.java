package com.yeon.colorpalette.palette.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yeon.colorpalette.exception.member.MemberNotFoundException;
import com.yeon.colorpalette.exception.palette.InvalidPaletteCreatorException;
import com.yeon.colorpalette.exception.palette.PaletteAlreadyExistsException;
import com.yeon.colorpalette.exception.palette.PaletteNotFoundException;
import com.yeon.colorpalette.exception.palette.TagNotFoundException;
import com.yeon.colorpalette.member.domain.Member;
import com.yeon.colorpalette.member.infrastructure.MemberRepository;
import com.yeon.colorpalette.palette.application.request.PaletteCreateServiceRequest;
import com.yeon.colorpalette.palette.application.response.PaletteCreateResponse;
import com.yeon.colorpalette.palette.application.response.PaletteReadResponse;
import com.yeon.colorpalette.palette.application.response.PaletteReadSliceResponse;
import com.yeon.colorpalette.palette.domain.Palette;
import com.yeon.colorpalette.palette.domain.Tag;
import com.yeon.colorpalette.palette.infrastructure.PaletteRepository;
import com.yeon.colorpalette.palette.infrastructure.TagRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PaletteService {

	private final PaletteRepository paletteRepository;
	private final MemberRepository memberRepository;
	private final TagRepository tagRepository;

	@Transactional
	public PaletteCreateResponse create(PaletteCreateServiceRequest request) {
		String signature = generateSignature(request);
		checkPaletteAlreadyExists(signature);

		Member member = findMember(request.getMemberId());
		Tag tag = findTag(request.getTagId());

		return PaletteCreateResponse.from(paletteRepository.save(createPalette(request, signature, member, tag)));
	}

	@Transactional
	public int delete(Long paletteId, Long memberId) {
		Palette palette = findPalette(paletteId);

		if (palette.getMember().getId().equals(memberId)) {
			return paletteRepository.deleteSoftlyById(palette.getId());
		} else {
			throw new InvalidPaletteCreatorException();
		}
	}

	public PaletteReadSliceResponse read(Pageable pageable, String color, Long tag) {
		Slice<Palette> palettes = paletteRepository.findSlice(pageable, color, findTag(tag));

		return new PaletteReadSliceResponse(palettes.hasNext(),
			palettes.getContent().stream().map(PaletteReadResponse::from).collect(Collectors.toList()));
	}

	private String generateSignature(PaletteCreateServiceRequest request) {
		List<String> colors = new ArrayList<>();
		colors.add(request.getColor1());
		colors.add(request.getColor2());
		colors.add(request.getColor3());
		colors.add(request.getColor4());

		Collections.sort(colors);

		return colors.stream().map(String::toUpperCase).collect(Collectors.joining());
	}

	private void checkPaletteAlreadyExists(String signature) {
		if (paletteRepository.existsBySignature(signature)) {
			throw new PaletteAlreadyExistsException();
		}
	}

	private Palette findPalette(Long paletteId) {
		return paletteRepository.findById(paletteId).orElseThrow(PaletteNotFoundException::new);
	}

	private Member findMember(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
	}

	private Tag findTag(Long tagId) {
		return tagId == null ? null : tagRepository.findById(tagId).orElseThrow(TagNotFoundException::new);
	}

	private Palette createPalette(PaletteCreateServiceRequest request, String signature, Member member, Tag tag) {
		return new Palette(null, member,
			request.getColor1(), request.getColor2(), request.getColor3(), request.getColor4(),
			signature, tag, 0, new ArrayList<>(), LocalDateTime.now(), false);
	}

}
