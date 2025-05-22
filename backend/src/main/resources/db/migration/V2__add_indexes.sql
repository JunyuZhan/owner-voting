-- 为投票主题表添加索引
CREATE INDEX idx_vote_topic_community_id ON vote_topic (community_id);
CREATE INDEX idx_vote_topic_status ON vote_topic (status);
CREATE INDEX idx_vote_topic_start_end_time ON vote_topic (start_time, end_time);

-- 为投票记录表添加索引
CREATE INDEX idx_vote_record_topic_id ON vote_record (topic_id);
CREATE INDEX idx_vote_record_voter_id ON vote_record (voter_id);
CREATE INDEX idx_vote_record_option_id ON vote_record (option_id);
CREATE INDEX idx_vote_record_house_id ON vote_record (house_id);
CREATE INDEX idx_vote_record_topic_voter_house ON vote_record (topic_id, voter_id, house_id);

-- 为投票选项表添加索引
CREATE INDEX idx_vote_option_topic_id ON vote_option (topic_id);

-- 为业主表添加索引
CREATE INDEX idx_owner_community_id ON owner (community_id);
CREATE INDEX idx_owner_phone ON owner (phone);
CREATE INDEX idx_owner_id_card ON owner (id_card);
CREATE INDEX idx_owner_status ON owner (status);

-- 为房屋表添加索引
CREATE INDEX idx_house_community_id ON house (community_id);
CREATE INDEX idx_house_owner_id ON house (owner_id);
CREATE INDEX idx_house_building_unit ON house (building, unit);

-- 为系统日志表添加索引
CREATE INDEX idx_system_log_user_id ON system_log (user_id);
CREATE INDEX idx_system_log_user_type ON system_log (user_type);
CREATE INDEX idx_system_log_operation ON system_log (operation);
CREATE INDEX idx_system_log_created_time ON system_log (created_time);

-- 为公告表添加索引
CREATE INDEX idx_announcement_community_id ON announcement (community_id);
CREATE INDEX idx_announcement_type ON announcement (type);
CREATE INDEX idx_announcement_publish_time ON announcement (publish_time);

-- 为意见建议表添加索引
CREATE INDEX idx_suggestion_community_id ON suggestion (community_id);
CREATE INDEX idx_suggestion_owner_id ON suggestion (owner_id);
CREATE INDEX idx_suggestion_status ON suggestion (status);
CREATE INDEX idx_suggestion_created_at ON suggestion (created_at);

-- 为意见回复表添加索引
CREATE INDEX idx_suggestion_reply_suggestion_id ON suggestion_reply (suggestion_id);
CREATE INDEX idx_suggestion_reply_replier_id ON suggestion_reply (replier_id);
CREATE INDEX idx_suggestion_reply_replier_type ON suggestion_reply (replier_type);

-- 为审核日志表添加索引
CREATE INDEX idx_review_log_reviewer_id ON review_log (reviewer_id);
CREATE INDEX idx_review_log_owner_id ON review_log (owner_id);
CREATE INDEX idx_review_log_status ON review_log (status);
CREATE INDEX idx_review_log_review_time ON review_log (review_time); 