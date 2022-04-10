drop table if exists item;
create table item (id serial, name text, quantity integer);

create or replace function item_added_trigger() returns trigger as $psql$ begin perform pg_notify('item_added', NEW.id::text); return NULL; end; $psql$ language plpgsql;

create or replace function item_updated_trigger() returns trigger as $psql$ begin perform pg_notify('item_updated', NEW.id::text); return NULL; end;$psql$ language plpgsql;

create or replace function item_deleted_trigger() returns trigger as $psql$ begin perform pg_notify('item_deleted', OLD.id::text);return new; end;$psql$ language plpgsql;

create or replace trigger insert_trigger after insert on item for each row execute procedure item_added_trigger();
create or replace trigger update_trigger after update on item for each row execute procedure item_updated_trigger();
create or replace trigger delete_trigger after delete on item for each row execute procedure item_deleted_trigger();
