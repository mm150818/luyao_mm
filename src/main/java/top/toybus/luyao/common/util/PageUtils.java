package top.toybus.luyao.common.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;

/**
 * 分页工具类
 * 
 * @author sunxg
 *
 */
public class PageUtils {
	/**
	 * JPA分页对象转
	 */
	public static Map<String, Object> toMap(String listKey, Page<?> page) {
		Map<String, Object> map = new HashMap<>();
		map.put(listKey, page.getContent());
		Map<String, Object> pager = new HashMap<>();
		pager.put("page", page.getNumber());
		pager.put("size", page.getSize());
		pager.put("total", page.getTotalElements());
		pager.put("totalPages", page.getTotalPages());
		pager.put("first", page.isFirst());
		pager.put("last", page.isLast());
		map.put("pager", pager);
		return map;
	}
}
