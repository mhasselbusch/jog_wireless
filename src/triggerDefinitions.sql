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

create or replace trigger updateQuanity after insert on soldphones
  for each row
begin
    if :NEW.store_id <> '0'
    then
      update inventory
      set quantity = QUANTITY - 1
      where store_id = :NEW.store_id and model = :NEW.model and MANUFACTURER = :NEW.manufacturer;
    end if;
end;