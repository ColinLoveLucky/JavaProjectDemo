CREATE TABLE user (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  mobile varchar(255),
  nick_name varchar(255),
  password varchar(255),
  registration_date datetime,
  update_date datetime,
  PRIMARY KEY (id)
) ;
