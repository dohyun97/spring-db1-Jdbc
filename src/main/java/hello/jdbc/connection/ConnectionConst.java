package hello.jdbc.connection;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

@Slf4j
public abstract class ConnectionConst {


    public static final String URL = "url";


    public static final String USERNAME = "username";
    public static final String PW = "pw";
}
