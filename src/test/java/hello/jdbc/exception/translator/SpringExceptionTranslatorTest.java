package hello.jdbc.exception.translator;

import hello.jdbc.connection.ConnectionConst;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
public class SpringExceptionTranslatorTest {
    DataSource dataSource;

    @BeforeEach
    void init(){
        dataSource = new DriverManagerDataSource(URL, USERNAME, PW);
    }

    @Test //이런식으로 계속 에러코드 직접 확인하기는 너무 힘들어
    void sqlExceptionErrorCode(){
        String sql = "select BAD Grammar";
        try {
            Connection con = dataSource.getConnection();
            PreparedStatement psmt = con.prepareStatement(sql);
            psmt.executeQuery();
        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(1054);
            int errorCode = e.getErrorCode();
            log.info("errorCode: {}",errorCode);
            log.info("error",e);
        }
    }

    @Test //그래서 spring이 제공하는 translator 사용
    void exceptionTranslator(){
        String sql = "select BAD Grammar";
        try {
            Connection con = dataSource.getConnection();
            PreparedStatement psmt = con.prepareStatement(sql);
            psmt.executeQuery();
        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(1054);
            //Translator
            SQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            DataAccessException resultEx = exTranslator.translate("select", sql, e);
            log.info("resulEx",resultEx);
            log.info("resultEx.get---------- {}",resultEx.getClass());
            assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);

        }
    }
}

