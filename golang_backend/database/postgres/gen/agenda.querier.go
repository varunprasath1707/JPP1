// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.20.0

package gen

import (
	"context"
)

type AgendaQuerier interface {
	AcceptanceAgendaCountByAgendaUserId(ctx context.Context, arg AcceptanceAgendaCountByAgendaUserIdParams) (int64, error)
	AgendaChoice(ctx context.Context, arg AgendaChoiceParams) (int64, error)
	AgnedaCommentDeleteByAgendaIDUserId(ctx context.Context, arg AgnedaCommentDeleteByAgendaIDUserIdParams) error
	CommentCountByAgendaUserId(ctx context.Context, arg CommentCountByAgendaUserIdParams) (int64, error)
	CommentDiLikeCountByCommentUserId(ctx context.Context, arg CommentDiLikeCountByCommentUserIdParams) (int64, error)
	CommentLikeCountByCommentUserId(ctx context.Context, arg CommentLikeCountByCommentUserIdParams) (int64, error)
	CommentReplyListByCommentID(ctx context.Context, arg CommentReplyListByCommentIDParams) ([]*CommentReplyListByCommentIDRow, error)
	CommentReportCountByCommentUserId(ctx context.Context, arg CommentReportCountByCommentUserIdParams) (int64, error)
	CommentRreplyCountByAgendaUserId(ctx context.Context, arg CommentRreplyCountByAgendaUserIdParams) (int64, error)
	DeleteLikeDislikeByUserIdAgnedaId(ctx context.Context, arg DeleteLikeDislikeByUserIdAgnedaIdParams) error
	DeleteNotificationById(ctx context.Context, notificationID int64) error
	GetComentInfoByCommentID(ctx context.Context, agendaCommentID int64) ([]*AgendaComment, error)
	GetGraphAgenda(ctx context.Context, arg GetGraphAgendaParams) (*GetGraphAgendaRow, error)
	InsertAgendaAcceptance(ctx context.Context, arg InsertAgendaAcceptanceParams) (int64, error)
	InsertAgendaComment(ctx context.Context, arg InsertAgendaCommentParams) (int64, error)
	InsertAgendaCommentDisLike(ctx context.Context, arg InsertAgendaCommentDisLikeParams) (int64, error)
	InsertAgendaCommentLike(ctx context.Context, arg InsertAgendaCommentLikeParams) (int64, error)
	InsertAgendaCommentReply(ctx context.Context, arg InsertAgendaCommentReplyParams) (int64, error)
	InsertAgendaCommentReport(ctx context.Context, arg InsertAgendaCommentReportParams) (int64, error)
	InsertAgendaInfo(ctx context.Context, arg InsertAgendaInfoParams) (int64, error)
	InsertAgendaVote(ctx context.Context, arg InsertAgendaVoteParams) (int64, error)
	InsertPartyInquiry(ctx context.Context, arg InsertPartyInquiryParams) (int64, error)
	Insertagendachoice(ctx context.Context, arg InsertagendachoiceParams) (int64, error)
	NotificationByUserId(ctx context.Context, userID int64) ([]*Notification, error)
	NotificationInsert(ctx context.Context, arg NotificationInsertParams) (int64, error)
	SelectAgenda(ctx context.Context) ([]*SelectAgendaRow, error)
	SelectAgendaAllInfo(ctx context.Context) ([]*SelectAgendaAllInfoRow, error)
	SelectAgendaAllInfoAdmin(ctx context.Context) ([]*SelectAgendaAllInfoAdminRow, error)
	SelectAgendaAllInfoBackup(ctx context.Context) ([]*SelectAgendaAllInfoBackupRow, error)
	SelectAgendaApproved(ctx context.Context, arg SelectAgendaApprovedParams) ([]*SelectAgendaApprovedRow, error)
	SelectAgendaApprovedByID(ctx context.Context, arg SelectAgendaApprovedByIDParams) ([]*SelectAgendaApprovedByIDRow, error)
	SelectAgendaPreApproved(ctx context.Context, arg SelectAgendaPreApprovedParams) ([]*SelectAgendaPreApprovedRow, error)
	SelectCommentByAgendaID(ctx context.Context, agendaMasterID int64) ([]*SelectCommentByAgendaIDRow, error)
	SelectHomeContent(ctx context.Context) ([]*SelectHomeContentRow, error)
	UpdateAgendaApprovedStatus(ctx context.Context, agendaMasterID int64) error
	UpdateAgendaStatus(ctx context.Context) error
	UpdateDeletedByAgendaId(ctx context.Context, agendaMasterID int64) error
	VoteAgendaCountByAgendaUserId(ctx context.Context, arg VoteAgendaCountByAgendaUserIdParams) (int64, error)
}

var _ AgendaQuerier = (*Queries)(nil)
