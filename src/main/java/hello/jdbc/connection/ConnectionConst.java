package hello.jdbc.connection;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

@Slf4j
public abstract class ConnectionConst {


    public static final String URL = "jdbc:mysql://localhost:3306/app";


    public static final String USERNAME = "kimdo";
    public static final String PW = "Hyun9753!";
}
