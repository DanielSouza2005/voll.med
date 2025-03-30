UPDATE medico SET especialidade =
    CASE 
        WHEN especialidade = 'ORTOPEDIA' THEN 1
        WHEN especialidade = 'CARDIOLOGIA' THEN 2
        WHEN especialidade = 'GINECOLOGIA' THEN 3
        WHEN especialidade = 'DERMATOLOGIA' THEN 4
        ELSE 0
    END;