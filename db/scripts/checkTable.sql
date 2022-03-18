select exists (
    select 1 from information_schema.columns
    where table_name = 'post');