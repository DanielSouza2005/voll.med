UPDATE medico SET especialidade =
    CASE
        WHEN especialidade = 1 THEN 0
        WHEN especialidade = 2 THEN 1
        WHEN especialidade = 3 THEN 2
        WHEN especialidade = 4 THEN 3
        ELSE 0
    END;