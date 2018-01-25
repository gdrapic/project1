

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component("AppCache")
public class AppCache implements Cache, InitializingBean{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AppCache.class);
	
	private CacheManager cacheManager;
	
	private Cache cache;
	
	private static final LinkedBlockingQueue<Object> cacheKeys = new LinkedBlockingQueue<Object>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		cacheManager = new ConcurrentMapCacheManager(AppConstants.APP_CACHE);
		cache = cacheManager.getCache(AppConstants.APP_CACHE);
	}

	@Override
	public String getName() {
		return cache.getName();
	}

	@Override
	public Object getNativeCache() {
		return cache.getNativeCache();
	}

	@Override
	public ValueWrapper get(Object key) {
		return cache.get(key);
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		return cache.get(key,type);
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		return cache.get(key,valueLoader);
	}

	@Override
	public void put(Object key, Object value) {
		try {
			if(!cacheKeys.contains(key)) {
				cacheKeys.put(key);
				cache.putIfAbsent(key, value);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		try {
			if(!cacheKeys.contains(key)) {
				cacheKeys.put(key);
				return cache.putIfAbsent(key, value);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void evict(Object key) {
		cache.evict(key);
		cacheKeys.remove(key);
	}

	@Override
	public void clear() {
		cache.clear();
		cacheKeys.clear();
	}

	@Scheduled(initialDelay=1*60*1000, fixedDelay = 5*60*1000 )
	private void cleanupCache() {
	
		LOGGER.info("Cleaning up Cache...");
		synchronized(cacheKeys) {
			for (Object object : cacheKeys) {
				
				String key = (String) object;
				if(!key.startsWith(AppConstants.APP_CACHE_KEY)) continue;
				
									if(idcCompleted) {
						LOGGER.info("Evicting Caches: {}", object);
						cache.evict(object);	
					}
				}
			}
		}
		LOGGER.info("Cleaning up Cache done!");
	}
}
