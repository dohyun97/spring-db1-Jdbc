package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Transaction -sync connection by TrasactionTemplate with transaction manager
 */
class MemberServiceV3_2Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";


    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_2 memberService;

    @BeforeEach
    void beforeEach(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PW);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource); //TransactionManager make connection with datasource
        this.memberRepository = new MemberRepositoryV3(dataSource);
        this.memberService = new MemberServiceV3_2(transactionManager,memberRepository);
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
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);
    }

}