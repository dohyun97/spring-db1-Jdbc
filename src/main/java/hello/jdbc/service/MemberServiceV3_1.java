package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;

//With Transaction- 파라미터로 같은 커넥션 유지, 커넥션 풀을 고려한 autocommit 종료
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceV3_1 {
    private final MemberRepositoryV3 memberRepository;
    private final PlatformTransactionManager transactionManager;

    public void accountTransfer(String fromId,String toId, int money) throws SQLException {
        //start transaction. return transaction status
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {

           bizLogic(fromId, toId, money);
           transactionManager.commit(status); //commit when success
       }catch (Exception e){
           transactionManager.rollback(status); //rollback when fail
           throw new IllegalArgumentException(e);
       }
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

    private void release(Connection con) throws SQLException {
        if(con!=null){
            try {
                con.setAutoCommit(true);
                con.close();
            }catch (Exception e){
                log.error("error",e);
            }
        }
    }
}
