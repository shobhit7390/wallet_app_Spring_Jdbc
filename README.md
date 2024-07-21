

create database wallet_jdbc_db; 
use wallet_jdbc_db;

create table Users( 
 userId int auto_increment primary key, 
 userName varchar(50) not null,
 userPassword varchar(50) not null,
 userEmail varchar(50) not null unique,
 userPhone varchar(15) not null unique,
 upiId varchar(50) not null unique,
 accountNumber varchar(50) not null unique,
 userPin varchar(4) not null,
 balance decimal(10,2) not null default 0.0
 );

create table Transactions(
 transactionId int auto_increment primary key,
 userId int, amount decimal(10,2) not null,
 transactionType varchar(50) not null,
 transTimestamp timestamp default current_timestamp,
 foreign key(userId) references Users(userId)
 );

show databases; 
show tables;

drop table Users;
drop table Transactions;

ALTER TABLE Transactions MODIFY COLUMN transactionType VARCHAR(100);
delete from Users where userId=2;

Select * from Users where userName="jhgg" AND userPassword="nmnm";

select * from Users;

select * from Transactions;

insert into Users(userId, userName, userPassword, userEmail, balance) values(123, "Shobhit Yadav", "shobhit@123", "shobhit@email.com", 37000); 
insert into Users(userName, userPassword, userEmail, balance) values("Rohan Rana", "rohan@123", "rohan@email.com", 42000);
