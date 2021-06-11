package com.bakdata.conquery.models.auth.oidc;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bakdata.conquery.models.identifiable.ids.specific.UserId;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shiro.authc.BearerToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.keycloak.common.VerificationException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtPkceVerifyingRealmTest {

    public static final String ALTERNATIVE_ID_CLAIM = "alternativeId";
    private static JwtPkceVerifyingRealm REALM;
    private static RSAPrivateKey PRIVATE_KEY;
    private static RSAPublicKey PUBLIC_KEY;

    @BeforeAll
    static void setup() throws NoSuchAlgorithmException {
        // Generate a key pair
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(1024);
        KeyPair pair = keyGenerator.genKeyPair();
        PRIVATE_KEY = (RSAPrivateKey) pair.getPrivate();
        PUBLIC_KEY = (RSAPublicKey) pair.getPublic();

        // Create the realm
        REALM = new JwtPkceVerifyingRealm(PUBLIC_KEY, List.of(ALTERNATIVE_ID_CLAIM));
    }


    @Test
    void verifyValidToken() {

        // Setup the expected user id
        UserId expected = new UserId("Test");

        Date issueDate = new Date();
        Date expDate = DateUtils.addMinutes(issueDate, 1);
        String token = JWT.create()
                .withIssuer(REALM.getName())
                .withSubject(expected.getEmail())
                .withIssuedAt(issueDate)
                .withExpiresAt(expDate)
                .sign(Algorithm.RSA256(PUBLIC_KEY, PRIVATE_KEY));
        BearerToken accessToken = new BearerToken(token);

        assertThat(REALM.doGetConqueryAuthenticationInfo(accessToken).getPrincipals().getPrimaryPrincipal()).isEqualTo(expected);
    }

    @Test
    void verifyValidTokenAlternativeId() {

        // Setup the expected user id
        UserId expected = new UserId("Test");

        Date issueDate = new Date();
        Date expDate = DateUtils.addMinutes(issueDate, 1);
        final String primId = UUID.randomUUID().toString();
        String token = JWT.create()
                .withIssuer(REALM.getName())
                .withSubject(primId)
                .withIssuedAt(issueDate)
                .withExpiresAt(expDate)
                .withClaim(ALTERNATIVE_ID_CLAIM, expected.getEmail())
                .sign(Algorithm.RSA256(PUBLIC_KEY, PRIVATE_KEY));
        BearerToken accessToken = new BearerToken(token);

        assertThat(REALM.doGetConqueryAuthenticationInfo(accessToken).getPrincipals()).containsAll(List.of(new UserId(primId),expected));
    }

    @Test
    void verifyInvalidToken() {

        // Setup the expected user id
        UserId expected = new UserId("Test");

        Date issueDate = new Date();
        Date expDate = DateUtils.addMinutes(issueDate, -1);
        String token = JWT.create().withIssuer(REALM.getName()).withSubject(expected.getEmail()).withIssuedAt(issueDate).withExpiresAt(expDate).sign(Algorithm.RSA256(PUBLIC_KEY, PRIVATE_KEY));
        BearerToken accessToken = new BearerToken(token);

        assertThatCode(() -> REALM.doGetConqueryAuthenticationInfo(accessToken)).hasCauseInstanceOf(VerificationException.class);
    }
}