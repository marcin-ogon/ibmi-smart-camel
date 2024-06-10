select
    lower(trim(device_id)) as device,
    upper(trim(type)) as type,
    trim(state) as state,
    lower(trim(color)) as color
    
from
    SMARTDEV
where
    status = 'PROC' 