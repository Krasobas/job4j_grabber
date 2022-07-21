select p.name, c.name from person as p
join company as c
on p.company_id = c.id
where c.id != 5;

select c.name, count(p.*) as persons from company as c
join person as p
on c.id = p.company_id
group by c.name
having count(p.*) >= all (select count(p.*) from person as p
                            group by p.company_id)
order by persons desc
limit 1;