package com.cohen.redis.assembly.cache.manager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 缓存失效策略，最近最少使用
 *
 * @author 林金成
 * @date 2018/5/1718:00
 */
public class LRUCacheManager implements CacheManager {

    private Map<String, Map<String, Object>> cacheMap = null;
    private int initCacheSize;// 缓存大小，默认为1024

    private LRUCacheManager() {
    }

    public LRUCacheManager(int initCacheSize) {
        this.initCacheSize = initCacheSize;
    }

    @Override
    public boolean containsNamespaceInCacheMap(String namespace) {
        return this.cacheMap.containsKey(namespace);
    }

    @Override
    public boolean containsCacheKeyInCacheMap(String namespace, String key) {
        return ((cacheMap.get(namespace) != null) && (cacheMap.get(namespace).containsKey(key)));
    }

    /**
     * 将缓存的key保存至map
     *
     * @return : 当map大小达到设置值时，会删除最老元素并返回其key，根据返回的key删除redis中的缓存数据
     */
    @Override
    public String cacheKey(String namespace, String key) {
        this.validNamespace(namespace);// 判断namespace是否存在，不存在创建一个LinkedHashMap放到CacheMap中
        return (String) this.cacheMap.get(namespace).put(key, null);
    }

    public void moveKey(String namespace, String key) {
        this.cacheMap.get(namespace).get(key);// LinkedHashMap如果入参assessOrder设置为true, 则调用get方法时会将get到的元素从链表中移动至末端
    }

    /**
     * 判断namespace是否存在，不存在则创建
     *
     * @param namespace
     */
    private void validNamespace(String namespace) {
        if (this.cacheMap.get(namespace) == null) {
            // accessOrder设为true时，LinkedHashMap在执行get操作时，会将get到的元素移动到链表的末端
            this.cacheMap.put(namespace, new LinkedHashMap<String, Object>(initCacheSize, 0.75f, true) {
                /**
                 * 保存当前map中的最老元素
                 */
                private Object eldest;

                /**
                 * 重写LinkedHashMap的removeEldestEntry方法
                 * 是否删除最老元素
                 */
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                    if (this.size() <= initCacheSize + 1) {// 如果插入后当前map的大小比initCacheSize大，则返回true删除最老元素
                        this.eldest = null;// 不需要删除时，返回null
                        return false;
                    }
                    this.eldest = eldest;// 需要删除时，赋值
                    return true;
                }

                /**
                 * 重写put方法，使其返回当前集合最老元素
                 */
                @Override
                public Object put(String key, Object value) {
                    super.put(key, value);// 调用父类方法，存储key和value
                    return this.eldest;// 如果本次插入需要删除元素，这里会返回最老元素值，如果不需要删除，会返回null
                }
            });
        }
    }
}
