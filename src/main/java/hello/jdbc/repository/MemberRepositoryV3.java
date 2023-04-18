package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;


/**
 * Spring Transaction manager
 * DataSourceUtils.getConnection()
 * DataSourceUtils.releaseConnection()
 */

@Slf4j
public class MemberRepositoryV3 {

    private final DataSource dataSource;
    //DI
    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
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
            throw e;
        }finally {
            close(con,psmt,null);
        }

    }

    public Member findById(String memberId) throws SQLException {
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
            throw e;
        }finally {
            close(con,psmt,rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
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
            throw e;
        }finally {
            close(con,psmt,null);
        }
    }


    public void delete(String memberId) throws SQLException {
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
            throw e;
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
