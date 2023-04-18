package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

//With Transaction- @Transaction AOP
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_3 {
    private final MemberRepositoryV3 memberRepository;

    @Transactional    //이 메소드가 성공적으로 끝나면 커밋, 예외 발생하면 롤백
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        bizLogic(fromId,toId,money);
        }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember); //To make an error in this method
        memberRepository.update(toId, toMember.getMoney() + money);
    }


    private void validation(Member member){
        if (member.getMemberId().equals("ex")){
            throw new IllegalArgumentException("Error in transfer");
        }
    }

   
}
