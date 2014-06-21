package fr.synchrotron.soleil.ica.ci.service.legacymavenproxy.pommetadata;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;
import org.vertx.java.core.shareddata.SharedData;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Gregory Boissinot
 */
public class POMCache {

    private static final String KEY_CACHE_POM_CONTENT = "pomContent";
    private static final String KEY_CACHE_POM_SHA1 = "pomContentSha1";

    private final ConcurrentSharedMap<String, String> pomContentMap;
    private final ConcurrentSharedMap<String, String> pomSha1Map;

    private MessageDigest digester;

    public POMCache(Vertx vertx) {
        final SharedData sharedData = vertx.sharedData();
        pomContentMap = sharedData.getMap(KEY_CACHE_POM_CONTENT);
        pomSha1Map = sharedData.getMap(KEY_CACHE_POM_SHA1);

        try {
            digester = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSha1(String pomSha1Path) {
        return pomSha1Map.get(pomSha1Path);
    }

    public void putPomContent(String pomPath, String pomContent) {

        //--POM CONTENT
        pomContentMap.put(pomPath, pomContent);

        //--SHA1
        final String sha1Path = pomPath + ".sha1";
        digester.reset();
        final byte[] bytes = pomContent.getBytes();
        digester.update(bytes, 0, bytes.length);
        final String sha1 = encode(digester.digest());
        pomSha1Map.put(sha1Path, sha1);

    }


    private String encode(byte[] binaryData) {
        if (binaryData.length != 16 && binaryData.length != 20) {
            int bitLength = binaryData.length * 8;
            throw new IllegalArgumentException("Unrecognised length for binary data: " + bitLength + " bits");
        }

        String retValue = "";

        for (int i = 0; i < binaryData.length; i++) {
            String t = Integer.toHexString(binaryData[i] & 0xff);

            if (t.length() == 1) {
                retValue += ("0" + t);
            } else {
                retValue += t;
            }
        }

        return retValue.trim();
    }

}
