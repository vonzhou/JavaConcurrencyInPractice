package com.vonzhou.learn.jcip.shareobject;

import java.util.*;

/**
 * Secrets
 * <p>
 * Publishing an object
 *
 * @author Brian Goetz and Tim Peierls
 */
class Secrets {
    public static Set<Secret> knownSecrets;

    public void initialize() {
        knownSecrets = new HashSet<Secret>();
    }
}


class Secret {
}
