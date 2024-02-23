package controller

import (
	Database "ZenitusJPP/database"
	"ZenitusJPP/database/postgres/gen"
	"database/sql"
	"errors"
	"fmt"
	"strings"
	"time"

	"ZenitusJPP/models"
	service "ZenitusJPP/service"
	"encoding/json"

	"net/http"

	"github.com/jackc/pgtype"
	"github.com/stripe/stripe-go/v76"
	"github.com/stripe/stripe-go/v76/checkout/session"
)

func setStripKey() (vapiKey string, err error) {
	vapiKey = "sk_test_51NuW2ZSJnC83nBRfCgIbRicxdkCTc0oQejmWuRoIyxyq5GCilqivrA99XG47Inv64v4FB3RjF9rvvP5wiItc9zMi00QD1AHpwh"
	// vapiKey = "sk_test_51OYmqXSDDDM6V47wVhiixHLC3igpb3fRHaiv8GaSzX6wYecAsXhbjy3MfXMdi0sqdLU3B6KZ23U62XA5rYKusomT0080QhBxyJ"
	vapiKey = "sk_test_51OUnr5KSVnyLl97IJ6lyv1FY6hSVplaOCwviN3x7VtfbwIHw0O7TstjMMES43RGsiAToTSurIPqmx7mYP8keGPxr00MuPKDbxl"
	return
}

type MasterAgenda struct {
	MasterUser    service.MasterUser
	AgendaQuerier gen.AgendaQuerier
	UserQuerier   gen.UserQuerier
}

func (c *MasterAgenda) CreateDonationSessionV1(resp http.ResponseWriter, req *http.Request) {
	fmt.Println("a")
	// nameobj := "test"
	emailobj := "adasda@asdasdasd.com"
	// amoobj := string(100)
	apiKey, err := setStripKey()
	stripe.Key = apiKey

	obj, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "payment-url")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	fmt.Println("b")

	// Check if there's at least one item in the result list
	if len(obj) == 0 {
		service.WriteResponseErr(resp, http.StatusBadRequest, errors.New("no dynamic data found for the specified key"))
		return
	}

	// data from first
	dynamicData := obj[0]

	// Concatenate constant value to dynamicData.Values
	successURL := dynamicData.Values + "#/donation-success"
	// Concatenate constant value to dynamicData.Values
	cancelURL := dynamicData.Values + "#/donation-failed"
	fmt.Println("c")

	// Create Stripe Checkout Session
	params := &stripe.CheckoutSessionParams{
		Mode: stripe.String(string(stripe.CheckoutSessionModePayment)),
		LineItems: []*stripe.CheckoutSessionLineItemParams{
			&stripe.CheckoutSessionLineItemParams{
				Quantity: stripe.Int64(1),
				Price:    stripe.String("price_1Og1rySJnC83nBRfbTclMi3X"),
			},
		},
		SuccessURL:    stripe.String(successURL),
		CancelURL:     stripe.String(cancelURL),
		CustomerEmail: stripe.String(emailobj),
	}
	fmt.Println("d")

	// Create the Stripe Checkout Session
	s, err := session.New(params)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	fmt.Println("e")

	// Write response for successful Stripe Checkout Session creation
	service.WriteResponse2(resp, models.ResponseObj2{
		Message: "Successfully created Checkout Session",
		// PaymentToken: token,
		ResponseData: s,
	})
}

func (c *MasterAgenda) CreateAgenda(resp http.ResponseWriter, req *http.Request) {

	var ReqObj models.InsertAgendaInfoParamsObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//validation
	if service.CheckBlankParameter(string(ReqObj.TopicName)) {
		err := errors.New("topic name Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New("user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.LocationLevel)) {
		err := errors.New("location level Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.DiscussionDetails)) {
		err := errors.New("discussion Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting user's data by user id
	MessageObj, err := c.UserQuerier.SelectUserInfoByUserId(req.Context(), ReqObj.UserID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	if !MessageObj.MobileVerification {
		err = errors.New("mobile number is not verified")
		service.WriteResponseErr(resp, http.StatusUnauthorized, err)
		return
	}

	var ReqObjAgenda gen.InsertAgendaInfoParams
	ReqObjAgenda.DiscussionDetails = ReqObj.DiscussionDetails
	ReqObjAgenda.LocationLevel = ReqObj.LocationLevel
	ReqObjAgenda.TopicName = ReqObj.TopicName
	ReqObjAgenda.UserID = ReqObj.UserID

	Agendaid, err := c.AgendaQuerier.InsertAgendaInfo(req.Context(), ReqObjAgenda)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Adding agenda choices
	// step 1 : split the commoa seperated string
	// loop
	// add choicesc
	fmt.Println(Agendaid)
	choicesarray := strings.Split(ReqObj.AgnedaChoices, ",")
	for _, value := range choicesarray {
		// fmt.Println(key, ":", value)
		vsp := strings.Split(value, "~")
		var ReqObjAgendaChoice gen.InsertagendachoiceParams
		ReqObjAgendaChoice.AgendaMasterID = Agendaid
		if vsp[1] == "???" {
			ReqObjAgendaChoice.ChoiceText = "その他"
		} else {
			ReqObjAgendaChoice.ChoiceText = vsp[1]
		}
		switch vsp[0] {
		case "choice_one":
			ReqObjAgendaChoice.ChoiceID = string(gen.SelectChoiceChoiceOne)
		case "choice_two":
			ReqObjAgendaChoice.ChoiceID = string(gen.SelectChoiceChoiceTwo)
		case "choice_three":
			ReqObjAgendaChoice.ChoiceID = string(gen.SelectChoiceChoiceThree)
		case "choice_four":
			ReqObjAgendaChoice.ChoiceID = string(gen.SelectChoiceChoiceFour)
		case "choice_five":
			ReqObjAgendaChoice.ChoiceID = string(gen.SelectChoiceChoiceFive)
		case "choice_six":
			ReqObjAgendaChoice.ChoiceID = string(gen.SelectChoiceChoiceSix)
		}
		_, err = c.AgendaQuerier.Insertagendachoice(req.Context(), ReqObjAgendaChoice)
		if err != nil {
			service.WriteResponseErr(resp, http.StatusBadRequest, err)
			return
		}
	}

	service.WriteResponse(resp, models.ResponseObj{
		Message: "Agenda Created Successfully",
	})
}

func (c *MasterAgenda) CreateDonationSessionBackup(resp http.ResponseWriter, req *http.Request) {
	// Decode the request body into ReqObj
	// var ReqObj models.PaymentObj
	// err := json.NewDecoder(req.Body).Decode(&ReqObj)
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	apiKey, err := setStripKey()
	stripe.Key = apiKey

	// // Generate JWT Token
	// var l service.IAuthentication
	// token, err := service.IAuthentication.JWTToken(l, models.JWTUserDetails{
	// 	Email: ReqObj.EmailId,
	// 	Name:  "Not Added",
	// }, "GetToken")
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	// // Update payment token in the database
	// err = c.UserQuerier.UpdatePaymentTokenByEmailIDID(req.Context(), gen.UpdatePaymentTokenByEmailIDIDParams{
	// 	EmailID:      ReqObj.EmailId,
	// 	PaymentToken: token,
	// })
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	obj, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "payment-url")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Check if there's at least one item in the result list
	if len(obj) == 0 {
		service.WriteResponseErr(resp, http.StatusBadRequest, errors.New("no dynamic data found for the specified key"))
		return
	}

	// data from first
	dynamicData := obj[0]

	// Concatenate constant value to dynamicData.Values
	successURL := dynamicData.Values + "#/donation-success"
	// Concatenate constant value to dynamicData.Values
	cancelURL := dynamicData.Values + "#/donation-failed"

	params := &stripe.CheckoutSessionParams{
		Mode: stripe.String(string(stripe.CheckoutSessionModePayment)),
		LineItems: []*stripe.CheckoutSessionLineItemParams{
			&stripe.CheckoutSessionLineItemParams{
				PriceData: &stripe.CheckoutSessionLineItemPriceDataParams{
					Currency: stripe.String("jpy"),
					ProductData: &stripe.CheckoutSessionLineItemPriceDataProductDataParams{
						Name: stripe.String("Yearly Membership"),
					},
					UnitAmount: stripe.Int64(100),
				},
				Quantity: stripe.Int64(1),
			},
		},
		SuccessURL: stripe.String(successURL),
		CancelURL:  stripe.String(cancelURL),
	}

	// Create the Stripe Checkout Session
	s, err := session.New(params)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// // Create Stripe Checkout Session
	// params := &stripe.CheckoutSessionParams{
	// 	Mode: stripe.String(string(stripe.CheckoutSessionModePayment)),
	// 	LineItems: []*stripe.CheckoutSessionLineItemParams{
	// 		&stripe.CheckoutSessionLineItemParams{
	// 			Quantity: stripe.Int64(1),
	// 			Price:    stripe.String("price_1OcAV8SDDDM6V47wJZMNLaZs"),
	// 		},
	// 	},
	// 	SuccessURL: stripe.String(successURL),
	// 	CancelURL:  stripe.String(cancelURL),
	// 	// CustomerEmail: stripe.String(ReqObj.EmailId),
	// }

	// // Create the Stripe Checkout Session
	// s, err := session.New(params)
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	// Write response for successful Stripe Checkout Session creation
	service.WriteResponse2(resp, models.ResponseObj2{
		Message: "Successfully created Checkout Session",
		// PaymentToken: token,
		ResponseData: s,
	})
}

func (c *MasterAgenda) AgendaAcceptance(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.InsertAgendaAcceptanceParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New("user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.AgendaMasterID))) {
		err := errors.New("agenda id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Check if user already aproved
	userApprovalCount, err := c.AgendaQuerier.AcceptanceAgendaCountByAgendaUserId(req.Context(), gen.AcceptanceAgendaCountByAgendaUserIdParams{UserID: ReqObj.UserID, AgendaMasterID: ReqObj.AgendaMasterID})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
		return
	}

	if userApprovalCount > 0 {
		err := errors.New("user has already approved this agenda")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	_, err = c.AgendaQuerier.InsertAgendaAcceptance(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	service.WriteResponse(resp, models.ResponseObj{
		Message: "You has Accepted this Agenda",
	})
}

func (c *MasterAgenda) AgendaComment(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.InsertAgendaCommentParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New("user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.AgendaMasterID))) {
		err := errors.New("tagenda id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.CommentText)) {
		err := errors.New("comment Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Check if user already commented
	userCommentCount, err := c.AgendaQuerier.CommentCountByAgendaUserId(req.Context(), gen.CommentCountByAgendaUserIdParams{UserID: ReqObj.UserID, AgendaMasterID: ReqObj.AgendaMasterID})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
		return
	}

	if userCommentCount > 0 {
		err := errors.New("user has already commented")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	_, err = c.AgendaQuerier.InsertAgendaComment(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	err = c.AgendaQuerier.UpdateAgendaStatus(req.Context())
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	service.WriteResponse(resp, models.ResponseObj{
		Message: "You has Successfully Added the comment",
	})
}

func (c *MasterAgenda) AgendaCommentReply(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.InsertAgendaCommentReplyParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// fmt.Println(ReqObj, " : ", ReqObj.UserID, " : ", string(rune(ReqObj.UserID)), " : ", rune(ReqObj.UserID), ":", service.CheckBlankParameter(string(rune(ReqObj.UserID))))
	// //validation
	// if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
	// 	err := errors.New("user id Parameter Is Missing")
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }
	// if service.CheckBlankParameter(string(rune(ReqObj.AgendaMasterID))) {
	// 	err := errors.New("tagenda id Parameter Is Missing")
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }
	// if service.CheckBlankParameter(string(ReqObj.CommentText)) {
	// 	err := errors.New("comment Parameter Is Missing")
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }
	// Check if user already commented
	// userCommentCount, err := c.AgendaQuerier.CommentRreplyCountByAgendaUserId(req.Context(), gen.CommentRreplyCountByAgendaUserIdParams{
	// 	UserID:             ReqObj.UserID,
	// 	AgendaMasterID:     ReqObj.AgendaMasterID,
	// 	RefAgendaCommentID: ReqObj.RefAgendaCommentID,
	// })
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusInternalServerError, err)
	// 	return
	// }

	// if userCommentCount > 0 {
	// 	err := errors.New("user has already commented")
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	_, err = c.AgendaQuerier.InsertAgendaCommentReply(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	service.WriteResponse(resp, models.ResponseObj{
		Message: "You has Successfully Added the comment reply",
	})
}

func (c *MasterAgenda) AgendaChoice(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.InsertagendachoiceParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.AgendaMasterID))) {
		err := errors.New("agenda id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.ChoiceID)) {
		err := errors.New("choice id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	_, err = c.AgendaQuerier.Insertagendachoice(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	service.WriteResponse(resp, models.ResponseObj{
		Message: "You has Selected the give choice",
	})
}

func (c *MasterAgenda) AgendaVote(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.InsertAgendaVoteParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New("user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.AgendaMasterID))) {
		err := errors.New("agenda id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Check if user already voted
	userVoteCount, err := c.AgendaQuerier.VoteAgendaCountByAgendaUserId(req.Context(), gen.VoteAgendaCountByAgendaUserIdParams{UserID: ReqObj.UserID, AgendaMasterID: ReqObj.AgendaMasterID})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
		return
	}

	if userVoteCount > 0 {
		err := errors.New("user has already Voted")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	_, err = c.AgendaQuerier.InsertAgendaVote(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "Agenda Voted successfully",
	})
}

func (c *MasterAgenda) CommentAgendaLike(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.InsertAgendaCommentLikeParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New("user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.AgendaCommentID))) {
		err := errors.New("agenda id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// // Check if user already liked
	// userLikedCount, err := c.AgendaQuerier.CommentLikeCountByCommentUserId(req.Context(), gen.CommentLikeCountByCommentUserIdParams{UserID: ReqObj.UserID, AgendaCommentID: ReqObj.AgendaCommentID})
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusInternalServerError, err)
	// 	return
	// }

	// // Check if user already disliked
	// userDisLikedCount, err := c.AgendaQuerier.CommentDiLikeCountByCommentUserId(req.Context(), gen.CommentDiLikeCountByCommentUserIdParams{UserID: ReqObj.UserID, AgendaCommentID: ReqObj.AgendaCommentID})
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusInternalServerError, err)
	// 	return
	// }

	// if userLikedCount > 0 {
	// 	err := errors.New("user has already Like this comment")
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	// if userDisLikedCount > 0 {
	// 	err := errors.New("user has already Dislike this comment")
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	err = c.AgendaQuerier.DeleteLikeDislikeByUserIdAgnedaId(req.Context(), gen.DeleteLikeDislikeByUserIdAgnedaIdParams{
		AgendaCommentID: ReqObj.AgendaCommentID,
		UserID:          ReqObj.UserID,
	})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	_, err = c.AgendaQuerier.InsertAgendaCommentLike(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "Comment Liked successfully",
	})
}

func (c *MasterAgenda) CommentAgendaDisLike(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.InsertAgendaCommentDisLikeParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New("user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.AgendaCommentID))) {
		err := errors.New("agenda id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Check if user already liked
	// userLikedCount, err := c.AgendaQuerier.CommentLikeCountByCommentUserId(req.Context(), gen.CommentLikeCountByCommentUserIdParams{UserID: ReqObj.UserID, AgendaCommentID: ReqObj.AgendaCommentID})
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusInternalServerError, err)
	// 	return
	// }
	// // Check if user already Disliked
	// userDisLikedCount, err := c.AgendaQuerier.CommentDiLikeCountByCommentUserId(req.Context(), gen.CommentDiLikeCountByCommentUserIdParams{UserID: ReqObj.UserID, AgendaCommentID: ReqObj.AgendaCommentID})
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusInternalServerError, err)
	// 	return
	// }

	// if userLikedCount > 0 {
	// 	err := errors.New("user has already Like this comment")
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	// if userDisLikedCount > 0 {
	// 	err := errors.New("user has already Dislike this comment")
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	err = c.AgendaQuerier.DeleteLikeDislikeByUserIdAgnedaId(req.Context(), gen.DeleteLikeDislikeByUserIdAgnedaIdParams{
		AgendaCommentID: ReqObj.AgendaCommentID,
		UserID:          ReqObj.UserID,
	})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	_, err = c.AgendaQuerier.InsertAgendaCommentDisLike(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "Comment DisLiked successfully",
	})
}

func (c *MasterAgenda) CommentAgendaReport(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.InsertAgendaCommentReportParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New("user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.AgendaCommentID))) {
		err := errors.New("agenda id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Check if user already Reported
	userReportCount, err := c.AgendaQuerier.CommentReportCountByCommentUserId(req.Context(), gen.CommentReportCountByCommentUserIdParams{UserID: ReqObj.UserID, AgendaCommentID: ReqObj.AgendaCommentID})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
		return
	}

	if userReportCount > 0 {
		err := errors.New("user has already Reported this comment")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	_, err = c.AgendaQuerier.InsertAgendaCommentReport(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//get user data
	MessageObj, err := c.UserQuerier.SelectUserInfoByUserId(req.Context(), ReqObj.UserID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// // get Agenda comment data
	MessageObj2, err := c.AgendaQuerier.GetComentInfoByCommentID(req.Context(), ReqObj.AgendaCommentID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	fmt.Println(MessageObj2)

	obj, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-mail")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	obj2, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-pswd")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	obj3, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "admin-mail")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	obj4, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	obj5, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT-PORT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Send email notification to support team
	if len(MessageObj2) > 0 {
		err = service.SendAgendaReportEmail(MessageObj, MessageObj2[0], obj[0], obj2[0], obj3[0], obj4[0], obj5[0])
		if err != nil {
			// Handle the error
			fmt.Println("Error sending agenda report email:", err)
		}
	} else {
		fmt.Println("No AgendaComment found to send report email.")
	}

	service.WriteResponse(resp, models.ResponseObj{
		Message: "Comment Reported successfully",
	})
}

func (c *MasterAgenda) AgendaList(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.AgendaListOprion
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.OptionListing))) {
		err := errors.New("filter id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	var ReqObjObj gen.SelectAgendaPreApprovedParams
	var ReqObjObjA gen.SelectAgendaApprovedParams
	if ReqObj.OptionListing == 0 {
		ReqObjObj.LocationLevel = "tokyo"
		ReqObjObj.LocationLevel_2 = "kyoto"
		ReqObjObj.LocationLevel_3 = "other"
		ReqObjObjA.LocationLevel = "tokyo"
		ReqObjObjA.LocationLevel_2 = "kyoto"
		ReqObjObjA.LocationLevel_3 = "other"
	} else if ReqObj.OptionListing == 1 {
		ReqObjObj.LocationLevel = "tokyo"
		ReqObjObj.LocationLevel_2 = "tokyo"
		ReqObjObj.LocationLevel_3 = "tokyo"
		ReqObjObjA.LocationLevel = "tokyo"
		ReqObjObjA.LocationLevel_2 = "tokyo"
		ReqObjObjA.LocationLevel_3 = "tokyo"
	} else if ReqObj.OptionListing == 2 {
		ReqObjObj.LocationLevel = "kyoto"
		ReqObjObj.LocationLevel_2 = "kyoto"
		ReqObjObj.LocationLevel_3 = "kyoto"
		ReqObjObjA.LocationLevel = "kyoto"
		ReqObjObjA.LocationLevel_2 = "kyoto"
		ReqObjObjA.LocationLevel_3 = "kyoto"
	} else if ReqObj.OptionListing == 3 {
		ReqObjObj.LocationLevel = "other"
		ReqObjObj.LocationLevel_2 = "other"
		ReqObjObj.LocationLevel_3 = "other"
		ReqObjObjA.LocationLevel = "other"
		ReqObjObjA.LocationLevel_2 = "other"
		ReqObjObjA.LocationLevel_3 = "other"
	}
	fmt.Println("ReqObjObj")
	fmt.Println(ReqObjObj)
	objpreapproved, err := c.AgendaQuerier.SelectAgendaPreApproved(req.Context(), ReqObjObj)
	fmt.Println("erra : ", err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	fmt.Println("ReqObjObjA")
	fmt.Println(ReqObjObjA)
	objapproved, err := c.AgendaQuerier.SelectAgendaApproved(req.Context(), ReqObjObjA)
	fmt.Println("errb : ", err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	var respobj models.AgendaResponseObj
	respobj.PreApproved = objpreapproved
	respobj.ApprovedAgenda = objapproved

	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed Agenda List",
		ResponseData: respobj,
	})
}

func (c *MasterAgenda) AgendaReplyList(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.CommentReplyListByCommentIDParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.AgendaMasterID))) {
		err := errors.New("petition id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(rune(ReqObj.RefAgendaCommentID))) {
		err := errors.New("petition id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	objreply, err := c.AgendaQuerier.CommentReplyListByCommentID(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed Agenda comment reply List",
		ResponseData: objreply,
	})
}

func (c *MasterAgenda) AgendaDetail(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.GetGraphAgendaParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.Agendaid))) {
		err := errors.New("agenda id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	objcomment, err := c.AgendaQuerier.SelectCommentByAgendaID(req.Context(), ReqObj.Agendaid)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	objagenda, err := c.AgendaQuerier.SelectAgendaApprovedByID(req.Context(), gen.SelectAgendaApprovedByIDParams{
		AgendaMasterID: ReqObj.Agendaid,
		Male:           ReqObj.Male,
		Female:         ReqObj.Female,
		Agea:           ReqObj.Agea,
		Ageb:           ReqObj.Ageb,
		Agec:           ReqObj.Agec,
		Aged:           ReqObj.Aged,
		Agee:           ReqObj.Agee,
		Agef:           ReqObj.Agef,
		Ageg:           ReqObj.Ageg,
		Ageh:           ReqObj.Ageh,
	})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	graphobj, err := c.AgendaQuerier.GetGraphAgenda(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	type GenderDataObj struct {
		FemaleCount string `json:"femalecount,omitempty"`
		MaleCount   string `json:"malecount,omitempty"`
	}
	type AgeDataObj struct {
		G1Count string `json:"g1count,omitempty"`
		G2Count string `json:"g2count,omitempty"`
		G3Count string `json:"g3count,omitempty"`
		G4Count string `json:"g4count,omitempty"`
		G5Count string `json:"g5count,omitempty"`
		G6Count string `json:"g6count,omitempty"`
		G7Count string `json:"g7count,omitempty"`
		G8Count string `json:"g8count,omitempty"`
	}
	type GraphDataObj struct {
		GenderData GenderDataObj `json:"genderdata,omitempty"`
		AgeData    AgeDataObj    `json:"agedata,omitempty"`
	}
	var GraphData GraphDataObj
	GraphData.GenderData.MaleCount = ReturnString(graphobj.Malerecords)
	GraphData.GenderData.FemaleCount = ReturnString(graphobj.Femalerecords)
	GraphData.AgeData.G1Count = ReturnString(graphobj.Agegroupa)
	GraphData.AgeData.G2Count = ReturnString(graphobj.Agegroupb)
	GraphData.AgeData.G3Count = ReturnString(graphobj.Agegroupc)
	GraphData.AgeData.G4Count = ReturnString(graphobj.Agegroupd)
	GraphData.AgeData.G5Count = ReturnString(graphobj.Agegroupe)
	GraphData.AgeData.G6Count = ReturnString(graphobj.Agegroupf)
	GraphData.AgeData.G7Count = ReturnString(graphobj.Agegroupg)
	GraphData.AgeData.G8Count = ReturnString(graphobj.Agegrouph)
	var agendacommentobj models.AgendaDetailObj
	agendacommentobj.AgendaDetail = objagenda
	agendacommentobj.AgendaComment = objcomment
	agendacommentobj.GraphData = GraphData
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed Agenda List",
		ResponseData: agendacommentobj,
	})
}

func ReturnString(para int64) (rtn string) {
	if para == 0 {
		rtn = "0.0"
	} else {
		rtn = fmt.Sprintf("%v", para)
	}
	return
}
func (c *MasterAgenda) AgendaPreAproved(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.AgendaListOprion
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.OptionListing))) {
		err := errors.New("filter id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	var ReqObjObj gen.SelectAgendaPreApprovedParams
	// var ReqObjObjA gen.SelectAgendaApprovedParams
	if ReqObj.OptionListing == 0 {
		ReqObjObj.LocationLevel = "tokyo"
		ReqObjObj.LocationLevel_2 = "kyoto"
		ReqObjObj.LocationLevel_3 = "other"
		// ReqObjObjA.LocationLevel = "tokyo"
		// ReqObjObjA.LocationLevel_2 = "kyoto"
		// ReqObjObjA.LocationLevel_3 = "other"
	} else if ReqObj.OptionListing == 1 {
		ReqObjObj.LocationLevel = "tokyo"
		ReqObjObj.LocationLevel_2 = "tokyo"
		ReqObjObj.LocationLevel_3 = "tokyo"
		// ReqObjObjA.LocationLevel = "tokyo"
		// ReqObjObjA.LocationLevel_2 = "kyoto"
		// ReqObjObjA.LocationLevel_3 = "other"
	} else if ReqObj.OptionListing == 2 {
		ReqObjObj.LocationLevel = "kyoto"
		ReqObjObj.LocationLevel_2 = "kyoto"
		ReqObjObj.LocationLevel_3 = "kyoto"
		// ReqObjObjA.LocationLevel = "tokyo"
		// ReqObjObjA.LocationLevel_2 = "kyoto"
		// ReqObjObjA.LocationLevel_3 = "other"
	} else if ReqObj.OptionListing == 3 {
		ReqObjObj.LocationLevel = "other"
		ReqObjObj.LocationLevel_2 = "other"
		ReqObjObj.LocationLevel_3 = "other"
		// ReqObjObjA.LocationLevel = "tokyo"
		// ReqObjObjA.LocationLevel_2 = "kyoto"
		// ReqObjObjA.LocationLevel_3 = "other"
	}
	obj, err := c.AgendaQuerier.SelectAgendaPreApproved(req.Context(), ReqObjObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed Agenda pre Aproved member",
		ResponseData: obj,
	})

}

func (c *MasterAgenda) PartyInquiry(resp http.ResponseWriter, req *http.Request) {
	var ReqObj gen.InsertPartyInquiryParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	//validation
	if service.CheckBlankParameter(string(ReqObj.UserName)) {
		err := errors.New("user name Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.EmailID)) {
		err := errors.New("email id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if service.CheckBlankParameter(string(ReqObj.MessageContent)) {
		err := errors.New("message Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	fmt.Println(ReqObj)
	fmt.Println(c)
	fmt.Println(c.AgendaQuerier)

	//getting data from dynamic values using keys
	obj, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-mail")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	//getting data from dynamic values using keys
	obj1, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-pswd")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj2, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "admin-mail")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj3, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj4, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT-PORT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// function to send emails
	err = service.SendPartyInquiryEmail(ReqObj, obj[0], obj1[0], obj2[0], obj3[0], obj4[0])
	if err != nil {
		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
		return
	}

	_, err = c.AgendaQuerier.InsertPartyInquiry(req.Context(), ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "successfully added Party Inquiry",
	})
}

func (c *MasterAgenda) Home(resp http.ResponseWriter, req *http.Request) {
	obj, err := c.AgendaQuerier.SelectHomeContent(req.Context())
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "Successfully list Home Details",
		ResponseData: obj,
	})

}

func (c *MasterAgenda) CreateCheckoutSession(resp http.ResponseWriter, req *http.Request) {
	// Decode the request body into ReqObj
	var ReqObj models.PaymentObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	userobj, err := c.UserQuerier.SelectUserInfoByEmail(req.Context(), ReqObj.EmailId)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	userobjobj, err := c.UserQuerier.SelectUserInfoByUserId(req.Context(), userobj.UserID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	if userobjobj.IsPaid == true {
		fmt.Println(":", userobjobj.MembershipCompletionDate, " : ", userobjobj.IsPaid)
		t := fmt.Sprintf("%v", userobjobj.MembershipCompletionDate)
		tarray := strings.Split(t, " ")
		fmt.Println(tarray)
		fmt.Println(":;", t)
		t1, err := time.Parse("2006-01-02", tarray[0])
		fmt.Println(err)
		if err != nil {
			service.WriteResponseErr(resp, http.StatusBadRequest, err)
			return
		}
		fmt.Println(t1.Format("02 Jan 2006"), " : ", time.Now().Format("02 Jan 2006"))

		if t1.After(time.Now()) {
			opterr := errors.New("You have already paid")
			service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
			return
		}

		fmt.Println("You can paye")
		// return
	}
	apiKey, err := setStripKey()
	stripe.Key = apiKey

	// Generate JWT Token
	var l service.IAuthentication
	token, err := service.IAuthentication.JWTToken(l, models.JWTUserDetails{
		Email: ReqObj.EmailId,
		Name:  "Not Added",
	}, "GetToken")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Update payment token in the database
	err = c.UserQuerier.UpdatePaymentTokenByEmailIDID(req.Context(), gen.UpdatePaymentTokenByEmailIDIDParams{
		EmailID:      ReqObj.EmailId,
		PaymentToken: token,
	})
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	obj, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "payment-url")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Check if there's at least one item in the result list
	if len(obj) == 0 {
		service.WriteResponseErr(resp, http.StatusBadRequest, errors.New("no dynamic data found for the specified key"))
		return
	}

	// data from first
	dynamicData := obj[0]

	// Concatenate constant value to dynamicData.Values
	successURL := dynamicData.Values + "#/payment-success"
	// Concatenate constant value to dynamicData.Values
	cancelURL := dynamicData.Values + "#/payment-failed"

	// Create Stripe Checkout Session
	params := &stripe.CheckoutSessionParams{
		Mode: stripe.String(string(stripe.CheckoutSessionModePayment)),
		LineItems: []*stripe.CheckoutSessionLineItemParams{
			&stripe.CheckoutSessionLineItemParams{
				PriceData: &stripe.CheckoutSessionLineItemPriceDataParams{
					Currency: stripe.String("jpy"),
					ProductData: &stripe.CheckoutSessionLineItemPriceDataProductDataParams{
						Name: stripe.String("Yearly Membership"),
					},
					UnitAmount: stripe.Int64(100),
				},
				Quantity: stripe.Int64(1),
			},
		},
		SuccessURL:    stripe.String(successURL),
		CancelURL:     stripe.String(cancelURL),
		CustomerEmail: stripe.String(ReqObj.EmailId),
	}

	// Create the Stripe Checkout Session
	s, err := session.New(params)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// AAAAAA
	var ReqObjObj gen.InsertTransactionParams
	ReqObjObj.UserID = userobj.UserID
	ReqObjObj.TransactionUrl = s.URL
	ReqObjObj.TransactionToken = token

	_, err = c.UserQuerier.InsertTransaction(req.Context(), ReqObjObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Write response for successful Stripe Checkout Session creation
	service.WriteResponse2(resp, models.ResponseObj2{
		Message:      "Successfully created Checkout Session",
		PaymentToken: token,
		ResponseData: s,
	})
}

func (c *MasterAgenda) PaymentProcess(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.PaymentObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	UserObj, Err := c.UserQuerier.SelectUserInfoByEmail(req.Context(), ReqObj.EmailId)
	if Err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, Err)
		return
	}
	if UserObj.PaymentToken != ReqObj.Token {
		opterr := errors.New("token does not match")
		service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
		return
	}

	// Update payment token in the database
	err = c.UserQuerier.UpdatePaymentTokenByEmailIDID(req.Context(), gen.UpdatePaymentTokenByEmailIDIDParams{
		EmailID:      ReqObj.EmailId,
		PaymentToken: ReqObj.Token,
		IsPaid:       true,
		PaidDate: Database.NullTime{
			NullTime: sql.NullTime{
				Time:  time.Now(),
				Valid: true,
			},
		},
	})

	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	err = c.UserQuerier.UpdateTransactionById(req.Context(), gen.UpdateTransactionByIdParams{
		UserID:            UserObj.UserID,
		TransactionToken:  ReqObj.Token,
		TransactionStatus: true,
	})

	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	MessageObj, err := c.UserQuerier.SelectUserInfoByUserId(req.Context(), UserObj.UserID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-mail")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj1, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-pswd")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj2, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj3, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT-PORT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	membershipCompletionDate, ok := MessageObj.MembershipCompletionDate.(time.Time)
	if !ok {
		// Handle the case where the assertion fails (e.g., log an error)
		err := errors.New("failed to assert MembershipCompletionDate as time.Time")
		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
		return
	}

	fullName := MessageObj.FirstName + " " + MessageObj.LastName

	// Send payment success email
	err = service.SendPaymentSuccessEmail(MessageObj.EmailID, fullName, membershipCompletionDate, obj[0], obj1[0], obj2[0], obj3[0])
	if err != nil {
		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
		return
	}

	service.WriteResponse(resp, models.ResponseObj{
		JapaneseMessage: "支払いが正常に追加されました",
		Message:         "Payment Added Successfully",
	})

}

func (c *MasterAgenda) PaymentReturn(resp http.ResponseWriter, req *http.Request) {
	// var ReqObj models.PaymentObj
	// err := json.NewDecoder(req.Body).Decode(&ReqObj)
	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }
	// // Validation
	// UserObj, Err := c.UserQuerier.SelectUserInfoByEmail(req.Context(), ReqObj.EmailId)
	// if Err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, Err)
	// 	return
	// }
	// if UserObj.PaymentToken != ReqObj.Token {
	// 	opterr := errors.New("token does not match")
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
	// 	return
	// }

	// // Update payment token in the database
	// err = c.UserQuerier.UpdatePaymentTokenByEmailIDID(req.Context(), gen.UpdatePaymentTokenByEmailIDIDParams{
	// 	EmailID: ReqObj.EmailId,
	// 	IsPaid:  false,
	// })

	// if err != nil {
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }

	// Write a success response if everything is successful
	service.WriteResponse(resp, models.ResponseObj{
		Message: "Payment Failed",
		// Additional fields if needed
	})

}

func (c *MasterAgenda) CreateDonationSession(resp http.ResponseWriter, req *http.Request) {
	// Decode the request body into ReqObj
	var ReqObj models.DonationObj
	// gen.InsertDonationTransactionParams
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	apiKey, err := setStripKey()
	stripe.Key = apiKey

	obj, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "payment-url")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Check if there's at least one item in the result list
	if len(obj) == 0 {
		service.WriteResponseErr(resp, http.StatusBadRequest, errors.New("no dynamic data found for the specified key"))
		return
	}

	// Generate JWT Token
	var l service.IAuthentication
	token, err := service.IAuthentication.JWTToken(l, models.JWTUserDetails{
		Email: ReqObj.UserEmail,
		Name:  "Not Added",
	}, "GetToken")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// data from first
	dynamicData := obj[0]

	// Concatenate constant value to dynamicData.Values
	successURL := dynamicData.Values + "#/donation-success"
	// Concatenate constant value to dynamicData.Values
	cancelURL := dynamicData.Values + "#/donation-failed"
	x := new(pgtype.Numeric)
	err = x.Set("3.145")
	err = x.Set(ReqObj.UserAmount)
	fmt.Println(err, " : ", x)
	var donationamt int = int((ReqObj.UserAmount).(float64))
	// donationamt, errb := (ReqObj.UserAmount).(int)
	// fmt.Println(donationamt, " : ", errb, " : ", ReqObj.UserAmount)
	// if !errb {
	// 	err := errors.New("Unable to convert the number")
	// 	service.WriteResponseErr(resp, http.StatusBadRequest, err)
	// 	return
	// }
	donationtext := fmt.Sprintf("(寄付金額)-%v", donationamt)
	donationtext = "(寄付金額)"
	params := &stripe.CheckoutSessionParams{
		Mode: stripe.String(string(stripe.CheckoutSessionModePayment)),
		LineItems: []*stripe.CheckoutSessionLineItemParams{
			&stripe.CheckoutSessionLineItemParams{
				PriceData: &stripe.CheckoutSessionLineItemPriceDataParams{
					Currency: stripe.String("jpy"),
					ProductData: &stripe.CheckoutSessionLineItemPriceDataProductDataParams{
						Name: stripe.String(donationtext),
					},
					UnitAmount: stripe.Int64(int64(donationamt)),
				},
				Quantity: stripe.Int64(1),
			},
		},
		SuccessURL:    stripe.String(successURL),
		CancelURL:     stripe.String(cancelURL),
		CustomerEmail: stripe.String(ReqObj.UserEmail),
	}

	// Create the Stripe Checkout Session
	s, err := session.New(params)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	userObj, err := c.UserQuerier.SelectUserInfoByEmail(req.Context(), ReqObj.UserEmail)
	if err != nil {
		// Check if the error is due to "no rows in result set"
		if err.Error() == "no rows in result set" {
			// Set UserID to 0
			userObj.UserID = 0
		} else {
			service.WriteResponseErr(resp, http.StatusBadRequest, err)
			return
		}
	}

	var ReqObjObj gen.InsertDonationTransactionParams
	ReqObjObj.UserID = userObj.UserID
	ReqObjObj.TransactionUrl = s.URL
	ReqObjObj.TransactionToken = token
	ReqObjObj.UserName = ReqObj.UserName
	ReqObjObj.UserEmail = ReqObj.UserEmail
	ReqObjObj.UserAmount = *x
	// ReqObj.UserAmount

	_, err = c.UserQuerier.InsertDonationTransaction(req.Context(), ReqObjObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Write response for successful Stripe Checkout Session creation
	service.WriteResponse2(resp, models.ResponseObj2{
		Message:      "Successfully created Checkout Session",
		PaymentToken: token,
		ResponseData: s,
	})
}

func (c *MasterAgenda) DonationProcess(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.DonationObj2
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	MessageObj2, err := c.UserQuerier.SelectTDonationTransactionByUserID(req.Context(), ReqObj.Token)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//Append data from MessageObj2 to a string
	appendedData := ""

	for _, transaction := range MessageObj2 {
		appendedData += transaction.TransactionToken // Assuming there's a field named TransactionData
	}

	if appendedData != ReqObj.Token {
		opterr := errors.New("token does not match")
		service.WriteResponseErr(resp, http.StatusBadRequest, opterr)
		return
	}

	err = c.UserQuerier.UpdateDonationTransactionByToken(req.Context(), gen.UpdateDonationTransactionByTokenParams{
		TransactionToken:  ReqObj.Token,
		TransactionStatus: true,
	})

	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-mail")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj1, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "support-pswd")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj2, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	//getting data from dynamic values using keys
	obj3, err := c.UserQuerier.SelectDynamicDataByKey(req.Context(), "SMPT-PORT")
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}

	// Send payment success email
	err = service.SendDonationPaymentSuccessEmail(ReqObj.EmailId, ReqObj.UserName, obj[0], obj1[0], obj2[0], obj3[0])
	if err != nil {
		service.WriteResponseErr(resp, http.StatusInternalServerError, err)
		return
	}

	service.WriteResponse(resp, models.ResponseObj{
		JapaneseMessage: "支払いが正常に追加されました",
		Message:         "Donation Added Successfully",
	})
}

func (c *MasterAgenda) DonationReturn(resp http.ResponseWriter, req *http.Request) {
	// Write a success response if anyone is Not successful
	service.WriteResponse(resp, models.ResponseObj{
		Message: "Donation Failed",
		// Additional fields if needed
	})
}

func (c *MasterAgenda) UserPaymentHistory(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.UserObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	fmt.Println(err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation: Check if UserID is blank
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New(" user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	MessageObj, err := c.UserQuerier.SelectTransactionByUserID(req.Context(), ReqObj.UserID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed User Payment Transaction",
		ResponseData: MessageObj,
	})
}

func (c *MasterAgenda) DonationHistory(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.PaymentObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	fmt.Println(err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation: Check if UserID is blank
	if service.CheckBlankParameter(string(ReqObj.EmailId)) {
		err := errors.New("mail id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	MessageObj, err := c.UserQuerier.SelectDonationTransactionByEmailID(req.Context(), ReqObj.EmailId)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed User Donation Transaction",
		ResponseData: MessageObj,
	})
}

func (c *MasterAgenda) Notification(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.UserObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	fmt.Println(err)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	//validation
	if service.CheckBlankParameter(string(rune(ReqObj.UserID))) {
		err := errors.New("user id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	MessageObj, err := c.AgendaQuerier.NotificationByUserId(req.Context(), ReqObj.UserID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message:      "successfully listed Notification",
		ResponseData: MessageObj,
	})
}

func (c *MasterAgenda) NotificationDelete(resp http.ResponseWriter, req *http.Request) {
	var ReqObj models.NotificationObj
	err := json.NewDecoder(req.Body).Decode(&ReqObj)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	if service.CheckBlankParameter(string(rune(ReqObj.NotificationID))) {
		err := errors.New("notification id Parameter Is Missing")
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	// Validation
	err = c.AgendaQuerier.DeleteNotificationById(req.Context(), ReqObj.NotificationID)
	if err != nil {
		service.WriteResponseErr(resp, http.StatusBadRequest, err)
		return
	}
	service.WriteResponse(resp, models.ResponseObj{
		Message: "successfully deleted notification list",
	})
}
