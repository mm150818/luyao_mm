package top.toybus.luyao.common.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import top.toybus.luyao.api.formbean.BaseForm;

/**
 * 分页工具类
 * 
 * @author sunxg
 *
 */
public class PageUtils {
    /**
     * JPA分页对象转Map
     * 
     * @param listKey
     *            列表键名
     * @param page
     *            分页列表对象
     * @return Map
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

    /**
     * 表单转分页对象
     * 
     * @param baseForm
     * @return PageRequest
     */
    public static Pageable toPageRequest(BaseForm baseForm) {
        return new PageRequest(baseForm.getPage(), baseForm.getSize());
    }

}
