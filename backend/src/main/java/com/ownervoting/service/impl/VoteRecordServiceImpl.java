package com.ownervoting.service.impl;

import com.ownervoting.config.CacheConfig;
import com.ownervoting.exception.BusinessException;
import com.ownervoting.model.entity.VoteRecord;
import com.ownervoting.repository.VoteRecordRepository;
import com.ownervoting.service.VoteRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.ownervoting.service.SystemLogService;
import com.ownervoting.model.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class VoteRecordServiceImpl implements VoteRecordService {

    @Autowired
    private VoteRecordRepository voteRecordRepository;

    @Autowired
    private SystemLogService systemLogService;

    // 基于内存的简单分布式锁（生产环境可改为Redis）
    private static final ConcurrentHashMap<String, ReentrantLock> VOTE_LOCKS = new ConcurrentHashMap<>();
    
    /**
     * 获取投票锁
     * @param topicId 投票议题ID
     * @param voterId 投票人ID
     * @param houseId 房产ID
     * @return 锁对象
     */
    private ReentrantLock getVoteLock(Long topicId, Long voterId, Long houseId) {
        String lockKey = String.format("vote:%d:%d:%d", topicId, voterId, houseId);
        return VOTE_LOCKS.computeIfAbsent(lockKey, k -> new ReentrantLock());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE) // 使用最高隔离级别确保数据一致性
    public VoteRecord addVoteRecord(VoteRecord record) {
        Long topicId = record.getTopic().getId();
        Long voterId = record.getVoter().getId();
        Long houseId = record.getHouse().getId();
        
        ReentrantLock lock = getVoteLock(topicId, voterId, houseId);
        try {
            // 尝试获取锁
            if (!lock.tryLock()) {
                throw new BusinessException("您的投票请求正在处理中，请勿重复提交");
            }
            
            // 检查是否已投票（同一议题、同一用户、同一房产只能投一次）
            List<VoteRecord> existingRecords = findByTopicIdAndVoterIdAndHouseId(topicId, voterId, houseId);
            if (!existingRecords.isEmpty()) {
                throw new BusinessException(409, "您已投过票");
            }
            
            // 保存投票记录
            VoteRecord saved = voteRecordRepository.save(record);
            
            // 清除相关缓存
            // 注: 因为使用了内存缓存无法手动清除，在生产环境使用Redis时应用@CacheEvict注解
            
            // 日志埋点
            SystemLog log = new SystemLog();
            log.setUserId(voterId);
            log.setUserType(SystemLog.UserType.OWNER);
            log.setOperation("投票");
            log.setDetail("用户投票，议题ID：" + topicId);
            systemLogService.addLog(log);
            
            return saved;
        } finally {
            // 确保释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional
    public void deleteVoteRecord(Long id) {
        voteRecordRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = CacheConfig.VOTE_CACHE, key = "'vote_record:' + #id")
    public VoteRecord findById(Long id) {
        return voteRecordRepository.findById(id).orElse(null);
    }

    @Override
    @Cacheable(value = CacheConfig.VOTE_CACHE, key = "'vote_records:topic:' + #topicId")
    public List<VoteRecord> findByTopicId(Long topicId) {
        return voteRecordRepository.findByTopicId(topicId);
    }

    @Override
    @Cacheable(value = CacheConfig.VOTE_CACHE, key = "'vote_records:owner:' + #ownerId")
    public List<VoteRecord> findByOwnerId(Long ownerId) {
        return voteRecordRepository.findByVoterId(ownerId);
    }

    @Override
    public List<VoteRecord> findByTopicIdAndVoterIdAndHouseId(Long topicId, Long voterId, Long houseId) {
        return voteRecordRepository.findByTopicIdAndVoterIdAndHouseId(topicId, voterId, houseId);
    }

    @Override
    public Page<VoteRecord> findPage(Pageable pageable) {
        return voteRecordRepository.findAll(pageable);
    }
} 