package com.sparta.greeypeople.like.controller;

import com.sparta.greeypeople.auth.security.UserDetailsImpl;
import com.sparta.greeypeople.common.DataCommonResponse;
import com.sparta.greeypeople.common.StatusCommonResponse;
import com.sparta.greeypeople.like.service.MenuLikesService;
import com.sparta.greeypeople.menu.dto.response.MenuResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores/{storeId}/menus")
public class MenuLikesController {

	private final MenuLikesService menuLikesService;


	/**
	 * 좋아요 등록 기능
	 *
	 * @param storeId    : 가게 ID
	 * @param menuId      : 좋아요 등록 할 메뉴의 Id
	 * @return : 좋아요 등록 메시지 상태 코드 반환
	 */
	@PostMapping("/{menuId}/like")
	public ResponseEntity<StatusCommonResponse> addMenuLike(@PathVariable Long storeId, @PathVariable Long menuId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		menuLikesService.addMenuLike(storeId, menuId, userDetails.getUser());
		StatusCommonResponse commonResponse = new StatusCommonResponse(201, "메뉴 좋아요 등록 성공");
		return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
	}

	/**
	 * 좋아요 삭제 기능
	 *
	 * @param storeId    : 가게 ID
	 * @param menuLikeId      : 좋아요 삭제 할 메뉴의 좋아요 Id
	 * @return : 좋아요 삭제 메시지 상태 코드 반환
	 */
	@DeleteMapping("/{menuLikeId}/like")
	public ResponseEntity<StatusCommonResponse> removeMenuLike(@PathVariable Long storeId, @PathVariable Long menuLikeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		menuLikesService.removeMenuLike(storeId, menuLikeId, userDetails.getUser());
		StatusCommonResponse commonResponse = new StatusCommonResponse(204, "메뉴 좋아요 삭제 성공");
		return new ResponseEntity<>(commonResponse, HttpStatus.OK);
	}

	/**
	 * 좋아요한 메뉴 목록 조회 기능
	 *
	 * @param userDetails : 사용자 정보
	 * @param page        : 페이지 번호
	 * @return : 좋아요한 메뉴 목록
	 */
	@GetMapping("/likes")
	public ResponseEntity<DataCommonResponse<Page<MenuResponseDto>>> getLikedMenus(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam(defaultValue = "0") int page
	) {
		PageRequest pageRequest = PageRequest.of(page, 5);
		Page<MenuResponseDto> likedMenus = menuLikesService.getLikedMenus(userDetails.getUser(), pageRequest);
		DataCommonResponse<Page<MenuResponseDto>> response = new DataCommonResponse<>(200, "좋아요한 메뉴 목록 조회 성공", likedMenus);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
