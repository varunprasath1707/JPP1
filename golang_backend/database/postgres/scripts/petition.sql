-- name: UpdateDeletedByPetitionId :exec
UPDATE petition_master
SET
    is_deleted = TRUE
WHERE
    petition_master_id = $1;

-- name: InsertPetitionInfo :one
INSERT INTO petition_master (
    user_id,
    title,
    locationlevel,
    user_target,
    petition_content,
    submission_address,
    deadline,
    recruitmentcomments
)
VALUES (
    $1,
    $2,
    $3,
    $4,
    $5,
    $6,
    $7,
    $8
)
RETURNING petition_master_id;

-- name: SelectPetitionInfo :many
WITH petitionacceptancecount AS (
    SELECT
        petition_master_id AS pi_id,
        count(*) AS cnt
    FROM
        petition_acceptence
    GROUP BY
        petition_master_id
) SELECT --noqa: L022
    petition_master.petition_master_id,
    petition_master.user_id,
    petition_master.title,
    petition_master.locationlevel,
    petition_master.user_target,
    petition_master.submission_address,
    petition_master.deadline,
    petition_master.recruitmentcomments,
    petition_master.is_deleted,
    petition_master.created_at,
    petition_master.updated_at,
    users.user_id,
    users.display_name,
    users.email_id,
    users.gender,
    users.dob,
    users.created_on,
    petitionacceptancecount.pi_id,
    cast(pgp_sym_decrypt(users.first_name, 'SLMSZ1929S') AS VARCHAR) AS first_name,
    cast(pgp_sym_decrypt(users.last_name, 'SLMSZ1929S') AS VARCHAR) AS last_name,
    cast(pgp_sym_decrypt(users.user_address, 'SLMSZ1929S') AS VARCHAR) AS user_address,
    cast(pgp_sym_decrypt(users.ph_fixed, 'SLMSZ1929S') AS VARCHAR) AS ph_fixed,
    cast(pgp_sym_decrypt(users.ph_mobile, 'SLMSZ1929S') AS VARCHAR) AS ph_mobile,
    cast(pgp_sym_decrypt(users.handle_name, 'SLMSZ1929S') AS VARCHAR) AS handle_name,
    regexp_replace(petition_master.petition_content, E'[\\n\\r]+', '<br/>', 'g' ) AS petition_content,
    coalesce(petitionacceptancecount.cnt, 0) AS pcount
FROM
    petition_master
INNER JOIN users ON petition_master.user_id = users.user_id
LEFT JOIN petitionacceptancecount ON petition_master.petition_master_id = petitionacceptancecount.pi_id
WHERE
    petition_master.is_deleted = FALSE;

-- name: UpdatePetitionInfoByID :exec
UPDATE petition_master
SET
    title = $1,
    locationlevel = $2,
    user_target = $3,
    petition_content = $4,
    submission_address = $5,
    deadline = $6,
    recruitmentcomments = $7
WHERE
    petition_master_id = $8;

-- name: SelectPetitionInfoByPetitionId :many
WITH petitionacceptancecount AS (
    SELECT
        petition_master_id AS pi_id,
        count(*) AS cnt
    FROM
        petition_acceptence
    GROUP BY
        petition_master_id
) SELECT --noqa: L022
    petition_master.petition_master_id,
    petition_master.user_id,
    petition_master.title,
    petition_master.locationlevel,
    petition_master.user_target,
    petition_master.submission_address,
    petition_master.deadline,
    petition_master.is_deleted,
    petition_master.created_at,
    petition_master.updated_at,
    users.user_id,
    users.display_name,
    users.email_id,
    users.gender,
    users.dob,
    users.created_on,
    petitionacceptancecount.pi_id,
    cast(pgp_sym_decrypt(users.first_name, 'SLMSZ1929S') AS VARCHAR) AS first_name,
    cast(pgp_sym_decrypt(users.last_name, 'SLMSZ1929S') AS VARCHAR) AS last_name,
    cast(pgp_sym_decrypt(users.user_address, 'SLMSZ1929S') AS VARCHAR) AS user_address,
    cast(pgp_sym_decrypt(users.ph_fixed, 'SLMSZ1929S') AS VARCHAR) AS ph_fixed,
    cast(pgp_sym_decrypt(users.ph_mobile, 'SLMSZ1929S') AS VARCHAR) AS ph_mobile,
    cast(pgp_sym_decrypt(users.handle_name, 'SLMSZ1929S') AS VARCHAR) AS handle_name,
    regexp_replace(petition_master.petition_content, E'[\\n\\r]+', '<br/>', 'g' ) AS petition_content,
    regexp_replace(petition_master.recruitmentcomments, E'[\\n\\r]+', '<br/>', 'g' ) AS recruitmentcomments,
    coalesce(petitionacceptancecount.cnt, 0) AS pcount
FROM petition_master
INNER JOIN users ON petition_master.user_id = users.user_id
LEFT JOIN petitionacceptancecount ON petition_master.petition_master_id = petitionacceptancecount.pi_id
WHERE
    petition_master.petition_master_id = $1;

-- name: InsertPetitionSign :one
INSERT INTO petition_acceptence (
    user_id,
    petition_master_id
) VALUES (
    $1,
    $2
) RETURNING petition_acceptence_id;


-- name: SelectAllUseracceptenceByPetitionID :many
SELECT
    petition_acceptence.petition_acceptence_id,
    petition_acceptence.petition_master_id,
    users.user_id,
    users.display_name,
    users.email_id,
    users.gender,
    users.dob,
    users.created_on,
    cast(pgp_sym_decrypt(users.first_name, 'SLMSZ1929S') AS VARCHAR) AS first_name,
    cast(pgp_sym_decrypt(users.last_name, 'SLMSZ1929S') AS VARCHAR) AS last_name,
    cast(pgp_sym_decrypt(users.user_address, 'SLMSZ1929S') AS VARCHAR) AS user_address,
    cast(pgp_sym_decrypt(users.ph_fixed, 'SLMSZ1929S') AS VARCHAR) AS ph_fixed,
    cast(pgp_sym_decrypt(users.ph_mobile, 'SLMSZ1929S') AS VARCHAR) AS ph_mobile,
    cast(pgp_sym_decrypt(users.handle_name, 'SLMSZ1929S') AS VARCHAR) AS handle_name
FROM
    petition_acceptence
INNER JOIN users ON petition_acceptence.user_id = users.user_id
WHERE
    petition_acceptence.petition_master_id = $1;

-- name: SelectCountpetitionacceptancebyUserIDPetitionAcceptanceID :one
SELECT count(*) AS cnt
FROM petition_acceptence
WHERE
    petition_master_id = $1
    AND user_id = $2;

-- name: InsertPetitionMailInfo :one
INSERT INTO petition_mail
(
    petition_master_id,
    user_id,
    email_id
) VALUES (
    $1,
    $2,
    $3
) RETURNING petition_mail_id;

-- name: CheckPetitionMailCount :one
SELECT count(*)
FROM petition_mail
WHERE
    petition_master_id = $1
    AND email_id = $2;

-- name: SelectPetitionList :many
WITH petitionacceptancecount AS (
    SELECT
        petition_master_id AS pi_id,
        count(*) AS cnt
    FROM
        petition_acceptence
    GROUP BY
        petition_master_id
) SELECT --noqa: L022
    petition_master.petition_master_id,
    users.display_name,
    petition_master.user_id,
    petition_master.title,
    petition_master.locationlevel,
    petition_master.user_target,
    petition_master.submission_address,
    petition_master.deadline,
    petition_master.created_at,
    petitionacceptancecount.pi_id,
    cast(pgp_sym_decrypt(users.first_name, 'SLMSZ1929S') AS VARCHAR) AS first_name,
    cast(pgp_sym_decrypt(users.last_name, 'SLMSZ1929S') AS VARCHAR) AS last_name,
    cast(pgp_sym_decrypt(users.user_address, 'SLMSZ1929S') AS VARCHAR) AS user_address,
    cast(pgp_sym_decrypt(users.ph_fixed, 'SLMSZ1929S') AS VARCHAR) AS ph_fixed,
    cast(pgp_sym_decrypt(users.ph_mobile, 'SLMSZ1929S') AS VARCHAR) AS ph_mobile,
    cast(pgp_sym_decrypt(users.handle_name, 'SLMSZ1929S') AS VARCHAR) AS handle_name,
    regexp_replace(petition_master.petition_content, E'[\\n\\r]+', '<br/>', 'g' ) AS petition_content,
    regexp_replace(petition_master.recruitmentcomments, E'[\\n\\r]+', '<br/>', 'g' ) AS recruitmentcomments,
    coalesce(petitionacceptancecount.cnt, 0) AS pcount
FROM petition_master
INNER JOIN users ON petition_master.user_id = users.user_id
LEFT JOIN petitionacceptancecount ON petition_master.petition_master_id = petitionacceptancecount.pi_id
WHERE
    (
        petition_master.locationlevel = $1
        OR petition_master.locationlevel = $2
        OR petition_master.locationlevel = $3
    )
    AND petition_master.is_deleted = FALSE
ORDER BY petition_master.petition_master_id DESC;
