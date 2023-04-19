package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {
    @Test   //예외를 잡아줘서 정상적으로 리턴
    void checked_catch(){
        Service service = new Service();
        service.callCatch();
    }

    @Test   //예외를 계속 잡지 않았으므로 여기서 예외 발생. 정상적으로 리턴 불가능
    void checked_throw(){
        Service service = new Service();
        service.callCatch();
        Assertions.assertThatThrownBy(()->service.callThrow()).isInstanceOf(MyCheckedException.class);
    }


    /**
     * RuntimeException을 상속받아서 unchecked exception
     */
    static class MyCheckedException extends RuntimeException{
        public MyCheckedException(String message){
            super(message);
        }
    }

    /**
     * checked exception은 잡거나 던지거나 둗중하나를 꼭 해야돼
     */
    static class Service{
        Repository repository = new Repository();
        /**
         * 예외를 잡아서 처리하는 코드
         */
        public void callCatch(){
            try {
                repository.call();
            }catch (MyCheckedException e){   //Exception e
                log.info("예외처리 메세지: {}",e.getMessage(),e);
            }
        }

        /**
         * 체크예외를 던지는 코드 (thorws선언 x)
         */
        public void callThrow()  {
            repository.call();
        }
    }

    static class Repository{

        //checked exception을 잡지 않았으므로 던져져. throws 사용안해도
        public void call()  {
            throw new MyCheckedException("ex");
        }
    }
}
