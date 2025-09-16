package com.example.msbackend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

  private final RedisTemplate<String, Object> redisTemplate;

  @Value("${redis.key.expire}")
  private Long defaultExpire; // 默认过期时间（从YML注入）

  public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  // ============================ String ==============================
  public void set(String key, Object value) {
    set(key, value, defaultExpire); // 使用默认过期时间
  }

  public boolean set(String key, Object value, long time) {
    try {
      if (time > 0) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
      } else {
        redisTemplate.opsForValue().set(key, value); // 无过期时间
      }
      return true;
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("设置Redis值失败，key: " + key + ", value: " + value + ", time: " + time);
      e.printStackTrace();
      return false;
    }
  }

  // ============================ Hash ==============================
  public boolean hset(String key, Map<String, Object> map) {
    return hset(key, map, defaultExpire); // 使用默认过期时间
  }

  public boolean hset(String key, Map<String, Object> map, long time) {
    try {
      redisTemplate.opsForHash().putAll(key, map);
      if (time > 0) expire(key, time);
      return true;
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("设置Redis Hash失败，key: " + key + ", map: " + map + ", time: " + time);
      e.printStackTrace();
      return false;
    }
  }

  public boolean hset(String key, String item, Object value) {
    return hset(key, item, value, defaultExpire); // 使用默认过期时间
  }

  public boolean hset(String key, String item, Object value, long time) {
    try {
      redisTemplate.opsForHash().put(key, item, value);
      if (time > 0) expire(key, time);
      return true;
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("设置Redis Hash字段失败，key: " + key + ", item: " + item + ", value: " + value + ", time: " + time);
      e.printStackTrace();
      return false;
    }
  }

  // ============================ Set ==============================
  public Long sSet(String key, Object... values) {
    return sSet(key, defaultExpire, values); // 使用默认过期时间
  }

  public Long sSet(String key, long time, Object... values) {
    try {
      Long count = redisTemplate.opsForSet().add(key, values);
      if (time > 0) expire(key, time);
      return count;
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("设置Redis Set失败，key: " + key + ", values: " + Arrays.toString(values) + ", time: " + time);
      e.printStackTrace();
      return 0L;
    }
  }

  // ============================ List ==============================
  public boolean lSet(String key, Object value) {
    return lSet(key, value, defaultExpire); // 使用默认过期时间
  }

  public boolean lSet(String key, Object value, long time) {
    try {
      redisTemplate.opsForList().rightPush(key, value);
      if (time > 0) expire(key, time);
      return true;
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("设置Redis List失败，key: " + key + ", value: " + value + ", time: " + time);
      e.printStackTrace();
      return false;
    }
  }

  public boolean lSet(String key, List<Object> value) {
    return lSet(key, value, defaultExpire); // 使用默认过期时间
  }

  public boolean lSet(String key, List<Object> value, long time) {
    try {
      redisTemplate.opsForList().rightPushAll(key, value);
      if (time > 0) expire(key, time);
      return true;
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("设置Redis List批量失败，key: " + key + ", value: " + value + ", time: " + time);
      e.printStackTrace();
      return false;
    }
  }

  // ============================ 通用方法 ==============================
  public void expire(String key, long time) {
    try {
      if (time > 0) {
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
      }
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("设置Redis键过期时间失败，key: " + key + ", time: " + time);
      e.printStackTrace();
    }
  }

  public long getExpire(String key) {
    try {
      return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("获取Redis键过期时间失败，key: " + key);
      e.printStackTrace();
      return -1L;
    }
  }

  public boolean hasKey(String key) {
    try {
      return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("检查Redis键是否存在失败，key: " + key);
      e.printStackTrace();
      return false;
    }
  }

  public void delKey(String... key) {
    if (key != null && key.length > 0) {
      try {
        if (key.length == 1) {
          redisTemplate.delete(key[0]);
        } else {
          redisTemplate.delete(Arrays.asList(key));
        }
      } catch (Exception e) {
        // TODO: 引入日志框架（如SLF4J）替换System.err
        System.err.println("删除Redis键失败，keys: " + Arrays.toString(key));
        e.printStackTrace();
      }
    }
  }

  public Object get(String key) {
    try {
      return key == null ? null : redisTemplate.opsForValue().get(key);
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("获取Redis值失败，key: " + key);
      e.printStackTrace();
      return null;
    }
  }

  public Object hget(String key, String item) {
    try {
      return key == null ? null : redisTemplate.opsForHash().get(key, item);
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("获取Redis Hash值失败，key: " + key + ", item: " + item);
      e.printStackTrace();
      return null;
    }
  }

  public Map<Object, Object> hgetAll(String key) {
    try {
      return redisTemplate.opsForHash().entries(key);
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("获取Redis Hash所有值失败，key: " + key);
      e.printStackTrace();
      return null;
    }
  }

  public boolean hHasKey(String key, String item) {
    try {
      return Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, item));
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("检查Redis Hash字段是否存在失败，key: " + key + ", item: " + item);
      e.printStackTrace();
      return false;
    }
  }

  public Set<Object> sGet(String key) {
    try {
      return redisTemplate.opsForSet().members(key);
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("获取Redis Set值失败，key: " + key);
      e.printStackTrace();
      return null;
    }
  }

  public boolean sHasKey(String key, Object value) {
    try {
      return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("检查Redis Set元素是否存在失败，key: " + key + ", value: " + value);
      e.printStackTrace();
      return false;
    }
  }

  public Long sRemove(String key, Object... values) {
    try {
      return redisTemplate.opsForSet().remove(key, values);
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("从Redis Set移除元素失败，key: " + key + ", values: " + Arrays.toString(values));
      e.printStackTrace();
      return 0L;
    }
  }

  public Long sGetSetSize(String key) {
    try {
      return redisTemplate.opsForSet().size(key);
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("获取Redis Set大小失败，key: " + key);
      e.printStackTrace();
      return 0L;
    }
  }

  public List<Object> lGet(String key, long start, long end) {
    try {
      return redisTemplate.opsForList().range(key, start, end);
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("获取Redis List范围元素失败，key: " + key + ", start: " + start + ", end: " + end);
      e.printStackTrace();
      return null;
    }
  }

  public Long lGetListSize(String key) {
    try {
      return redisTemplate.opsForList().size(key);
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("获取Redis List长度失败，key: " + key);
      e.printStackTrace();
      return 0L;
    }
  }

  public Object lGetIndex(String key, long index) {
    try {
      return redisTemplate.opsForList().index(key, index);
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("获取Redis List指定索引元素失败，key: " + key + ", index: " + index);
      e.printStackTrace();
      return null;
    }
  }

  public Long incr(String key, long delta) {
    try {
      return redisTemplate.opsForValue().increment(key, delta);
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("Redis自增操作失败，key: " + key + ", delta: " + delta);
      e.printStackTrace();
      return 0L;
    }
  }

  public Long lRemove(String key, long count, Object value) {
    try {
      return redisTemplate.opsForList().remove(key, count, value);
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("从Redis List移除元素失败，key: " + key + ", count: " + count + ", value: " + value);
      e.printStackTrace();
      return 0L;
    }
  }

  public boolean lUpdateIndex(String key, long index, Object value) {
    try {
      redisTemplate.opsForList().set(key, index, value);
      return true;
    } catch (Exception e) {
      // TODO: 引入日志框架（如SLF4J）替换System.err
      System.err.println("更新Redis List指定索引元素失败，key: " + key + ", index: " + index + ", value: " + value);
      e.printStackTrace();
      return false;
    }
  }
}