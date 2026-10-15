package com.hostel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/** Stateless signed token: base64(user|role|expiry).HMAC-SHA256 */
@Component
public class TokenService {
  @Value("${app.secret}") private String secret;
  private static String b64(byte[] b) { return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }
  private String sign(String p) {
    try { Mac m = Mac.getInstance("HmacSHA256"); m.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")); return b64(m.doFinal(p.getBytes(StandardCharsets.UTF_8))); }
    catch (Exception e) { throw new IllegalStateException(e); }
  }
  public String create(String user, Role role) {
    String p = user + "|" + role + "|" + (System.currentTimeMillis() + 8 * 3600_000L);
    return b64(p.getBytes(StandardCharsets.UTF_8)) + "." + sign(p);
  }
  /** returns [username, role] or null if invalid/expired */
  public String[] parse(String t) {
    try {
      String[] x = t.split("\\.");
      String p = new String(Base64.getUrlDecoder().decode(x[0]), StandardCharsets.UTF_8);
      if (!MessageDigest.isEqual(sign(p).getBytes(), x[1].getBytes())) return null;
      String[] f = p.split("\\|");
      return Long.parseLong(f[2]) < System.currentTimeMillis() ? null : f;
    } catch (Exception e) { return null; }
  }
}
