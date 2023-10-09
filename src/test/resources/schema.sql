DROP TABLE IF EXISTS `bookmark`;
DROP TABLE IF EXISTS `palette`;
DROP TABLE IF EXISTS `tag`;
DROP TABLE IF EXISTS `member`;

CREATE TABLE `member` (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	email VARCHAR(100) UNIQUE,
	nickname VARCHAR(20) UNIQUE,
	password VARCHAR(255),
	provider VARCHAR(10) DEFAULT 'BASIC',
	role VARCHAR(10) DEFAULT 'MEMBER',
	is_deleted BOOLEAN DEFAULT false
);

CREATE TABLE `tag` (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(20)
);

CREATE TABLE `palette` (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	member_id BIGINT,
	color1 VARCHAR(6),
	color2 VARCHAR(6),
	color3 VARCHAR(6),
	color4 VARCHAR(6),
	tag_id BIGINT,
	views INT DEFAULT 0,
	created_at TIMESTAMP,
	is_deleted BOOLEAN DEFAULT false,
	FOREIGN KEY(member_id) REFERENCES `member`(id),
	FOREIGN KEY(tag_id) REFERENCES `tag`(id)
);

CREATE TABLE `bookmark` (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	member_id BIGINT,
	palette_id BIGINT,
	FOREIGN KEY(member_id) REFERENCES `member`(id),
	FOREIGN KEY(palette_id) REFERENCES `palette`(id)
);
