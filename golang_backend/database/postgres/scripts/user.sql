-- name: UpdateDeletedByUserId :exec
UPDATE users
SET
    is_deleted = TRUE
WHERE
    user_id = $1;

-- name: SelectUserInfoByUserId :one
SELECT
    display_name,
    email_id,
    gender,
    dob,
    mobile_verification,
    email_verification,
    is_paid,
    cast(pgp_sym_decrypt(first_name, 'SLMSZ1929S') AS VARCHAR) AS first_name,
    cast(pgp_sym_decrypt(last_name, 'SLMSZ1929S') AS VARCHAR) AS last_name,
    cast(pgp_sym_decrypt(user_address, 'SLMSZ1929S') AS VARCHAR) AS user_address,
    cast(pgp_sym_decrypt(ph_fixed, 'SLMSZ1929S') AS VARCHAR) AS ph_fixed,
    cast(pgp_sym_decrypt(ph_mobile, 'SLMSZ1929S') AS VARCHAR) AS ph_mobile,
    cast(pgp_sym_decrypt(handle_name, 'SLMSZ1929S') AS VARCHAR) AS handle_name,
    coalesce((paid_date + INTERVAL '365DAY')::TIMESTAMP, '0001-01-01') AS membership_completion_date
FROM users
WHERE
    user_id = $1;

-- name: SelectNotificationUser :one
SELECT setnotificationforall($1, $2);

-- name: SelectUserIDByToken :one
SELECT
    user_id,
    display_name,
    email_id,
    gender,
    dob,
    token,
    mobile_verification,
    is_admin,
    cast(pgp_sym_decrypt(first_name, 'SLMSZ1929S') AS VARCHAR) AS first_name,
    cast(pgp_sym_decrypt(last_name, 'SLMSZ1929S') AS VARCHAR) AS last_name,
    cast(pgp_sym_decrypt(user_address, 'SLMSZ1929S') AS VARCHAR) AS user_address,
    cast(pgp_sym_decrypt(ph_fixed, 'SLMSZ1929S') AS VARCHAR) AS ph_fixed,
    cast(pgp_sym_decrypt(ph_mobile, 'SLMSZ1929S') AS VARCHAR) AS ph_mobile,
    cast(pgp_sym_decrypt(handle_name, 'SLMSZ1929S') AS VARCHAR) AS handle_name
FROM users
WHERE
    token = $1
LIMIT 1;

-- name: UpdateOTPByEmailIDID :exec
UPDATE users
SET
    otp = $2,
    otp_email = $3
WHERE
    email_id = $1;

-- name: SelectUserCountByEmail :one
SELECT count(*)
FROM users
WHERE
    email_id = $1;

-- name: SelectUserCountByUsername :one
SELECT count(*)
FROM users
WHERE
    cast(pgp_sym_decrypt(handle_name, 'SLMSZ1929S') AS VARCHAR) = cast($1 AS VARCHAR);

-- name: SelectUserIDByEmailPassword :one
SELECT
    user_id,
    display_name,
    email_id,
    gender,
    dob,
    token,
    mobile_verification,
    is_admin,
    is_paid,
    email_verification,
    cast(pgp_sym_decrypt(first_name, 'SLMSZ1929S') AS VARCHAR) AS first_name,
    cast(pgp_sym_decrypt(last_name, 'SLMSZ1929S') AS VARCHAR) AS last_name,
    cast(pgp_sym_decrypt(user_address, 'SLMSZ1929S') AS VARCHAR) AS user_address,
    cast(pgp_sym_decrypt(ph_fixed, 'SLMSZ1929S') AS VARCHAR) AS ph_fixed,
    cast(pgp_sym_decrypt(ph_mobile, 'SLMSZ1929S') AS VARCHAR) AS ph_mobile,
    cast(pgp_sym_decrypt(handle_name, 'SLMSZ1929S') AS VARCHAR) AS handle_name
FROM users
WHERE
    email_id = $1
    AND jpp_password = $2
LIMIT 1;

-- name: SelectUserInfoByEmail :one
SELECT
    user_id,
    cast(pgp_sym_decrypt(handle_name, 'SLMSZ1929S') AS VARCHAR) AS handle_name,
    email_id,
    jpp_password,
    token,
    otp,
    otp_email,
    payment_token
FROM users
WHERE
    email_id = $1;

-- name: SelectUserInfoByMobile :one
SELECT
    user_id,
    handle_name,
    email_id,
    jpp_password,
    token,
    otp,
    otp_email
FROM users
WHERE
    ph_mobile = $1;

-- name: SelectUserInfoByMobileEmailIDInfo :one
SELECT
    user_id,
    handle_name,
    email_id,
    jpp_password,
    token,
    otp,
    otp_email
FROM users
WHERE
    cast(pgp_sym_decrypt(ph_mobile, 'SLMSZ1929S') AS VARCHAR) = cast($2 AS VARCHAR)
    AND email_id = $1;

-- name: SelectUserCountByID :one
SELECT count(*)
FROM users
WHERE
    user_id = $1;

-- name: UpdateVerifiedByOTP :exec
UPDATE users
SET
    mobile_verification = TRUE
WHERE
    email_id = $1;

-- name: UpdateVerifiedMobileByOTP :exec
UPDATE users
SET
    mobile_verification = TRUE
WHERE
    cast(pgp_sym_decrypt(ph_mobile, 'SLMSZ1929S') AS VARCHAR) = cast($1 AS VARCHAR);

-- name: UpdateVerifyEmailByOTP :exec
UPDATE users
SET
    email_verification = TRUE
WHERE
    email_id = $1;

-- name: UpdateUserPasswordByEmailIDOTP :exec
UPDATE users
SET
    jpp_password = $2
WHERE
    email_id = $1
    AND otp_email = $3;

-- name: UpdateTokenByID :exec
UPDATE users
SET
    token = $1,
    login_datetime = now()
WHERE
    user_id = $2;

-- name: InsertDonationTransaction :one
INSERT INTO user_donation_transaction
(
    user_id,
    transaction_url,
    transaction_token,
    user_name,
    user_email,
    user_amount
) VALUES (
    $1,
    $2,
    $3,
    $4,
    $5,
    $6
) RETURNING udt_id;

-- name: UpdateDonationTransactionById :exec
UPDATE user_donation_transaction
SET
    transaction_status = $3
WHERE
    user_id = $1
    AND transaction_token = $2;

-- name: UpdateDonationTransactionByToken :exec
UPDATE user_donation_transaction
SET
    transaction_status = $2
WHERE
    transaction_token = $1;

-- name: SelectTDonationTransactionByUserID :many
SELECT *
FROM
    user_donation_transaction
WHERE
    transaction_token = $1
ORDER BY date_time DESC;

-- name: InsertUserInfo :one
INSERT INTO users (
    first_name,
    last_name,
    handle_name,
    display_name,
    email_id,
    gender,
    dob,
    user_address,
    ph_fixed,
    ph_mobile,
    jpp_password,
    is_admin,
    mobile_verification
)
VALUES (
    pgp_sym_encrypt($1, 'SLMSZ1929S')::BYTEA,
    pgp_sym_encrypt($2, 'SLMSZ1929S')::BYTEA,
    pgp_sym_encrypt($3, 'SLMSZ1929S')::BYTEA,
    $4,
    $5,
    $6,
    $7,
    pgp_sym_encrypt($8, 'SLMSZ1929S')::BYTEA,
    pgp_sym_encrypt($9, 'SLMSZ1929S')::BYTEA,
    pgp_sym_encrypt($10, 'SLMSZ1929S')::BYTEA,
    $11,
    $12,
    $13
)
RETURNING user_id;

-- name: CheckIP :one
SELECT count(*) AS cnt
FROM unique_visitor
WHERE vistor_ip = $1;

-- name: AddIP :exec
INSERT INTO unique_visitor
(
    vistor_ip
) VALUES (
    $1
);

-- name: SelectUserCountByMobileNo :one
SELECT count(*)
FROM users
WHERE
    cast(pgp_sym_decrypt(users.ph_mobile, 'SLMSZ1929S') AS VARCHAR) = cast($1 AS VARCHAR);

-- name: SelectUserCountByMobileNoEmail :one
SELECT count(*)
FROM users
WHERE
    cast(pgp_sym_decrypt(users.ph_mobile, 'SLMSZ1929S') AS VARCHAR) = cast($2 AS VARCHAR)
    AND users.email_id = $1;

-- name: SelectDynamicData :many
SELECT
    dynamic_data.dynamic_data_id,
    dynamic_data.keys,
    dynamic_data.values
FROM
    dynamic_data
ORDER BY dynamic_data.dynamic_data_id;

-- name: SelectDynamicDataByKey :many
SELECT
    dynamic_data.dynamic_data_id,
    dynamic_data.keys,
    dynamic_data.values
FROM
    dynamic_data
WHERE
    dynamic_data.keys = $1;

-- name: UpdateUserPassword :exec
UPDATE users
SET
    jpp_password = $2
WHERE
    user_id = $1
    AND email_id = $4
    AND jpp_password = $3;

-- name: UpdateUserInfoByID :exec
UPDATE users
SET
    first_name = pgp_sym_encrypt($2, 'SLMSZ1929S')::BYTEA,
    last_name = pgp_sym_encrypt($3, 'SLMSZ1929S')::BYTEA,
    display_name = $4,
    gender = $5,
    dob = $6,
    user_address = pgp_sym_encrypt($7, 'SLMSZ1929S')::BYTEA,
    ph_fixed = pgp_sym_encrypt($8, 'SLMSZ1929S')::BYTEA,
    ph_mobile = pgp_sym_encrypt($9, 'SLMSZ1929S')::BYTEA,
    mobile_verification = $10
WHERE
    user_id = $1;

-- name: SelectUserInfo :many
SELECT
    users.user_id,
    users.email_id,
    users.gender,
    cast(pgp_sym_decrypt(users.first_name, 'SLMSZ1929S') AS VARCHAR) AS first_name,
    cast(pgp_sym_decrypt(users.handle_name, 'SLMSZ1929S') AS VARCHAR) AS handle_name
FROM
    users
WHERE
    users.is_deleted = FALSE;

-- name: SelectNotice :many
SELECT
    srno,
    notice_text,
    created_on,
    last_updated_on
FROM
    notice_master;

-- name: UpdateNotice :exec
UPDATE notice_master
SET
    notice_text = $2
WHERE
    srno = $1;

-- name: InsertTransaction :one
INSERT INTO user_payment_transaction
(
    user_id,
    transaction_url,
    transaction_token
) VALUES (
    $1,
    $2,
    $3
) RETURNING upt_id;

-- name: UpdateLoginStatus :exec
UPDATE users
SET
    token = ''
WHERE
    login_datetime IS NOT NULL
    AND login_datetime + INTERVAL '+24 Hours' < now();

-- name: MemberShipUser :many
SELECT
    user_id,
    last_name,
    handle_name,
    display_name,
    email_id,
    token,
    gender,
    dob,
    user_address,
    ph_fixed,
    ph_mobile,
    jpp_password,
    otp,
    is_admin,
    mobile_verification,
    is_deleted,
    created_on,
    last_updated_on,
    email_verification,
    otp_email,
    payment_token,
    is_paid,
    paid_date,
    login_datetime
FROM
    users
WHERE
    is_paid = TRUE
    AND paid_date + INTERVAL '+358 Days' < now();

-- name: UpdateTransactionById :exec
UPDATE user_payment_transaction
SET
    transaction_status = $3
WHERE
    user_id = $1
    AND transaction_token = $2;

-- name: SelectTransactionByUserID :many
SELECT *
FROM user_payment_transaction
WHERE
    user_id = $1
ORDER BY date_time DESC;

-- name: UpdatePaymentTokenByEmailIDID :exec
UPDATE users
SET
    payment_token = $2,
    is_paid = $3,
    paid_date = $4
WHERE
    email_id = $1;

-- name: SelectAdminContent :many
WITH regular_member AS (
    SELECT
        1 AS rid,
        count(*) AS rcnt
    FROM users
    WHERE
        mobile_verification = TRUE
), associate_member AS ( --noqa: L022
    SELECT
        1 AS aid,
        count(*) AS acnt
    FROM users
), visitor_member AS ( --noqa: L022
    SELECT
        1 AS fid,
        count(*) AS fcnt
    FROM unique_visitor
), agenda_count AS ( --noqa: L022
    SELECT
        1 AS agenda_id,
        count(*) AS agenda_count
    FROM agenda_master
), petition_count AS ( --noqa: L022
    SELECT
        1 AS petition_id,
        count(*) AS petition_count
    FROM petition_master
), admin_member AS ( --noqa: L022
    SELECT
        1 AS adid,
        count(*) AS adcnt
    FROM users
    WHERE
        is_admin = TRUE
), female_member AS ( --noqa: L022
    SELECT
        1 AS femaleid,
        count(*) AS femalecount
    FROM users
    WHERE
        gender = 'female'
), male_member AS ( --noqa: L022
    SELECT
        1 AS maleid,
        count(*) AS malecount
    FROM users
    WHERE
        gender = 'male'
), token_count AS ( --noqa: L022
    SELECT
        1 AS tokenid,
        count(DISTINCT token) AS tokencount
    FROM users
) SELECT --noqa: L022
    regular_member.rcnt AS regularcount,
    associate_member.acnt AS associatecount,
    visitor_member.fcnt AS membercount,
    agenda_count.agenda_count,
    petition_count.petition_count,
    admin_member.adcnt AS admincount,
    female_member.femalecount AS femalecount,
    male_member.malecount AS malecount,
    token_count.tokencount AS tokencount
FROM
    regular_member
INNER JOIN associate_member ON regular_member.rid = associate_member.aid
INNER JOIN admin_member ON regular_member.rid = admin_member.adid
INNER JOIN visitor_member ON regular_member.rid = visitor_member.fid
INNER JOIN agenda_count ON regular_member.rid = agenda_count.agenda_id
INNER JOIN petition_count ON regular_member.rid = petition_count.petition_id
INNER JOIN female_member ON regular_member.rid = female_member.femaleid
INNER JOIN male_member ON regular_member.rid = male_member.maleid
INNER JOIN token_count ON regular_member.rid = token_count.tokenid;

-- name: SelectDonationTransactionByUserID :many
SELECT
    udt_id,
    user_id,
    transaction_url,
    transaction_status,
    transaction_token,
    date_time,
    user_name,
    user_email,
    user_amount
FROM
    user_donation_transaction
WHERE
    user_id = $1
ORDER BY date_time DESC;

-- name: SelectDonationTransactionByEmailID :many
SELECT
    udt_id,
    user_id,
    transaction_url,
    transaction_status,
    transaction_token,
    date_time,
    user_name,
    user_email,
    user_amount
FROM
    user_donation_transaction
WHERE
    user_email = $1
ORDER BY date_time DESC;

-- name: InsertUserSMS :exec
INSERT INTO user_sms
(
    cnt,
    user_id
)
VALUES (
    $1,
    $2
) RETURNING us_id;

-- name: SelectExecutedTime :one
SELECT
    coalesce(max(keys), '-') AS keys,
    coalesce(max(values), '-') AS valueskeys,
    count(*) AS cnt
FROM
    dynamic_data
WHERE
    keys = 'sms_process';

-- name: UpdateProcessTime :exec
UPDATE
dynamic_data
SET
    values = now() + INTERVAL '1 hour'
WHERE
    keys = 'sms_process';

-- name: InsertSMSProcess :exec
INSERT INTO dynamic_data
(
    keys,
    values
)
VALUES (
    'sms_process',
    now() + INTERVAL '1 hour'
);

-- name: SMSDelete :exec
DELETE FROM sms_status
WHERE
    status = TRUE;

-- name: UpdateSMS :exec
UPDATE sms_status
SET
    status = TRUE
WHERE
    sms_id = $1;

-- name: SelectSMSData :many
SELECT
    sms_id,
    email,
    body,
    email_subject,
    status
FROM sms_status
WHERE
    status = FALSE
ORDER BY sms_id;

-- name: SelectUserSMSCount :one
SELECT count(*) AS cnt
FROM
    user_sms
WHERE
    user_id = $1;

-- name: DeleteOlderData :exec
DELETE FROM user_sms
WHERE
    processed_time + INTERVAL '60 min' < now();

-- name: InsertSMS :one
INSERT INTO sms_status
(
    email,
    body,
    email_subject
) VALUES (
    $1,
    $2,
    $3
) RETURNING sms_id;
