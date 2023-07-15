package com.seniors.domain.post.controller;

import com.seniors.common.annotation.LoginUsers;
import com.seniors.common.dto.DataResponseDto;
import com.seniors.config.security.CustomUserDetails;
import com.seniors.domain.post.dto.PostDto;
import com.seniors.domain.post.dto.PostDto.GetPostRes;
import com.seniors.domain.post.dto.PostDto.ModifyPostReq;
import com.seniors.domain.post.dto.PostDto.PostCreateDto;
import com.seniors.domain.post.dto.PostDto.SavePostReq;
import com.seniors.domain.post.entity.Post;
import com.seniors.domain.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "게시글", description = "게시글 API 명세서")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

	private final PostService postService;

	@Operation(summary = "게시글 생성")
	@ApiResponse(responseCode = "200", description = "생성 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataResponseDto.class)))
	@PostMapping("")
	public DataResponseDto<String> postAdd(
			@RequestBody @Valid SavePostReq postDto,
			@LoginUsers CustomUserDetails userDetails) {
		postDto.setUserId(userDetails.getUserId());
		postService.addPost(postDto);
		return DataResponseDto.of("SUCCESS");
	}

	@Operation(summary = "게시글 단건 조회")
	@ApiResponse(responseCode = "200", description = "단건 조회 성공",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataResponseDto.class)))
	@GetMapping("/{postId}")
	public DataResponseDto<GetPostRes> postDetails(
			@Parameter(description = "게시글 ID") @PathVariable(value = "postId") Long postId,
			@LoginUsers CustomUserDetails userDetails) {
		GetPostRes post = postService.findOnePost(postId, userDetails.getUserId());
		return DataResponseDto.of(post);
	}

	@Operation(summary = "게시글 리스트 조회")
	@ApiResponse(responseCode = "200", description = "리스트 조회 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataResponseDto.class)))
	@GetMapping("")
	public DataResponseDto<List<GetPostRes>> postList(
			@LoginUsers CustomUserDetails userDetails,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false) int offset
	) {
		List<GetPostRes> postResList = postService.findPost(page, offset);
		return DataResponseDto.of(postResList);
	}

	@Operation(summary = "게시글 수정")
	@ApiResponse(responseCode = "200", description = "단건 수정 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataResponseDto.class)))
	@PatchMapping("/{postId}")
	public DataResponseDto<String> postModify(
			@Parameter(description = "게시글 ID") @PathVariable(value = "postId") Long postId,
			@RequestBody @Valid ModifyPostReq postDto) {
		postService.modifyPost(postDto, postId);
		return DataResponseDto.of("SUCCESS");
	}

	@Operation(summary = "게시글 단건 삭제")
	@ApiResponse(responseCode = "200", description = "단건 삭제 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataResponseDto.class)))
	@DeleteMapping("/{postId}")
	public DataResponseDto<String> postRemove(@Parameter(description = "게시글 ID") @PathVariable(value = "postId") Long postId) {
		postService.removePost(postId);
		return DataResponseDto.of("SUCCESS");
	}
}
