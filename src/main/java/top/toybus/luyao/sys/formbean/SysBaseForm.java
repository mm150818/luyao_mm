package top.toybus.luyao.sys.formbean;

import lombok.Data;

@Data
public abstract class SysBaseForm {
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
