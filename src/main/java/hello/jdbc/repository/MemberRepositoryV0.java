package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;


/**
 * JDBC-DriverManager 사용
 */

@Slf4j
public class MemberRepositoryV0 {
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id,money) values (?,?)";
        Connection con = null;
        PreparedStatement psmt = null;

        try {
            con = DBConnectionUtil.getConnection();
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
            con = DBConnectionUtil.getConnection();
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
            con = DBConnectionUtil.getConnection();
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
            con = DBConnectionUtil.getConnection();
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
          if(rs!=null){
              try {
                  rs.close();
              } catch (SQLException e) {
                  log.info("error",e);
              }
          }

        if(stmt!=null){
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error",e);
            }
        }
        if(con!=null){
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error",e);
            }
        }

    }
}
