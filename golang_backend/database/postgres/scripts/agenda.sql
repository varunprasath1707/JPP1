-- name: UpdateDeletedByAgendaId :exec
UPDATE agenda_master
SET
    is_deleted = TRUE
WHERE
    agenda_master_id = $1;

-- name: InsertAgendaInfo :one
INSERT INTO agenda_master (
    topic_name,
    location_level,
    discussion_details,
    is_approved,
    is_deleted,
    user_id
)
VALUES (
    $1,
    $2,
    $3,
    FALSE,
    FALSE,
    $4
) RETURNING agenda_master_id;

-- name: NotificationInsert :one
INSERT INTO notification
(
    user_id,
    notification_text
)
VALUES (
    $1,
    $2
) RETURNING notification_id;

-- name: DeleteNotificationById :exec
DELETE FROM notification
WHERE
    notification_id = $1;

-- name: AgendaChoice :one
INSERT INTO agenda_choice
(
    agenda_master_id,
    choice_text,
    display_order
) VALUES (
    $1,
    $2,
    $3
) RETURNING agenda_choice_id;

-- name: InsertAgendaVote :one
INSERT INTO agenda_vote (
    user_id,
    agenda_master_id,
    agenda_choice_id
) VALUES (
    $1,
    $2,
    $3
) RETURNING agenda_vote_id;

-- name: InsertAgendaAcceptance :one
INSERT INTO agenda_acceptence (
    user_id,
    agenda_master_id
) VALUES (
    $1,
    $2
) RETURNING agenda_acceptence_id;

-- name: InsertAgendaComment :one
INSERT INTO agenda_comment (
    user_id,
    agenda_master_id,
    comment_text
) VALUES (
    $1,
    $2,
    $3
) RETURNING agenda_comment_id;

-- name: InsertAgendaCommentReply :one
INSERT INTO agenda_comment (
    user_id,
    agenda_master_id,
    comment_text,
    ref_agenda_comment_id
) VALUES (
    $1,
    $2,
    $3,
    $4
) RETURNING agenda_comment_id;

-- name: InsertAgendaCommentLike :one
INSERT INTO agenda_comment_reportlikedislike (
    user_id,
    agenda_comment_id,
    comment_response
) VALUES (
    $1,
    $2,
    'Liked'
) RETURNING agenda_comment_rll;

-- name: InsertAgendaCommentDisLike :one
INSERT INTO agenda_comment_reportlikedislike (
    user_id,
    agenda_comment_id,
    comment_response
) VALUES (
    $1,
    $2,
    'Disliked'
) RETURNING agenda_comment_rll;

-- name: InsertAgendaCommentReport :one
INSERT INTO agenda_comment_reportlikedislike (
    user_id,
    agenda_comment_id,
    comment_response
) VALUES (
    $1,
    $2,
    'Report'
) RETURNING agenda_comment_rll;

-- name: SelectAgendaPreApproved :many
WITH precount AS (
    SELECT
        agenda_master_id AS amid,
        count(*) AS cnt
    FROM
        agenda_acceptence
    GROUP BY
        agenda_master_id
) SELECT --noqa: L022
    agenda_master.agenda_master_id,
    agenda_master.topic_name,
    agenda_master.location_level,
    agenda_master.discussion_details,
    agenda_master.is_approved,
    agenda_master.is_deleted,
    coalesce(precount.cnt, 0) AS acceptancecount
FROM
    agenda_master
LEFT JOIN precount ON agenda_master.agenda_master_id = precount.amid
WHERE
    (
        coalesce(precount.cnt, 0) < 11
        AND agenda_master.is_approved = FALSE
    ) AND (
        agenda_master.location_level = $1
        OR agenda_master.location_level = $2
        OR agenda_master.location_level = $3
    )
ORDER BY coalesce(precount.cnt, 0) DESC;

-- name: SelectAgendaApproved :many
WITH precount AS (
    SELECT
        agenda_master_id AS amid,
        count(*) AS cnt
    FROM
        agenda_acceptence
    GROUP BY
        agenda_master_id
), commentcount AS ( --noqa: L022
    SELECT
        agenda_master_id AS c_amid,
        count(*) AS c_cnt
    FROM
        agenda_comment
    GROUP BY
        agenda_master_id
), optionacount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pa_amid,
        getchoicevote(max(agenda_choice_id), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) AS pa_cnt,
        max(choice_text) AS pa_text
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_one'
    GROUP BY
        agenda_master_id
), optionbcount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pb_amid,
        getchoicevote(max(agenda_choice_id), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) AS pb_cnt,
        max(choice_text) AS pb_text
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_two'
    GROUP BY
        agenda_master_id
), optionccount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pc_amid,
        getchoicevote(max(agenda_choice_id), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) AS pc_cnt,
        max(choice_text) AS pc_text
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_three'
    GROUP BY
        agenda_master_id
), optiondcount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pd_amid,
        getchoicevote(max(agenda_choice_id), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) AS pd_cnt,
        max(choice_text) AS pd_text
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_four'
    GROUP BY
        agenda_master_id
), optionecount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pe_amid,
        getchoicevote(max(agenda_choice_id), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) AS pe_cnt,
        max(choice_text) AS pe_text
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_five'
    GROUP BY
        agenda_master_id
), optionfcount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pf_amid,
        getchoicevote(max(agenda_choice_id), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) AS pf_cnt,
        max(choice_text) AS pf_text
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_six'
    GROUP BY
        agenda_master_id
) SELECT --noqa: L022
    agenda_master.agenda_master_id,
    agenda_master.topic_name,
    agenda_master.location_level,
    agenda_master.discussion_details,
    agenda_master.is_approved,
    agenda_master.is_deleted,
    coalesce(precount.cnt, 0) AS acceptancecount,
    coalesce(commentcount.c_cnt, 0) AS commentcount,
    coalesce(optionacount.pa_cnt, 0) AS choicea_count,
    coalesce(optionacount.pa_text, '') AS choicea_text,
    coalesce(optionbcount.pb_cnt, 0) AS choiceb_count,
    coalesce(optionbcount.pb_text, '') AS choiceb_text,
    coalesce(optionccount.pc_cnt, 0) AS choicec_count,
    coalesce(optionccount.pc_text, '') AS choicec_text,
    coalesce(optiondcount.pd_cnt, 0) AS choiced_count,
    coalesce(optiondcount.pd_text, '') AS choiced_text,
    coalesce(optionecount.pe_cnt, 0) AS choicee_count,
    coalesce(optionecount.pe_text, '') AS choicee_text,
    coalesce(optionfcount.pf_cnt, 0) AS choicef_count,
    coalesce(optionfcount.pf_text, '') AS choicef_text
FROM
    agenda_master
INNER JOIN precount ON agenda_master.agenda_master_id = precount.amid
LEFT JOIN commentcount ON agenda_master.agenda_master_id = commentcount.c_amid
LEFT JOIN optionacount ON agenda_master.agenda_master_id = optionacount.pa_amid
LEFT JOIN optionbcount ON agenda_master.agenda_master_id = optionbcount.pb_amid
LEFT JOIN optionccount ON agenda_master.agenda_master_id = optionccount.pc_amid
LEFT JOIN optiondcount ON agenda_master.agenda_master_id = optiondcount.pd_amid
LEFT JOIN optionecount ON agenda_master.agenda_master_id = optionecount.pe_amid
LEFT JOIN optionfcount ON agenda_master.agenda_master_id = optionfcount.pf_amid
WHERE
    (
        precount.cnt > 10
        OR agenda_master.is_approved = TRUE
    )
    AND (
        agenda_master.location_level = $1
        OR agenda_master.location_level = $2
        OR agenda_master.location_level = $3
    )
ORDER BY coalesce(precount.cnt, 0) DESC;

-- name: SelectAgendaApprovedByID :many
WITH precount AS ( --noqa: L022
    SELECT
        agenda_master_id AS amid,
        count(*) AS cnt
    FROM
        agenda_acceptence
    GROUP BY
        agenda_master_id
), commentcount AS ( --noqa: L022
    SELECT
        agenda_master_id AS c_amid,
        count(*) AS c_cnt
    FROM
        agenda_comment
    GROUP BY
        agenda_master_id
), optionacount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pa_amid,
        getchoicevote(max(agenda_choice_id), $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS pa_cnt,
        max(choice_text) AS pa_text,
        max(agenda_choice_id) AS pa_choice_id
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_one'
    GROUP BY
        agenda_master_id
), optionbcount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pb_amid,
        getchoicevote(max(agenda_choice_id), $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS pb_cnt,
        max(choice_text) AS pb_text,
        max(agenda_choice_id) AS pb_choice_id
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_two'
    GROUP BY
        agenda_master_id
), optionccount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pc_amid,
        getchoicevote(max(agenda_choice_id), $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS pc_cnt,
        max(choice_text) AS pc_text,
        max(agenda_choice_id) AS pc_choice_id
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_three'
    GROUP BY
        agenda_master_id
), optiondcount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pd_amid,
        getchoicevote(max(agenda_choice_id), $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS pd_cnt,
        max(choice_text) AS pd_text,
        max(agenda_choice_id) AS pd_choice_id
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_four'
    GROUP BY
        agenda_master_id
), optionecount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pe_amid,
        getchoicevote(max(agenda_choice_id), $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS pe_cnt,
        max(choice_text) AS pe_text,
        max(agenda_choice_id) AS pe_choice_id
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_five'
    GROUP BY
        agenda_master_id
), optionfcount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pf_amid,
        getchoicevote(max(agenda_choice_id), $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS pf_cnt,
        max(choice_text) AS pf_text,
        max(agenda_choice_id) AS pf_choice_id
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_six'
    GROUP BY
        agenda_master_id
) SELECT --noqa: L022
    agenda_master.agenda_master_id,
    agenda_master.topic_name,
    agenda_master.location_level,
    agenda_master.discussion_details,
    agenda_master.is_approved,
    agenda_master.is_deleted,
    coalesce(precount.cnt, 0) AS acceptancecount,
    coalesce(commentcount.c_cnt, 0) AS commentcount,
    coalesce(optionacount.pa_cnt, 0) AS choicea_count,
    coalesce(optionacount.pa_text, '') AS choicea_text,
    coalesce(optionacount.pa_choice_id, 0) AS choicea_choice_id,
    coalesce(optionbcount.pb_cnt, 0) AS choiceb_count,
    coalesce(optionbcount.pb_text, '') AS choiceb_text,
    coalesce(optionbcount.pb_choice_id, 0) AS choiceb_choice_id,
    coalesce(optionccount.pc_cnt, 0) AS choicec_count,
    coalesce(optionccount.pc_text, '') AS choicec_text,
    coalesce(optionccount.pc_choice_id, 0) AS choicec_choice_id,
    coalesce(optiondcount.pd_cnt, 0) AS choiced_count,
    coalesce(optiondcount.pd_text, '') AS choiced_text,
    coalesce(optiondcount.pd_choice_id, 0) AS choiced_choice_id,
    coalesce(optionecount.pe_cnt, 0) AS choicee_count,
    coalesce(optionecount.pe_text, '') AS choicee_text,
    coalesce(optionecount.pe_choice_id, 0) AS choicee_choice_id,
    coalesce(optionfcount.pf_cnt, 0) AS choicef_count,
    coalesce(optionfcount.pf_text, '') AS choicef_text,
    coalesce(optionfcount.pf_choice_id, 0) AS choicef_choice_id
FROM
    agenda_master
INNER JOIN precount ON agenda_master.agenda_master_id = precount.amid
LEFT JOIN commentcount ON agenda_master.agenda_master_id = commentcount.c_amid
LEFT JOIN optionacount ON agenda_master.agenda_master_id = optionacount.pa_amid
LEFT JOIN optionbcount ON agenda_master.agenda_master_id = optionbcount.pb_amid
LEFT JOIN optionccount ON agenda_master.agenda_master_id = optionccount.pc_amid
LEFT JOIN optiondcount ON agenda_master.agenda_master_id = optiondcount.pd_amid
LEFT JOIN optionecount ON agenda_master.agenda_master_id = optionecount.pe_amid
LEFT JOIN optionfcount ON agenda_master.agenda_master_id = optionfcount.pf_amid
WHERE
    agenda_master.agenda_master_id = $1
ORDER BY coalesce(precount.cnt, 0) DESC;

-- name: SelectCommentByAgendaID :many
WITH likequery AS (
    SELECT
        agenda_comment_id AS l_id,
        count(*) AS l_count
    FROM
        agenda_comment_reportlikedislike
    WHERE
        comment_response = 'Liked'
    GROUP BY
        agenda_comment_id
), dislikequery AS ( --noqa: L022
    SELECT
        agenda_comment_id AS l_id,
        count(*) AS d_count
    FROM
        agenda_comment_reportlikedislike
    WHERE
        comment_response = 'Disliked'
    GROUP BY
        agenda_comment_id
), replycount AS ( --noqa: L022
    SELECT
        ref_agenda_comment_id AS r_id,
        count(*) AS r_count
    FROM
        agenda_comment
    GROUP BY
        ref_agenda_comment_id
)

SELECT
    agenda_comment.*,
    users.user_id,
    users.display_name,
    users.email_id,
    users.gender,
    users.dob,
    cast(pgp_sym_decrypt(users.first_name, 'SLMSZ1929S') AS VARCHAR) AS first_name,
    cast(pgp_sym_decrypt(users.last_name, 'SLMSZ1929S') AS VARCHAR) AS last_name,
    cast(pgp_sym_decrypt(users.user_address, 'SLMSZ1929S') AS VARCHAR) AS user_address,
    cast(pgp_sym_decrypt(users.ph_fixed, 'SLMSZ1929S') AS VARCHAR) AS ph_fixed,
    cast(pgp_sym_decrypt(users.ph_mobile, 'SLMSZ1929S') AS VARCHAR) AS ph_mobile,
    cast(pgp_sym_decrypt(users.handle_name, 'SLMSZ1929S') AS VARCHAR) AS handle_name,
    getagendauservote(agenda_comment.agenda_master_id, agenda_comment.user_id) AS uservote,
    coalesce(likequery.l_count, 0) AS likecount,
    coalesce(dislikequery.d_count, 0) AS dislikecount,
    coalesce(replycount.r_count, 0) AS replycount
FROM
    agenda_comment
INNER JOIN users ON agenda_comment.user_id = users.user_id
LEFT JOIN likequery ON agenda_comment.agenda_comment_id = likequery.l_id
LEFT JOIN dislikequery ON agenda_comment.agenda_comment_id = dislikequery.l_id
LEFT JOIN replycount ON agenda_comment.agenda_comment_id = replycount.r_id
WHERE
    agenda_comment.agenda_master_id = $1
    AND agenda_comment.ref_agenda_comment_id = 0
ORDER BY agenda_comment.agenda_comment_id DESC;

-- name: SelectHomeContent :many
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
) SELECT --noqa: L022
    notice_master.notice_text,
    regular_member.rcnt AS regularcount,
    associate_member.acnt AS associatecount,
    visitor_member.fcnt AS membercount
FROM
    notice_master
INNER JOIN regular_member ON notice_master.srno = regular_member.rid
INNER JOIN associate_member ON notice_master.srno = associate_member.aid
INNER JOIN visitor_member ON notice_master.srno = visitor_member.fid;

-- name: Insertagendachoice :one
INSERT INTO agenda_choice (
    agenda_master_id,
    choice_id,
    choice_text
)
VALUES (
    $1,
    $2,
    $3
) RETURNING agenda_choice_id;

-- name: SelectAgenda :many
SELECT
    agenda_master_id,
    topic_name,
    location_level,
    discussion_details,
    is_approved,
    is_deleted
FROM
    agenda_master
WHERE
    is_deleted = FALSE;

-- name: CommentCountByAgendaUserId :one
SELECT count(*) AS cnt
FROM
    agenda_comment
WHERE
    agenda_master_id = $1
    AND user_id = $2
    AND ref_agenda_comment_id = 0;

-- name: CommentRreplyCountByAgendaUserId :one
SELECT count(*) AS cnt
FROM
    agenda_comment
WHERE
    agenda_master_id = $1
    AND user_id = $2
    AND ref_agenda_comment_id = $3;

-- name: CommentLikeCountByCommentUserId :one
SELECT count(*) AS cnt
FROM
    agenda_comment_reportlikedislike
WHERE
    agenda_comment_id = $1
    AND comment_response = 'Liked'
    AND user_id = $2;

-- name: CommentReplyListByCommentID :many
SELECT
    agenda_comment.*,
    users.*
FROM
    agenda_comment
INNER JOIN users ON agenda_comment.user_id = users.user_id
WHERE
    agenda_comment.ref_agenda_comment_id = $1
    AND agenda_comment.agenda_master_id = $2;

-- name: NotificationByUserId :many
SELECT
    notification.*
FROM
    notification
WHERE
    notification.user_id = $1
    AND notification.is_deleted = FALSE
ORDER BY
    notification.created_at DESC;

-- name: CommentDiLikeCountByCommentUserId :one
SELECT count(*) AS cnt
FROM
    agenda_comment_reportlikedislike
WHERE
    agenda_comment_id = $1
    AND comment_response = 'Disliked'
    AND user_id = $2;

-- name: CommentReportCountByCommentUserId :one
SELECT count(*) AS cnt
FROM
    agenda_comment_reportlikedislike
WHERE
    agenda_comment_id = $1
    AND comment_response = 'Report'
    AND user_id = $2;

-- name: AcceptanceAgendaCountByAgendaUserId :one
SELECT count(*) AS cnt
FROM
    agenda_acceptence
WHERE
    agenda_master_id = $1
    AND user_id = $2;

-- name: VoteAgendaCountByAgendaUserId :one
SELECT count(*) AS cnt
FROM
    agenda_vote
WHERE
    agenda_master_id = $1
    AND user_id = $2;

-- name: InsertPartyInquiry :one
INSERT INTO inquiry
(
    user_name,
    email_id,
    message_content
)
VALUES
(
    $1,
    $2,
    $3
)
RETURNING inquiry_id;

-- name: DeleteLikeDislikeByUserIdAgnedaId :exec
DELETE FROM agenda_comment_reportlikedislike
WHERE
    comment_response IN ('Liked', 'Disliked')
    AND agenda_comment_id = $1
    AND user_id = $2;

-- name: AgnedaCommentDeleteByAgendaIDUserId :exec
SELECT deleteagendacomment($1, $2);

-- name: GetGraphAgenda :one
SELECT
    agenda_master_id,
    getgraphmale($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS malerecords,
    getgraphfemale($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS femalerecords,
    getgraphage1($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS agegroupa,
    getgraphage2($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS agegroupb,
    getgraphage3($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS agegroupc,
    getgraphage4($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS agegroupd,
    getgraphage5($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS agegroupe,
    getgraphage6($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS agegroupf,
    getgraphage7($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS agegroupg,
    getgraphage8($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11) AS agegrouph
FROM
    agenda_master
WHERE
    agenda_master_id = $1;

-- name: GetComentInfoByCommentID :many
SELECT
    *
FROM
    agenda_comment
WHERE
    agenda_comment_id = $1;

-- name: SelectAgendaAllInfoBackup :many
SELECT
    agenda_master_id,
    topic_name,
    location_level,
    discussion_details,
    is_approved,
    is_deleted
FROM
    agenda_master;

-- name: SelectAgendaAllInfoAdmin :many
WITH precount AS ( --noqa: L022
    SELECT
        agenda_master_id AS amid,
        count(*) AS cnt
    FROM
        agenda_acceptence
    GROUP BY
        agenda_master_id
), commentcount AS ( --noqa: L022
    SELECT
        agenda_master_id AS c_amid,
        count(*) AS c_cnt
    FROM
        agenda_comment
    GROUP BY
        agenda_master_id
), optionacount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pa_amid,
        getchoicevote(max(agenda_choice_id), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) AS pa_cnt,
        max(choice_text) AS pa_text,
        max(agenda_choice_id) AS pa_choice_id
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_one'
    GROUP BY
        agenda_master_id
), optionbcount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pb_amid,
        getchoicevote(max(agenda_choice_id), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) AS pb_cnt,
        max(choice_text) AS pb_text,
        max(agenda_choice_id) AS pb_choice_id
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_two'
    GROUP BY
        agenda_master_id
), optionccount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pc_amid,
        getchoicevote(max(agenda_choice_id), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) AS pc_cnt,
        max(choice_text) AS pc_text,
        max(agenda_choice_id) AS pc_choice_id
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_three'
    GROUP BY
        agenda_master_id
), optiondcount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pd_amid,
        getchoicevote(max(agenda_choice_id), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) AS pd_cnt,
        max(choice_text) AS pd_text,
        max(agenda_choice_id) AS pd_choice_id
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_four'
    GROUP BY
        agenda_master_id
), optionecount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pe_amid,
        getchoicevote(max(agenda_choice_id), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) AS pe_cnt,
        max(choice_text) AS pe_text,
        max(agenda_choice_id) AS pe_choice_id
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_five'
    GROUP BY
        agenda_master_id
), optionfcount AS ( --noqa: L022
    SELECT
        agenda_master_id AS pf_amid,
        getchoicevote(max(agenda_choice_id), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) AS pf_cnt,
        max(choice_text) AS pf_text,
        max(agenda_choice_id) AS pf_choice_id
    FROM
        agenda_choice
    WHERE
        choice_id = 'choice_six'
    GROUP BY
        agenda_master_id
) SELECT --noqa: L022
    agenda_master.agenda_master_id,
    agenda_master.user_id,
    agenda_master.topic_name,
    agenda_master.location_level,
    agenda_master.discussion_details,
    agenda_master.is_approved,
    agenda_master.is_deleted,
    agenda_master.created_at,
    agenda_master.updated_at,
    users.display_name,
    users.email_id,
    users.gender,
    users.dob,
    users.jpp_password,
    users.otp,
    users.is_admin,
    users.mobile_verification,
    users.is_deleted AS user_is_deleted,
    users.email_verification,
    users.otp_email,
    users.is_paid,
    users.paid_date,
    cast(pgp_sym_decrypt(users.first_name, 'SLMSZ1929S') AS VARCHAR) AS first_name,
    cast(pgp_sym_decrypt(users.last_name, 'SLMSZ1929S') AS VARCHAR) AS last_name,
    cast(pgp_sym_decrypt(users.user_address, 'SLMSZ1929S') AS VARCHAR) AS user_address,
    cast(pgp_sym_decrypt(users.ph_fixed, 'SLMSZ1929S') AS VARCHAR) AS ph_fixed,
    cast(pgp_sym_decrypt(users.ph_mobile, 'SLMSZ1929S') AS VARCHAR) AS ph_mobile,
    cast(pgp_sym_decrypt(users.handle_name, 'SLMSZ1929S') AS VARCHAR) AS handle_name,
    coalesce(precount.cnt, 0) AS acceptancecount,
    coalesce(commentcount.c_cnt, 0) AS commentcount,
    coalesce(optionacount.pa_cnt, 0) AS choicea_count,
    coalesce(optionacount.pa_text, '') AS choicea_text,
    coalesce(optionacount.pa_choice_id, 0) AS choicea_choice_id,
    coalesce(optionbcount.pb_cnt, 0) AS choiceb_count,
    coalesce(optionbcount.pb_text, '') AS choiceb_text,
    coalesce(optionbcount.pb_choice_id, 0) AS choiceb_choice_id,
    coalesce(optionccount.pc_cnt, 0) AS choicec_count,
    coalesce(optionccount.pc_text, '') AS choicec_text,
    coalesce(optionccount.pc_choice_id, 0) AS choicec_choice_id,
    coalesce(optiondcount.pd_cnt, 0) AS choiced_count,
    coalesce(optiondcount.pd_text, '') AS choiced_text,
    coalesce(optiondcount.pd_choice_id, 0) AS choiced_choice_id,
    coalesce(optionecount.pe_cnt, 0) AS choicee_count,
    coalesce(optionecount.pe_text, '') AS choicee_text,
    coalesce(optionecount.pe_choice_id, 0) AS choicee_choice_id,
    coalesce(optionfcount.pf_cnt, 0) AS choicef_count,
    coalesce(optionfcount.pf_text, '') AS choicef_text,
    coalesce(optionfcount.pf_choice_id, 0) AS choicef_choice_id
FROM
    agenda_master
INNER JOIN users ON agenda_master.user_id = users.user_id
INNER JOIN precount ON agenda_master.agenda_master_id = precount.amid
LEFT JOIN commentcount ON agenda_master.agenda_master_id = commentcount.c_amid
LEFT JOIN optionacount ON agenda_master.agenda_master_id = optionacount.pa_amid
LEFT JOIN optionbcount ON agenda_master.agenda_master_id = optionbcount.pb_amid
LEFT JOIN optionccount ON agenda_master.agenda_master_id = optionccount.pc_amid
LEFT JOIN optiondcount ON agenda_master.agenda_master_id = optiondcount.pd_amid
LEFT JOIN optionecount ON agenda_master.agenda_master_id = optionecount.pe_amid
LEFT JOIN optionfcount ON agenda_master.agenda_master_id = optionfcount.pf_amid
WHERE
    agenda_master.is_deleted = FALSE;

-- name: UpdateAgendaStatus :exec
UPDATE agenda_master
SET
    is_approved = TRUE
WHERE
    agenda_master_id IN
    (
        SELECT agenda_master_id
        FROM
            agenda_acceptence
        GROUP BY agenda_master_id
        HAVING count(*) > 10
    );

-- name: UpdateAgendaApprovedStatus :exec
UPDATE public.agenda_master
SET is_approved = TRUE
WHERE agenda_master_id = $1;

-- name: SelectAgendaAllInfo :many
SELECT
    agenda_master.agenda_master_id,
    agenda_master.user_id,
    agenda_master.topic_name,
    agenda_master.location_level,
    agenda_master.discussion_details,
    agenda_master.is_approved,
    agenda_master.is_deleted,
    agenda_master.created_at,
    agenda_master.updated_at,
    users.display_name,
    users.email_id,
    users.gender,
    users.dob,
    users.jpp_password,
    users.otp,
    users.is_admin,
    users.mobile_verification,
    users.is_deleted AS user_is_deleted,
    users.email_verification,
    users.otp_email,
    users.is_paid,
    users.paid_date,
    cast(pgp_sym_decrypt(users.first_name, 'SLMSZ1929S') AS VARCHAR) AS first_name,
    cast(pgp_sym_decrypt(users.last_name, 'SLMSZ1929S') AS VARCHAR) AS last_name,
    cast(pgp_sym_decrypt(users.user_address, 'SLMSZ1929S') AS VARCHAR) AS user_address,
    cast(pgp_sym_decrypt(users.ph_fixed, 'SLMSZ1929S') AS VARCHAR) AS ph_fixed,
    cast(pgp_sym_decrypt(users.ph_mobile, 'SLMSZ1929S') AS VARCHAR) AS ph_mobile,
    cast(pgp_sym_decrypt(users.handle_name, 'SLMSZ1929S') AS VARCHAR) AS handle_name
FROM
    agenda_master
INNER JOIN
    users ON agenda_master.user_id = users.user_id
WHERE
    agenda_master.is_deleted = FALSE;
