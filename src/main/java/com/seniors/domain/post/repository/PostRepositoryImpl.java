package com.seniors.domain.post.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seniors.common.exception.type.NotFoundException;
import com.seniors.common.repository.BasicRepoSupport;
import com.seniors.domain.comment.entity.QComment;
import com.seniors.domain.post.dto.PostDto.GetPostRes;
import com.seniors.domain.post.dto.PostDto.ModifyPostReq;
import com.seniors.domain.post.entity.Post;
import com.seniors.domain.post.entity.QPost;
import com.seniors.domain.users.entity.QUsers;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;


@Slf4j
@Repository
public class PostRepositoryImpl extends BasicRepoSupport implements PostRepositoryCustom {

	private final static QPost post = QPost.post;
	private final static QComment comment = QComment.comment;
	private final static QUsers users = QUsers.users;

	protected PostRepositoryImpl(JPAQueryFactory jpaQueryFactory, EntityManager em) {
		super(jpaQueryFactory, em);
	}

	@Override
	public GetPostRes findOnePost(Long postId, Long userId) {
		List<Post> postResList = jpaQueryFactory
				.selectFrom(post)
				.leftJoin(post.comments, comment).fetchJoin()
				.innerJoin(post.users, users).fetchJoin()
				.where(post.id.eq(postId).and(post.users.id.eq(userId)))
				.fetch();

		if (postResList.isEmpty()) {
			throw new NotFoundException("Post Not Found");
		}

		List<GetPostRes> content = postResList.stream()
				.map(p -> new GetPostRes(
						p.getId(),
						p.getTitle(),
						p.getContent(),
						p.getCreatedAt(),
						p.getLastModifiedDate(),
						p.getUsers(),
						p.getComments())).toList();

		return content.get(0);
	}

	public void modifyPost(ModifyPostReq modifyPostReq, Long postId, Long userId) {
		jpaQueryFactory
				.update(post)
				.set(post.title, modifyPostReq.getTitle())
				.set(post.content, modifyPostReq.getContent())
				.where(post.id.eq(postId).and(post.users.id.eq(userId)))
				.execute();
	}

	@Override
	public Page<GetPostRes> findAllPost(Pageable pageable) {
		JPAQuery<Post> query = jpaQueryFactory
				.selectFrom(post)
				.leftJoin(post.comments, comment).fetchJoin()
				.join(post.users, users).fetchJoin();
		super.setPageQuery(query, pageable, post);
		List<GetPostRes> content = query.fetch().stream()
				.map(p -> new GetPostRes(
						p.getId(),
						p.getTitle(),
						p.getContent(),
						p.getCreatedAt(),
						p.getLastModifiedDate(),
						p.getUsers(),
						p.getComments())).toList();

		JPAQuery<Long> countQuery = jpaQueryFactory
				.select(post.id.count())
				.from(post);
		Long count = countQuery.fetchOne();
		count = count == null ? 0 : count;

		return new PageImpl<>(content, super.getValidPageable(pageable), count);
	}
}

