[toc]
# 工业界ABTest生成逻辑
## ABTest定义

ABTest实验分组是算法工程师上线策略必须用到的工具，保证流量分组的公平和均匀是实验置信的关键，所以AB设计尤其重要。

参考：https://yangwenbo.com/articles/abtest-traffic-diversion.html

## 生成AB分组逻辑
AB分层和分组逻辑常采用user_id + seed的方式，然后调用hash函数，取模得到桶号，seed为随机数种子，跟每层的参数设置相关，具体实现如下，python和java可以保证完全一致，也能够作为两种语言的转换。

### python
```python
# -*- coding: utf-8 -*-

import hashlib

# ab层的参数
worlds_info = {
    'push_uid_world_0': {
        'id': 0,
        'type': 0,
        'biz': 0,
        'ct': 1648698904,
    },

    'push_uid_world_1': {
        'id': 1,
        'type': 1,
        'biz': 1,
        'ct': 1647578681,
    }
}

# hash获取ab
def lookup_user_bucket(user_id, world_name):
    world = worlds_info[world_name]
    world_id = world['id']
    world_type = world['type']
    world_biz = world['biz']
    world_ct = world['ct']
    seed = world_id + world_type + world_biz + world_ct
    md5 = hashlib.md5(str(user_id+seed).encode("utf8"))
    hexdigest = md5.hexdigest()
    hexdigest = hexdigest[10:25]
    hexdigest_as_int = int(hexdigest, 16)
    return hexdigest_as_int % 10


if __name__ == '__main__':

    user_id_start = 353438170
    user_ids = [user_id_start + i for i in range(4)]

    world_name = ['push_uid_world_0', 'push_uid_world_1']

    for world in world_name:

        print(world)

        for user in user_ids:

            bucket_id = lookup_user_bucket(user, world)

            print("user: " + str(user) + "   ab_bucket:    " + world + "-exp" + str(bucket_id))

```

### java
```java
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

```

输出结果如下：
```
push_uid_world_0
user: 353438170   ab_bucket:    push_uid_world_0-exp6
user: 353438171   ab_bucket:    push_uid_world_0-exp0
user: 353438172   ab_bucket:    push_uid_world_0-exp2
user: 353438173   ab_bucket:    push_uid_world_0-exp4
push_uid_world_1
user: 353438170   ab_bucket:    push_uid_world_1-exp8
user: 353438171   ab_bucket:    push_uid_world_1-exp1
user: 353438172   ab_bucket:    push_uid_world_1-exp1
user: 353438173   ab_bucket:    push_uid_world_1-exp8
```