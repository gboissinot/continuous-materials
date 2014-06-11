package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy;

import org.apache.commons.codec.digest.DigestUtils;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;
import org.vertx.java.core.shareddata.SharedData;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Gregory Boissinot
 */
public class POMCache {

    private static final String KEY_CACHE_POM_CONTENT = "pomContent";
    private static final String KEY_CACHE_POM_SHA1 = "pomContentSha1";

    public String loadPomContentFromCache(Vertx vertx, String pomPath) {
        final SharedData sharedData = vertx.sharedData();
        final ConcurrentSharedMap<Object, Object> pomContentMap = sharedData.getMap(KEY_CACHE_POM_CONTENT);
        return (String) pomContentMap.get(pomPath);
    }

    public String getSha1(Vertx vertx, String pomSha1Path) {
        final SharedData sharedData = vertx.sharedData();
        final ConcurrentSharedMap<Object, Object> map = sharedData.getMap(KEY_CACHE_POM_SHA1);
        String sha1Result = (String) map.get(pomSha1Path);

        return sha1Result;
    }

    public void putPomContent(Vertx vertx, String pomPath, String pomContent) {
        final SharedData sharedData = vertx.sharedData();

        //Store PomContent
        final ConcurrentSharedMap<Object, Object> pomContentMap = sharedData.getMap(KEY_CACHE_POM_CONTENT);
        pomContentMap.put(pomPath, pomContent);

        //Store sha1 pomContent
        final String sha1Path = pomPath + ".sha1";

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(pomContent.getBytes("UTF-8"));

            String sha1 = new BigInteger(1, crypt.digest()).toString(16);
            String sha2 = DigestUtils.shaHex(pomContent.getBytes());

            final ConcurrentSharedMap<Object, Object> map = sharedData.getMap(KEY_CACHE_POM_SHA1);
            map.put(sha1Path, sha1);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }


    }
}
