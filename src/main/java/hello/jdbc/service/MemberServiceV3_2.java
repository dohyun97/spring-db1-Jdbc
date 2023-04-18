package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

//With Transaction- TransactionTemplate
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceV3_2 {
    private final MemberRepositoryV3 memberRepository;
    private final TransactionTemplate txTemplate;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager,MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
        this.txTemplate = new TransactionTemplate(transactionManager);
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        txTemplate.executeWithoutResult((status)->{
            try {
                bizLogic(fromId,toId,money);
            } catch (SQLException e) {
                throw new IllegalArgumentException(e);
            }
        });



//        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
//        try {
//
//           bizLogic(fromId, toId, money);
//           transactionManager.commit(status); //commit when success
//       }catch (Exception e){
//           transactionManager.rollback(status); //rollback when fail
//           throw new IllegalArgumentException(e);
//       }
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
