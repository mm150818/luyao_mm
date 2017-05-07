package top.toybus.luyao.api.formbean;

import lombok.Data;
import top.toybus.luyao.api.entity.User;

@Data
public abstract class BaseForm {
	/** 登录的用户 */
	private User loginUser;

	/**
	 * @return 默认分页索引，基于0
	 */
	public int getPage() {
		return 0;
	}

	/**
	 * @return 分页大小，默认10
	 */
	public int getSize() {
		return 10;
	}
}
