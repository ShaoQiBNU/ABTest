import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class AbBucketInfo {

    // 获取用户ab信息
    public Long getAbBucket(Long userId, Map<String, Integer> abBucketWorldInfo) {

        String s = String.valueOf(abBucketWorldInfo.values().stream().mapToLong(x->x).sum() + userId);

        Long abBucket = getBucketId(getMd5(s));

        return abBucket;
    }

    // 桶号
    public Long getBucketId(String input) {

        Long id = Long.parseLong(input.substring(10, 25).trim(), 16) % 10;

        return id;
    }

    // md5 hash
    public String getMd5(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Map<String, Integer>> abBucketWorldInfo = new HashMap<String, Map<String, Integer>>() {{

        put("push_uid_world_0",
                new HashMap<String, Integer>() {{
                    put("id", 0);
                    put("type", 0);
                    put("biz", 0);
                    put("ct", 1648698904);
                }}
        );

        put("push_uid_world_1",
                new HashMap<String, Integer>() {{
                    put("id", 1);
                    put("type", 1);
                    put("biz", 1);
                    put("ct", 1647578681);
                }}
        );

    }};

    public static void main(String[] args) {

        AbBucketInfo abBucketInfo = new AbBucketInfo();

        Long user_id_start = 353438170L;

        for(String world : abBucketWorldInfo.keySet()) {

            System.out.println(world);

            for(Long i=0L; i<4L; i++) {

                Long user_id = user_id_start + i;

                Long abBucket = abBucketInfo.getAbBucket(user_id, abBucketWorldInfo.get(world));

                System.out.println("user: " + user_id + "   ab_bucket:    " + world + "-exp" + abBucket);

            }
        }
    }

}
