package hu.juranyi.zsolt.heritrixremote.model;

import java.util.Date;

/**
 * Handles an error - prints out error message and exits with error code.
 *
 * @author Zsolt Jurányi
 */
public class ErrorHandler {

    public ErrorHandler(ErrorType et) {
        this(et, null);
    }

    public ErrorHandler(ErrorType et, Exception ex) {
        System.out.println("ERROR: " + et.getMessage());
        if (null != ex) {
            System.out.println("Please contact the developer or send a ticket with the following stack trace on http://juzraai.github.io/HeritrixRemote !");
            ex.printStackTrace();
        }
        System.out.println(new Date());
        System.out.println();
        System.exit(et.getExitCode());
    }
}
