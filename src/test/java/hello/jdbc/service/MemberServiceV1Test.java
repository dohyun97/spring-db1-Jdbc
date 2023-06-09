package hello.jdbc.service;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;


/**
 * Without Transaction
 */
class MemberServiceV1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";


    private MemberRepositoryV1 memberRepository;
    private MemberServiceV1 memberService;

    @BeforeEach
    void beforeEach(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PW);
        this.memberRepository = new MemberRepositoryV1(dataSource);
        this.memberService = new MemberServiceV1(memberRepository);
    }

    @AfterEach
    void afterEach() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("Transfer Success")
    void accountTransfer() throws SQLException {
        //Given
        Member memberA = new Member(MEMBER_A,10000);
        Member memberB = new Member(MEMBER_B,10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        //When
        memberService.accountTransfer(memberA.getMemberId(),memberB.getMemberId(),2000);
        //Then
        int memberAMoney = memberRepository.findById(memberA.getMemberId()).getMoney();
        int memberBMoney = memberRepository.findById(memberB.getMemberId()).getMoney();
        assertThat(memberAMoney).isEqualTo(8000);
        assertThat(memberBMoney).isEqualTo(12000);
    }

    @Test
    @DisplayName("Transfer Fail")
    void accountTransferEx() throws SQLException {
        //Given
        Member memberA = new Member(MEMBER_A,10000);
        Member memberEx = new Member(MEMBER_EX,10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);
        //When
        assertThatThrownBy(()->memberService.accountTransfer(memberA.getMemberId(),memberEx.getMemberId(),2000))
                .isInstanceOf(IllegalArgumentException.class);
        //Then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEx = memberRepository.findById(memberEx.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);
    }

}