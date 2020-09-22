 CREATE TABLE IF NOT EXIST `participant_tbl` (
  `meeting_id` varchar(250) NOT NULL,
  `participant_id` varchar(250) NOT NULL,
  `instance_id` int(11) NOT NULL,
  `user_id` varchar(250) DEFAULT NULL,
  `join_time` datetime DEFAULT NULL,
  `leave_time` datetime DEFAULT NULL,
  PRIMARY KEY (`meeting_id`,`participant_id`,`instance_id`)
)