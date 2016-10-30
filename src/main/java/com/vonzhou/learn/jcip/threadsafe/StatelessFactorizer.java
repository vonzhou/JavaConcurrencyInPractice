package com.vonzhou.learn.jcip.threadsafe;

import com.vonzhou.learn.jcip.annotations.ThreadSafe;

import java.math.BigInteger;
import javax.servlet.*;


/**
 * StatelessFactorizer
 * <p>
 * A stateless servlet
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class StatelessFactorizer extends GenericServlet implements Servlet {

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = factor(i);
        encodeIntoResponse(resp, factors);
    }

    void encodeIntoResponse(ServletResponse resp, BigInteger[] factors) {
    }

    BigInteger extractFromRequest(ServletRequest req) {
        return new BigInteger("7");
    }

    BigInteger[] factor(BigInteger i) {
        // Doesn't really factor
        return new BigInteger[]{i};
    }
}
