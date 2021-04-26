package njit.mt.whackaquack;

import org.junit.Test;

import njit.mt.whackaquack.data.model.LoggedInUser;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest {
    private final String user_id = "whatever";
    private final String display_name = "a name";
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void user_id_isCorrect(){
        LoggedInUser user = new LoggedInUser(user_id, display_name);
        System.out.println(user.getUserId());

        assertEquals(user_id, user.getUserId());
    }
    @Test
    public void user_display_name_isCorrect(){
        LoggedInUser user = new LoggedInUser("", display_name);
        assertEquals(display_name, user.getDisplayName());
    }



}