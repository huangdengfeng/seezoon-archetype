package com.seezoon.domain.dao.mapper;

import com.seezoon.domain.dao.po.TaskInfoPO;
import org.apache.ibatis.annotations.Param;

public interface TaskInfoMapper {

    /**
     * 初始化锁记录（lock_by=NULL 表示空闲）
     */
    int insert(TaskInfoPO row);

    /**
     * 按 task_id 查询
     */
    TaskInfoPO selectByTaskId(String taskId);

    /**
     * tryLock：非阻塞尝试加锁。
     * 条件：lock_by IS NULL（空闲）或 expire_time < NOW()（锁已过期）
     *
     * @return affected rows，1=加锁成功，0=锁被占用
     */
    int tryLock(@Param("taskId") String taskId, @Param("lockBy") String lockBy, @Param("lockSeconds") int lockSeconds);

    /**
     * unlock：释放锁（仅持有者可释放）
     *
     * @return affected rows，1=释放成功，0=非持有者或锁不存在
     */
    int unlock(@Param("taskId") String taskId, @Param("lockBy") String lockBy);

    /**
     * renew：续期（仅持有者可续期）
     *
     * @return affected rows，1=续期成功，0=非持有者或锁不存在
     */
    int renew(@Param("taskId") String taskId, @Param("lockBy") String lockBy, @Param("lockSeconds") int lockSeconds);
}
