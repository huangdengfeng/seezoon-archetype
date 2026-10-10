package com.seezoon.domain.dao.po;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 * 任务锁
 *
 * <p>表 {@code t_task_info}：
 *
 * <ul>
 *   <li>记录预初始化（{@code lock_by} 为 {@code NULL} 表示空闲）
 *   <li>运行时通过 UPDATE 控制加锁/释放，不增删记录
 *   <li>并发安全依赖 InnoDB 行锁 + {@code lock_by / expire_time} 条件判断
 * </ul>
 */
@Getter
@Setter
public class TaskInfoPO {

    /** 任务标识（主键，锁的唯一 key） */
    private String taskId;

    /** 锁持有者标识（NULL 表示空闲） */
    private String lockBy;

    /** 获取锁的时间 */
    private Instant lockTime;

    /** 锁过期时间 */
    private Instant expireTime;

    /** 创建时间 */
    private Instant createTime;

    /** 更新时间 */
    private Instant updateTime;
}
