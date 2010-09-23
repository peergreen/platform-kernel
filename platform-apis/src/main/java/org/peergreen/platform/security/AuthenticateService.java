package org.peergreen.platform.security;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 15 sept. 2010
 * Time: 20:30:26
 * To change this template use File | Settings | File Templates.
 */
public interface AuthenticateService {
    // TODO should returns a JAAS Subject
    boolean authenticate(String user, Object credentials);
}
