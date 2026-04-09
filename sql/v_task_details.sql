-- Vista de tareas detalladas con campos calculados
-- Ejecutar en PostgreSQL

CREATE OR REPLACE VIEW v_task_details AS
SELECT 
    t.id,
    t.title,
    t.description,
    t.status,
    t.created_at,
    t.due_date,
    t.completed_at,
    t.user_id,
    u.email AS user_email,
    u.full_name AS user_full_name,
    -- Días restantes (negativo si ya pasó)
    CASE 
        WHEN t.due_date IS NULL THEN NULL
        WHEN t.status = 'COMPLETED' THEN NULL
        ELSE EXTRACT(DAY FROM (t.due_date - CURRENT_DATE))
    END AS days_remaining,
    -- Si está vencida
    CASE 
        WHEN t.due_date IS NULL THEN false
        WHEN t.status = 'COMPLETED' THEN false
        WHEN t.due_date < CURRENT_DATE THEN true
        ELSE false
    END AS is_overdue,
    -- Días de retraso (positivos si vencida)
    CASE 
        WHEN t.due_date IS NULL THEN NULL
        WHEN t.status = 'COMPLETED' THEN NULL
        WHEN t.due_date < CURRENT_DATE THEN EXTRACT(DAY FROM (CURRENT_DATE - t.due_date))
        ELSE 0
    END AS days_overdue
FROM tasks t
INNER JOIN users u ON t.user_id = u.id;

-- Verificar la vista
SELECT * FROM v_task_details LIMIT 10;