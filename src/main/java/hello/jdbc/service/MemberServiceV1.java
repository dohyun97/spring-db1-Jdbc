package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
//Without Transaction.AutoCommit
@Service
@RequiredArgsConstructor
public class MemberServiceV1 {
    private final MemberRepositoryV1 memberRepository;

    public void accountTransfer(String fromId,String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney()-money);
        validation(toMember); //To make an error in this method
        memberRepository.update(toId, toMember.getMoney()+money);
    }

    private void validation(Member member){
        if (member.getMemberId().equals("ex")){
            throw new IllegalArgumentException("Error in transfer");
        }
    }
}
