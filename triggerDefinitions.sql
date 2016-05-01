create or replace trigger updateCount after insert or delete ON phonenumber
  for each row
begin 
  case
    when inserting then
      update account
      set phones_assigned = phones_assigned + 1
      where account_number = :NEW.account_number;
    when deleting then
      update account
      set phones_assigned = phones_assigned - 1
      where account_number = :NEW.account_number and phones_assigned >= 1;
    end case;
end;

