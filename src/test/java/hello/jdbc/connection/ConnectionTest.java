package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {
    @Test
    void driveManager() throws SQLException {
        Connection connection1 = DriverManager.getConnection(URL, USERNAME, PW);
        Connection connection2 =DriverManager.getConnection(URL, USERNAME, PW);

        log.info("connection = {}, class = {}",connection1,connection1.getClass());
        log.info("connection = {}, class = {}",connection2,connection2.getClass());
    }

    @Test
    void dataSourceDriverManager() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PW);
        useDataSource(dataSource);
    }

    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        //hikari proxy connection
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PW);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        Thread.sleep(1000); //connection pool생성 대기시간
    }

    @Test
    private void useDataSource(DataSource dataSource) throws SQLException {

            Connection connection1 = dataSource.getConnection();
            Connection connection2 =dataSource.getConnection();

            log.info("connection = {}, class = {}",connection1,connection1.getClass());
            log.info("connection = {}, class = {}",connection2,connection2.getClass());
    }
}
