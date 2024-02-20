package org.ohgiraffers.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ohgiraffers.board.domain.dto.*;
import org.ohgiraffers.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//테스트위에다가
/* 포스트컨트롤러 테스트할거니,

★ 단위테스트 vs 통합테스트  (오호..

O먼저 "통합테스트" : 모듈을 통합하는 과정에서 모듈간의 호환성을 확인하기 위해 수행되는 테스트를 일컫는다.
                  API의 모든 과정이 올바르게 동작하는지 확인하는 것
                  //실제 어플리케이션개발시 더 복잡한 로직이생김.. 유저엔티티도있어야해서 맵핑해야하고, 참조연결이필요..ㅋ 이런 전체연결을 확인하는걸 통합테스트라함.

    "단위테스트" : Unit테스트 라고도하고, 하나의 모듈을 기준으로 독립적으로 진행되는 가장 작은 단위의 테스트로,,
                 (작은단위 = 한수, 메소드 를 의미)
                 또 단위테스트는 어플리케이션을 하나의 기능으로 구성하는게 올바른지 독립적으로 테스트하는것

   webmvc테스트 해볼것!
   컨트롤러가 잘 동작하는지 확인해볼 수 있다. (자주쓰는걸 모은거라던데..컨트롤러는)
   웹 어플리케이션을 어플리케이션 서버에 배포하지 않고 테스트용 mvc환경을 만들어 요청 및 전송 응답기능을 제공해준다.
 */

// MOC서버, 가짜 ㅋ 인데  단위테스트는 독립적진행하거든?  모키토,
//컨트롤러는 서비스가져와서 사용하는데,  서비스에서 리스폰스 잘줫는지만 확인하면됨.ㅋ 더알필요없고,
        //포스트서비스 가져와서사용하는걸 : 의존성주입 했다..라 표현..
        // 근데 이 의존성짤라내서 컨트롤러가 독립적으로 사용되는지 확인하는걸 현재 해보는것!..

@WebMvcTest(PostController.class)

public class PostControllerTest {
    //그러니 우선 오토와이어드 어노테이션사용함
    /*
    @오토와이어드 어노테이션이란..?
    : 의존성 주입(DI라함)할때 사용하는 필용한 의존객체의 타입이 해당하는 빈을 찾아 주입한다.

     *의존성이란? : 하나의 코드가 다른 코드에 의존하는 상태를 뜻함.
                ex) A코드, B코드있으면  A라는코드에서 B코드사용시, A는 B에 의존한다 라 할수있다.
                    A코드에서 B코드 직접생성이 아닌, 외부에서 B클래스 인스턴스생성해서 주입하는걸 의미.(간접이군)
                    이럴대 오토와이어드쓰는군ㅋ
     */
    @Autowired
    MockMvc mockMvc;
    /* @MockBean이란?
        :스프링의 의존성 주입기능을 사용해, 테스트시랳ㅇ시 실제 Bean대신에 'Mockito'(모키토)로 생성된 모의개체를 스프링 어플리케이션 컨텍스트에 추가한다.
        //의존성주입시, 가짜객체 사용하는데, 포스트서비스를 가짜객체로만들어서 주입해준다.테스트시..ㅋ
        가짜객체로만들어서 주입해서 사용한다. 테스트시!
     */
    @MockBean
    //포스트서비스를 가짜객체로 만들어주ㅗㄱ
    PostService postService;

    /*  그리고 오브젝트 맵퍼란..?
            : 특정 객체를 json형태로 바꾸기 위해서 사용한다.

     */
    //그리고 오토와이어드로 오브젝트맵퍼를 주입해주자.
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글 작성 기능 테스트")
    void create_post_test() throws Exception {
        //테스트사용시, 무슨구조사용??? 아~ 이 3개 ㅋ

        //given 에서 먼저
        CreatePostRequest request = new CreatePostRequest("테스트제목", "테스트내용");
        CreatePostResponse response = new CreatePostResponse(1L, "테스트제목", "테스트내용");

        //의존성덕에 끊었기에, 컨트롤러 내부에서 다 해결해야해서 ㅋ 세팅을 어떻게하냐면,

        given(postService.createPost(any())).willReturn(response);
        //any는 어느것을 담아보내도, 동작가능
        //해석: 크리에이트포스트메서드사용시, 애니 즉, 우리가만든걸 이용해준다의미
        //포스트서비스는 가짜객체니 ㅋ

        //when & then 이제 목mvc사용가능한데, 포스트요청할거니 ㅋ

        mockMvc.perform(post("/api/v1/posts") // 포스트api로 이 url형식으로 요청시,(이런내용으로 담아보내준다는뜼)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
        ) //요청보내는 환경이 만들어진것 여기서

                .andExpect(status().isOk())  //스테이터스로 목mvc의존성추가해주고,    1이출력
                .andExpect(jsonPath("$.postId").value(1L))  //이런 내용들이 나오는지 1L랑 제목,내용
                .andExpect(jsonPath("$.title").value("테스트제목"))
                .andExpect(jsonPath("$.content").value("테스트내용"))
                .andDo(print());
                //생성자를만들어주자
                //$ 표시하고 이런건 json 표시라..  포스트id에 해당하는걸 찾아서 올수있다.
                //그래서  위에 같은내용 2개가 일치하는지 확인하는것!
                // 처음엔 이 테스트짜는게 이해안가도 ㅋ
        //1. given: 어떤걸 oo할지 정해놓고,
        //2. mockMVC로 테스트하는환경을 적어준다. 순서는..

    }
    @Test
    @DisplayName("게시글을 단일 조회하는 테스트")
    void read_post_test() throws Exception {
        //3개 주자 테스트시

        //given  어떤걸할지
        Long postId =1L;
        ReadPostResponse response = new ReadPostResponse(1L, "테스트제목", "테스트내용");
                //기븐으로 리드시, 서비스호출했으니
        given(postService.readPostById(any())).willReturn(response);
        //포스트서비스는 테스트시 가짜객체만들어서 테스트하니,
        //when & then
        mockMvc.perform(get("/api/v1/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(1L))
                .andExpect(jsonPath("$.title").value("테스트제목"))
                .andExpect(jsonPath("$.content").value("테스트내용"))
                .andDo(print());
    }
    //서비스에서 리드포스트시,  만든 응답해주도록하고, 목엠브이시부분이 실행되는거임.ㅋ

    //--테스트방법은--
    //★given 세팅하고, 서비스쪽으로 갔을때 어떻게 리턴될건지 세팅하고, 목mvc로 요청보내보면된다! <--이거다!


    //현재 내가 혼자 짜보는중. 정답
    @Test
    @DisplayName("게시글 업데이트 기능")
    void update_post_test() throws Exception {
        //given  포스트id지정해줘야함.

         Long postId=1L;
         //리퀘스트도 지정ㅎ애줘야함  이렇게 리퀘스트보내면
        UpdatePostRequest request = new UpdatePostRequest("변경제목", "변경내용");
        //리스폰스도 나오겠지 이렇게
        UpdatePostResponse response = new UpdatePostResponse(1L,"변경제목","변경내용");
        //리스폰스로하면 테스트제목,내용 변경되었을테니. 기븐으로

        given(postService.updatePost(any(Long.class),any(UpdatePostRequest.class))).willReturn(response);  //리퀘스트대로 잘 바뀌는지 확인하는거니..

        //그리고 any가 불안하면, 타입지정가능..   포스트id,리퀘스트 넣어주지? Long타입. 형식지정해줄수도있다.이렇게 들어올거라고.

        //when & then,  그리고 목mvc

        mockMvc.perform(put("/api/v1/posts/{postId}",postId)  //풋메소드
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(1L))
                .andExpect(jsonPath("$.title").value("변경제목"))
                .andExpect(jsonPath("$.content").value("변경내용"))
                .andDo(print());
    }
    //given은 서비스를 가짜로만들었으니,  mock으로만든단건, 서비스로직을 껍데기만 가져와서,
    //메소드는있지만 안의 내용이없으니.. 똑같이 업데이트리스폰스를


}



//--테스트방법은--
//★given 세팅하고, 서비스쪽으로 갔을때 어떻게 리턴될건지 세팅하고, 목mvc로 요청보내보면된다! <--이거다!
