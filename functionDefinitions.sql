create or replace function verifyAccount(input in account.account_number%type) return number
is
  cursor acc_cursor is select account_number from account;
  acc_num account.account_number%type;
begin
  open acc_cursor;
  fetch acc_cursor into acc_num;
  while acc_cursor%found
  loop
  if acc_num = input
  then
    return 1;
  end if;
  fetch acc_cursor into acc_num;
  end loop;
  return 0;
end;

create or replace function getLimit(input in account.account_number%type) return number
is
  cursor acc_cursor is select number_limit from account where account_number = input;
  limit number;
begin
  open acc_cursor;
  fetch acc_cursor into limit;
  return limit;
end;

create or replace function getAssigned(input in account.account_number%type) return number
is
  cursor acc_cursor is select phones_assigned from account where account_number = input;
  assigned number;
begin
  open acc_cursor;
  fetch acc_cursor into assigned;
  return assigned;
end;

create or replace function confirmAvailablePhones(input in account.account_number%type) return number
is
  cursor acc_cursor is select number_limit, phones_assigned from account where account_number = input;
  limit number;
  assigned number;
begin
  open acc_cursor;
  fetch acc_cursor into limit, assigned;
  
  /*
    Individual account (or empty account)
  */
  if assigned = 0
  then 
    return 1;
  end if;
  /*
    Family account with free space
  */
  if limit = 5 and assigned < 5
  then  
    return 1;
  end if;
  /*
    Business account (unlimited phones)
  */
  if limit = 999
  then 
    return 1;
  end if;
  /*
    If the account cannot have more phones added
  */
  return 0;
end;

create or replace function checkPhoneNum(input in phonenumber.phone_number%type) return number
is
  cursor phone_cursor is select phone_number from phonenumber;
  phone_num phonenumber.phone_number%type;
begin
  open phone_cursor;
  fetch phone_cursor into phone_num;
  while phone_cursor%found
  loop
  if phone_num = input
  then 
    return 0;
  end if;
  fetch phone_cursor into phone_num;
  end loop;
  return 1;
end;

create or replace function checkMEID(input in phonenumber.meid%type) return number
is
  cursor meid_cursor is select meid from phonenumber;
  meid phonenumber.meid%type;
begin
  open meid_cursor;
  fetch meid_cursor into meid;
  while meid_cursor%found
  loop
  if meid = input
  then 
    return 0;
  end if;
  fetch meid_cursor into meid;
  end loop;
  return 1;
end;

create or replace function genBill1(account_num in account.account_number%type, bill_month in billsPlan1.month%type, 
bill_year in billsPlan1.year%type) RETURN number
is 
  phone_num varchar(10);
  min_total number;
  data_total number;
  text_total number;
  total number;
  cursor phone_cursor is select phone_number from phonenumber where account_number = account_num;
  cursor check_cursor is select account_number,month,year from billsplan1 where account_number = account_num and month = bill_month and year = bill_year;
  check_var varchar(5);
  check_month number;
  check_year number;
begin
  min_total := 0;
  data_total := 0;
  text_total := 0;
  open check_cursor;
  fetch check_cursor into check_var, check_month, check_year; 
  if check_var = account_num and check_month = bill_month and check_year = bill_year
  then
    return 1;
  end if;
  open phone_cursor;
  fetch phone_cursor into phone_num;
  while phone_cursor%found
  loop
    min_total := min_total + getTotalMins(bill_month, bill_year, phone_num);
    data_total := data_total + getTotalData(bill_month, bill_year, phone_num);
    text_total := text_total + getTotalText(bill_month, bill_year, phone_num);
  fetch phone_cursor into phone_num;
  end loop;
  min_total := round((min_total / 60),0);
  data_total := round((data_total / 1000000), 2);
  total := round((min_total * 0.05) + (data_total * 5) + (text_total * 0.01), 2);
  insert into billsPlan1 
  values(account_num,bill_month,bill_year,min_total,text_total,data_total,0.05,0.01,5,total,0);
  return 1;
exception 
  when others then
    return 0;
end;

create or replace function genBill2(account_num in account.account_number%type, bill_month in billsPlan2.month%type, 
bill_year in billsPlan2.year%type) RETURN number
is 
  phone_num varchar(10);
  min_total number;
  data_total number;
  text_total number;
  cursor phone_cursor is select phone_number from phonenumber where account_number = account_num;
  cursor check_cursor is select account_number,month,year from billsplan2 where account_number = account_num and month = bill_month and year = bill_year;
  check_var varchar(5);
  check_month number;
  check_year number;
begin
  min_total := 0;
  data_total := 0;
  text_total := 0;
  open check_cursor;
  fetch check_cursor into check_var, check_month, check_year; 
  if check_var = account_num and check_month = bill_month and check_year = bill_year
  then
    return 1;
  end if;
  open phone_cursor;
  fetch phone_cursor into phone_num;
  while phone_cursor%found
  loop
    min_total := min_total + getTotalMins(bill_month, bill_year, phone_num);
    data_total := data_total + getTotalData(bill_month, bill_year, phone_num);
    text_total := text_total + getTotalText(bill_month, bill_year, phone_num);
  fetch phone_cursor into phone_num;
  end loop;
  min_total := round((min_total / 60),0);
  data_total := round((data_total / 1000000), 2);
  insert into billsPlan2 
  values(account_num,bill_month,bill_year,min_total,text_total,data_total,60,0);
  return 1;
exception 
  when others then
    return 0;
end;

create or replace function genBill3(account_num in account.account_number%type, bill_month in billsPlan3.month%type, 
bill_year in billsPlan3.year%type) RETURN number
is 
  phone_num varchar(10);
  min_total number;
  data_total number;
  text_total number;
  cursor phone_cursor is select phone_number from phonenumber where account_number = account_num;
  cursor check_cursor is select account_number,month,year from billsplan3 where account_number = account_num and month = bill_month and year = bill_year;
  check_var varchar(5);
  check_month number;
  check_year number;
begin
  min_total := 0;
  data_total := 0;
  text_total := 0;
  open check_cursor;
  fetch check_cursor into check_var, check_month, check_year; 
  if check_var = account_num and check_month = bill_month and check_year = bill_year
  then
    return 1;
  end if;
  open phone_cursor;
  fetch phone_cursor into phone_num;
  while phone_cursor%found
  loop
    min_total := min_total + getTotalMins(bill_month, bill_year, phone_num);
    data_total := data_total + getTotalData(bill_month, bill_year, phone_num);
    text_total := text_total + getTotalText(bill_month, bill_year, phone_num);
  fetch phone_cursor into phone_num;
  end loop;
  min_total := round((min_total / 60),0);
  data_total := round((data_total / 1000000), 2);
  insert into billsPlan3 
  values(account_num,bill_month,bill_year,min_total,text_total,data_total,100,0);
  return 1;
exception 
  when others then
    return 0;
end;

create or replace function generateBill(account_num in account.account_number%type, month in billsPlan1.month%type, bill_year in billsPlan1.year%type) RETURN number
is
  bill_type varchar(1);
  cursor acc_cursor is select bill_plan from account where account_number = account_num;
begin
  
  if bill_year > extract(year from SYSTIMESTAMP)
  then
      return 2;
  end if;
  if month > extract(month from SYSTIMESTAMP)
  then
    if bill_year >= extract(year from SYSTIMESTAMP)
    then
      return 3;
    end if;
  end if;
  
  open acc_cursor;
  fetch acc_cursor into bill_type;
  if bill_type = '1'
  then
    return genBill1(account_num,month,bill_year);
  end if;
  if bill_type = '2'
  then
    return genBill2(account_num,month,bill_year);
  end if;
  if bill_type = '3'
  then
    return genBill3(account_num,month,bill_year);
  end if;
  return 0;
end;

create or replace function generateDuration(input in varchar) return number
as
  cursor call_cursor is (select phone_number, source_number, dest_number, call_starttime, call_endtime, duration from calllog);
  phone varchar(10);
  source varchar(10);
  dest varchar(10);
  start_time timestamp;
  end_time timestamp;
  duration number;
begin
  open call_cursor;
  fetch call_cursor into phone, source, dest, start_time, end_time, duration;
  while call_cursor%found
  loop
    if duration is null
    then
      update calllog 
      set duration = (extract (day from (end_time-start_time))*24*60*60+
        extract (hour from (end_time-start_time))*60*60+
        extract (minute from (end_time-start_time))*60+
        extract (second from (end_time-start_time)))
      where phone_number = phone and source_number = source and
      dest_number = dest and call_starttime = start_time;
    end if;
    fetch call_cursor into phone, source, dest, start_time, end_time, duration;
  end loop;
  return 1;
end;

create or replace function getData(phone_num in datalog.phone_number%type) return number
is
  temp_data number;
  total_data number;
  cursor size_cursor is select to_number(size_kb) from datalog where phone_number = phone_num;
begin
  temp_data :=0;
  total_data :=0;
  open size_cursor;
  fetch size_cursor into temp_data;
  while size_cursor%found
  loop
    total_data := total_data + temp_data;
    fetch size_cursor into temp_data;
  end loop;
  return total_data;
exception 
  when others then
    return 0;
end;

create or replace FUNCTION getDate return TIMESTAMP
is
  char_time varchar2(100);
begin
  char_time := to_char(SYSTIMESTAMP, 'DD-MON-RR HH12:MI:SS');
  return char_time;
end;

create or replace function getLimit(input in account.account_number%type) return number
is
  cursor acc_cursor is select number_limit from account where account_number = input;
  limit number;
  assigned number;
begin
  open acc_cursor;
  fetch acc_cursor into limit;
  return limit;
end;

create or replace function getMins(phone_num in calllog.phone_number%type) return number
is
  temp_mins number;
  total_mins number;
  cursor duration_cursor is select to_number(duration) from calllog where phone_number = phone_num;
begin
  total_mins := 0;
  open duration_cursor;
  fetch duration_cursor into temp_mins;
  while duration_cursor%found
  loop
    total_mins := total_mins + temp_mins;
    fetch duration_cursor into temp_mins;
  end loop;
  return total_mins;
exception 
  when others then
    return 0;
end;

create or replace function getTexts(phone_num in datalog.phone_number%type) return number
is
  total_text number;
  cursor text_cursor is select count(*) from textlog where phone_number = phone_num;
begin
  total_text := 0;
  open text_cursor;
  fetch text_cursor into total_text;
  return to_number(total_text);
exception 
  when others then
    return 0;
end;

create or replace function getTotalData(
bill_month in billsPlan1.month%type, 
bill_year in billsPlan1.year%type, 
phone_num in calllog.phone_number%type) RETURN number
is 
  current_total number;
  phone varchar(10);
  cursor phone_cursor is select phone_number from datalog where 
  extract (month from data_date) = bill_month and extract (year from data_date) = bill_year and phone_number = phone_num;
begin
  current_total := 0;
  open phone_cursor;
  fetch phone_cursor into phone;
  while phone_cursor%found
  loop
    current_total := current_total + getData(phone);
    fetch phone_cursor into phone;
  end loop;
  return current_total;
exception 
  when others then
    return 0;
end;

create or replace function getTotalMins(
bill_month in billsPlan1.month%type, 
bill_year in billsPlan1.year%type, 
phone_num in calllog.phone_number%type) RETURN number
is 
  current_total number;
  total number;
  cursor duration_cursor is select duration from calllog where 
  extract (month from call_starttime) = bill_month and extract(year from call_starttime) = bill_year
  and phone_number = phone_num;
begin
  current_total := 0;
  total := 0;
  open duration_cursor;
  fetch duration_cursor into current_total;
  while duration_cursor%found
  loop
    total := total + current_total;
    fetch duration_cursor into current_total;
  end loop;
  return total;
exception 
  when others then
    return 0;
end;

create or replace function getTotalText(
bill_month in billsPlan1.month%type, 
bill_year in billsPlan1.year%type, 
phone_num in calllog.phone_number%type) RETURN number
is 
  current_total number;
  phone varchar(10);
  cursor phone_cursor is select unique phone_number from textlog where 
  extract (month from text_datetime) = bill_month and extract (year from text_datetime) = bill_year and phone_number = phone_num;
begin
  current_total := 0;
  open phone_cursor;
  fetch phone_cursor into phone;
  while phone_cursor%found
  loop
    current_total := current_total + getTexts(phone);
    fetch phone_cursor into phone;
  end loop;
  return current_total;
exception 
  when others then
    return 0;
end;

create or replace function VERIFYACCOUNT(input in account.account_number%type) return number
is
  cursor acc_cursor is select account_number from account;
  acc_num account.account_number%type;
begin
  open acc_cursor;
  fetch acc_cursor into acc_num;
  while acc_cursor%found
  loop
  if acc_num = input
  then
    return 1;
  end if;
  fetch acc_cursor into acc_num;
  end loop;
  return 0;
end;
