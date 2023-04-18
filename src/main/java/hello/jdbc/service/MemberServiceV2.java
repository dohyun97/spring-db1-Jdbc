package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//With Transaction- 파라미터로 같은 커넥션 유지, 커넥션 풀을 고려한 autocommit 종료
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {
    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;

    public void accountTransfer(String fromId,String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
       try {
           con.setAutoCommit(false);
           bizLogic(con,fromId, toId, money);
           con.commit();
       }catch (Exception e){
           con.rollback();
           throw new IllegalArgumentException(e);
       }finally {
           release(con);
       }
    }

    private void bizLogic(Connection con,String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con,fromId);
        Member toMember = memberRepository.findById(con,toId);

        memberRepository.update(con,fromId, fromMember.getMoney() - money);
        validation(toMember); //To make an error in this method
        memberRepository.update(con,toId, toMember.getMoney() + money);
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
