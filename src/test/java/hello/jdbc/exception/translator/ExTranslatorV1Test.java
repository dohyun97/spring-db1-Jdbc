package hello.jdbc.exception.translator;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import hello.jdbc.repository.ex.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ExTranslatorV1Test {

    Repository repository;
    Service service;

    @BeforeEach
    void init(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PW);
        this.repository = new Repository(dataSource);
        this.service = new Service(repository);
    }

    @Test
    void duplicateKey(){
        service.create("myId");
        service.create("myId");
    }

    @RequiredArgsConstructor
    static class Service{
        private final Repository repository;
        public void create(String memberId){
            try {
                repository.save(new Member(memberId,0));
                log.info("saveId = {}",memberId);
            }catch (MyDuplicateKeyException e){
                log.info("Duplicate Key, make new id");
                String retryId = generateNewKey(memberId);
                log.info("retryID = {}",retryId);
                repository.save(new Member(retryId,0));
            }catch (MyDbException e){
                log.info("db exception",e);
                throw e;
            }
        }
        private String generateNewKey(String memberId){
            return memberId+ new Random().nextInt(1000);
        }
    }

    @RequiredArgsConstructor
    static class Repository{
        private final DataSource dataSource;

        public Member save(Member member){
            String sql = "insert into member(member_id,money) values(?,?)";
            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1,member.getMemberId());
                pstmt.setInt(2,member.getMoney());
                pstmt.executeUpdate();
                return member;
            } catch (SQLException e) {
                //mySql db
                if(e.getErrorCode() == 1062){
                    throw  new MyDuplicateKeyException(e);
                }
                throw new MyDbException(e);
            }finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }
        }
    }
}
