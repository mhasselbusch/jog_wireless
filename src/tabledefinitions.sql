/*
  STORE table
*/
create table store
(store_id varchar(5),
 address  varchar(40),
 city varchar(40),
 state  varchar(20),
 phone_number varchar(10) check (length(phone_number) = 10),
 primary key (store_id));
/*
  INVENTORY table
*/
create table inventory
(store_id varchar(5),
 model varchar(15),
 manufacturer varchar(15),
 quantity numeric(3,0),
 primary key (store_id, model, manufacturer),
 foreign key (store_id) references store on delete cascade);
/*
  SOLDPHONES table
*/
create table soldPhones
(store_id varchar(5),
 meid varchar(14) check (length(meid) = 14),
 model varchar(15),
 manufacturer varchar(15),
 activate_date timestamp(0),
 primary key (meid),
 foreign key (store_id) references store);

/*
  REMOVEDPHONES table
*/

create table removedPhones
(meid varchar(14) check (length(meid) = 14),
 remove_date timestamp(0),
 primary key (meid),
 foreign key (meid) references soldPhones on delete cascade);

/*
  PHONENUMBER table
*/

create table phoneNumber
 (account_number varchar(5),
  phone_number varchar(10) check (length(phone_number) = 10),
  meid varchar(14) constraint meid unique check (length(meid) = 14),
  primary key (phone_number),
  foreign key (account_number) references account on delete cascade,
  foreign key (meid) references soldPhones on delete set null);

/*
  DATALOG table
*/
create table dataLog
(phone_number varchar(10) check (length(phone_number) = 10),
 size_kb numeric(3,0) check (size_kb > 0),
 data_date timestamp(0),
 primary key (phone_number, data_date),
 foreign key (phone_number) references phoneNumber on delete cascade);

/*
  CALLLOG table
*/
create table callLog
(phone_number varchar(10) check (length(phone_number) = 10),
 source_number varchar(10) check (length(source_number) = 10),
 dest_number varchar(10) check (length(dest_number) = 10),
 call_starttime timestamp(0),
 call_endtime timestamp(0),
 duration varchar(6),
 primary key (phone_number, source_number, dest_number, call_starttime),
 foreign key (phone_number) references phoneNumber on delete cascade);

/*
  TEXTLOG table
*/

create table textLog
(phone_number varchar(10) check (length(phone_number) = 10),
 source_number varchar(10) check (length(source_number) = 10),
 dest_number varchar(10) check (length(dest_number) = 10),
 text_datetime timestamp(0),
 size_b numeric(3,0) check (size_b > 0),
 primary key (phone_number, source_number, dest_number, text_datetime),
 foreign key (phone_number) references phoneNumber on delete cascade);

/*
  ACCOUNT table
*/
create table account
(account_number varchar(5),
 customer_number varchar(5),
 primary_phone varchar(10) check (length(primary_phone) = 10),
 number_limit numeric(3,0) check (number_limit in (1,5,999)),
 phones_assigned numeric(3,0),
 bill_plan varchar(1) check (bill_plan in ('1','2','3')),
 primary key (account_number),
 foreign key (customer_number) references customer on delete cascade);
/*
  CUSTOMER table
*/
create table customer
(customer_number varchar(10),
store_id varchar(5),
name varchar(70),
address varchar(40),
city varchar(20),
state varchar(2),
primary key (customer_number),
foreign key (store_id) references store on delete set null);
/*
  PLAN1 table
*/
create table billsPlan1
(account_number varchar(5),
 month numeric(2,0) check (month >= 1 and month <= 12),
 year numeric(4,0) check (year >= 2014),
 num_min numeric(10,0) check (num_min >= 0),
 num_text numeric(10,0) check (num_text >= 0),
 num_gig numeric(10,2) check (num_gig >= 0),
 min_cost numeric(3,2) check (min_cost = 0.05),
 text_cost numeric(3,2) check (text_cost = 0.01),
 gig_cost numeric(1,0) check (gig_cost =5),
 total numeric(10,2) check (total >= 0),
 paid numeric(1,0) check (paid = 0 or paid = 1), 
 primary key(account_number, month, year),
 foreign key(account_number) references account);
/*
  PLAN2 table
*/
create table billsPlan2
(account_number varchar(5),
 month numeric(2,0) check (month >= 1 and month <= 12),
 year numeric(4,0) check (year >= 2014),
 num_min numeric(10,0) check (num_min >= 0),
 num_text numeric(10,0) check (num_text >= 0),
 num_gigs numeric(10,2) check (num_gigs >= 0),
 cost_month numeric(2,0) check (cost_month = 60),
 paid numeric(1,0) check (paid = 0 or paid = 1), 
 primary key(account_number, month, year),
 foreign key(account_number) references account);
 
/*
  PLAN3 table
*/
create table billsPlan3
(account_number varchar(5),
 month numeric(2,0) check (month >= 1 and month <= 12),
 year numeric(4,0) check (year >= 2014),
 num_min numeric(10,0) check (num_min >= 0),
 num_text numeric(10,0) check (num_text >= 0),
 num_gigs numeric(10,2) check (num_gigs >= 0),
 cost_month numeric(3,0) check (cost_month = 100),
 paid numeric(1,0) check (paid = 0 or paid = 1), 
 primary key(account_number, month, year),
 foreign key(account_number) references account);

 
