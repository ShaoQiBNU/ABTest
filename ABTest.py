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
