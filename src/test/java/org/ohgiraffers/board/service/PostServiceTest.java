package org.ohgiraffers.board.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ohgiraffers.board.domain.dto.*;
import org.ohgiraffers.board.domain.entity.Post;
import org.ohgiraffers.board.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


//그리고 테스트메소드 위에다 Extendwidt를.. 좀더 모키토를 사용한다는뜻. 서비스테스트시엔
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    /*
    @Mock 어노테이션은 가짜객체로,
    테스트시랳ㅇ시 실제가 아닌 Mock객체를 반환하느거임 ㅋ 테스트하려고 말그대로.

    */
    @Mock
    private PostRepository postRepository;
    //서비스에선 포스트 래포지토리 주입받아사용하니ㅏ, 가짜로 만들어줘야함 ㅋ 목으로

    /* InjectionMocks
    Mock객체가 주입될 클래스를 지정한다.

    목으로 포스트레포지토리 했고 (테스트위해), 포스트서비스에 주입할거라고 해주는것!.
     */
    @InjectMocks
    private PostService postService;

    //클래스에 변수 미리 선언해주자
    private Post savedPost;
    private Post post;
    private CreatePostRequest createPostRequest;
    private UpdatePostRequest updatePostRequest;
    @BeforeEach
    // : 실행되기전에 한번ㅆ?ㅣㄱ 실행해보는
    void setup() {
        //초기화
        post = new Post(1L, "테스트제목", "테스트내용");
        savedPost = new Post(2L, "저장되어있던 테스트제목", "저장되어있던 테스트내용");
        createPostRequest = new CreatePostRequest("테스트제목", "테스트내용");
        updatePostRequest = new UpdatePostRequest("변경된 테스트 제목", "변경된 테스트 내용");

        //이러면 모든 메소드들이 테스트실행전, 비포리치가 실행되면서 초기화시켜준다.ㅋ
    }
    @Test
    //크리에이트먼저만들어보자
    @DisplayName("게시글작성기능테스트")
    void create_post_test(){
        //given -> when으로
        // when(postRepository.save(any())).thenReturn(post);
        //BDDMockito형태로
        given(postRepository.save(any())).willReturn(post);

        //when
        CreatePostResponse createPostResponse= postService.createPost(createPostRequest);
        //이 포스트서비스로 온 값을 포스트1인 내용과 같은지 확인해보자

        //then
        assertThat(createPostResponse.getPostId()).isEqualTo(1L);
        assertThat(createPostResponse.getTitle()).isEqualTo("테스트제목");
        assertThat(createPostResponse.getContent()).isEqualTo("테스트내용");

///이렇게하면 레포지토리 안가도 정상적으로 리스폰스만들어지는지 확인가능

    }
    //이번엔 리드기능.

    @Test
    @DisplayName("postId로 게시글 조회하는 기능 테스트")
    void read_post_by_id_1() {  //지금 예외처리하는중.. 테스트하나더만들자

        //서비스보면ㅁ, 얘도 레포지토리에서 파인드바이 아이디로 가져오는구조라

        //given
        when(postRepository.findById(any())).thenReturn(Optional.of(savedPost));

        //when

        ReadPostResponse readPostResponse = postService.readPostById(savedPost.getPostId());

        //서비스에서 리드포스트리스폰스가 서비스통해 제대로 만들어져야

        //then에서 어설트댓으로 잘되었는지 확인해보자
        assertThat(readPostResponse.getPostId()).isEqualTo(savedPost.getPostId());
        assertThat(readPostResponse.getTitle()).isEqualTo(savedPost.getTitle());
        assertThat(readPostResponse.getContent()).isEqualTo(savedPost.getContent());
        
    }

    @Test
    @DisplayName("postId로 게시글 찾지 못했을때, 지정한 Exception을 발생시키는지 테스트 -예외처리한거")
    void read_post_by_id_2() {

        //given  이렇게 3개 나누고,
        //일단 기븐에는

        given(postRepository.findById(any())).willReturn(Optional.empty());
        //파인드바이에 애니넣고, 발생시켜야지?

        //when &then
        assertThrows(EntityNotFoundException.class, () ->
                postService.readPostById(1L));
        //이렇게 실행시 제대로 통과됨 확인.
    }
    //그다음엔,
    @Test //전체조회를해보자.
    @DisplayName("전체 게시글 조회 기능 테스트")
    void read_all_post() {
        //given  이번엔 페이지어블 해줘야지..( 리퀘스트도할수있고 페이지어블 바로도 가능
        Pageable pageable = PageRequest.of(0,5);
        List<Post> posts = Arrays.asList(post,savedPost);
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());
        //굿.
        given(postRepository.findAll(pageable)).willReturn(postPage);

        //when
        Page<ReadPostResponse> responses = postService.readAllPost(pageable);

        //포스트레포지토리에선, 포트스페이지가 나오면 그 페이지를 , v포스트리드포스트가 잘만들어졋는지 확인해보면된다ㅣ.

        //then 으로해주고,
        assertThat(responses.getContent()).hasSize(2);
        assertThat(responses.getContent().get(0).getTitle()).isEqualTo("테스트제목");
        assertThat(responses.getContent().get(0).getContent()).isEqualTo("테스트내용");
        assertThat(responses.getContent().get(1).getTitle()).isEqualTo("저장되어있던 테스트제목");
        assertThat(responses.getContent().get(1).getContent()).isEqualTo("저장되어있던 테스트내용");
        //0번째 내용이 포스트면 - 제목이라 나와야지
        //첫번재ㅔ는 저장되 ㄴ제목
    }

    //딜리트랑 업데이트 만들어보자 ( 예외처리도)  어후..ㅋ

    //자 만들어보자.나머지, 수정기능
    @Test
    @DisplayName("게시글 수정 기능 테스트")
    void update_post() {

        //given 에서는 수정기능이니.. 서비스에서보면, 업데이트포스트에 파인드바이아이디 가짜로만들엇으니, 원하는 포스트 리턴하도록 만들자.
        given(postRepository.findById(any())).willReturn(Optional.of(savedPost));
        // 항상 세이브으포스트가 리턴되겟지?

        //when
        UpdatePostResponse updatePostResponse=postService.updatePost(savedPost.getPostId(), updatePostRequest);  //아이디랑 리퀘스트 서비스보니 받아왓네?
        //이 시점에선, 업뎅이트포스트리스폰스를 비교하면서, 서비스코드가 잘작동하는지 확인하는것!

        //then 만들고,
        assertThat(updatePostResponse.getPostId()).isEqualTo(savedPost.getPostId());
        assertThat(updatePostResponse.getTitle()).isEqualTo("변경된 테스트 제목");
        assertThat(updatePostResponse.getContent()).isEqualTo("변경된 테스트 내용");


    }
    //마지막 딜리트

    @Test
    @DisplayName("게스글삭제테스트")
    void delete_post(){
        //given
        when(postRepository.findById(any())).thenReturn(Optional.of(savedPost));
        //when
        DeletePostResponse deletePostResponse = postService.deletePost(savedPost.getPostId());
        //then  ,결과가같아야하니,비교시
        assertThat(deletePostResponse.getPostId()).isEqualTo(2L);
        //2L은 롱타입.ㅋ 그냥 숫자2면 int형. 근데 에러발생할수있어서 Long타입으로함

                //서비스보면 딜리트메소드작성보면 리스폰스 잘 하는지까지...
    }

    //삭제도 포스트레포지토리에서 아이디이ㅆ는지 확인부터

}