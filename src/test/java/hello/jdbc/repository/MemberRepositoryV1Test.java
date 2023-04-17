package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class MemberRepositoryV1Test {
    MemberRepositoryV1 memberRepository;
    @BeforeEach
    void beforeEach(){
        //DriverManagerDataSource
        //DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PW);

        //Hikari
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setPassword(PW);
        dataSource.setUsername(USERNAME);

        memberRepository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud() throws SQLException {
        //save
        Member member = new Member("memberV0",1000);
        memberRepository.save(member);
        //findById
        Member findMember = memberRepository.findById(member.getMemberId());
        log.info("findMember = {}",findMember);
        assertThat(findMember).isEqualTo(member);
        //update
        memberRepository.update(member.getMemberId(),2000);
        Member updateMember = memberRepository.findById(member.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(2000);
        //delete
        memberRepository.delete(member.getMemberId());
        assertThatThrownBy(()->memberRepository.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);

    }

}
