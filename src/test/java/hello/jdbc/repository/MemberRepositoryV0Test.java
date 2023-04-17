package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;


@Slf4j
class MemberRepositoryV0Test {
    MemberRepositoryV0 memberRepository = new MemberRepositoryV0();
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