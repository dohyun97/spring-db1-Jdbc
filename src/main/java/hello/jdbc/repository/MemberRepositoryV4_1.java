package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;


/**
 * 예외 누수 해결
 * 체크예외를 런타임 예외로 변경
 * memberRepository 인터페이스 사용
 * throws SQLException 제거
 */

@Slf4j
public class MemberRepositoryV4_1 implements MemberRepository{

    private final DataSource dataSource;
    //DI
    public MemberRepositoryV4_1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member)  {
        String sql = "insert into member(member_id,money) values (?,?)";
        Connection con = null;
        PreparedStatement psmt = null;

        try {
            con = getConnection();
            psmt = con.prepareStatement(sql);
            psmt.setString(1, member.getMemberId());
            psmt.setInt(2,member.getMoney());
            psmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error",e);
            throw new MyDbException(e);
        }finally {
            close(con,psmt,null);
        }

    }

    @Override
    public Member findById(String memberId)  {
        String sql = "select * from member where member_id=?";
        Connection con = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            psmt = con.prepareStatement(sql);
            psmt.setString(1,memberId);
            rs = psmt.executeQuery();

            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else {
                throw new NoSuchElementException("member not found memberId = "+memberId);
            }
        } catch (SQLException e) {
            log.error("db error",e);
            throw new MyDbException(e);
        }finally {
            close(con,psmt,rs);
        }
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";
        Connection con = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            psmt = con.prepareStatement(sql);
            psmt.setInt(1,money);
            psmt.setString(2,memberId);
            int resultSize = psmt.executeUpdate();
            log.info("result size = {}",resultSize);

        } catch (SQLException e) {
            log.error("db error",e);
            throw new MyDbException(e);
        }finally {
            close(con,psmt,null);
        }
    }

    @Override
    public void delete(String memberId)  {
        String sql = "delete from member where member_id=?";
        Connection con = null;
        PreparedStatement psmt = null;

        try {
            con = getConnection();
            psmt = con.prepareStatement(sql);
            psmt.setString(1,memberId);

           psmt.executeUpdate();


        } catch (SQLException e) {
            log.error("db error",e);
            throw new MyDbException(e);
        }finally {
            close(con,psmt,null);
        }
    }


    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //트랜젝션 동기화를 사용하려면 DataSourceUils를 사용해야 한다
        //트랜젝션을 사용하기 위해 동기화된 커넥션은 커넥션을 닫지 않고 그대로 유지.트랜젝션 동기화 매니저가 관리하는 커넥션이 없는 경우 해당 커넥션을 닫아
        DataSourceUtils.releaseConnection(con,dataSource);
    }

    private  Connection getConnection() throws SQLException {
        //트랜젝션 동기화를 사용하려면 DataSourceUils를 사용해야 한다
        //트랜젝션 동기화 매니저가 관리하는 커넥션이 있으면 해당 커넥션을 반환. 없으면 새로운 커넥션 생성
        Connection connection = DataSourceUtils.getConnection(dataSource);
        log.info("get connection = {}, class = {}",connection,connection.getClass());
        return connection;
    }
}
